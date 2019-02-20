package com.software.thincnext.kawasaki.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.software.thincnext.kawasaki.R;

import java.util.List;

public class MyAdapter  extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private List<ItemList> itemList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, subtitle;
        public ImageView icon;
        private LinearLayout main;

        public MyViewHolder(final View parent) {
            super(parent);


            icon = (ImageView) parent.findViewById(R.id.single_item_subs_img);


        }
    }

    public MyAdapter(List<ItemList> itemList) {
        this.itemList = itemList;
    }

    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ItemList row = itemList.get(position);

        holder.icon.setImageResource(row.getImageId());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
