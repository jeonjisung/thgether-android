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

    //권한 체크 함수
    public void checkPermission() {
        //현재 버전 6.0 미만이면 종료 --> 6이후 부터 권한 허락
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return;

        //각 권한 허용 여부를 확인
        for (String permission : permissionList) {
            int chk = checkCallingOrSelfPermission(permission);
            //거부 상태라면
            if (chk == PackageManager.PERMISSION_DENIED) {
                //사용자에게 권한 허용여부를 확인하는 창을 띄운다.
                requestPermissions(permissionList, 0); //권한 검사 필요한 것들만 남는다.
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
            Toast.makeText(this, "뒤로 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
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

    // 서비스 내부로 Set 되어 스텝카운트의 변화와 Unbind의 결과를 전달하는 콜백 객체의 구현체
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
            Log.d(TAG, "해제됨");
        }
    };

    // 서비스 바인드를 담당하는 객체의 구현체
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            PedometerService.PedometerBinder mb = (PedometerService.PedometerBinder) iBinder;
            pedometerService = mb.getService();
            pedometerService.setCallback(stepCallback);
            isRunning = true;
            Log.d(TAG, "serviceConnection 연결됨");
        }

        // 요거는 사실상 서비스가 킬되거나 아예 죽임 당했을 때만 호출된다고 보면 됨
        // stopService 또는 unBindService 때 호출되지 않음.
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isRunning = false;
            Log.d(TAG, "serviceConnection 해제됨");
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
            // 액티비티를 벗어났을 땐 서비스와 액티비티의 바인드를 끊어줌: 백그라운드에서 UI를 건들지 않게 하기 위함
            unbindService(serviceConnection);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //PedometerService.NotiCheck = true;
    }
}