package kr.co.oliveandwine.ssgether;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import kr.co.oliveandwine.ssgether.Adapter.ExpandableListAdapter;

public class info_question extends AppCompatActivity {

    private RecyclerView recyclerview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_question);

        recyclerview = findViewById(R.id.question_recycler);
        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        List<ExpandableListAdapter.Item> data = new ArrayList<>();


        ExpandableListAdapter.Item question1 = new ExpandableListAdapter.Item(ExpandableListAdapter.HEADER, "Q. 마커 등록이 안돼요.");
        question1.invisibleChildren = new ArrayList<>();
        question1.invisibleChildren.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, "A. 지도 좌측 상단에 있는 더하기 버튼을 누른 후 등록하고 싶은 위치에 대고 꾹 누르면 등록 할 수 있습니다.(구글 아이디 로그인 필요)"));
        data.add(question1);
        ExpandableListAdapter.Item question2 = new ExpandableListAdapter.Item(ExpandableListAdapter.HEADER, "Q. 만보기는 왜 있나요 ?");
        question2.invisibleChildren = new ArrayList<>();
        question2.invisibleChildren.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, "A. '쓰게더'는 주변 길거리 환경을 개선하고 건강도 증진 시키자는 목표로 개발된 서비스 입니다. "));
        data.add(question2);
        ExpandableListAdapter.Item question3 = new ExpandableListAdapter.Item(ExpandableListAdapter.HEADER, "Q. 안내 장소에 왔는데 쓰레기통이 없어요.");
        question3.invisibleChildren = new ArrayList<>();
        question3.invisibleChildren.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, "A. 마커 위치가 정확하지 않은 이유\n" +
                " 1. 각 지방자치단체에서 제공 받은 공공 데이터가 부정확한 경우 위치가 맞지 않을 수 있습니다.\n" +
                " 2. 데이터 변환 과정에서 위치 정보에 오차가 발생하는 경우가 있습니다. 쓰게더는 향후 업데이트를 통해 오차를 계속 수정 해 나갈 계획입니다."));
        data.add(question3);
        ExpandableListAdapter.Item question4 = new ExpandableListAdapter.Item(ExpandableListAdapter.HEADER, "Q. 향후 업데이트 계획이 있나요 ?");
        question4.invisibleChildren = new ArrayList<>();
        question4.invisibleChildren.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, "A. '쓰게더'의 향후 계획\n" +
                " 1. 쓰레기통 위치정보 추가 안내\n" +
                " 2. 만보기 기능 강화\n" +
                " 3. 환경 관련 캠페인 추진(ESG 경영 추진 기업과 연계)\n" +
                " 4. IoT 기반 스마트 쓰레기통 제작및 연계 서비스 제공"));
        data.add(question4);
        recyclerview.setAdapter(new ExpandableListAdapter(data));
    }
}