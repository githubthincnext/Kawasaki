package com.software.thincnext.kawasaki.Activity;

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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.software.thincnext.kawasaki.ApiRequest.OtpRequest;
import com.software.thincnext.kawasaki.ApiRequest.OtpResponse;
import com.software.thincnext.kawasaki.Models.Primary.Login;
import com.software.thincnext.kawasaki.Models.Register.Otp.OtpOutPut;
import com.software.thincnext.kawasaki.Primary.PrimaryActivity;
import com.software.thincnext.kawasaki.R;
import com.software.thincnext.kawasaki.Services.API;
import com.software.thincnext.kawasaki.Services.ConnectionDetector;
import com.software.thincnext.kawasaki.Services.Constants;
import java.util.concurrent.TimeUnit;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OTPVerfication extends AppCompatActivity {


    @BindView(R.id.tv_otpVerification_codeError)
    TextView mOtpError;

    @BindView(R.id.et_otpVerification_code)
    EditText codeText;

    //Sharedpreferences
    private SharedPreferences sharedPreferences;

    private static final String TAG = OTPVerfication.class.getSimpleName();
    private static OkHttpClient.Builder builder;


    //Declaring progress dialog
    private ProgressDialog mProgress;

    //internet connection
    private ConnectionDetector connectionDetector;

    @BindView(R.id.linear_layout)
    LinearLayout parentLayout;

    private String otp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverfication);

        ButterKnife.bind(this);

        sharedPreferences = getSharedPreferences(Constants.PREFERENCE_NAME, MODE_PRIVATE);

        otp = sharedPreferences.getString(Constants.OTP, null);
        codeText.setText(otp);

        //Initialising progress dialog
        mProgress = new ProgressDialog(this);

        // internet intialisation
        connectionDetector = new ConnectionDetector(this);


        //Initialising shared preferences
        sharedPreferences = getSharedPreferences(Constants.PREFERENCE_NAME, MODE_PRIVATE);


        //Hiding Keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //Hiding views
        mOtpError.setVisibility(View.GONE);
    }


    @Override
    public void onBackPressed() {

        finish();

    }

    @OnClick({R.id.ll_otpVerification_verifyClick})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_otpVerification_verifyClick:

                //Reading input values
                String verifyCode = codeText.getText().toString();

                //Validating text fields
                boolean isValid = validateTextFields(verifyCode);

                if (isValid) {


                   checkInternetConnection();



                }

                break;


        }


    }

    private void checkInternetConnection() {

        // check internet connection
        boolean isInternetPresent = connectionDetector.isConnectingToInternet();

        if (isInternetPresent) {

            verifyOtp();


        }
        else
        {
            Snackbar snackbar = Snackbar.make(parentLayout, "No internet connection!", Snackbar.LENGTH_LONG);

            snackbar.show();
        }

    }

    private void verifyOtp() {



        //progress dialog
        showProgressDialog(getResources().getString(R.string.please_wait));

        String otpStatus=sharedPreferences.getString(Constants.OTP_STATUS,null);
        String mobNumber=sharedPreferences.getString(Constants.MOBILE_NUMBER,null);



        OtpRequest otpRequest=new OtpRequest();
        otpRequest.setmOtp(otp);
        otpRequest.setmOtpStatus(otpStatus);
        otpRequest.setmPhoneNumber(mobNumber);

        builder = getHttpClient();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create()).client(builder.build()).build();
        API gi = retrofit.create(API.class);


        Call<JsonArray> call = (Call<JsonArray>) gi.VerifyOtp(otpRequest);
        call.enqueue(new Callback<JsonArray>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> jsonArrayResponse) {
                if (mProgress != null) {
                    mProgress.dismiss();
                }

                //Checking for  null response
                if (jsonArrayResponse != null) {
                    //Checking for response code
                    if (jsonArrayResponse.code() == 200) {
                        Log.e("OTP VERIFY MESSAGE", jsonArrayResponse.message());

                        //Saving sharedpreference values
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(Constants.IS_LOGGED_IN, true);
                        editor.commit();

                        Log.e("RESPONSE SIZE", jsonArrayResponse.body().size()+"");

                        if (jsonArrayResponse.body().size()==1){
                            Intent homeIntent = new Intent(OTPVerfication.this, HomeActivity.class);
                            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(homeIntent);
                        }
else {
                            Intent homeIntent = new Intent(OTPVerfication.this, PrimaryActivity.class);
                            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(homeIntent);
                        }

                    }

                        else {
                            Toast.makeText(OTPVerfication.this,"OTP is Not Verified",Toast.LENGTH_SHORT).show();
                        }






                    } else {

                        if (mProgress != null) {
                            mProgress.dismiss();
                        } else {


                            if (mProgress != null) {
                                mProgress.dismiss();
                            }
                            Toast.makeText(OTPVerfication.this, "Something went wrong! Error :" , Toast.LENGTH_SHORT).show();


                        }


                    }
                }


            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Toast.makeText(OTPVerfication.this, "Something went wrong!  Try again", Toast.LENGTH_SHORT).show();
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

    private boolean validateTextFields(String verifyCode) {
        boolean validFlag = true;

        if (verifyCode.isEmpty())
        {
            mOtpError.setText(R.string.enter_4_digits);
            mOtpError.setVisibility(View.VISIBLE);

            validFlag = false;
        }

            else
            {
                mOtpError.setVisibility(View.GONE);

        }

        return validFlag;

    }
}
