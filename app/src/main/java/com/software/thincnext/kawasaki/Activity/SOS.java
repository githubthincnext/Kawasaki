package com.software.thincnext.kawasaki.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.software.thincnext.kawasaki.DealerAdapter.CustomSpinerDealerAdapter;
import com.software.thincnext.kawasaki.Models.DealerLsit.DealerRequest;
import com.software.thincnext.kawasaki.Models.SosRequest.SOSRequest;
import com.software.thincnext.kawasaki.R;
import com.software.thincnext.kawasaki.Services.API;
import com.software.thincnext.kawasaki.Services.ConnectionDetector;
import com.software.thincnext.kawasaki.Services.Constants;
import com.software.thincnext.kawasaki.SosSerachCity.SosSearchCity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
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

public class SOS extends AppCompatActivity  implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, OnMapReadyCallback {

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

    @BindView(R.id.cityName)
    EditText mCityName;

    @BindView(R.id.spinner_dealer)
    Spinner mSpinnerDealer;

    //Declaring progress dialog
    private ProgressDialog mProgress;

    //internet connection
    private ConnectionDetector connectionDetector;


    private static final String TAG = SOS.class.getSimpleName();
    private static OkHttpClient.Builder builder;

    private SharedPreferences sharedPreferences;

    private CustomSpinerDealerAdapter customSpinerDealerAdapter;

    //Declare ApiClient
    private GoogleApiClient mGoogleApiClient;

    private static final int PERMISSIONS_REQUEST_LOCATION = 99;
    // permissions for gps
    private String[] permissions = {
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.ACCESS_COARSE_LOCATION"
    };


    private Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);
        ButterKnife.bind(this);


        //Hiding Keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        sharedPreferences = getSharedPreferences(Constants.PREFERENCE_NAME, MODE_PRIVATE);



        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-message_one"));

        //Initialising progress dialog
        mProgress = new ProgressDialog(this);

        connectionDetector = new ConnectionDetector(this);

        checkInternetConnection();

        mSpinnerDealer.setPrompt(  "Select");
        customSpinerDealerAdapter=new CustomSpinerDealerAdapter(this);

        mSpinnerDealer.setAdapter(customSpinerDealerAdapter);



        //Check Gps Mode
        LocationManager Lmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!Lmanager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }

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


        //check googleApi
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }


    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String ItemName = intent.getStringExtra("item");
            // Toast.makeText(BookService.this,ItemName +" " ,Toast.LENGTH_SHORT).show();
            mCityName.setText(ItemName);
        }
    };


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
            // call DealerListService
            selectDealerName();

        } else {

            Snackbar snackbar = Snackbar.make(constraintLayout, "No internet connection!", Snackbar.LENGTH_LONG);

            snackbar.show();
        }


    }

    private void selectDealerName() {
        //progress dialog
        showProgressDialog(getResources().getString(R.string.please_wait));

        DealerRequest dealerRequest=new DealerRequest();
        String cityName=mCityName.getText().toString();
        dealerRequest.setmCityName("Bangalore");



        builder = getHttpClient();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create()).client(builder.build()).build();
        API gi = retrofit.create(API.class);



        Call<JsonArray> call = (Call<JsonArray>) gi.getDealerList(dealerRequest);
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
                            //   JsonArray jsonArray=jsonResponse.body().get(i).getA;


                            JsonObject jsonObject = jsonResponse.body().get(i).getAsJsonObject();
                            String message = jsonObject.get("Message").getAsString();
                            Log.e("MESSAGE",message);
                            if (message.equalsIgnoreCase("1,Success")) {

                                customSpinerDealerAdapter.updateCategeryItems(jsonResponse.body().getAsJsonArray());
                                customSpinerDealerAdapter.notifyDataSetChanged();



                            }
                        }
                    } else {


                        Toast.makeText(SOS.this," Dealer List Not Found",Toast.LENGTH_SHORT).show();


                        if (mProgress != null) {
                            mProgress.dismiss();
                        } else {


                            if (mProgress != null) {
                                mProgress.dismiss();
                            }
                            Toast.makeText(SOS.this, "Something went wrong! Error :" + jsonResponse.message(), Toast.LENGTH_SHORT).show();


                        }


                    }
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Toast.makeText(SOS.this, "Something went wrong!  Try again", Toast.LENGTH_SHORT).show();
            }


        });
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

    @OnClick({ R.id.back_button,R.id.search_city})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_button:
                finish();
                break;
            case R.id.search_city:
                Intent intent=new Intent(SOS.this, SosSearchCity.class);
                startActivity(intent);
                break;

        }
    }

    @Override
    protected void onResume() {

        super.onResume();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_LOCATION:

                if (mGoogleApiClient == null) {
                    mGoogleApiClient = new GoogleApiClient.Builder(this)
                            .addConnectionCallbacks(this)
                            .addOnConnectionFailedListener(this)
                            .addApi(LocationServices.API)
                            .build();
                }
                mGoogleApiClient.connect();
        }


    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                requestPermissions(permissions, PERMISSIONS_REQUEST_LOCATION);
            }
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            String location = String.valueOf(mLastLocation.getLatitude()) + " and " + String.valueOf(mLastLocation.getLongitude());

            Geocoder mCoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> fromLocation = mCoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
                Address address = fromLocation.get(0);
                if (address != null) {
                    String addressLine = address.getAddressLine(0);
                    String postalCode = address.getPostalCode();
                    String premises = address.getPremises();
                    String locality = address.getLocality();
                    String subLocality = address.getSubLocality();
                    Log.d(TAG, " !!!!the address values are " + addressLine + " prem:" + premises + " loca:" + locality + " subloca:" + subLocality + "postal: " + postalCode);
                    String fullAddress;

                    //   Toast.makeText(BookService.this, locality, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "City" + locality);
                    ;
                  /*  if (addressLine != null) {
                        fullAddress = addressLine;
                        if (subLocality != null) {
                            fullAddress = fullAddress + "," + subLocality;
                        } else if (locality != null) {
                            fullAddress = fullAddress + "," + locality;
                        } else if (postalCode != null) {
                            fullAddress = fullAddress + "," + postalCode;
                        }*/

                    mCityName.setText(locality);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            //mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}
