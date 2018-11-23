package com.software.thincnext.kawasaki.Activity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.software.thincnext.kawasaki.DatePicker.CustomDateTimePicker;
import com.software.thincnext.kawasaki.DealerAdapter.CustomSpinerDealerAdapter;
import com.software.thincnext.kawasaki.Models.DealerLsit.DealerListResponse;
import com.software.thincnext.kawasaki.Models.DealerLsit.DealerRequest;
import com.software.thincnext.kawasaki.Primary.PrimaryActivity;
import com.software.thincnext.kawasaki.R;
import com.software.thincnext.kawasaki.SearchActivity.CityItem;
import com.software.thincnext.kawasaki.SearchActivity.SearchCity;
import com.software.thincnext.kawasaki.Services.API;
import com.software.thincnext.kawasaki.Services.ConnectionDetector;
import com.software.thincnext.kawasaki.Services.Constants;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

public class BookService extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, OnMapReadyCallback {

 private static final String TAG=BookService.class.getSimpleName();


    @BindView(R.id.date)
    EditText mDate;

    @BindView(R.id.datePicker)
    ImageView mDatePicker;

    @BindView(R.id.from_date)
    EditText mFromDate;

    @BindView(R.id.fromDatePicker)
    ImageView mFromDatePicker;

    @BindView(R.id.to_date)
    EditText mToDate;

    @BindView(R.id.cityName)
    EditText mCityName;

    @BindView(R.id.spinner_dealer)
    Spinner mSpinnerDealer;

    @BindView(R.id.constraintLayout)
    ConstraintLayout parentLayout;


    @BindView(R.id.FreeServiceCheckBox)
    CheckBox mCheckFreeService;

    @BindView(R.id.periodicServiceCheckBox)
    CheckBox mCheckPeriodicService;

    @BindView(R.id.breakDownCheck)
    CheckBox mBreakDownCheck;

    @BindView(R.id.washingCheckBox)
    CheckBox mWashingCheck;


    CustomDateTimePicker custom;

    //Intialisation sharedpefrence
    private SharedPreferences sharedPreferences;

    //Declaring variables
    private CityItem rItem;

    private   CheckBox[] chkArray;

    private CustomSpinerDealerAdapter customSpinerDealerAdapter;

    private static final int PERMISSIONS_REQUEST_LOCATION = 99;

    private Location mLastLocation;
        //Declare ApiClient
    private GoogleApiClient mGoogleApiClient;

    private int mHour, mMinute;
              // permissions for gps
    private String[] permissions = {
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.ACCESS_COARSE_LOCATION"
    };


    private static OkHttpClient.Builder builder;


    //Declaring progress dialog
    private ProgressDialog mProgress;

