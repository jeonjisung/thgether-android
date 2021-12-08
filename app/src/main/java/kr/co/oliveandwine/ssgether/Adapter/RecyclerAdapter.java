package kr.co.oliveandwine.ssgether.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kr.co.oliveandwine.ssgether.Adapter.Item.RecyclerItem;
import kr.co.oliveandwine.ssgether.R;


/*
 * RecyclerAdapter 클래스 ( 메인 RecyclerView 이벤트 담당 )
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    View view;
    ViewGroup parent;
    Context context;

    boolean firstTime1 = true, firstTime2 = true;

    RecyclerViewHolder1 holder1;
    public int count = 0;

    ArrayList<RecyclerItem> list;
    ArrayList<Integer> receiveCheck = new ArrayList<>();
    ArrayList<String> selection = new ArrayList<>();
    ArrayList singleSectionItems;
    String available = "U3RyaW5nIENvcHlSaWdodCA9ICJKQ1kiOw==";

    int position;

    public RecyclerAdapter(ArrayList<RecyclerItem> list, Context context) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.parent = parent;
//        switch (viewType) {
//            case 0:
//                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comments_item, parent, false);
//                return new RecyclerViewHolder1(view);
//        }
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_listview, parent, false);
        return new RecyclerViewHolder1(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final RecyclerItem item = list.get(position);

        holder1 = (RecyclerViewHolder1) holder;
        holder1.imageView.setImageResource(item.getProfileImage());

        String name = item.getName();
        String content = item.getContent();
        holder1.textView1.setText(name);
        holder1.textView2.setText(content);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).hashCode();
    }

    @Override
    public int getItemViewType(int position) {
        this.position = position;
        return list.get(position).type;
    }


    public void addItem(RecyclerItem item) {
        list.add(item);
    }

    public class RecyclerViewHolder1 extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView1, textView2, time;
        CardView cardView;

        public RecyclerViewHolder1(@NonNull View itemView) {
            super(itemView);

//            imageView = itemView.findViewById(R.id.comments_item_profile);
//            textView1 = itemView.findViewById(R.id.comments_item_name);
//            textView2 = itemView.findViewById(R.id.comments_item_content);
//            cardView = itemView.findViewById(R.id.comments_cardView);
//            time = itemView.findViewById(R.id.comments_item_time);

        }
    }
}