package com.software.thincnext.kawasaki.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.software.thincnext.kawasaki.Activity.BookService;
import com.software.thincnext.kawasaki.Activity.HomeActivity;
import com.software.thincnext.kawasaki.Inbox.InboxActivity;
import com.software.thincnext.kawasaki.Profile.ProfileActivity;
import com.software.thincnext.kawasaki.R;
import com.software.thincnext.kawasaki.ServiceHistory;

import java.util.List;

public class MyHomeListAdapter extends RecyclerView.Adapter<MyHomeListAdapter.MyViewHolder> {
    private List<HomeList> itemList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, subtitle;
        public ImageView icon;
        private LinearLayout linearLayout;

        public MyViewHolder(final View parent) {
            super(parent);


            icon = (ImageView) parent.findViewById(R.id.imageView);
            title=(TextView)parent.findViewById(R.id.type_text);
            linearLayout=(LinearLayout)parent.findViewById(R.id.linear_layout);


        }
    }

    public MyHomeListAdapter(List<HomeList> itemList,Context context) {
        this.itemList = itemList;
        this.context=context;
    }

    @Override
    public MyHomeListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_list_adapter, parent, false);
        return new MyHomeListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyHomeListAdapter.MyViewHolder holder, final int position) {
        final HomeList row = itemList.get(position);

        holder.icon.setImageResource(row.getImageId());
        holder.title.setText(row.getTitle());

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             String itemName= (String) holder.title.getText();

                if (itemName.equalsIgnoreCase("Service")){
                  //  Intent intent=new Intent(context, BookService.class);
                    //context.startActivity(intent);


                    ((HomeActivity) context).viewDialog();
                }

                if (itemName.equalsIgnoreCase("Stores")){
                  //  Intent intent=new Intent(context, ServiceHistory.class);
                    //context.startActivity(intent);
                    Toast.makeText(context,"Coming Soon",Toast.LENGTH_LONG).show();
                    return;
                }
                if (itemName.equalsIgnoreCase("Events")){
                    Intent intent=new Intent(context, InboxActivity.class);
                    context.startActivity(intent);
                }

                if (itemName.equalsIgnoreCase("Profile")){
                    Intent intent=new Intent(context, ProfileActivity.class);
                    context.startActivity(intent);
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
