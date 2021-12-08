package kr.co.oliveandwine.ssgether;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import kr.co.oliveandwine.ssgether.Adapter.ExpandableListAdapter;

public class info_notice extends AppCompatActivity {

    private RecyclerView recyclerview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_notice);

        recyclerview = findViewById(R.id.notice_recycler);
        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        List<ExpandableListAdapter.Item> data = new ArrayList<>();
//        data.add(new ExpandableListAdapter.Item(ExpandableListAdapter.HEADER, "질문 1"));
//        data.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, "질문 1-1"));
//
//        data.add(new ExpandableListAdapter.Item(ExpandableListAdapter.HEADER, "질문 2"));
//        data.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, "질문 2-1"));

        ExpandableListAdapter.Item notice1 = new ExpandableListAdapter.Item(ExpandableListAdapter.HEADER, "당신 근처에 있는 쓰레기통을 알려주세요.");
        notice1.invisibleChildren = new ArrayList<>();
        notice1.invisibleChildren.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, "여러분이 길을 걷다 발견한 공공쓰레기통이 있다면 '쓰게더'에 등록해 주세요. 여러분의 작은 노력이 우리 동네를 더 깨끗하게 만듭니다."));
        data.add(notice1);
        ExpandableListAdapter.Item notice2 = new ExpandableListAdapter.Item(ExpandableListAdapter.HEADER, "'쓰게더' v1.0 정식 출시");
        notice2.invisibleChildren = new ArrayList<>();
        notice2.invisibleChildren.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, "'쓰게더' v1.0이 정식 출시 되었습니다."));
        data.add(notice2);
        recyclerview.setAdapter(new ExpandableListAdapter(data));
    }
}