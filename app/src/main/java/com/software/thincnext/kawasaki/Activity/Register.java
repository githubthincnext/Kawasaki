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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.software.thincnext.kawasaki.Models.Primary.Login;
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

    public class Register extends AppCompatActivity {

    @BindView(R.id.tv_login_mobileNumberError)
    TextView mobileError;

    @BindView(R.id.et_login_mobileNumber)
    EditText mMobileNumber;

    private static final String TAG = Register.class.getSimpleName();
    private static OkHttpClient.Builder builder;


    //internet connection
    private ConnectionDetector connectionDetector;


    //Declaring progress dialog
    private ProgressDialog mProgress;

    @BindView(R.id.relative_layout)
    RelativeLayout parentLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        //Initialising progress dialog
        mProgress = new ProgressDialog(this);

        connectionDetector = new ConnectionDetector(this);



        //Hiding Keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //Hiding views
        mobileError.setVisibility(View.GONE);




    }

    private void checkInternetCondition() {

        // check internet connection
        boolean isInternetPresent = connectionDetector.isConnectingToInternet();

        if (isInternetPresent) {
            // call otpService
            callOtpService();

        }
        else
        {




            Snackbar snackbar = Snackbar.make(parentLayout, "No internet connection!", Snackbar.LENGTH_LONG);

            snackbar.show();
        }
    }

    private void callOtpService() {

        //progress dialog
        showProgressDialog(getResources().getString(R.string.please_wait));
        String mob = mMobileNumber.getText().toString();

        Login request = new Login();
        request.setmMobileNumber(mob);


        builder = getHttpClient();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create()).client(builder.build()).build();
        API gi = retrofit.create(API.class);


        Call<JsonArray> call = (Call<JsonArray>) gi.login(request);
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
                            String otpStatus = jsonObject.get("OTPStatus").getAsString();
                            String otp=jsonObject.get("OTP").getAsString();
                            String mobNumber=jsonObject.get("MobileNo").getAsString();
                            String registerNumber=jsonObject.get("RegistrationNo").getAsString();
                            String name=jsonObject.get("CustomerName").getAsString();
                            String email=jsonObject.get("EmailID").getAsString();


                            Log.e("OTP STATUS", otpStatus);


                                 //save data to shared preference
                            SharedPreferences mPref = getSharedPreferences(Constants.PREFERENCE_NAME, MODE_PRIVATE);
                            SharedPreferences.Editor editor =  mPref.edit();
                            editor.putString(Constants.OTP,otp);
                            editor.putString(Constants.OTP_STATUS,otpStatus);
                            editor.putString(Constants.MOBILE_NUMBER,mobNumber);
                            editor.putString(Constants.REGISTER_NUMBER,registerNumber);
                            editor.putString(Constants.CUSTOMER_NAME,name);
                            editor.putString(Constants.EMAIL,email);
                            editor.apply();

                            if (otpStatus.equalsIgnoreCase("1")) {
                                Intent intent = new Intent(Register.this, OTPVerfication.class);
                                startActivity(intent);
                            }

                            else {
                                Toast.makeText(Register.this,"Mobile is not Registered with Our Network",Toast.LENGTH_SHORT).show();
                            }
                        }





                    } else {

                        if (mProgress != null) {
                            mProgress.dismiss();
                        } else {


                            if (mProgress != null) {
                                mProgress.dismiss();
                            }
                            Toast.makeText(Register.this, "Something went wrong! Error :" + jsonResponse.code(), Toast.LENGTH_SHORT).show();


                        }


                    }
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Toast.makeText(Register.this, "Something went wrong!  Try again", Toast.LENGTH_SHORT).show();
            }


        });
    }

    @OnClick({R.id.ll_login_continueClick})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_login_continueClick:

                if (validateDetails())
                {



                    checkInternetCondition();




                }
                break;
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


    private void callOtpRandom() {


        showProgressDialog(getResources().getString(R.string.please_wait));

        String mobileNumber = mMobileNumber.getText().toString();


        builder = getHttpClient();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create()).client(builder.build()).build();
        API gi = retrofit.create(API.class);

        Call<ResponseBody> call = (Call<ResponseBody>) gi.sendOtp(mobileNumber);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                Log.i(TAG, String.valueOf(response.body()));



                if (mProgress != null) {
                    mProgress.dismiss();
                }

                if (response.code() == 200) {

                    Toast.makeText(Register.this, "Otp Sent", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Register.this, OTPVerfication.class);
                    startActivity(intent);

                } else {

                    if (mProgress != null) {
                        mProgress.dismiss();
                    }

                    else {


                        if (mProgress != null) {
                            mProgress.dismiss();
                        }
                        Toast.makeText(Register.this, "Something went wrong! Error :" + response.code(), Toast.LENGTH_SHORT).show();


                    }

                }
            }


            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(Register.this,"Something went wrong!  Try again",Toast.LENGTH_SHORT).show();



            }

        });



    }







    private boolean validateDetails() {
        boolean validFlag = true;
        //Reading input values
        String mobileNumber = mMobileNumber.getText().toString();

        if (mobileNumber.isEmpty())
        {
            mobileError.setText(R.string.enter_mobile_number);
            mobileError.setVisibility(View.VISIBLE);

            validFlag = false;
        }
        else
        {
            if (mobileNumber.length() <10 || mobileNumber.length() >10)
            {
                mobileError.setText(R.string.enter_10_digits_mob_number);
                mobileError.setVisibility(View.VISIBLE);

                validFlag = false;
            }
            else
            {
                mobileError.setVisibility(View.GONE);
            }
        }

        return validFlag;





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

    @Override
    public void onBackPressed() {

        finish();

    }
}
