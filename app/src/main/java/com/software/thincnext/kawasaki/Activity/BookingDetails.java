package com.software.thincnext.kawasaki.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.software.thincnext.kawasaki.BookingCancel.CancelBooking;
import com.software.thincnext.kawasaki.R;
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

public class BookingDetails extends AppCompatActivity {

      @BindView(R.id.constraintLayout)
      LinearLayout parentLayout;


    //Declaring progress dialog
    private ProgressDialog mProgress;

      //internet connection
      private ConnectionDetector connectionDetector;

    private static OkHttpClient.Builder builder;

    private SharedPreferences sharedPreferences;

    @BindView(R.id.booking_date)
    TextView mBookingDate;

    @BindView(R.id.booking_num)
    TextView mBookingNumber;

    @BindView(R.id.booking_branch)
    TextView mBookingBranch;

    @BindView(R.id.service_date)
    TextView mServiceDate;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_details);
        ButterKnife.bind(this);

        connectionDetector = new ConnectionDetector(this);

        //Hiding Keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        sharedPreferences = getSharedPreferences(Constants.PREFERENCE_NAME, MODE_PRIVATE);

        //Initialising progress dialog
        mProgress = new ProgressDialog(this);


        //call internet connection
        checkInternetConnection();


}

      private void checkInternetConnection() {
          // check internet connection
          boolean isInternetPresent = connectionDetector.isConnectingToInternet();

          if (isInternetPresent) {
             // call BookingDetailsInfo
              callBookingDetailsInfo();


          }
          else
          {
              Snackbar snackbar = Snackbar.make(parentLayout, "No internet connection!", Snackbar.LENGTH_LONG);

              snackbar.show();
          }
      }

      private void callBookingDetailsInfo() {
          //progress dialog
          showProgressDialog(getResources().getString(R.string.please_wait));
          String RegistrationNo=sharedPreferences.getString(Constants.REGISTER_NUMBER,"");

          ServiceRequest request = new ServiceRequest();
          request.setmRegistrationNumber(RegistrationNo);


          builder = getHttpClient();
          Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create()).client(builder.build()).build();
          API gi = retrofit.create(API.class);


          Call<JsonArray> call = (Call<JsonArray>) gi.getBookingDetailsInfo(request);
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
                              String bookingNumber = jsonObject.get("BookingNo").getAsString();
                              String bookingDate=jsonObject.get("BookingDate").getAsString();
                              String bookingBranch=jsonObject.get("BookingBranch").getAsString();
                              String serviceDate=jsonObject.get("ServiceDate").getAsString();

                              mBookingBranch.setText(bookingBranch);
                              mBookingDate.setText(bookingDate);
                              mServiceDate.setText(serviceDate);
                              mBookingNumber.setText(bookingNumber);

                          }





                      } else {

                          if (mProgress != null) {
                              mProgress.dismiss();
                          } else {


                              if (mProgress != null) {
                                  mProgress.dismiss();
                              }
                              Toast.makeText(BookingDetails.this, "Something went wrong! Error :" + jsonResponse.code(), Toast.LENGTH_SHORT).show();


                          }


                      }
                  }
              }

              @Override
              public void onFailure(Call<JsonArray> call, Throwable t) {
                  Toast.makeText(BookingDetails.this, "Something went wrong!  Try again", Toast.LENGTH_SHORT).show();
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


    @OnClick({ R.id.back_button,R.id.home,R.id.cancel_bookingg})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_button:
                finish();
                break;


            case R.id.home:
                // go to home page
                Intent intent = new Intent(BookingDetails.this, HomeActivity.class);
                startActivity(intent);
                break;

            case R.id.cancel_bookingg:
                // go to cancel booking page
                Intent intent0 = new Intent(BookingDetails.this, CancelBooking.class);
                startActivity(intent0);
                break;




        }
    }



}