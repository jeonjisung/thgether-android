package kr.co.oliveandwine.ssgether;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.LatLngBounds;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.Projection;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.overlay.PathOverlay;
import com.naver.maps.map.util.FusedLocationSource;
import com.pd.chocobar.ChocoBar;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executor;

import kr.co.oliveandwine.ssgether.Adapter.CustomListViewAdapter;
import kr.co.oliveandwine.ssgether.Adapter.Item.JavaItem;
import kr.co.oliveandwine.ssgether.Save.Save_Var;
import kr.co.oliveandwine.ssgether.util.PedometerService;
import ted.gun0912.clustering.geometry.TedLatLng;
import ted.gun0912.clustering.geometry.TedLatLngBounds;
import ted.gun0912.clustering.naver.TedNaverClustering;


public class mapFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = mapFragment.class.getSimpleName();

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource LocationSource;
    private NaverMap naverMap;

    //엑셀에서 쓰레기통 위치 데이터를 가져와서 담을 리스트 선언
    private List<trashCanData> trashCanDataList = new ArrayList<>();

    private MapView mapView;

    ViewGroup viewGroup;
    Marker marker1, marker2;

    boolean checkService;

    TextView tvAddress;
    Button markerDelete, manboStart;

    ImageView imageView;

    SlidingUpPanelLayout sliding;

    FirebaseUser currentUser;

    TedNaverClustering.Builder<JavaItem> builder;

    trashCanData tcd;
    String[] tokens;

    OverlayImage image = OverlayImage.fromResource(R.drawable.marker_public);
    OverlayImage image2 = OverlayImage.fromResource(R.drawable.marker_individual);
    OverlayImage image3 = OverlayImage.fromResource(R.drawable.marker_select);

    List<DocumentReference> dr = new ArrayList<>();
    List<Marker> mk = new ArrayList<>();
    List<String> contents = new ArrayList<>();
    List<String> userIDs = new ArrayList<>();

    String filename;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    List<Marker> markers = new ArrayList<>();

    FirebaseStorage storage;
    FirebaseAuth mAuth;

    CreateDialog Create;
    LatLng platLng;

    TextView mWriter, mContent;
    Display display;
    int width, height;

    static HashMap<Integer, JavaItem> cache = new HashMap<>();

    List<JavaItem> javaItems = new ArrayList<>();

    LocationManager lm;

    //마커와 내 위치 연결해줄 패스와 위치정보 변수
    PathOverlay path;
    Location nowLocation, markerLocation;
    boolean manboMode = false;
    JavaItem targetJav;

    double loclng;
    double loclat;

    TextView customText;
    ImageView customImageView;

    ConstraintLayout container;
    ConstraintLayout constraintLayout;

    public mapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Save_Var.getInstance().setMapFragment(this);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size); // or getSize(size)
        width = size.x;
        height = size.y;

        lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        if(S_Preference.getInt(getContext(), "memoCount")==-1){
            S_Preference.setInt(getContext(),"memoCount", 0);
        }

        if(S_Preference.getInt(getContext(), "walkCountSum")==-1){
            S_Preference.setInt(getContext(),"walkCountSum", 0);
        }


        if (PedometerService.Path_overlay != null) {
            path = PedometerService.Path_overlay;
            customText = new TextView(getContext());
            customText.setText("");
            customText.setTextSize(20);
            customText.setTextColor(Color.rgb(255, 255, 255));
            customText.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "cafe24ssurround.ttf"));

        } else {
            path = new PathOverlay();
        }

        if (PedometerService.location != null) {
            nowLocation = PedometerService.location;
        }

        if (PedometerService.location2 != null) {
            markerLocation = PedometerService.location2;
        }

        manboMode = PedometerService.manbo;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_map, container, false);
        setHasOptionsMenu(true);


        return viewGroup;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle saveInstanceState) {
        super.onViewCreated(view, saveInstanceState);

        this.container = (ConstraintLayout) view.findViewById(R.id.back_layout);

        mapView = view.findViewById(R.id.map_view);
        mapView.onCreate(saveInstanceState);
        mapView.getMapAsync(this);

        storage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();

        LocationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

        sliding = view.findViewById(R.id.main_frame);
        tvAddress = view.findViewById(R.id.address);
        markerDelete = view.findViewById(R.id.markerDelete);
        manboStart = view.findViewById(R.id.manboStart);
        mWriter = view.findViewById(R.id.mWriter);
        mContent = view.findViewById(R.id.mContent);

        if (PedometerService.javaItems==null || cache==null) {
            readData();
            for (int i = 0; i < trashCanDataList.size(); i++) {
                final int finalI = i;
                Double longitude = trashCanDataList.get(i).getLongitude();
                Double latitude = trashCanDataList.get(i).getLatitude();
                String address = trashCanDataList.get(i).getAddress();
                // Create a new user with a first and last name
                JavaItem ji = new JavaItem(new LatLng(latitude, longitude));
                ji.setAddress(address);
                javaItems.add(ji);
                cache.put(i, ji);
            }
            PedometerService.javaItems = cache;
        } else {
            cache = PedometerService.javaItems;
        }
    }

    View.OnClickListener CreateConfirm = new View.OnClickListener() {
        public void onClick(View v) {
            CreateMarker(platLng);
            Create.dismiss();
        }
    };

    private View.OnClickListener CreateCancel = new View.OnClickListener() {
        public void onClick(View v) {
            //Toast.makeText(getActivity(), "생성되지 않았습니다.", Toast.LENGTH_SHORT).show();
            Create.dismiss();
        }
    };


    public void CreateMarker(LatLng latLng) {
        marker1 = new Marker();

        marker1.setPosition(latLng);
        marker1.setMap(naverMap);

        Double latitude = latLng.latitude;
        Double longitude = latLng.longitude;

        // Create a new user with a first and last name
        Map<String, Object> marker = new HashMap<>();
        marker.put("isPublic", Save_Var.getInstance().isPublic());
        marker.put("trashCode", Save_Var.getInstance().getTrashCode());
        marker.put("latitude", latitude);
        marker.put("longitude", longitude);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        marker.put("userUID", currentUser.getUid());
        marker.put("userID", currentUser.getDisplayName());
        marker.put("content", Save_Var.getInstance().getContentText().getText().toString());
        marker.put("picture", filename);
        filename = null;
        if (Save_Var.getInstance().isPublic()) {
            marker1.setIcon(image2);
            marker1.setWidth(80);
            marker1.setHeight(102);
        } else {
            OverlayImage image2 = OverlayImage.fromResource(R.drawable.marker_individual);
            marker1.setIcon(image2);
            marker1.setWidth(80);
            marker1.setHeight(102);
        }

        db.collection("markerData")
                .add(marker)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        markers.add(marker1);
                        mk.add(marker1);
                        dr.add(documentReference);
                        contents.add(Save_Var.getInstance().getContentText().getText().toString());
                        userIDs.add(currentUser.getDisplayName());

                        for (int i = 0; i < dr.size(); i++) {
                            int finalI = i;
                            mk.get(i).setOnClickListener(new Overlay.OnClickListener() {
                                @Override
                                public boolean onClick(@NonNull Overlay overlay) {
                                    checkService = MainActivity.mainActivity.isServiceRunningCheck();
                                    if (!checkService) {
                                        MainActivity.mainActivity.startService();
                                        ChocoList.getInstance().chocoGood(getActivity(), "만보기 서비스를 시작합니다.");
                                    } else {
                                        MainActivity.mainActivity.stopForeground();
                                        ChocoList.getInstance().chocoBad(getActivity(), "만보기 시스템을 중지합니다.");
                                    }
//                                    sliding.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED); //to open
//                                    markerDelete.setVisibility(markerDelete.VISIBLE);
//                                    tvAddress.setText("");
//                                    mContent.setText(contents.get(finalI));
//                                    mWriter.setText(userIDs.get(finalI));
                                    markerDelete.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            db.collection("markerData")
                                                    .document(dr.get(finalI).getId())
                                                    .delete()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            sliding.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED); //to close
                                                            overlay.setMap(null);
                                                            Log.d(TAG, "DocumentSnapshot successfully delete!");
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w(TAG, "Error deleting document", e);
                                                        }
                                                    });
                                        }
                                    });
                                    return false;
                                }
                            });
                        }
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

    }


    //    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (LocationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
