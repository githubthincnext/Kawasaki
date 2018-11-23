package com.software.thincnext.kawasaki.ServiceDetails;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.software.thincnext.kawasaki.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ServiceHelathAdapter  extends BaseAdapter {
    private SharedPreferences mPref;
    private JsonArray items = new JsonArray();

    private Context context;

    public ServiceHelathAdapter(Context context) {

        this.context = context;
    }

    public void listUpdate(JsonArray item) {
        this.items = item;
    }


    public void updatepref(SharedPreferences mpref) {
        this.mPref = mpref;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public JsonObject getItem(int position) {
        return items.get(position).getAsJsonObject();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.service_accuracy_adapter, parent, false);
        }
        ViewHolder holder = new ViewHolder(convertView);
        JsonObject jsonElement = items.get(position).getAsJsonObject();

        final String serviceType=jsonElement.get("ServiceType").getAsString();


        String kmReading=jsonElement.get("Kmreading").getAsString();

        holder.mService.setText(serviceType);
        holder.mDateService.setText(kmReading);








        return convertView;
    }


    class ViewHolder {
        @BindView(R.id.service_name)
        TextView mService;

        @BindView(R.id.dateOfService)
        TextView mDateService;


        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
