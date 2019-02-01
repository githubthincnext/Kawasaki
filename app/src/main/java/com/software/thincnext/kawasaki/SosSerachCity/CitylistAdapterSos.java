package com.software.thincnext.kawasaki.SosSerachCity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.software.thincnext.kawasaki.R;
import com.software.thincnext.kawasaki.SearchActivity.CityItem;
import com.software.thincnext.kawasaki.SearchActivity.CitylistAdapter;

import java.util.ArrayList;

public class CitylistAdapterSos extends RecyclerView.Adapter<CitylistAdapterSos.ViewHolder> implements Filterable {

    //Declaring variables
    private ArrayList<CityItem> rList;
    private ArrayList<CityItem> rListFiltered;
    private Context context;


    public CitylistAdapterSos(Context context, ArrayList<CityItem> rList) {

        this.context = context;
        this.rList = rList;
        this.rListFiltered = rList;


    }

    @Override
    public CitylistAdapterSos.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_city_list, parent, false);
        return new CitylistAdapterSos.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(CitylistAdapterSos.ViewHolder holder, int position) {

        //Setting values
        holder.rName.setText(rListFiltered.get(position).getrName());


    }

    @Override
    public int getItemCount() {

        return rListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override

            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    rListFiltered = rList;
                } else {
                    ArrayList<CityItem> filteredList = new ArrayList<>();

                    for (int index = 0; index < rList.size(); index++) {
                        if (rList.get(index).getrName().toLowerCase().contains(charString) ||rList.get(index).getrName().toUpperCase().contains(charString)) {
                            filteredList.add(rList.get(index));
                        }
                        if (rList.get(index).getrName().toLowerCase().contains(charString) &&rList.get(index).getrName().toUpperCase().contains(charString)) {
                            filteredList.add(rList.get(index));
                        }

                    }


                    rListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = rListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                rListFiltered = (ArrayList<CityItem>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        //Declaring views
        private TextView rName, rRating;
        private ImageView rImage;


        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);


            //Initialising views
            rName = view.findViewById(R.id.tv_cityList_name);


        }


        @Override
        public void onClick(View view) {


            Intent intent = new Intent("custom-message_one");
            intent.putExtra("item",rName.getText().toString());
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            ((Activity)context).finish();





        }

    }
}



