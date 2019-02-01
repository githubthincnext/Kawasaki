package com.software.thincnext.kawasaki.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.software.thincnext.kawasaki.R;
import com.software.thincnext.kawasaki.SearchActivity.SearchCity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RemarksActivity extends AppCompatActivity {
    
    @BindView(R.id.remarks)
    EditText mRemarks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remarks);
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
                Intent intent = new Intent(RemarksActivity.this, HomeActivity.class);
                startActivity(intent);
                break;
                
            case R.id.book_service:
                //validate 
                validationDetails();
                break;


        }
    }

    private void validationDetails() {
        String remarksContent=mRemarks.getText().toString();
        if (TextUtils.isEmpty(remarksContent)){
            Toast.makeText(RemarksActivity.this,"Please Enter Remarks ",Toast.LENGTH_SHORT).show();
            return;
        }
        //Calling service to save the remarks
        callServiceToSaveData();
    }

    private void callServiceToSaveData() {
    }
}
