package kr.co.oliveandwine.trashcan;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import kr.co.oliveandwine.trashcan.Adapter.CustomListViewAdapter;
import kr.co.oliveandwine.trashcan.Adapter.Item.CustomListItem;
import kr.co.oliveandwine.trashcan.Adapter.Item.WalkLogItem;

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

    ArrayList<WalkLogItem> walkLogItems;
    ArrayList<CustomListItem> itemArrayList = new ArrayList<>();

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
        walkLogItems = S_Preference.getWalkLogArrayPref(getContext(), "walklog");
        for (int i = 0; i < walkLogItems.size(); i++) {
            listViewAdapter.addItem(
                    walkLogItems.get(i).getTime(),
                    walkLogItems.get(i).getKcal(),
                    walkLogItems.get(i).getWalkcount()+" 걸음");
        }

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("해당 기록을 삭제하시겠습니까?");
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        S_Preference.setInt(getContext(), "walkCountSum",
                                S_Preference.getInt(getContext(), "walkCountSum")-walkLogItems.get(position).getWalkcount());
                        walkLogItems.remove(position);
                        S_Preference.setWalkLogArrayPref(getContext(), "walklog", walkLogItems);
                        listViewAdapter.remove(position);
                        listViewAdapter.notifyDataSetChanged();
                        sumWalk.setText(S_Preference.getInt(getContext(), "walkCountSum") + " 걸음");
                        Toast.makeText(getContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();

                    }
                });
                builder.setNegativeButton("취소", null);
                builder.create().show();
                return false;
            }
        });

        listView.setAdapter(listViewAdapter);
        sumWalk = viewGroup.findViewById(R.id.sum_walk);
        sumWalk.setText(S_Preference.getInt(getContext(), "walkCountSum") + " 걸음");
        // Inflate the layout for this fragment
        return viewGroup;
    }
}