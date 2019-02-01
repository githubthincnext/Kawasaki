package com.software.thincnext.kawasaki.DisplayImage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.software.thincnext.kawasaki.Activity.HomeActivity;
import com.software.thincnext.kawasaki.R;
import com.software.thincnext.kawasaki.Services.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DisplaySelectedImage extends AppCompatActivity {

    @BindView(R.id.imageView)
    ImageView imageView;

    @BindView(R.id.button)
    Button ok;


    //Sharedpreferences
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_selected_image);
        ButterKnife.bind(this);



      //  imageView.setImageBitmap(otp);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(DisplaySelectedImage.this, HomeActivity.class);
                finish();
            }
        });


      //  getSupportActionBar().setDisplayShowHomeEnabled(false);

    }

}
