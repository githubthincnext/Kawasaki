package com.software.thincnext.kawasaki.DisplayImage;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.software.thincnext.kawasaki.Activity.HomeActivity;
import com.software.thincnext.kawasaki.R;
import com.software.thincnext.kawasaki.Services.Constants;

import java.io.FileInputStream;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DisplaySelectedImage extends AppCompatActivity {

    @BindView(R.id.imageView)
    ImageView imageView;

    @BindView(R.id.button)
    ImageView ok;


    //Sharedpreferences
    private SharedPreferences sharedPreferences;

    Bitmap bmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_selected_image);
        ButterKnife.bind(this);
        Bitmap bmp = null;
        String filename = getIntent().getStringExtra("selectedBitmap");
        try {
            FileInputStream is = this.openFileInput(filename);
            bmp = BitmapFactory.decodeStream(is);
            imageView.setImageBitmap(bmp);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }






        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


             /*   final ImageView imageView = (ImageView) view.findViewById(R.id.image);
                final BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
                final Bitmap yourBitmap = bitmapDrawable.getBitmap();



                ByteArrayOutputStream bStream = new ByteArrayOutputStream();
                yourBitmap.compress(Bitmap.CompressFormat.PNG, 100, bStream);
                byte[] byteArray = bStream.toByteArray(); */


                Intent returnIntent = new Intent();
              //  returnIntent.putExtra("returnedImage",byteArray);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });


        //  getSupportActionBar().setDisplayShowHomeEnabled(false);

    }

}