//            if (!LocationSource.isActivated()) {
//                naverMap.setLocationTrackingMode(LocationTrackingMode.None);
//            }
//            return;
//        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }
    void updateMarkers(NaverMap navermap, Marker markers) {
        markers.setMap(null);
    }

    void showMarker(NaverMap navermap, Marker markers) {
        if (markers.getMap() != null) return;
        markers.setMap(navermap);
    }

    void hideMarker(NaverMap navermap, Marker markers) {
        markers.setMap(null);
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
        //NaverMap 객체 받아서 NaverMap 객체에 위치 소스 지정
        naverMap.setLocationSource(LocationSource);

        /** 거리 측정 */
        naverMap.addOnLocationChangeListener(new NaverMap.OnLocationChangeListener() {
            @Override
            public void onLocationChange(@NonNull Location location) {
                nowLocation = location;
                if (manboMode) {
                    double distanceMeter =
                            distance(nowLocation.getLatitude(), nowLocation.getLongitude(), markerLocation.getLatitude(), markerLocation.getLongitude(), "meter");

                    List<LatLng> lts = new ArrayList<>();
                    lts.add(new LatLng(nowLocation.getLatitude(), nowLocation.getLongitude()));
                    lts.add(PedometerService.javaItem.getLatLng());

                    PedometerService.Path_overlay = path;
                    PedometerService.location = location;
                    PedometerService.location2 = markerLocation;
                    PedometerService.manbo = manboMode;

                    customText.setText((int) distanceMeter + "미터");
                }
            }
        });
        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setScaleBarEnabled(true); // 거리
        uiSettings.setZoomControlEnabled(true); // 줌
        uiSettings.setLogoGravity(Gravity.RIGHT | Gravity.TOP);

        sliding.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED); //to open

        LatLngBounds bounds = naverMap.getContentBounds();

        LatLng southWest = bounds.getSouthWest();
        LatLng northEast = bounds.getNorthEast();
        double lngSpan = northEast.toLatLng().longitude - southWest.toLatLng().longitude;
        double latSpan = northEast.toLatLng().latitude - southWest.toLatLng().latitude;


        ImageButton marker_place = (ImageButton) viewGroup.findViewById(R.id.marker_place);
        marker_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
            }
        });

        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
