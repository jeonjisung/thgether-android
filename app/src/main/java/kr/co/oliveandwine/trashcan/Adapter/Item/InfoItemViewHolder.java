package kr.co.oliveandwine.trashcan.Adapter.Item;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import kr.co.oliveandwine.trashcan.R;

public class InfoItemViewHolder {
    public ImageView ivIcon;

    public TextView ivInfo;

    public InfoItemViewHolder(View view) {
        ivIcon = view.findViewById(R.id.iv_icon);
        ivInfo = view.findViewById(R.id.iv_info);
    }
}
