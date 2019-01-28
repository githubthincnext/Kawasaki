package com.software.thincnext.kawasaki.Profile;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.software.thincnext.kawasaki.Dialog.EditProfileDialog;
import com.software.thincnext.kawasaki.Dialog.LogoutAppDialog;
import com.software.thincnext.kawasaki.Models.Primary.Login;
import com.software.thincnext.kawasaki.R;
import com.software.thincnext.kawasaki.Services.API;
import com.software.thincnext.kawasaki.Services.Constants;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfileActivity extends AppCompatActivity {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;


    private ProfileAdapter mAdapter;

    @BindView(R.id.name)
    TextView mName;

    @BindView(R.id.mobile_number)
    TextView mMobileNumber;

    @BindView(R.id.email)
    TextView mEmail;

    private List<Bike> bikeList = new ArrayList<>();


    //Intialisation sharedpefrence
    private SharedPreferences sharedPreferences;

    private static OkHttpClient.Builder builder;


    //Declaring progress dialog
    private ProgressDialog mProgress;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        //Hiding Keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        sharedPreferences = getSharedPreferences(Constants.PREFERENCE_NAME, MODE_PRIVATE);

        String name = sharedPreferences.getString(Constants.CUSTOMER_NAME, null);
        String mobileNumber = sharedPreferences.getString(Constants.MOBILE_NUMBER, null);
        String email=sharedPreferences.getString(Constants.EMAIL,null);

        if (name != null) {
            mName.setText(name);
        }

        if (mobileNumber != null) {
            mMobileNumber.setText(mobileNumber);
        }

        if (email==null){
            mEmail.setText(R.string.no_email);
        }
        else {
            mEmail.setText(email);

        }

        //Initialising progress dialog
        mProgress = new ProgressDialog(this);

        // call primary bike service
        callPrimaryBikeService();

    //   mAdapter = new ProfileAdapter(bikeList);

        mAdapter = new ProfileAdapter(this,ProfileActivity.this);

        //Recyclerview
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManagerTopic = new LinearLayoutManager(ProfileActivity.this);
        linearLayoutManagerTopic.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManagerTopic);

        recyclerView.setAdapter(mAdapter);

       // addDataToRecylerView();


    }

    private void callPrimaryBikeService() {

        String mobNumber=sharedPreferences.getString(Constants.MOBILE_NUMBER,"");
        //progress dialog
        showProgressDialog(getResources().getString(R.string.please_wait));


        Login request = new Login();
        request.setmMobileNumber(mobNumber);


        builder = getHttpClient();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create()).client(builder.build()).build();
        API gi = retrofit.create(API.class);



        Call<JsonArray> call = (Call<JsonArray>) gi.login(request);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> jsonResponse) {
                if (mProgress != null) {
                    mProgress.dismiss();
                }

                //Checking for response code
                if (jsonResponse != null) {
                    if (jsonResponse.code() == 200) {
                        for (int i = 0; i < jsonResponse.body().size(); i++) {
                            JsonObject object = jsonResponse.body().get(i).getAsJsonObject();

                            Log.e("Primary Bike Details", jsonResponse.body() + "");

                            mAdapter.listUpdate(jsonResponse.body());
                            mAdapter.notifyDataSetChanged();

                        }


                    } else {

                        if (mProgress != null) {
                            mProgress.dismiss();
                        } else {


                            if (mProgress != null) {
                                mProgress.dismiss();
                            }
                            Toast.makeText(ProfileActivity.this, "Something went wrong! Error :" + jsonResponse.code(), Toast.LENGTH_SHORT).show();


                        }


                    }
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Something went wrong!  Try again", Toast.LENGTH_SHORT).show();
            }


        });

    }

    private void showProgressDialog(String string) {

    }


    public OkHttpClient.Builder getHttpClient() {
        if (builder == null) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder client = new OkHttpClient.Builder();
            client.addInterceptor(loggingInterceptor);
            client.writeTimeout(60000, TimeUnit.MILLISECONDS);
            client.readTimeout(60000, TimeUnit.MILLISECONDS);
            client.connectTimeout(60000, TimeUnit.MILLISECONDS);
            return client;
        }
        return builder;
    }

    private void addDataToRecylerView() {

        Bike bike = new Bike("KA 09 4535",R.drawable.bike_image);
        bikeList.add(bike);

        bike = new Bike("KA 09 4535",R.drawable.bike_image);
        bikeList.add(bike);

        bike = new Bike("KA 09 4535",R.drawable.bike_image);
        bikeList.add(bike);

        bike = new Bike("KA 09 4535",R.drawable.bike_image);
        bikeList.add(bike);

        bike = new Bike("KA 09 4535",R.drawable.bike_image);
        bikeList.add(bike);

        bike = new Bike("KA 09 4535",R.drawable.bike_image);
        bikeList.add(bike);

        mAdapter.notifyDataSetChanged();


    }

    @OnClick({ R.id.back_button})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_button:
                finish();
                break;



        }
    }

    private void editProfileDetails() {

        //Calling dialog
        FragmentManager logoutManager = getFragmentManager();
        EditProfileDialog editProfileDialog = new EditProfileDialog();
        editProfileDialog.show(logoutManager, "EDIT_PROFILE_DIALOG");

    }

}