//        naverMap.moveCamera(
//                CameraUpdate.toCameraPosition(
//                        new CameraPosition(NaverMap.DEFAULT_CAMERA_POSITION.target, NaverMap.DEFAULT_CAMERA_POSITION.zoom))
//        );

//TODO [엑셀파일에서 경도, 위도, 주소 데이터를 픽업하여 파이어베이스에 등록]

//        readData();
//        for (int i = 0; i < trashCanDataList.size(); i++) {
//            final int finalI = i;
//            Double longitude = trashCanDataList.get(i).getLongitude();
//            Double latitude = trashCanDataList.get(i).getLatitude();
//            String address = trashCanDataList.get(i).getAddress();
//            // Create a new user with a first and last name
//            Map<String, Object> marker = new HashMap<>();
//            marker.put("isPublic", true);
//            marker.put("trashCode", 0);
//            marker.put("latitude", latitude);
//            marker.put("longitude", longitude);
//            currentUser = FirebaseAuth.getInstance().getCurrentUser();
//            marker.put("userUID", currentUser.getUid());
//            marker.put("userID", currentUser.getDisplayName());
//            marker.put("address", address);
//            marker.put("content", "");
//            marker.put("picture", null);
//            // Add a new document with a generated ID
//            db.collection("markerData")
//                    .add(marker)
//                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                        @Override
//                        public void onSuccess(DocumentReference documentReference) {
//                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Log.w(TAG, "Error adding document", e);
//                        }
//                    });
//        }


        builder = TedNaverClustering.with(getContext(), naverMap);
        builder.clusterBackground(clusterItem -> {
            return 0x9F1B9C12;
        });


        builder.customMarker(JavaItem -> {
            Marker marker = new Marker();
            marker.setIcon(image);
            marker.setWidth(80);
            marker.setHeight(102);
            // Marker 에 title 관련 property 및 함수가 없습니다. Bytecode 를 보니 CustomMarkerClusterActivity 의 함수로 보여지네요.
            // marker.setTitle("" + clusterItem.getPosition().latitude);
            JavaItem.setMarker(marker);
            if(JavaItem == PedometerService.javaItem){
                JavaItem.getMarker().setWidth(120);
                JavaItem.getMarker().setHeight(152);
                JavaItem.getMarker().setIcon(image3);
                JavaItem.setMarker(marker);
            }
            return marker;
        });

        builder.markerClickListener(JavaItem -> {
            
            markerDelete.setVisibility(markerDelete.GONE);
            manboStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkService = MainActivity.mainActivity.isServiceRunningCheck();
                    if (!checkService) {
                        MainActivity.mainActivity.startService();
                        ChocoList.getInstance().chocoGood(getActivity(), "만보기 서비스를 시작합니다.");

                        targetJav = JavaItem;
                        PedometerService.javaItem = JavaItem;

                        List<LatLng> lts = new ArrayList<>();
                        lts.add(new LatLng(nowLocation.getLatitude(), nowLocation.getLongitude()));
                        lts.add(PedometerService.javaItem.getLatLng());

                        markerLocation = new Location("");
                        markerLocation.setLatitude(PedometerService.javaItem.getLatLng().latitude);
                        markerLocation.setLongitude(PedometerService.javaItem.getLatLng().longitude);
                        manboMode = true;

                        dynamicCreate();

                        PedometerService.javaItem.getMarker().setWidth(120);
                        PedometerService.javaItem.getMarker().setHeight(152);
                        PedometerService.javaItem.getMarker().setIcon(image3);
//                naverMap.setCameraPosition(nCameraPosition);//카메라 위치 이동

                    } else {
                        MainActivity.mainActivity.stopForeground();
                        ChocoList.getInstance().chocoBad(getActivity(), "만보기 시스템을 중지합니다.");
                        PedometerService.javaItem.getMarker().setWidth(80);
                        PedometerService.javaItem.getMarker().setHeight(102);
                        PedometerService.javaItem.getMarker().setIcon(image);
//                container.removeView(PedometerService.saveTxt);
//                container.removeView(PedometerService.saveImg);
                        container.removeView(PedometerService.saveLayout);
                        customText = null;
                        customImageView = null;
                        constraintLayout = null;
                        manboMode = false;

                        Date currentTime = Calendar.getInstance().getTime();
                        SimpleDateFormat weekdayFormat = new SimpleDateFormat("EE", Locale.getDefault());
                        SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());
                        SimpleDateFormat monthFormat = new SimpleDateFormat("MM", Locale.getDefault());
                        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
                        SimpleDateFormat hourFormat = new SimpleDateFormat("hh", Locale.getDefault());
                        SimpleDateFormat minuteFormat = new SimpleDateFormat("mm", Locale.getDefault());

                        String weekDay = weekdayFormat.format(currentTime);
                        String year = yearFormat.format(currentTime);
                        String month = monthFormat.format(currentTime);
                        String day = dayFormat.format(currentTime);
                        String hour = hourFormat.format(currentTime);
                        String minute = minuteFormat.format(currentTime);
                        String sum = year + "." + month + "." + day + " " + hour + ":" + minute;
                        Save_Var.getInstance().setWalkStopTime(sum);

                        int memoCount = S_Preference.getInt(getContext(), "memoCount");

                        S_Preference.setInt(getContext(),"memoCount", memoCount+1);

                        ArrayList<String> saveString = new ArrayList<>();
                        saveString.add(sum);
                        saveString.add(Float.toString(PedometerService.mSteps/33)+" Kcal");
                        saveString.add(PedometerService.mSteps+ " 걸음");

                        S_Preference.setInt(getContext(),"walkCountSum", S_Preference.getInt(getContext(), "walkCountSum")+PedometerService.mSteps);
                        S_Preference.setStringArrayPref(getContext(), "memo"+memoCount, saveString);

                    }
                }
            });

            sliding.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED); //to open
            tvAddress.setText(JavaItem.getAddress());
            mContent.setText("공공 데이터입니다.");
            mWriter.setText("올리브앤와인");
            markerDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ChocoList.getInstance().chocoRed(getActivity(), "공공 데이터는 삭제할 수 없습니다.");
                }
            });
            return null;
        });

        builder.clusterClickListener(JavaItem -> {
            LatLng lng = new LatLng(JavaItem.getPosition().getLatitude(), JavaItem.getPosition().getLongitude());
            naverMap.moveCamera(
                    CameraUpdate.toCameraPosition(
                            new CameraPosition(lng, NaverMap.DEFAULT_CAMERA_POSITION.zoom)).animate(CameraAnimation.Fly)
            );
            return null;
        });

        builder.minClusterSize(7);
        builder.items(cache.values());
        builder.make();

        db.collection("markerData")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                marker2 = new Marker();
                                Double latitude = document.getDouble("latitude");
                                Double longitude = document.getDouble("longitude");
                                boolean isPublic = document.getBoolean("isPublic");

                                int trashCode = document.get("trashCode", Integer.TYPE);
                                String userUID = document.getString("userUID");
                                String userID = document.getString("userID");
                                String address = document.getString("address");
                                String content = document.getString("content");
                                marker2.setPosition(new LatLng(latitude, longitude));

                                marker2.setMap(naverMap);
                                currentUser = FirebaseAuth.getInstance().getCurrentUser();

                                if (isPublic) {
                                    marker2.setIcon(image2);
                                    marker2.setWidth(80);
                                    marker2.setHeight(102);
                                } else {
                                    OverlayImage image2 = OverlayImage.fromResource(R.drawable.marker_individual);
                                    marker2.setIcon(image2);
                                    marker2.setWidth(80);
                                    marker2.setHeight(102);
                                }

                                marker2.setOnClickListener(new Overlay.OnClickListener() {
                                    @Override
                                    public boolean onClick(@NonNull Overlay overlay) {
                                        checkService = MainActivity.mainActivity.isServiceRunningCheck();
                                        if (!checkService) {
                                        } else {
                                        }
                                        sliding.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED); //to open
                                        tvAddress.setText(address);
                                        mContent.setText(content);
                                        mWriter.setText(userID);
                                        if (currentUser != null) {
                                            if (currentUser.getUid().equals(userUID)) {
                                                markerDelete.setVisibility(markerDelete.VISIBLE);
                                            } else if (!currentUser.getUid().equals(userUID)) {
                                                markerDelete.setVisibility(markerDelete.GONE);
                                            }
                                            markerDelete.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    if (currentUser.getUid().equals(userUID)) {
                                                        db.collection("markerData")
                                                                .document(document.getId())
                                                                .delete()
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        sliding.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED); //to close
                                                                        overlay.setMap(null);
                                                                        Log.d(TAG, "DocumentSnapshot successfully delete!");
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Log.w(TAG, "Error deleting document", e);
                                                                    }
                                                                });
                                                    } else if (!currentUser.getUid().equals(userUID)) {
                                                        ChocoList.getInstance().chocoRed(getActivity(), "본인만 삭제 가능합니다.");
                                                    }
                                                }
                                            });
                                        }

                                        return false;
                                    }
                                });
                                markers.add(marker2);

                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        sliding.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {

            }

            @Override
            public void onPanelStateChanged(View view, SlidingUpPanelLayout.PanelState panelState, SlidingUpPanelLayout.PanelState panelState1) {

            }
        });

        ImageButton marker_plus = (ImageButton) viewGroup.findViewById(R.id.marker_plus);
        marker_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser == null)
                    ChocoList.getInstance().chocoRed(getActivity(), "로그인 후 이용 가능합니다.");
                else {
                    ChocoList.getInstance().chocoGreen(getActivity(), "원하는 위치를 꾹 누른 후 아이콘을 등록 해보세요.");
                    naverMap.setOnMapLongClickListener(new NaverMap.OnMapLongClickListener() {
                        @Override
                        public void onMapLongClick(@NonNull PointF pointF, @NonNull LatLng latLng) {

                            Create = new CreateDialog(getContext(), CreateConfirm, CreateCancel, "위치 등록");

                            platLng = latLng;
                            Create.show();

                            // Add a new document with a generated ID
                        }
                    });
                }
            }
        });


