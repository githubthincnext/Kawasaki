package com.software.thincnext.kawasaki.Primary;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.software.thincnext.kawasaki.Activity.HomeActivity;
import com.software.thincnext.kawasaki.R;
import com.software.thincnext.kawasaki.Services.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PrimaryBikeAdapter extends BaseAdapter {
    private SharedPreferences mPref;
    private JsonArray items = new JsonArray();

    private Context context;

    public PrimaryBikeAdapter(Context context) {

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
            convertView = LayoutInflater.from(context).inflate(R.layout.primary_bike_adapter, parent, false);
        }
        ViewHolder holder = new ViewHolder(convertView);
        JsonObject jsonElement = items.get(position).getAsJsonObject();

        final String regNumber=jsonElement.get("RegistrationNo").getAsString();


        String mobNumber=jsonElement.get("MobileNo").getAsString();


        mPref.edit().putString(Constants.MOBILE_NUMBER,mobNumber).apply();



        holder.mRegisterNumber.setText(regNumber);

        holder.mBikeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Intent homeIntent = new Intent(context, HomeActivity.class);
                Toast.makeText(context,regNumber,Toast.LENGTH_SHORT).show();

                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(homeIntent);
            }
        });


        return convertView;
    }


    class ViewHolder {
        @BindView(R.id.register_number)
        TextView mRegisterNumber;

        @BindView(R.id.bike_switch)
        Switch mBikeSwitch;


        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
