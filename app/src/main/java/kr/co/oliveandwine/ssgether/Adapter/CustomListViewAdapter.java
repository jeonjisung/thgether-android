package kr.co.oliveandwine.ssgether.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import kr.co.oliveandwine.ssgether.Adapter.Item.CustomListItem;
import kr.co.oliveandwine.ssgether.R;
import kr.co.oliveandwine.ssgether.Save.Save_Var;


public class CustomListViewAdapter extends BaseAdapter {
    Context mContext;

    /* 아이템을 세트로 담기 위한 어레이 */
    private ArrayList<CustomListItem> mItems = new ArrayList<>();

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public CustomListItem getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        mContext = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_listview, parent, false);
        }

        TextView tv_name = (TextView) convertView.findViewById(R.id.custom_list_title);
        TextView tv_kcal = (TextView) convertView.findViewById(R.id.custom_list_kcal);
        TextView tv_contents = (TextView) convertView.findViewById(R.id.custom_list_content);

        CustomListItem myItem = getItem(position);

        tv_name.setText(myItem.getName());
        tv_kcal.setText(myItem.getKcal());
        tv_contents.setText(myItem.getContents());

        convertView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_UP:
                        switch (position) {
                            case 0:
                                if (tv_name.getText().equals("1")) {
                                }
                                break;
                            case 1:
                                if (tv_name.getText().equals("2")) {
                                }
                                break;
                            case 2:
                                if (tv_name.getText().equals("3")) {
                                }
                                break;
                        }
                }

                return false;
            }
        });
        return convertView;
    }

    /* 아이템 데이터 추가를 위한 함수. 자신이 원하는대로 작성 */
    public void addItem(String name, String kcal, String content) {

        CustomListItem mItem = new CustomListItem();

        /* MyItem에 아이템을 setting한다. */
        mItem.setName(name);
        mItem.setKcal(kcal);
        mItem.setContents(content);
        /* mItems에 MyItem을 추가한다. */
        mItems.add(mItem);

    }
}
