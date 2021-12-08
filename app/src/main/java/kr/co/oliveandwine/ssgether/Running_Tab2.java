package kr.co.oliveandwine.ssgether;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import kr.co.oliveandwine.ssgether.Adapter.CustomListViewAdapter;
import kr.co.oliveandwine.ssgether.Save.Save_Var;
import kr.co.oliveandwine.ssgether.util.PedometerService;

public class Running_Tab2 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ViewGroup viewGroup;
    ListView listView;
    TextView sumWalk;

    public Running_Tab2() {
        // Required empty public constructor
    }

    public static Running_Tab2 newInstance(String param1, String param2) {
        Running_Tab2 fragment = new Running_Tab2();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_running_tab2, container, false);
        listView = viewGroup.findViewById(R.id.running_tab2_list);


        CustomListViewAdapter listViewAdapter = new CustomListViewAdapter();
        for(int i = 0; i < S_Preference.getInt(getContext(), "memoCount"); i++){
            ArrayList<String> arrayList = S_Preference.getStringArrayPref(getContext(), "memo"+i);
            listViewAdapter.addItem(""+arrayList.get(0), ""+arrayList.get(1), ""+arrayList.get(2));
        }

        listView.setAdapter(listViewAdapter);
        sumWalk = viewGroup.findViewById(R.id.sum_walk);
        sumWalk.setText(S_Preference.getInt(getContext(),"walkCountSum")+" 걸음");
        // Inflate the layout for this fragment
        return viewGroup;
    }
}