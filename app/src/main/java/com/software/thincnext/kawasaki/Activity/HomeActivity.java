package com.software.thincnext.kawasaki.Activity;


import android.Manifest;



import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.software.thincnext.Request.BookService.ServiceBookingRequest;
import com.software.thincnext.kawasaki.Adapter.CenterZoomLayoutManager;
import com.software.thincnext.kawasaki.Adapter.HomeList;
import com.software.thincnext.kawasaki.Adapter.ItemList;
import com.software.thincnext.kawasaki.Adapter.LinePagerIndicatorDecoration;
import com.software.thincnext.kawasaki.Adapter.MyAdapter;
import com.software.thincnext.kawasaki.Adapter.MyHomeListAdapter;
import com.software.thincnext.kawasaki.ApiRequest.DashBoardInfo;
import com.software.thincnext.kawasaki.ApiRequest.FeedBackRequest;
import com.software.thincnext.kawasaki.BookingCancel.CancelBooking;
import com.software.thincnext.kawasaki.DataBase.DatabseHelper;
import com.software.thincnext.kawasaki.Dialog.ChangePicDialog;
import com.software.thincnext.kawasaki.Dialog.FeedbackDialog;
import com.software.thincnext.kawasaki.Dialog.LogoutAppDialog;
import com.software.thincnext.kawasaki.DisplayImage.DisplaySelectedImage;
import com.software.thincnext.kawasaki.Inbox.InboxActivity;
import com.software.thincnext.kawasaki.Profile.ProfileActivity;
import com.software.thincnext.kawasaki.R;
import com.software.thincnext.kawasaki.SearchActivity.RecylerTouchListener;
import com.software.thincnext.kawasaki.ServiceHistory;
import com.software.thincnext.kawasaki.Services.API;
import com.software.thincnext.kawasaki.Services.ConnectionDetector;
import com.software.thincnext.kawasaki.Services.Constants;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.widget.GridLayout.HORIZONTAL;


