package com.software.thincnext.kawasaki.Profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.software.thincnext.kawasaki.R;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;



public class ProfileAdapter extends  RecyclerView.Adapter<ProfileAdapter.ViewHolder> {

    private SharedPreferences sharedPreferences;


    private ProfileActivity primaryActivity;
    private Context context;

    private JsonArray items = new JsonArray();


    public ProfileAdapter(Context context,ProfileActivity profileActivity) {

        this.primaryActivity = profileActivity;
        this.context=context;


    }

    public void listUpdate(JsonArray houseListOutputList) {


        this.items = houseListOutputList;

    }

    @Override
    public ProfileAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bike_list_row, parent, false);

        ViewHolder holder = new ViewHolder(view);
        JsonObject jsonElement = items.get(viewType).getAsJsonObject();

        final String regNumber=jsonElement.get("RegistrationNo").getAsString();
        holder.mRegisterNumber.setText(regNumber);

        return new ProfileAdapter.ViewHolder(view);
    }

    @Override
    public int getItemCount() {

        if (items==null){

            return 0;

        } else {

            return items.size();

        }
    }

    @Override
    public void onBindViewHolder(final ProfileAdapter.ViewHolder holder, final int position) {





    }

    public class ViewHolder extends RecyclerView.ViewHolder {



        @BindView(R.id.register_number)
        TextView mRegisterNumber;


        public ViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);

        }
    }
}
