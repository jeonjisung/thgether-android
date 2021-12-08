package kr.co.oliveandwine.ssgether.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import kr.co.oliveandwine.ssgether.Adapter.Item.InfoItem;
import kr.co.oliveandwine.ssgether.Adapter.Item.InfoItemViewHolder;
import kr.co.oliveandwine.ssgether.R;

public class InfoArrayAdapter extends ArrayAdapter<InfoItem> {
    private static final int LAYOUT_RESOURCE_ID = R.layout.content_list_item;

    private Context mContext;
    private List<InfoItem> mItemList;

    public InfoArrayAdapter(Context context, List<InfoItem> itemList){
        super(context, LAYOUT_RESOURCE_ID, itemList);

        mContext = context;
        mItemList = itemList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        InfoItemViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.content_list_item, parent, false);

            viewHolder = new InfoItemViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (InfoItemViewHolder) convertView.getTag();
        }

        InfoItem infoItem = mItemList.get(position);

        // Icon 설정
        viewHolder.ivIcon.setImageResource(infoItem.getImageResId());

        // Country 설정
        viewHolder.ivInfo.setText(infoItem.getInfo());

        return convertView;
    }
}