public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {



    //capturing from camera and gallery
    public static final int REQUEST_CAMERA_PROFILEIMAGE = 1088;
    public static final int REQUEST_GALLERY_PROFILEIMAGE = 1089;
    private static final int MY_PERMISSIONS_REQUEST_ACCOUNTS = 1;

    private static final String TAG = HomeActivity.class.getSimpleName();

    private SharedPreferences sharedPreferences;
    private DatabseHelper databseHelper;



    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;



    @BindView(R.id.service_type)
    TextView mServiceType;

    @BindView(R.id.recyclerviewItems)
    RecyclerView recyclerViewItem;


    @BindView(R.id.constraintLayout)
    RelativeLayout parentLayout;



    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;


      @BindView(R.id.sos_button)
    Button mSosButton;

    CircleImageView chooseImage;
    TextView HeaderCustomerName;
    TextView HeaderMobNumber;
    FrameLayout frameLayout;


    private static OkHttpClient.Builder builder;


    //Declaring progress dialog
    private ProgressDialog mProgress;

    //internet connection
    private ConnectionDetector connectionDetector;

    public static final int REQUEST_DISPLAY_IMAGE = 1090;


    boolean doubleBackToExitPressedOnce = false;




    private List<ItemList> itemList = new ArrayList<>();

    private List<HomeList> homeLists=new ArrayList<>();
    private Context context;

    private MyAdapter mAdapter;
    private MyHomeListAdapter myHomeListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);



        mAdapter = new MyAdapter(itemList);

        CenterZoomLayoutManager linearLayoutManager= (CenterZoomLayoutManager) new CenterZoomLayoutManager(HomeActivity.this, CenterZoomLayoutManager.HORIZONTAL, false);
        //recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true));
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setOnFlingListener(null);
        recyclerView.setAdapter(mAdapter);
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        recyclerView.addItemDecoration(new LinePagerIndicatorDecoration());
        setDataToRecylerview();



        // add a divider after each item for more clarity
        // groceryRecyclerView.addItemDecoration(new DividerItemDecoration(MainActivity.this, LinearLayoutManager.HORIZONTAL));
        myHomeListAdapter = new MyHomeListAdapter(homeLists,HomeActivity.this);
        recyclerViewItem.setHasFixedSize(true);

        final int numberOfColumns = 4;
       // int numberOfColumns = Utility.calculateNoOfColumns(getApplicationContext());
        recyclerViewItem.setLayoutManager(new GridLayoutManager(this, numberOfColumns));


        DividerItemDecoration itemDecor = new DividerItemDecoration(HomeActivity.this, HORIZONTAL);
        recyclerViewItem.addItemDecoration(itemDecor);

        //   LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.HORIZONTAL, false);
        // recyclerViewItem.setLayoutManager(horizontalLayoutManager);
        recyclerViewItem.setAdapter(myHomeListAdapter);
        populateHomePageList();







        //Initialising progress dialog
        mProgress = new ProgressDialog(this);

        connectionDetector = new ConnectionDetector(this);

        //Hiding Keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        sharedPreferences = getSharedPreferences(Constants.PREFERENCE_NAME, MODE_PRIVATE);


        //call internet connection
        checkInternetConnection();


        databseHelper = new DatabseHelper(getApplicationContext());
        databseHelper.createDataBase();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

   mSosButton.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View view) {
           Intent intent=new Intent(HomeActivity.this,SOS.class);
           startActivity(intent);
       }
   });



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);


        String cutName=sharedPreferences.getString(Constants.CUSTOMER_NAME,"");
        String mobNumber=sharedPreferences.getString(Constants.MOBILE_NUMBER,"");


        chooseImage = (CircleImageView)findViewById(R.id.imageView);
        HeaderCustomerName=(TextView)header.findViewById(R.id.customer_name);
        HeaderMobNumber=(TextView)header.findViewById(R.id.mobile_number);
        frameLayout=(FrameLayout)header.findViewById(R.id.frame_layout);



        HeaderCustomerName.setText(cutName);
        HeaderMobNumber.setText(mobNumber);


        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(HomeActivity.this,ProfileActivity.class);
                startActivity(intent);
            }

        });


        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




                       // FragmentManager changePicManager = getFragmentManager();
                        ChangePicDialog changePicDialog = new ChangePicDialog();
                        //changePicDialog.show(changePicManager, "CHANGEPIC_DIALOG");
                        changePicDialog.show(getSupportFragmentManager(),changePicDialog.getTag());










            }
        });
    }

    private void populateHomePageList() {
        HomeList item = new HomeList(R.drawable.service_home,R.string.service_name_book);
        homeLists.add(item);

        item = new HomeList(R.drawable.stores,R.string.service_history_home);
        homeLists.add(item);



        item= new HomeList(R.drawable.events,R.string.events);
        homeLists.add(item);

        item = new HomeList(R.drawable.profile_iocn,R.string.profile);
        homeLists.add(item);

        myHomeListAdapter.notifyDataSetChanged();
        recyclerViewItem.setAdapter(myHomeListAdapter);
    }

    private void setDataToRecylerview() {
        ItemList item = new ItemList(R.drawable.bikeeee);
        itemList.add(item);
        item = new ItemList(R.drawable.bikeeee);
        itemList.add(item);
        item = new ItemList(R.drawable.bikeeee);
        itemList.add(item);
        item= new ItemList(R.drawable.bikeeee);
        itemList.add(item);
        mAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(mAdapter);
    }

    private void checkInternetConnection() {
        // check internet connection
        boolean isInternetPresent = connectionDetector.isConnectingToInternet();

        if (isInternetPresent) {
            // call otpService
            callWebserviceForHeaderType();


        }
        else
        {
            Snackbar snackbar = Snackbar.make(parentLayout, "No internet connection!", Snackbar.LENGTH_LONG);

            snackbar.show();
        }
    }

    private void
    callWebserviceForHeaderType() {

        //progress dialog
        showProgressDialog(getResources().getString(R.string.please_wait));

        final String RegistrationNo=sharedPreferences.getString(Constants.REGISTER_NUMBER,"");

         //   Toast.makeText(HomeActivity.this,RegistrationNo,Toast.LENGTH_SHORT).show();

        final DashBoardInfo request = new DashBoardInfo();
        request.setmRegisterNumber(RegistrationNo);
        Log.e("Register Number",RegistrationNo);


        builder = getHttpClient();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create()).client(builder.build()).build();
        API gi = retrofit.create(API.class);


        Call<JsonArray> call = (Call<JsonArray>) gi.
                ServiceType(request);
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
                            String message = jsonObject.get("Message").getAsString();
                            String messageType=jsonObject.get("MessageType").getAsString();
                            mServiceType.setText(message);



                            if (messageType.equalsIgnoreCase("OB")){

                                mServiceType.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent=new Intent(HomeActivity.this, BookingDetails.class);
                                        startActivity(intent);
                                    }
                                });
                            }


                            if (messageType.equalsIgnoreCase("FD")) {

                                if (!databseHelper.checkRegistrationNumber(RegistrationNo))
                                {
                                    FragmentManager feedbackManager = getFragmentManager();
                                    FeedbackDialog feedbackDialog = new FeedbackDialog();
                                    feedbackDialog.show(feedbackManager, "FEEDBACK_DIALOG");
                                }
                            }





                            Log.e("MESSAGE", message);
                            Log.e("MessageTyype",messageType);
                            Log.e("SIZE",jsonResponse.body().size()+"");



                        }





                    } else {

                        if (mProgress != null) {
                            mProgress.dismiss();
                        } else {


                            if (mProgress != null) {
                                mProgress.dismiss();
                            }
                            Toast.makeText(HomeActivity.this, "Something went wrong! Error :" + jsonResponse.code(), Toast.LENGTH_SHORT).show();


                        }


                    }
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Toast.makeText(HomeActivity.this, "Something went wrong!  Try again", Toast.LENGTH_SHORT).show();
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

    private void requestPermissionCameraGallery() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {


            /*Snackbar snackbar = Snackbar
                    .make(changeImageActivityContainer, "Please allow permission in App Settings.", Snackbar.LENGTH_LONG)
                    .setAction("Go settings", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                            startActivity(intent);

                        }
                    });

            snackbar.show();*/


        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {


           /* Snackbar snackbar = Snackbar
                    .make(changeImageActivityContainer, "Please allow permission in App Settings.", Snackbar.LENGTH_LONG)
                    .setAction("Go settings", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                            startActivity(intent);

                        }
                    });

            snackbar.show();*/


        } else {

            int permissionCAMERA = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA);


            int storagePermission = ContextCompat.checkSelfPermission(this,


                    Manifest.permission.READ_EXTERNAL_STORAGE);


            List<String> listPermissionsNeeded = new ArrayList<>();
            if (storagePermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (permissionCAMERA != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.CAMERA);
            }
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this,

                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MY_PERMISSIONS_REQUEST_ACCOUNTS);

            }


        }

    }

    private boolean checkPermissionCameraGallery() {

        int permissionCAMERA = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);


        int storagePermission = ContextCompat.checkSelfPermission(this,


                Manifest.permission.READ_EXTERNAL_STORAGE);


        List<String> listPermissionsNeeded = new ArrayList<>();
        if (storagePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (permissionCAMERA != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,


                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MY_PERMISSIONS_REQUEST_ACCOUNTS);
            return false;
        }

        return true;


    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {


            Snackbar snackbar = Snackbar
                    .make(drawerLayout, "Please allow permission in App Settings.", Snackbar.LENGTH_LONG)


                    .setAction("Go settings", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                            startActivity(intent);

                        }
                    });

            snackbar.show();


        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {


            Snackbar snackbar = Snackbar
                    .make(drawerLayout, "Please allow permission in App Settings.", Snackbar.LENGTH_LONG)
                    .setAction("Go settings", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                            startActivity(intent);

                        }
                    });

            snackbar.show();


        } else {

            int permissionCAMERA = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA);


            int storagePermission = ContextCompat.checkSelfPermission(this,


                    Manifest.permission.READ_EXTERNAL_STORAGE);


            List<String> listPermissionsNeeded = new ArrayList<>();
            if (storagePermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (permissionCAMERA != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.CAMERA);
            }
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this,

                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MY_PERMISSIONS_REQUEST_ACCOUNTS);

            }


        }
    }

    //Func - Checking permission granted
    private boolean checkPermission() {

        int permissionCAMERA = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);


        int storagePermission = ContextCompat.checkSelfPermission(this,


                Manifest.permission.READ_EXTERNAL_STORAGE);


        List<String> listPermissionsNeeded = new ArrayList<>();
        if (storagePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (permissionCAMERA != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,


                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MY_PERMISSIONS_REQUEST_ACCOUNTS);
            return false;
        }

        return true;
    }










    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK ) {

            if (requestCode == REQUEST_CAMERA_PROFILEIMAGE) {

                Bitmap capturedBitmap = (Bitmap) data.getExtras().get("data");
                chooseImage.setImageBitmap(capturedBitmap);

            }
            else if (requestCode == REQUEST_GALLERY_PROFILEIMAGE) {

                try {
                    Bitmap selectedBitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());

                    chooseImage.setImageBitmap(selectedBitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else {

           /* Snackbar snackbar = Snackbar
                    .make(, "Cancelled", Snackbar.LENGTH_LONG);

            snackbar.show();*/

        }
    }





    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
       // Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        Snackbar snackbar=Snackbar.make(parentLayout,"Please click BACK again to exit",Snackbar.LENGTH_SHORT);
        snackbar.show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_change_bike) {
            // Handle the camera action
        } else if (id == R.id.nav_book_service) {

            //Going to House Page
            Intent intent= new Intent(HomeActivity.this,BookService.class);
            startActivity(intent);

        } else if (id == R.id.nav_profile) {

            //Going to Profile Page
            Intent intent= new Intent(HomeActivity.this,ProfileActivity.class);
            startActivity(intent);


        } else if (id == R.id.nav_sos) {

            //Going to House Page
            Intent intent= new Intent(HomeActivity.this,SOS.class);
            startActivity(intent);



        }

        else if (id==R.id.nave_service_history){

            //Going to Service History Page
            Intent intent= new Intent(HomeActivity.this,ServiceHistory.class);
            startActivity(intent);


        }
        else if (id==R.id.nav_inbox){

            //Going to Inbox Page
            Intent intent= new Intent(HomeActivity.this,InboxActivity.class);
            startActivity(intent);

        }

        else if (id==R.id.nav_logout){

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.commit();


            DrawerLayout drawerLogout = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawerLogout.isDrawerOpen(GravityCompat.START)) {
                drawerLogout.closeDrawer(GravityCompat.START);
            }


            LogoutAppDialog logoutAppDialog = new LogoutAppDialog();
            logoutAppDialog.show(getSupportFragmentManager(),logoutAppDialog.getTag());

            //Calling dialog
          //  FragmentManager logoutManager = getFragmentManager();
            //LogoutAppDialog logoutAppDialog = new LogoutAppDialog();
            //logoutAppDialog.show(logoutManager, "LOGOUT_DIALOG");






        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Func -  Select Camera
    public void chooseCamera() {


                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, REQUEST_CAMERA_PROFILEIMAGE);


    }
    //Func - Select Gallery
    public void chooseGallery() {


        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(galleryIntent, REQUEST_GALLERY_PROFILEIMAGE);


    }



    //Func - Exiting app
    public void exitApp() {

        //Closing app
        finish();
    }


    //Func -  Logout app
    public void LogoutApp() {

        //Clear session
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        //Calling login activity
        startActivity(new Intent(HomeActivity.this, Register.class));
        finish();

    }

    public void saveRating(String ratingText, int selectedRating,String feedbackInfo) {

        String RegistrationNo=sharedPreferences.getString(Constants.REGISTER_NUMBER,"");

        databseHelper.insertRating(RegistrationNo,selectedRating,ratingText);

        boolean isInternetPresent = connectionDetector.isConnectingToInternet();

        if (isInternetPresent) {

            // call FeedbackService
            callSaveFeedBackService(ratingText,RegistrationNo,feedbackInfo);

        }
        else
        {




            Snackbar snackbar = Snackbar.make(parentLayout, "No internet connection!", Snackbar.LENGTH_LONG);

            snackbar.show();
        }

    }

    private void callSaveFeedBackService(String ratingText,String RegistrationNo,String feedbackInfo) {








        showProgressDialog(getResources().getString(R.string.please_wait));

        FeedBackRequest request = new FeedBackRequest();

        String InfoEndFeed = null;

        if (feedbackInfo.equalsIgnoreCase("Sad")){

            InfoEndFeed="N";

        }
        if (feedbackInfo.equalsIgnoreCase("Very Happy")){
            InfoEndFeed="V";

        }

        if (feedbackInfo.equalsIgnoreCase("Happy")){
            InfoEndFeed="H";
        }

      request.setmRegNumber(RegistrationNo);
      request.setmRating(ratingText);
      request.setmFeedBack(InfoEndFeed);


        builder = getHttpClient();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create()).client(builder.build()).build();
        API gi = retrofit.create(API.class);

        Call<JsonArray> call = (Call<JsonArray>) gi.saveFeedBackDetails(request);

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {

                if (mProgress != null) {
                    mProgress.dismiss();
                }

                //Checking for response code
                if (response != null) {
                    if (response.code() == 200) {

                        for (int i = 0; i < response.body().size(); i++) {

                            JsonObject object = response.body().get(i).getAsJsonObject();

                            Log.e("FeedBack Details", response.body() + "");

                            String message = object.get("Msg").getAsString();
                            Toast.makeText(HomeActivity.this,message,Toast.LENGTH_SHORT).show();
                        }

                            Log.e("Respones",response+"");

                    }

else {
    Toast.makeText(HomeActivity.this,"Something Went Wrong",Toast.LENGTH_SHORT).show();
                    }


                    }



                if (mProgress != null) {
                    mProgress.dismiss();
                }
                //If status code is not 200
                else {
                    Snackbar snackbar = Snackbar
                            .make(parentLayout, "Something went wrong! Try again", Snackbar.LENGTH_LONG);

                    snackbar.show();
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {

                Snackbar snackbar = Snackbar
                        .make(parentLayout, "Something went wrong! Try again", Snackbar.LENGTH_LONG);

                snackbar.show();


            }

        });

    }

    public void displayImage() {
    }

    public void removePicture() {
        chooseImage.setImageResource(R.drawable.ic_user);
    }

    public void viewDialog() {

        LayoutInflater inflater = getLayoutInflater();
        final View alertLayout = inflater.inflate(R.layout.custom_dialog_menu_sevice_list, null);
        TextView bookService,serviceHistory;
        bookService = (TextView) alertLayout.findViewById(R.id.book_service_text);

        serviceHistory = (TextView) alertLayout.findViewById(R.id.service_history_text);

        bookService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(HomeActivity.this,BookService.class);
                startActivity(intent);

            }
        });

        serviceHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(HomeActivity.this,ServiceHistory.class);
                startActivity(intent);
            }
        });


        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        // alert.setTitle("Info");
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);

        alert.setCancelable(true);
        final AlertDialog dialog = alert.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
      //  dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
        dialog.show();
      //  dialog.getWindow().setLayout(500, 800);

    }
}
