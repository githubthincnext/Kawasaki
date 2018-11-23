package com.software.thincnext.kawasaki.Inbox;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.software.thincnext.kawasaki.Activity.SOS;
import com.software.thincnext.kawasaki.Adapter.InboxAdapter;
import com.software.thincnext.kawasaki.R;
import com.software.thincnext.kawasaki.ServiceDetails.ServiceHistroryAdapter;
import com.software.thincnext.kawasaki.ServiceDetails.ServiceRequest;
import com.software.thincnext.kawasaki.Services.API;
import com.software.thincnext.kawasaki.Services.ConnectionDetector;
import com.software.thincnext.kawasaki.Services.Constants;

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

public class InboxActivity extends AppCompatActivity {

    @BindView(R.id.listView)
    ListView listView;

    @BindView(R.id.relative_layout)
    RelativeLayout parentLayout;




    //Declaring progress dialog
    private ProgressDialog mProgress;

    //internet connection
    private ConnectionDetector connectionDetector;


    private static final String TAG = InboxActivity.class.getSimpleName();
    private static OkHttpClient.Builder builder;

    private SharedPreferences sharedPreferences;

    private InboxAdapter inboxAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        ButterKnife.bind(this);

        //Hiding Keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        sharedPreferences = getSharedPreferences(Constants.PREFERENCE_NAME, MODE_PRIVATE);


        //Initialising progress dialog
        mProgress = new ProgressDialog(this);

        connectionDetector = new ConnectionDetector(this);




        inboxAdapter = new InboxAdapter(this);

        //checkInternetConnection
        checkInternetConnection();

        listView.setAdapter(inboxAdapter);

        listView.setDivider(null);





    }

    private void checkInternetConnection() {

        //Checking internet connection
        boolean isInternetPresent = connectionDetector.isConnectingToInternet();

        if (isInternetPresent) {
           // call InboxDetails
            callInboxDetails();

        } else {


            Snackbar snackbar = Snackbar.make(parentLayout, "No internet connection!", Snackbar.LENGTH_LONG);

            snackbar.show();

        }

    }

    private void callInboxDetails() {


        //progress dialog
        showProgressDialog(getResources().getString(R.string.please_wait));
        String registerNumber=sharedPreferences.getString(Constants.REGISTER_NUMBER,"");




        ServiceRequest serviceRequest = new ServiceRequest();
        serviceRequest.setmRegistrationNumber(registerNumber);


        builder = getHttpClient();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create()).client(builder.build()).build();
        API gi = retrofit.create(API.class);



        Call<JsonArray> call = (Call<JsonArray>) gi.getServiceHistory(serviceRequest);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> jsonResponse) {


                //Checking for response code
                if (jsonResponse != null) {
                    if (jsonResponse.code() == 200) {
                        for (int i = 0; i < jsonResponse.body().size(); i++) {
                            JsonObject object = jsonResponse.body().get(i).getAsJsonObject();

                            Log.e("Service Details", jsonResponse.body() + "");

                         //   mProgress.setVisibility(View.GONE);
                            listView.setVisibility(View.VISIBLE);

                            inboxAdapter.listUpdate(jsonResponse.body());
                            inboxAdapter.notifyDataSetChanged();

                        }


                    } else {



                        Toast.makeText(InboxActivity.this, "Something went wrong! Error :" + jsonResponse.code(), Toast.LENGTH_SHORT).show();


                    }
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Toast.makeText(InboxActivity.this, "Something went wrong!  Try again", Toast.LENGTH_SHORT).show();
            }


        });
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


    @OnClick({ R.id.back_button})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_button:
                finish();
                break;

        }
    }

}