//                FirebaseStorage storage = FirebaseStorage.getInstance("gs://trashcan-14f2f.appspot.com/");
//                StorageReference storageRef = storage.getReference();
//                storageRef.child("images/test.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                    @SuppressLint("RestrictedApi")
//                    @Override
//                    public void onSuccess(Uri uri) {
//                        //이미지 로드 성공시
//
//                        Glide.with(getApplicationContext())
//                                .load(uri)
//                                .into(img_test);
//
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception exception) {
//                        //이미지 로드 실패시
//                        Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT).show();
//                    }
//                });
//


//        ImageButton marker_place = (ImageButton) viewGroup.findViewById(R.id.marker_place);
//        marker_place.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
//            }
//        });

    }

    private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        if (unit == "kilometer") {
            dist = dist * 1.609344;
        } else if (unit == "meter") {
            dist = dist * 1609.344;
        }

        return (dist);
    }

    void dynamicCreate(){
        LayoutInflater layoutInflater = getLayoutInflater();
        constraintLayout = (ConstraintLayout) layoutInflater.inflate(R.layout.customlayout, container, false);

        customImageView = constraintLayout.findViewById(R.id.customImageView);
        customImageView.setBackgroundColor(Color.rgb(0, 188, 0));
        //layout_width, layout_height, gravity 설정
        PedometerService.saveImg = customImageView;

        //TextView 생성
        customText = constraintLayout.findViewById(R.id.customTextView);
        double distanceMeter =
                distance(nowLocation.getLatitude(), nowLocation.getLongitude(), markerLocation.getLatitude(), markerLocation.getLongitude(), "meter");

        customText.setText((int) distanceMeter + "미터");
        customText.setTextSize(20);
        customText.setTextColor(Color.rgb(255, 255, 255));

        PedometerService.saveTxt = customText;
        PedometerService.saveLayout = constraintLayout;

        container.addView(constraintLayout);
    }


    // This function converts decimal degrees to radians
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    // This function converts radians to decimal degrees
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    private Location getMyLocation() {
        Location currentLocation = null;
        // Register the listener with the Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            getMyLocation(); //이건 써도되고 안써도 되지만, 전 권한 승인하면 즉시 위치값 받아오려고 썼습니다!
            String locationProvider = LocationManager.GPS_PROVIDER;
            currentLocation = lm.getLastKnownLocation(locationProvider);
            if (currentLocation != null) {
                loclng = currentLocation.getLongitude();
                loclat = currentLocation.getLatitude();
            }
        } else {
            // 수동으로 위치 구하기
            String locationProvider = LocationManager.GPS_PROVIDER;
            currentLocation = lm.getLastKnownLocation(locationProvider);
            if (currentLocation != null) {
                loclng = currentLocation.getLongitude();
                loclat = currentLocation.getLatitude();
            }
        }
        return currentLocation;
    }


    //TODO [엑셀 파일 읽기 ]
    public void readData() {

        InputStream is = getResources().openRawResource(R.raw.trashcan);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );

        String line = "";
        try {
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                Log.d("MyActivity", "Line: " + line);
                tokens = line.split(",");
                tcd = new trashCanData();
                double longitude = Double.parseDouble(tokens[0]);
                double latitude = Double.parseDouble(tokens[1]);
                tcd.setLongitude(longitude);
                tcd.setLatitude(latitude);
                tcd.setAddress(tokens[2]);
                trashCanDataList.add(tcd);
                Log.d("MyActivity", "Just created: " + tcd);

            }
        } catch (IOException e) {
            Log.d("MyActivity", "Error reading data file on line" + line, e);
            e.printStackTrace();
        }
    }


    //TODO [1] : 권한 설정 => 매니페스트, MainActivity에 해당 코드 있음

    //TODO [2] : 카메라 호출 실시
    Uri image_uri;

    public void goCamera() {
        Log.d("---", "---");
        Log.d("//===========//", "================================================");
        Log.d("", "\n" + "[A_Camera > goCamera() 메소드 : 카메라 호출 실시]");
        Log.d("//===========//", "================================================");
        Log.d("---", "---");
        try {
            ContentValues values = new ContentValues(); //TODO [갤러리 Picture 폴더에 사진이 저장된다]
            values.put(MediaStore.Images.Media.TITLE, getNowTime24()); //TODO [사진 이름]
            values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera"); //TODO [설명]

            image_uri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);

            startActivity(cameraIntent); //TODO [일반 인텐트 실시]
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //TODO [3] : 카메라로 촬영한 사진 저장 판단 실시
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        //TODO [카메라 촬영 완료 후 사진 이미지 표시]
        try {
            if (image_uri != null) {
                try {
                    //TODO [저장된 이미지가 있는지 확인 실시]
                    InputStream is = getContext().getContentResolver().openInputStream(image_uri);
                    int size = is.read();
                    is.close();
                    Log.d("---", "---");
                    Log.w("//===========//", "================================================");
                    Log.d("", "\n" + "[A_Camera > onResume() 메소드 : 액티비티 실행 준비]");
                    Log.d("", "\n" + "[저장된 이미지 확인 : 이미지 저장됨]");
                    Log.d("", "\n" + "[콘텐츠 경로 : " + String.valueOf(image_uri) + "]");
                    Log.d("", "\n" + "[길이 : " + String.valueOf(size) + "]");
                    Log.w("//===========//", "================================================");
                    Log.d("---", "---");

//                    //TODO [이미지 뷰에 사진 표시 실시] => 나중에 다이얼로그에 들어갈 이미지 뷰
//                    imageView.setImageURI(image_uri);
//나중에
//                    OverlayImage overlayImage = OverlayImage.fromPath(uri2path(getContext(), image_uri));
//                    marker1.setIcon(overlayImage);
                    final String cu = mAuth.getUid();

                    //TODO [파이어베이스에 촬영한 사진 업로드]
                    filename = cu + "_" + System.currentTimeMillis();

                    StorageReference storageRef = storage.getReferenceFromUrl("gs://trashcan-14f2f.appspot.com").child("MarkerImage/" + filename);

                    UploadTask uploadTask;
                    uploadTask = storageRef.putFile(image_uri);

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            Log.v("알림", "사진 업로드 실패");
                            exception.printStackTrace();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override

                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.

                            Log.v("알림", "사진 업로드 성공 ");
                        }
                    });
                    //TODO [MediaStore 에 새로 JPEG 파일로 저장 실시]
                    saveFile(getNowTime24(), imageView);
                } catch (Exception e) {
                    Log.d("---", "---");
                    Log.e("//===========//", "================================================");
                    Log.d("", "\n" + "[A_Camera > onResume() 메소드 : 액티비티 실행 준비]");
                    Log.d("", "\n" + "[저장된 이미지 확인 : 이미지 저장되지 않음]");
                    Log.e("//===========//", "================================================");
                    Log.d("---", "---");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //TODO [4] : MediaStore 에 새로 JPEG 파일로 저장 실시
    private void saveFile(String fileName, ImageView view) {
        String deleteCheck = S_Preference.getString(getContext(), "saveCameraScopeContent");
        if (deleteCheck != null && deleteCheck.length() > 0) { //TODO 이전에 저장된 파일이 있을 경우 지운다
            try {
                ContentResolver contentResolver = getContext().getContentResolver();
                contentResolver.delete(
                        Uri.parse(S_Preference.getString(getContext(), "saveCameraScopeContent")),
                        null,
                        null);
                Log.d("---", "---");
                Log.e("//===========//", "================================================");
                Log.d("", "\n" + "[A_Camera > saveFile() 메소드 : 이전에 저장된 파일 삭제 실시]");
                Log.d("", "\n" + "[콘텐츠 파일 경로 : " + String.valueOf(deleteCheck) + "]");
                Log.d("", "\n" + "[절대 파일 경로 : " + S_Preference.getString(getContext(), "saveCameraScopeAbsolute") + "]");
                Log.e("//===========//", "================================================");
                Log.d("---", "---");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Log.d("---", "---");
        Log.d("//===========//", "================================================");
        Log.d("", "\n" + "[A_Camera > saveFile() 메소드 : MediaStore 파일 저장 실시]");
        Log.d("", "\n" + "[파일 이름 : " + String.valueOf(fileName) + "]");
        Log.d("//===========//", "================================================");
        Log.d("---", "---");

        //TODO [저장하려는 파일 타입, 이름 지정]
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName + ".JPG");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/*");
        //values.put(MediaStore.Images.Media.MIME_TYPE, getMIMEType(fileRoot));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.IS_PENDING, 1);
        }
        ContentResolver contentResolver = getContext().getContentResolver();
        Uri item = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        try {
            //TODO [쓰기 모드 지정]
            ParcelFileDescriptor pdf = contentResolver.openFileDescriptor(item, "w", null);

            if (pdf == null) {
                Log.d("---", "---");
                Log.e("//===========//", "================================================");
                Log.d("", "\n" + "[A_Camera > saveFile() 메소드 : MediaStore 파일 저장 실패]");
                Log.d("", "\n" + "[원인 : " + String.valueOf("ParcelFileDescriptor 객체 null") + "]");
                Log.e("//===========//", "================================================");
                Log.d("---", "---");
            } else {
                //TODO [이미지 뷰에 표시된 사진을 얻어온다]
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 4;
                //Bitmap bitmap= BitmapFactory.decodeFile(filePath, options);
                Bitmap bitmap = ((BitmapDrawable) view.getDrawable()).getBitmap();

                //TODO [이미지 리사이징 실시]
                int width = 354; // [축소시킬 너비]
                int height = 472; //[축소시킬 높이]
                float bmpWidth = bitmap.getWidth();
                float bmpHeight = bitmap.getHeight();

                if (bmpWidth > width) {
                    // [원하는 너비보다 클 경우의 설정]
                    float mWidth = bmpWidth / 100;
                    float scale = width / mWidth;
                    bmpWidth *= (scale / 100);
                    bmpHeight *= (scale / 100);
                } else if (bmpHeight > height) {
                    // [원하는 높이보다 클 경우의 설정]
                    float mHeight = bmpHeight / 100;
                    float scale = height / mHeight;
                    bmpWidth *= (scale / 100);
                    bmpHeight *= (scale / 100);
                }

                //TODO [리사이징 된 파일을 비트맵에 담는다]
                //Bitmap resizedBmp = Bitmap.createScaledBitmap(bitmap, (int) bmpWidth, (int) bmpHeight, true); //TODO [해상도 맞게 표시]
                Bitmap resizedBmp = Bitmap.createScaledBitmap(bitmap, (int) width, (int) height, true); //TODO [지정한 사이즈 기준]
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                resizedBmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageInByte = baos.toByteArray();

                //TODO [이미지를 저장하는 부분]
                FileOutputStream outputStream = new FileOutputStream(pdf.getFileDescriptor());
                outputStream.write(imageInByte);
                outputStream.close();

                //TODO [경로 저장 실시]
                //[콘텐츠 : 이미지 경로 저장]
                S_Preference.setString(getContext(), "saveCameraScopeContent", String.valueOf(item));

                //TODO [절대 : 이미지 경로 저장]
                Cursor c = getContext().getContentResolver().query(Uri.parse(String.valueOf(item)), null, null, null, null);
                c.moveToNext();
                String absolutePath = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));
                S_Preference.setString(getContext(), "saveCameraScopeAbsolute", absolutePath);

                Log.d("---", "---");
                Log.w("//===========//", "================================================");
                Log.d("", "\n" + "[A_Camera > saveFile() 메소드 : MediaStore 파일 저장 성공]");
                Log.d("", "\n" + "[콘텐츠 파일 경로 : " + S_Preference.getString(getContext(), "saveCameraScopeContent") + "]");
                Log.d("", "\n" + "[절대 파일 경로 : " + S_Preference.getString(getContext(), "saveCameraScopeAbsolute") + "]");
                Log.w("//===========//", "================================================");
                Log.d("---", "---");

                //TODO [다시 사진 표시 실시]
                readFile(imageView, S_Preference.getString(getContext(), "saveCameraScopeContent"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //TODO [5] : MediaStore 저장된 파일 읽기 실시
    private void readFile(ImageView view, String path) {
        Log.d("---", "---");
        Log.d("//===========//", "================================================");
        Log.d("", "\n" + "[A_Camera > readFile() 메소드 : MediaStore 파일 불러오기 실시]");
        Log.d("", "\n" + "[콘텐츠 파일 경로 : " + String.valueOf(path) + "]");
        Log.d("//===========//", "================================================");
        Log.d("---", "---");
        Uri externalUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.MIME_TYPE
        };

        Cursor cursor = getContext().getContentResolver().query(externalUri, projection, null, null, null);

        if (cursor == null || !cursor.moveToFirst()) {
            Log.d("---", "---");
            Log.e("//===========//", "================================================");
            Log.d("", "\n" + "[A_Camera > readFile() 메소드 : MediaStore 파일 불러오기 실패]");
            Log.d("", "\n" + "[원인 : " + String.valueOf("Cursor 객체 null") + "]");
            Log.e("//===========//", "================================================");
            Log.d("---", "---");
            return;
        }

        //TODO [특정 파일 불러오기 실시]
        String contentUrl = path;
        try {
            InputStream is = getContext().getContentResolver().openInputStream(Uri.parse(contentUrl));
            if (is != null) {
                // [선택한 이미지에서 비트맵 생성]
                Bitmap img = BitmapFactory.decodeStream(is);
                is.close();

                // [이미지 뷰에 이미지 표시]
                view.setImageBitmap(img);
                Log.d("---", "---");
                Log.w("//===========//", "================================================");
                Log.d("", "\n" + "[A_Camera > readFile() 메소드 : MediaStore 파일 불러오기 성공]");
                Log.d("", "\n" + "[콘텐츠 파일 경로 : " + String.valueOf(contentUrl) + "]");
                Log.w("//===========//", "================================================");
                Log.d("---", "---");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //TODO [로직 [6] : 일반 파일 이름 지정 메소드 정의]
    public static String getNowTime24() {
        long time = System.currentTimeMillis();
        //SimpleDateFormat dayTime = new SimpleDateFormat("hh:mm:ss");
        SimpleDateFormat dayTime = new SimpleDateFormat("yyyyMMddkkmmss");
        String str = dayTime.format(new Date(time));
        return "CM" + str; //TODO [CM는 카메라 의미]
    }

    //TODO [ URI => PATH 변환 함수 ]
    public static String uri2path(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};

        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        cursor.moveToNext();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
        Uri uri = Uri.fromFile(new File(path));

        cursor.close();
        return path;
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
        if(manboMode){
            dynamicCreate();
        }
    }

    /* [ 홈 버튼을 누르고 다시 앱으로 돌아오면 동적 생성한 뷰가 서비스를 종료해도 그대로 남아있는 버그를 발견했다. ]
    * 홈 버튼을 눌렀을 때 생명주기는 onPause()로 시작되고 앱으로 돌아왔을 때는 onStart()가 호출된다.
    * onStart()엔 뷰를 동적 생성하는 dynamicCreate() 메소드가 들어가 있기 때문에 이전의 뷰를 제거하지않고
    * onStart()가 호출되면 먼저 생성되어있던 동적 생성 뷰 위로 뷰가 새로 생성된다.
    * 그러므로 onPause()에 화면에 addView로 동적 생성한 뷰들을 제거하는 메소드를 넣어야한다.( 홈버튼 누를 시 즉각 처리 )
    * 그렇지 않으면 제거가 안된 상태에서 객체가 추가로 덮어 씌워지므로 먼저 동적 생성된
    * 뷰는 계속 그자리에 남아 있게 된다. [해결완료] */
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
//        container.removeView(PedometerService.saveTxt);
//        container.removeView(PedometerService.saveImg);
        container.removeView(PedometerService.saveLayout);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDestroy();
        javaItems.clear();
        trashCanDataList.clear();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}