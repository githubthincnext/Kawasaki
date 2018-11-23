package com.software.thincnext.kawasaki.DealerAdapter;

import android.content.Context;
import android.util.Log;
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



public class CustomSpinerDealerAdapter extends BaseAdapter {

    private Context mContext;
    private JsonArray mJsonArray;

    public CustomSpinerDealerAdapter(Context context) {
        mContext = context;
        mJsonArray = new JsonArray();
    }

    @Override
    public int getCount() {
        return mJsonArray.size();
    }

    @Override
    public JsonObject getItem(int position) {
        return mJsonArray.get(position).getAsJsonObject();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }




    public void updateCategeryItems(JsonArray jsonResponse) {
        mJsonArray=jsonResponse;
    }


    public JsonArray getJsonData() {
        return mJsonArray;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.custom_delaer_alert, parent, false);
        }

        ViewHolder holder = new ViewHolder(convertView);

        JsonObject object = getItem(position);
        String dealerSiNo = object.get("DealerSlno").getAsString();
        String dealerName=object.get("DealerName").getAsString();
        Log.e("DEALER SI NO",dealerSiNo);
        Log.e("Dealer Name",dealerName);
        holder.mSpinnerItem.setText(dealerName);


        return convertView;
    }

    class ViewHolder {
        @BindView(R.id.spinnerItem)
        TextView mSpinnerItem;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
