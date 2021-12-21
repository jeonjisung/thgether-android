package kr.co.oliveandwine.trashcan;

import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import kr.co.oliveandwine.trashcan.Adapter.InfoArrayAdapter;
import kr.co.oliveandwine.trashcan.Adapter.Item.InfoItem;

public class mypageFragment extends Fragment {

    ViewGroup viewGroup;
    Button login;
    ImageView iv_profile;
    TextView iv_name, iv_email;

    private List<InfoItem> mItemList;

    private BaseAdapter mInfoAdatper;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewGroup =  (ViewGroup)inflater.inflate(R.layout.fragment_mypage, container, false);

        login = viewGroup.findViewById(R.id.login_button);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user == null) {
                    Intent intent = new Intent(getActivity(), Login.class);
                    startActivity(intent);
                } else {
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(getContext(), "로그아웃 성공", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), getActivity().getClass());
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });

        mItemList = new ArrayList<>();
        mItemList.add(new InfoItem(R.drawable.info_tutorial, "튜토리얼 다시보기"));
        mItemList.add(new InfoItem(R.drawable.info_timeline, "쓰게더 공유"));
        mItemList.add(new InfoItem(R.drawable.info_head, "자주 묻는 질문"));
        mItemList.add(new InfoItem(R.drawable.info_mic, "공지사항"));

        mInfoAdatper = new InfoArrayAdapter(getContext(), mItemList);

        ListView listView = (ListView) viewGroup.findViewById(R.id.info_listview);
        listView.setAdapter(mInfoAdatper);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(position) {
                    case 0:
                        PrefManager prefManager = new PrefManager(getActivity());
                        prefManager.setFirstTimeLaunch(true);
                        Intent info_intent1 = new Intent(getActivity(), WelcomeActivity.class);
                        startActivity(info_intent1);
                        break;
                    case 1:
                        AlertDialog.Builder builder=  new AlertDialog.Builder(getActivity());
                        builder.setMessage("업데이트 예정입니다.");
                        builder.setNeutralButton("취소",null);
                        builder.create().show();
                        //sharekakao();
                        break;
                    case 2:
                        Intent info_intent2 = new Intent(getActivity(), info_question.class);
                        startActivity(info_intent2);
                        break;
                    case 3:
                        Intent info_intent3 = new Intent(getActivity(), info_notice.class);
                        startActivity(info_intent3);
                        break;
                }
            }
        });

        //ListView listview = (ListView)viewGroup.findViewById(R.id.profilelist);

//        ArrayList<profilelistview> data = new ArrayList<>();
//        profilelistview l1 = new profilelistview(R.drawable.marker_plus, "My Marker");
//        profilelistview l2 = new profilelistview(R.drawable.event, "Event");
//        profilelistview l3 = new profilelistview(R.drawable.tip, "Tip");
//
//        data.add(l1);
//        data.add(l2);
//        data.add(l3);
//
//        profilelistviewAdapter adapter = new profilelistviewAdapter(getActivity(), R.layout.profilelist, data);
//        listview.setAdapter(adapter);
//
//        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                switch (position){
//                    case 0:
//                        Intent mymarker_intent = new Intent(getActivity(), MyMarkerPage.class);
//                        startActivity(mymarker_intent);
//                        break;
//                    default:
//                        Toast.makeText(getContext(), "서비스 준비 중입니다.", Toast.LENGTH_SHORT).show();
//                        break;
//                }
//            }
//        });

        if (user != null) {

            login.setText("로그아웃");

            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = user.getUid();

            iv_name = viewGroup.findViewById(R.id.iv_name);
            iv_name.setText(name);
            iv_email = viewGroup.findViewById(R.id.iv_email);
            iv_email.setText(email);
            iv_profile = viewGroup.findViewById(R.id.iv_profile);
            Glide.with(this).load(photoUrl).into(iv_profile);
            iv_profile.setBackground(new ShapeDrawable(new OvalShape()));
            iv_profile.setClipToOutline(true);

        }

        return viewGroup;
    }

}