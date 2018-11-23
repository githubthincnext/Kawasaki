package com.software.thincnext.kawasaki.Primary;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.software.thincnext.kawasaki.Activity.HomeActivity;
import com.software.thincnext.kawasaki.Models.Primary.Login;
import com.software.thincnext.kawasaki.R;
import com.software.thincnext.kawasaki.Services.API;
import com.software.thincnext.kawasaki.Services.ConnectionDetector;
import com.software.thincnext.kawasaki.Services.Constants;
import java.util.concurrent.TimeUnit;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class PrimaryActivity extends AppCompatActivity {


    public static final String TAG = PrimaryActivity.class.getSimpleName();

    @BindView(R.id.listView)
    ListView mListView;

    @BindView(R.id.linear_layout)
    LinearLayout parentLayout;


    //Declaring progress dialog
    private ProgressDialog mProgress;

    //Connection detector class
    private ConnectionDetector connectionDetector;

    private static OkHttpClient.Builder builder;

    //adapter
    private PrimaryBikeAdapter primaryBikeAdapter;


    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_primary);
        //butterknfnife permission
        ButterKnife.bind(this);

        connectionDetector = new ConnectionDetector(this);

        sharedPreferences = getSharedPreferences(Constants.PREFERENCE_NAME, MODE_PRIVATE);


        //Initialising progress dialog
        mProgress = new ProgressDialog(this);

        //checkInternerConnection
        checkInternetConnection();

        // set listview to adapter
        primaryBikeAdapter = new PrimaryBikeAdapter(this);
        mListView.setAdapter(primaryBikeAdapter);
        primaryBikeAdapter.updatepref(sharedPreferences);
        mListView.setDivider(null);


    }

    private void checkInternetConnection() {
// check internet connection
        boolean isInternetPresent = connectionDetector.isConnectingToInternet();

        if (isInternetPresent) {


            // call primary bike service
            callPrimaryBikeService();
        } else {
            Snackbar snackbar = Snackbar.make(parentLayout, "No internet connection!", Snackbar.LENGTH_LONG);

            snackbar.show();
        }
    }


    private void showProgressDialog(String msg) {
        if (mProgress == null) {
            mProgress = new ProgressDialog(this);
        }
        mProgress.setMessage(msg);
        mProgress.setCancelable(false);
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();
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

                            primaryBikeAdapter.listUpdate(jsonResponse.body());
                            primaryBikeAdapter.notifyDataSetChanged();

                            }


                    } else {

                        if (mProgress != null) {
                            mProgress.dismiss();
                        } else {


                            if (mProgress != null) {
                                mProgress.dismiss();
                            }
                            Toast.makeText(PrimaryActivity.this, "Something went wrong! Error :" + jsonResponse.code(), Toast.LENGTH_SHORT).show();


                        }


                    }
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Toast.makeText(PrimaryActivity.this, "Something went wrong!  Try again", Toast.LENGTH_SHORT).show();
            }


        });
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

}
