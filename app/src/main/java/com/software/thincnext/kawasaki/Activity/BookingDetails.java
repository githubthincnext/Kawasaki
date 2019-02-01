package com.software.thincnext.kawasaki.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.software.thincnext.kawasaki.R;
import butterknife.ButterKnife;
import butterknife.OnClick;

  public class BookingDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_details);
        ButterKnife.bind(this);

}



    @OnClick({ R.id.back_button,R.id.home,R.id.book_service})
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




        }
    }



}