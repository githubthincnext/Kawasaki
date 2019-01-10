package com.software.thincnext.kawasaki.Dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.software.thincnext.kawasaki.Activity.HomeActivity;
import com.software.thincnext.kawasaki.R;
import com.software.thincnext.kawasaki.Services.Constants;

import static android.content.Context.MODE_PRIVATE;

public class EditProfileDialog extends DialogFragment implements View.OnClickListener {


    //Declaring views
    EditText Mname,Mphonenumber,Memail;

    public EditProfileDialog() {
    }



    //Intialisation sharedpefrence
    private SharedPreferences sharedPreferences;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //Creating Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //Inflating view
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.edit_profile_logout_app_dialog, null);



        Mname = (EditText) rootView.findViewById(R.id.name);
        Mphonenumber = (EditText) rootView.findViewById(R.id.mobile_number);
        Memail=(EditText)rootView.findViewById(R.id.email);

        sharedPreferences = getContext().getSharedPreferences(Constants.PREFERENCE_NAME, MODE_PRIVATE);

        String name = sharedPreferences.getString(Constants.CUSTOMER_NAME, null);
        String mobileNumber = sharedPreferences.getString(Constants.MOBILE_NUMBER, null);
        String email=sharedPreferences.getString(Constants.EMAIL,null);

        if (name != null) {
            Mname.setText(name);
        }

        if (mobileNumber != null) {
            Mphonenumber.setText(mobileNumber);
        }

        if (email==null){
            Memail.setText(R.string.no_email);
        }
        else {
            Memail.setText(email);

        }


        builder.setView(rootView);
        return builder.create();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {


        switch (v.getId()) {

            case R.id.update:

              validationAndUpdate();

                break;

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void validationAndUpdate() {

        String mobNumber=Mphonenumber.getText().toString();

        if (mobNumber.length() <10 || mobNumber.length() >10){
            Toast.makeText(getContext(),R.string.enter_10_digits_mob_number,Toast.LENGTH_SHORT).show();
        }
    }
}

