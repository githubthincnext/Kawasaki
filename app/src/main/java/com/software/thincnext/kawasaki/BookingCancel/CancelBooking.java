package com.software.thincnext.kawasaki.BookingCancel;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.software.thincnext.kawasaki.Activity.HomeActivity;
import com.software.thincnext.kawasaki.ApiRequest.CancelServiceRequest;
import com.software.thincnext.kawasaki.ApiRequest.DashBoardInfo;
import com.software.thincnext.kawasaki.Dialog.FeedbackDialog;
import com.software.thincnext.kawasaki.R;
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

public class CancelBooking extends AppCompatActivity {

    //internet connection
    private ConnectionDetector connectionDetector;


    private static OkHttpClient.Builder builder;

    //Declaring progress dialog
    private ProgressDialog mProgress;

    private SharedPreferences sharedPreferences;


    @BindView(R.id.linear_layout)
    LinearLayout parentLayout;

    @BindView(R.id.reason_text)
    EditText mReasonText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_booking);
        ButterKnife.bind(this);

        connectionDetector = new ConnectionDetector(this);

        //Initialising progress dialog
        mProgress = new ProgressDialog(this);

        //Hiding Keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        sharedPreferences = getSharedPreferences(Constants.PREFERENCE_NAME, MODE_PRIVATE);


    }


    @OnClick({ R.id.back_button,R.id.reason_text})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_button:
                Intent intent=new Intent(CancelBooking.this, HomeActivity.class);
                startActivity(intent);
                break;
            case R.id.reason_text:

                // call cancel booking service

                checkInternetConnection();



        }
    }

    private void checkInternetConnection() {
        // check internet connection
        boolean isInternetPresent = connectionDetector.isConnectingToInternet();

        if (isInternetPresent) {
            // call otpService
            callCancelServiceBoking();


        }
        else
        {
            Snackbar snackbar = Snackbar.make(parentLayout, "No internet connection!", Snackbar.LENGTH_LONG);

            snackbar.show();
        }
    }

    private void callCancelServiceBoking() {
        //progress dialog
        showProgressDialog(getResources().getString(R.string.please_wait));

        final String RegistrationNo=sharedPreferences.getString(Constants.REGISTER_NUMBER,"");

        String cancelationres=mReasonText.getText().toString();

        //   Toast.makeText(HomeActivity.this,RegistrationNo,Toast.LENGTH_SHORT).show();

        final CancelServiceRequest request = new CancelServiceRequest();
        request.setmRegisterNumber(RegistrationNo);
        request.setmCancelationReason(cancelationres);
        Log.e("Register Number",RegistrationNo);


        builder = getHttpClient();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create()).client(builder.build()).build();
        API gi = retrofit.create(API.class);


        Call<JsonArray> call = (Call<JsonArray>) gi.cancelServiceBooking(request);
        call.enqueue(new Callback<JsonArray>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> jsonResponse) {
                if (mProgress != null) {
                    mProgress.dismiss();
                }
                //Checking for response code
                if (jsonResponse != null) {
                    if (jsonResponse.code() == 200) {
                        for (int i = 0; i < jsonResponse.body().size(); i++) {
                            //   JsonArray jsonArray=jsonResponse.body().get(i).getA;

                            JsonObject jsonObject = jsonResponse.body().get(i).getAsJsonObject();
                          String message = jsonObject.get("Msg").getAsString();
                          Toast.makeText(CancelBooking.this,message,Toast.LENGTH_SHORT).show();

                            if (mProgress != null) {
                                mProgress.dismiss();
                            } else {


                                if (mProgress != null) {
                                    mProgress.dismiss();
                                }
                                Toast.makeText(CancelBooking.this, "Something went wrong! Error :" + jsonResponse.code(), Toast.LENGTH_SHORT).show();


                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Toast.makeText(CancelBooking.this, "Something went wrong!  Try again", Toast.LENGTH_SHORT).show();
            }


        });



    }

    private void showProgressDialog(String string) {
        if (mProgress == null) {
            mProgress = new ProgressDialog(this);
        }
        mProgress.setMessage(string);
        mProgress.setCancelable(false);
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();
    }

    private OkHttpClient.Builder getHttpClient() {
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