    //internet connection
    private ConnectionDetector connectionDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_service);
        ButterKnife.bind(this);

        //Hiding Keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        sharedPreferences = getSharedPreferences(Constants.PREFERENCE_NAME, MODE_PRIVATE);




        //Initialising progress dialog
        mProgress = new ProgressDialog(this);

        connectionDetector = new ConnectionDetector(this);

        //call internet connection
        checkInternetConnection();


        mSpinnerDealer.setPrompt(  "Select");
        customSpinerDealerAdapter=new CustomSpinerDealerAdapter(this);

        mSpinnerDealer.setAdapter(customSpinerDealerAdapter);


                       //Check Gps Mode
        LocationManager Lmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!Lmanager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }


             //check googleApi
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }


        rItem = new CityItem();



       chkArray = new CheckBox[4];

        chkArray[0] = (CheckBox) findViewById(R.id.FreeServiceCheckBox);
        chkArray[0].setOnClickListener(mListener);
        chkArray[1] = (CheckBox) findViewById(R.id.periodicServiceCheckBox); // what id do you have?
        chkArray[1].setOnClickListener(mListener);

        chkArray[2] = (CheckBox) findViewById(R.id.breakDownCheck); // what id do you have?
        chkArray[2].setOnClickListener(mListener);

        chkArray[3] = (CheckBox) findViewById(R.id.washingCheckBox); // what id do you have?
        chkArray[3].setOnClickListener(mListener);






        mDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                custom.showDialog();

            }
        });



        custom = new CustomDateTimePicker(BookService.this,
                new CustomDateTimePicker.ICustomDateTimeListener() {
                    @Override
                    public void onSet(Dialog dialog, Calendar calendarSelected,
                                      Date dateSelected, int year, String monthFullName,
                                      String monthShortName, int monthNumber, int date,
                                      String weekDayFullName, String weekDayShortName,
                                      int hour24, int hour12, int min, int sec,
                                      String AM_PM) {

                        String tempDayOfMonth = "";
                        String dateDisplay = "";


                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.MONTH, monthNumber);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM");
                        simpleDateFormat.setCalendar(calendar);
                        String monthName = simpleDateFormat.format(calendar.getTime());


                        if (date < 10) {
                            dateDisplay = "0" + String.valueOf(date);
                        } else {
                            dateDisplay = String.valueOf(date);
                        }
                        if (monthNumber < 10) {
                            tempDayOfMonth =   String.valueOf(monthName );
                        } else {
                            tempDayOfMonth = String.valueOf(monthName );
                        }


                        mDate.setText(dateDisplay
                                + " " + (tempDayOfMonth) + " " + year

                        );



                    }

                    @Override
                    public void onCancel() {

                    }
                });


        custom.setDate(Calendar.getInstance());



    }

    private void checkInternetConnection() {
        // check internet connection
        boolean isInternetPresent = connectionDetector.isConnectingToInternet();

        if (isInternetPresent) {
            // call DealerListService
            selectDealerName();


        }
        else
        {




            Snackbar snackbar = Snackbar.make(parentLayout, "No internet connection!", Snackbar.LENGTH_LONG);

            snackbar.show();
        }
    }

    private void selectDealerName() {

        //progress dialog
        showProgressDialog(getResources().getString(R.string.please_wait));

  DealerRequest dealerRequest=new DealerRequest();
  String cityName=mCityName.getText().toString();
  dealerRequest.setmCityName(cityName);



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
                            Log.e("MEAAGE",message);
                            if (message.equalsIgnoreCase("-1,Data not found")) {
                                Toast.makeText(BookService.this," Dealer List Not Found",Toast.LENGTH_SHORT).show();

                            }
                        }
                    } else {

                        customSpinerDealerAdapter.updateCategeryItems(jsonResponse.body().getAsJsonArray());
                        customSpinerDealerAdapter.notifyDataSetChanged();



                        if (mProgress != null) {
                            mProgress.dismiss();
                        } else {


                            if (mProgress != null) {
                                mProgress.dismiss();
                            }
                            Toast.makeText(BookService.this, "Something went wrong! Error :" + jsonResponse.message(), Toast.LENGTH_SHORT).show();


                        }


                    }
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Toast.makeText(BookService.this, "Something went wrong!  Try again", Toast.LENGTH_SHORT).show();
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


    // check checkbox selection
    private View.OnClickListener mListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            final int checkedId = v.getId();
            for (int i = 0; i < chkArray.length; i++) {
                final CheckBox current = chkArray[i];
                if (current.getId() == checkedId) {
                    current.setChecked(true);
                } else {
                    current.setChecked(false);
                }
            }
        }
    };


    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
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


    @OnClick({ R.id.back_button,R.id.fromDatePicker,R.id.todatePicker,R.id.save_continue,R.id.search_city})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_button:
                finish();
                break;

            case R.id.fromDatePicker:
                   //open From DatePicker
                openFromtimePicker();

                break;

            case R.id.todatePicker:
                //open To datePicker
                openToDatePicker();
                break;

            case R.id.save_continue:

                //validation
                validateDeatils();
                break;

            case R.id.search_city:

                Intent intent=new Intent(BookService.this, SearchCity.class);
                startActivity(intent);
                break;



        }
    }






    private void validateDeatils() {

        String date=mDate.getText().toString();

        String fromdate=mFromDate.getText().toString();

        String toDate=mToDate.getText().toString();

        String city=mCityName.getText().toString();

//        String dealerList=mSpinnerDealer.getSelectedItem().toString();
        boolean checkBox=mCheckFreeService.isChecked()||mCheckPeriodicService.isChecked()||mBreakDownCheck.isChecked()||mWashingCheck.isChecked();

        if (!checkBox==true){
            Toast.makeText(BookService.this,R.string.select_service_type,Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(date)){
            Toast.makeText(BookService.this,R.string.please_enter_date,Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(fromdate)){
            Toast.makeText(BookService.this,R.string.please_give_from_date,Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(toDate)){
            Toast.makeText(BookService.this,R.string.please_give_to_date,Toast.LENGTH_SHORT).show();
            return;
        }

    }

    private void openToDatePicker() {
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {


                        String am_pm;
                        if (hourOfDay > 12)
                        {
                            hourOfDay = hourOfDay - 12;
                            am_pm = "PM";
                        } else {
                            am_pm = "AM";
                        }


                        mToDate.setText(hourOfDay + ":" + minute + " " + am_pm);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();



    }

    private void openFromtimePicker() {
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {


                        String am_pm;
                        if (hourOfDay > 12)
                        {
                            hourOfDay = hourOfDay - 12;
                            am_pm = "PM";
                        } else {
                            am_pm = "AM";
                        }


                        mFromDate.setText(hourOfDay + ":" + minute + " " + am_pm);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
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
