package kr.co.oliveandwine.ssgether;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import kr.co.oliveandwine.ssgether.Adapter.Item.WalkLogItem;
import kr.co.oliveandwine.ssgether.databinding.ActivityMainBinding;

import androidx.databinding.DataBindingUtil;

import kr.co.oliveandwine.ssgether.databinding.ActivityMainBinding;
import kr.co.oliveandwine.ssgether.util.PedometerService;
import kr.co.oliveandwine.ssgether.util.StepCallback;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import kr.co.oliveandwine.ssgether.Save.Save_Var;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    boolean isRunning = false;
    Intent serviceIntent;
    private PedometerService pedometerService;
    public static final String TAG = "PedometerService";

    SensorManager sensorManager;
    Sensor stepCountSensor;

    public static TextView walk_num;

    private int steps = 0;
    private int counterSteps = 0;

    public static MainActivity mainActivity;
    long mNow;
    Date mDate;
    SimpleDateFormat mFormat = new SimpleDateFormat("hh:mm:ss");

    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private mapFragment mapFragment = new mapFragment();
    private Running runningFragment = new Running();
    private mypageFragment mypageFragment = new mypageFragment();

    AdView mAdView;

    private String[] permissionList = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_MEDIA_LOCATION,
            Manifest.permission.ACTIVITY_RECOGNITION};

    //?????? ?????? ??????
    public void checkPermission() {
        //?????? ?????? 6.0 ???????????? ?????? --> 6?????? ?????? ?????? ??????
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return;

        //??? ?????? ?????? ????????? ??????
        for (String permission : permissionList) {
            int chk = checkCallingOrSelfPermission(permission);
            //?????? ????????????
            if (chk == PackageManager.PERMISSION_DENIED) {
                //??????????????? ?????? ??????????????? ???????????? ?????? ?????????.
                requestPermissions(permissionList, 0); //?????? ?????? ????????? ????????? ?????????.
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
            super.onBackPressed();
        } else {
            backPressedTime = tempTime;
            Toast.makeText(this, "?????? ????????? ??? ??? ??? ????????? ???????????????.", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setContentView(R.layout.activity_main);
        checkPermission();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, mapFragment).commitAllowingStateLoss();
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new ItemSelectedListener());

        mainActivity = this;

        if(S_Preference.getWalkLogArrayPref(getApplicationContext(), "walklog")==null){
            ArrayList<WalkLogItem> arrayList = new ArrayList<>();
            S_Preference.setWalkLogArrayPref(getApplicationContext(), "walklog", arrayList);
        }

    }

    class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            switch (menuItem.getItemId()) {
                case R.id.running:
                    transaction.replace(R.id.frameLayout, runningFragment).commitAllowingStateLoss();
                    break;
                case R.id.map:
                    transaction.replace(R.id.frameLayout, mapFragment).commitAllowingStateLoss();
                    break;
                case R.id.person:
                    transaction.replace(R.id.frameLayout, mypageFragment).commitAllowingStateLoss();
                    break;
            }
            return true;
        }
    }

    public void startService() {
        serviceIntent = new Intent(MainActivity.this, PedometerService.class);
        PedometerService.serviceIntent = serviceIntent;
        if (!isServiceRunningCheck()) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                startService();
            } else {
                startForegroundService(serviceIntent);
            }
//            Toast.makeText(getApplicationContext(), "ff", Toast.LENGTH_SHORT).show();
            bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
        int num = PedometerService.mSteps;
        //binding.pedometerSteps.setText(MessageFormat.format("{0} / 5,000", num));
        //binding.pedometerProgress.setProgress(num);
        Save_Var.getInstance().setWalkCount(num);
    }

    public void stopForeground() {
        if (isServiceRunningCheck()) {
            stopService(PedometerService.serviceIntent);
//            Toast.makeText(getApplicationContext(), "ff", Toast.LENGTH_SHORT).show();
            if (!PedometerService.NotiCheck) {
                unbindService(serviceConnection);
            }
        }
    }

    // ????????? ????????? Set ?????? ?????????????????? ????????? Unbind??? ????????? ???????????? ?????? ????????? ?????????
    private StepCallback stepCallback = new StepCallback() {
        @Override
        public void onStepCallback(int step) {
            Save_Var.getInstance().setWalkCount(step);
            //binding.pedometerSteps.setText(MessageFormat.format("{0} / 5,000", step));
            //binding.pedometerProgress.setProgress(step);
        }

        @Override
        public void onUnbindService() {
            isRunning = false;
            Log.d(TAG, "?????????");
        }
    };

    // ????????? ???????????? ???????????? ????????? ?????????
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            PedometerService.PedometerBinder mb = (PedometerService.PedometerBinder) iBinder;
            pedometerService = mb.getService();
            pedometerService.setCallback(stepCallback);
            isRunning = true;
            Log.d(TAG, "serviceConnection ?????????");
        }

        // ????????? ????????? ???????????? ???????????? ?????? ?????? ????????? ?????? ??????????????? ?????? ???
        // stopService ?????? unBindService ??? ???????????? ??????.
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isRunning = false;
            Log.d(TAG, "serviceConnection ?????????");
        }
    };

    public boolean isServiceRunningCheck() {
        ActivityManager manager = (ActivityManager) this.getSystemService(Activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("kr.co.oliveandwine.ssgether.util.PedometerService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isServiceRunningCheck()) {
            bindService(PedometerService.serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isServiceRunningCheck() && serviceIntent != null) {
            bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isServiceRunningCheck()) {
            // ??????????????? ???????????? ??? ???????????? ??????????????? ???????????? ?????????: ????????????????????? UI??? ????????? ?????? ?????? ??????
            unbindService(serviceConnection);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //PedometerService.NotiCheck = true;
    }
}