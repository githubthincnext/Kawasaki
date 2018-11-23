package com.software.thincnext.kawasaki.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.software.thincnext.kawasaki.Models.SosRequest.SOSRequest;
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

public class SOS extends AppCompatActivity {

    @BindView(R.id.constraintLayout)
    ConstraintLayout constraintLayout;

    @BindView(R.id.last_dealer_mobile_number)
    TextView mlDealerMobNumber;

    @BindView(R.id.last_dealer_call_icon)
    ImageView mlDealerCall;

    @BindView(R.id.last_dealer_email)
    TextView mLastDealerEmail;

    @BindView(R.id.last_dealer_email_icon)
    ImageView mLastDealerEmailIcon;

    @BindView(R.id.search_dealer_mobile_number)
    TextView mSearchDealerMobNumber;

    @BindView(R.id.serach_dealer_call_icon)
    ImageView mSearchDealerCall;

    @BindView(R.id.search_dealer_email)
    TextView mSearchDealerEmail;

    @BindView(R.id.serach_dealer_email_icon)
    ImageView mSearchDealerEmailIcon;


    //Declaring progress dialog
    private ProgressDialog mProgress;

    //internet connection
    private ConnectionDetector connectionDetector;


    private static final String TAG = SOS.class.getSimpleName();
    private static OkHttpClient.Builder builder;

    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);
        ButterKnife.bind(this);


        //Hiding Keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        sharedPreferences = getSharedPreferences(Constants.PREFERENCE_NAME, MODE_PRIVATE);


        //Initialising progress dialog
        mProgress = new ProgressDialog(this);

        connectionDetector = new ConnectionDetector(this);

        checkInternetConnection();

        mlDealerCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String lastDealerMobile=mlDealerMobNumber.getText().toString();

                // make a phone call
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + lastDealerMobile));
                startActivity(intent);

            }
        });

        mSearchDealerCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String searchMobile=mSearchDealerMobNumber.getText().toString();
                // make a phone call
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + searchMobile));
                startActivity(intent);

            }
        });

        mLastDealerEmailIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                   sendLstDealerEmail();

            }
        });

        mSearchDealerEmailIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendSearchEmail();
            }
        });



    }

    private void sendSearchEmail() {
        Log.i("Send email", "");

        String[] TO = {mSearchDealerEmail.getText().toString()};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Your subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message goes here");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            //  getActivity(). finish();

        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(SOS.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }

    }

    private void sendLstDealerEmail() {
        Log.i("Send email", "");

        String[] TO = {mLastDealerEmail.getText().toString()};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Your subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message goes here");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            //  getActivity(). finish();

        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(SOS.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkInternetConnection() {

        //Checking internet connection
        boolean isInternetPresent = connectionDetector.isConnectingToInternet();

        if (isInternetPresent) {



            //Calling  sos details service
            callSosDetails();

        } else {

            Snackbar snackbar = Snackbar.make(constraintLayout, "No internet connection!", Snackbar.LENGTH_LONG);

            snackbar.show();
        }


    }

    private void callSosDetails() {


        //progress dialog
        showProgressDialog(getResources().getString(R.string.please_wait));

        String registerNumber = sharedPreferences.getString(Constants.REGISTER_NUMBER, "");




        SOSRequest sosRequest = new SOSRequest();
        sosRequest.setmRegistrationNumber(registerNumber);


        builder = getHttpClient();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create()).client(builder.build()).build();
        API gi = retrofit.create(API.class);


        Call<JsonArray> call = (Call<JsonArray>) gi.getSosDetails(sosRequest);
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

                            Log.e("SOS Details Details", jsonResponse.body() + "");

                            String lastServiceEmail=object.get("LastServiceDealerEmail").getAsString();

                            String lastServiceCall=object.get("LastServiceDealerPhone").getAsString();

                            String ServiceEmail=object.get("SoldDealerEmail").getAsString();

                            String ServiceCall=object.get("LastServiceDealerPhone").getAsString();


                            mLastDealerEmail.setText(lastServiceEmail);
                            mlDealerMobNumber.setText(lastServiceCall);

                            mSearchDealerEmail.setText(ServiceEmail);
                            mSearchDealerMobNumber.setText(ServiceCall);




                        }


                    } else {


                        if (mProgress != null) {
                            mProgress.dismiss();
                        }

                        Toast.makeText(SOS.this, "Something went wrong! Error :" + jsonResponse.code(), Toast.LENGTH_SHORT).show();


                    }
                }
            }



            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Toast.makeText(SOS.this, "Something went wrong!  Try again", Toast.LENGTH_SHORT).show();
            }


        });


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

    private void showProgressDialog(String msg) {
        if (mProgress == null) {
            mProgress = new ProgressDialog(this);
        }
        mProgress.setMessage(msg);
        mProgress.setCancelable(false);
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();
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
