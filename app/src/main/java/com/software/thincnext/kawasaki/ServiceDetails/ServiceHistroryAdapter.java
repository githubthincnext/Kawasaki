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

public class ServiceHistroryAdapter extends BaseAdapter {
    private SharedPreferences mPref;
    private JsonArray items = new JsonArray();

    private Context context;

    public ServiceHistroryAdapter(Context context) {

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
            convertView = LayoutInflater.from(context).inflate(R.layout.service_history_adapter, parent, false);
        }
        ViewHolder holder = new ViewHolder(convertView);
        JsonObject jsonElement = items.get(position).getAsJsonObject();

        final String serviceNumber=jsonElement.get("ServiceNo").getAsString();


        String serviceDate=jsonElement.get("ServiceDate").getAsString();
        String dealerName =jsonElement.get("DealershipName").getAsString();
        String branchName=jsonElement.get("BranchName").getAsString();
        String billAmount=jsonElement.get("BillAmount").getAsString();
        String serviceAdviser=jsonElement.get("ServiceAdvisor").getAsString();

        holder.mService.setText(serviceNumber);
        holder.mDateService.setText(serviceDate);


        return convertView;
    }


    class ViewHolder {
        @BindView(R.id.message)
        TextView mService;

        @BindView(R.id.dateOfService)
        TextView mDateService;


        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
