package com.software.thincnext.kawasaki.Activity;


import android.Manifest;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.software.thincnext.kawasaki.ApiRequest.DashBoardInfo;
import com.software.thincnext.kawasaki.Dialog.ChangePicDialog;
import com.software.thincnext.kawasaki.Dialog.ExitAppDialog;
import com.software.thincnext.kawasaki.Dialog.LogoutAppDialog;
import com.software.thincnext.kawasaki.Inbox.InboxActivity;
import com.software.thincnext.kawasaki.Profile.ProfileActivity;
import com.software.thincnext.kawasaki.R;
import com.software.thincnext.kawasaki.ServiceHistory;
import com.software.thincnext.kawasaki.Services.API;
import com.software.thincnext.kawasaki.Services.ConnectionDetector;
import com.software.thincnext.kawasaki.Services.Constants;
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


public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {



    //capturing from camera and gallery
    public static final int REQUEST_CAMERA_PROFILEIMAGE = 1088;
    public static final int REQUEST_GALLERY_PROFILEIMAGE = 1089;
    private static final int MY_PERMISSIONS_REQUEST_ACCOUNTS = 1;

    private static final String TAG = HomeActivity.class.getSimpleName();

    private SharedPreferences sharedPreferences;




    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @BindView(R.id.book_service)
    LinearLayout mBookService;

    @BindView(R.id.service_type)
    TextView mServiceType;

    @BindView(R.id.constraintLayout)
    ConstraintLayout parentLayout;







    CircleImageView chooseImage;
    TextView HeaderCustomerName;
    TextView HeaderMobNumber;
    FrameLayout frameLayout;


    private static OkHttpClient.Builder builder;


    //Declaring progress dialog
    private ProgressDialog mProgress;

    //internet connection
    private ConnectionDetector connectionDetector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);


        //Initialising progress dialog
        mProgress = new ProgressDialog(this);

        connectionDetector = new ConnectionDetector(this);

        //Hiding Keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        sharedPreferences = getSharedPreferences(Constants.PREFERENCE_NAME, MODE_PRIVATE);


        //call internet connection
        checkInternetConnection();





        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mBookService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(HomeActivity.this,BookService.class);
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


        chooseImage = (CircleImageView) header.findViewById(R.id.imageView);
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


                //Checking permission Marshmallow
                if (Build.VERSION.SDK_INT >= 23) {

                    if (checkPermissionCameraGallery()) {
                        FragmentManager changePicManager = getFragmentManager();
                        ChangePicDialog changePicDialog = new ChangePicDialog();
                        changePicDialog.show(changePicManager, "CHANGEPIC_DIALOG");

                    } else {

                        //Request permission
                        requestPermissionCameraGallery();
                    }
                } else {

                    FragmentManager changePicManager = getFragmentManager();
                    ChangePicDialog changePicDialog = new ChangePicDialog();
                    changePicDialog.show(changePicManager, "CHANGEPIC_DIALOG");


                }
            }
        });
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

    private void callWebserviceForHeaderType() {

        //progress dialog
        showProgressDialog(getResources().getString(R.string.please_wait));

        String RegistrationNo=sharedPreferences.getString(Constants.REGISTER_NUMBER,"");

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


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCOUNTS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    FragmentManager changePicManager = getFragmentManager();
                    ChangePicDialog changePicDialog = new ChangePicDialog();
                    changePicDialog.show(changePicManager, "CHANGEPIC_DIALOG");

                } else {


                }
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

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



    @OnClick({R.id.sos_list,R.id.book_service_layout,R.id.home_inbox,R.id.home_profile,R.id.service_history})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sos_list:
                Intent intent0=new Intent(HomeActivity.this,SOS.class);
                startActivity(intent0);
                break;

            case R.id.book_service_layout:

                Intent intent1=new Intent(HomeActivity.this,BookService.class);
                startActivity(intent1);
                break;

            case R.id.home_inbox:

                Intent intent2=new Intent(HomeActivity.this,InboxActivity.class);
                startActivity(intent2);
                break;



            case R.id.home_profile:

                Intent intent3=new Intent(HomeActivity.this,ProfileActivity.class);
                startActivity(intent3);
                break;


            case R.id.service_history:
                Intent intent4=new Intent(HomeActivity.this,ServiceHistory.class);
                startActivity(intent4);
                break;


                }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //Calling dialog
            FragmentManager exitManager = getFragmentManager();
            ExitAppDialog exitAppDialog = new ExitAppDialog();
            exitAppDialog.show(exitManager, "EXIT_DIALOG");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
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
    }


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



        } else if (id==R.id.nav_inbox){

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

            //Calling dialog
            FragmentManager logoutManager = getFragmentManager();
            LogoutAppDialog logoutAppDialog = new LogoutAppDialog();
            logoutAppDialog.show(logoutManager, "LOGOUT_DIALOG");






        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Func -  Select Camera
    public void chooseCamera() {
        //Checking permission Marshmallow
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermissionCameraGallery()) {

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, REQUEST_CAMERA_PROFILEIMAGE);

            } else {
                //Request permission
                requestPermissionCameraGallery();

            }
        } else {


            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, REQUEST_CAMERA_PROFILEIMAGE);
        }//zero can be replaced with any action code
    }
    //Func - Select Gallery
    public void chooseGallery() {
        //Checking permission Marshmallow
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermissionCameraGallery()) {

                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(galleryIntent, REQUEST_GALLERY_PROFILEIMAGE);


            } else {

                //Request permission
                requestPermissionCameraGallery();
            }
        } else {

            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(galleryIntent, REQUEST_GALLERY_PROFILEIMAGE);


        }
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
}
