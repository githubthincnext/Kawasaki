package com.software.thincnext.kawasaki.Profile;

import android.Manifest;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.software.thincnext.kawasaki.Activity.HomeActivity;
import com.software.thincnext.kawasaki.Dialog.ChangePicDialog;
import com.software.thincnext.kawasaki.Dialog.EditProfileDialog;
import com.software.thincnext.kawasaki.Dialog.ProfileChangePic;
import com.software.thincnext.kawasaki.Models.Primary.Login;
import com.software.thincnext.kawasaki.R;
import com.software.thincnext.kawasaki.Services.API;
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

public class ProfileActivity extends AppCompatActivity {

//    @BindView(R.id.recycler_view)
  //  RecyclerView recyclerView;


    private ProfileAdapter mAdapter;

    @BindView(R.id.name)
    TextView mName;

    @BindView(R.id.mobile_number)
    TextView mMobileNumber;

    @BindView(R.id.email)
    TextView mEmail;

    @BindView(R.id.imageView_one)
    ImageView displayImage;

    @BindView(R.id.register_number)
    TextView mRegisterdNumber;

    private List<Bike> bikeList = new ArrayList<>();


    //Intialisation sharedpefrence
    private SharedPreferences sharedPreferences;

    private static OkHttpClient.Builder builder;


    //Declaring progress dialog
    private ProgressDialog mProgress;

    CircleImageView chooseImage;


    //capturing from camera and gallery
    public static final int REQUEST_CAMERA_PROFILEIMAGE = 1088;
    public static final int REQUEST_GALLERY_PROFILEIMAGE = 1089;
    private static final int MY_PERMISSIONS_REQUEST_ACCOUNTS = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        //Hiding Keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        sharedPreferences = getSharedPreferences(Constants.PREFERENCE_NAME, MODE_PRIVATE);

        String name = sharedPreferences.getString(Constants.CUSTOMER_NAME, null);
        String mobileNumber = sharedPreferences.getString(Constants.MOBILE_NUMBER, null);
        String email=sharedPreferences.getString(Constants.EMAIL,null);
        String registredNumber=sharedPreferences.getString(Constants.REGISTER_NUMBER,null);

        chooseImage = (CircleImageView)findViewById(R.id.imageViewSelect);

        if (name != null) {
            mName.setText(name);
        }

        if (mobileNumber != null){
            mMobileNumber.setText(mobileNumber);
        }

        if (email==null){
            mEmail.setText(R.string.no_email);
        }
        else {
            mEmail.setText(email);
        }

        if (registredNumber!=null){
            mRegisterdNumber.setText(registredNumber);
        }

        //Initialising progress dialog
        mProgress = new ProgressDialog(this);

        // call primary bike service
        //callPrimaryBikeService();

    //   mAdapter = new ProfileAdapter(bikeList);

      //  mAdapter = new ProfileAdapter(this,ProfileActivity.this);


        //Recyclerview
      /*  recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManagerTopic = new LinearLayoutManager(ProfileActivity.this);
        linearLayoutManagerTopic.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManagerTopic);
        recyclerView.setAdapter(mAdapter); */



       // addDataToRecylerView();
        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                    ProfileChangePic changePicDialog = new ProfileChangePic();
                    changePicDialog.show(getSupportFragmentManager(),changePicDialog.getTag());

            }
        });


    }



    //Func -  Select Camera
    public void chooseCamera() {



                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, REQUEST_CAMERA_PROFILEIMAGE);



    }
    //Func - Select Gallery
    public void chooseGallery() {
        //Checking permission Marshmallow
        if (Build.VERSION.SDK_INT >= 23) {

                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(galleryIntent, REQUEST_GALLERY_PROFILEIMAGE);

        }

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





    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            if (requestCode == REQUEST_CAMERA_PROFILEIMAGE) {

                Bundle bundle = data.getExtras();

                Bitmap capturedBitmap = (Bitmap) data.getExtras().get("data");
                displayImage.setImageBitmap(capturedBitmap);

            }
            else if (requestCode == REQUEST_GALLERY_PROFILEIMAGE) {

                try {
                    final Bitmap selectedBitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());

                    displayImage.setImageBitmap(selectedBitmap);

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

    private void callPrimaryBikeService() {

        String mobNumber=sharedPreferences.getString(Constants.MOBILE_NUMBER,"");
        //progress dialog
        showProgressDialog(getResources().getString(R.string.please_wait));


        Login request = new Login();
        request.setmMobileNumber(mobNumber);


        builder = getHttpClient();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create()).client(builder.build()).build();
        API gi = retrofit.create(API.class);



        Call<JsonArray> call = (Call<JsonArray>) gi.login(request);
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

                            String regsistredNumber =object.get("RegistrationNo").getAsString();

                            mRegisterdNumber.setText(regsistredNumber);

                            Log.e("Primary Bike Details", jsonResponse.body() + "");

                         //   mAdapter.listUpdate(jsonResponse.body());
                           // mAdapter.notifyDataSetChanged();

                        }


                    } else {

                        if (mProgress != null) {
                            mProgress.dismiss();
                        } else {


                            if (mProgress != null) {
                                mProgress.dismiss();
                            }
                            Toast.makeText(ProfileActivity.this, "Something went wrong! Error :" + jsonResponse.code(), Toast.LENGTH_SHORT).show();


                        }


                    }
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Something went wrong!  Try again", Toast.LENGTH_SHORT).show();
            }


        });

    }

    private void showProgressDialog(String string) {

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



    private void addDataToRecylerView() {

        Bike bike = new Bike("KA 09 4535",R.drawable.bike_image);
        bikeList.add(bike);

        bike = new Bike("KA 09 4535",R.drawable.bike_image);
        bikeList.add(bike);

        bike = new Bike("KA 09 4535",R.drawable.bike_image);
        bikeList.add(bike);

        bike = new Bike("KA 09 4535",R.drawable.bike_image);
        bikeList.add(bike);

        bike = new Bike("KA 09 4535",R.drawable.bike_image);
        bikeList.add(bike);

        bike = new Bike("KA 09 4535",R.drawable.bike_image);
        bikeList.add(bike);

        mAdapter.notifyDataSetChanged();



    }





    @OnClick({ R.id.back_button})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_button:
                finish();
                break;



        }
    }

    private void editProfileDetails() {

        //Calling dialog
        FragmentManager logoutManager = getFragmentManager();
        EditProfileDialog editProfileDialog = new EditProfileDialog();
        editProfileDialog.show(logoutManager, "EDIT_PROFILE_DIALOG");

    }

    public void removeProfilePic() {

        displayImage.setImageResource(R.drawable.ic_user);
    }

    public void removePicture() {
    }
}
