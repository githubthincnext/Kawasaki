package com.software.thincnext.kawasaki.Dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import com.software.thincnext.kawasaki.Activity.HomeActivity;
import com.software.thincnext.kawasaki.R;

public class LogoutAppDialog  extends DialogFragment implements View.OnClickListener {

    //Declaring views
    Button okClick, cancelClick;


    public LogoutAppDialog() {
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //Creating Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //Inflating view
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.layout_logout_app_dialog, null);

        //Initilising views
        okClick = (Button) rootView.findViewById(R.id.btn_logoutAppDialog_ok);
        cancelClick = (Button) rootView.findViewById(R.id.btn_logoutAppDialog_cancel);

        //Setting onclick listner
        okClick.setOnClickListener(this);
        cancelClick.setOnClickListener(this);

        builder.setView(rootView);
        return builder.create();
    }

    @Override
    public void onClick(View v) {


        switch (v.getId()) {

            case R.id.btn_logoutAppDialog_ok:

                //Dismiss dialog
                dismiss();
                //Calling exit func in Home
                ((HomeActivity) getActivity()).LogoutApp();

                break;

            case R.id.btn_logoutAppDialog_cancel:

                //Dismiss dialog
                dismiss();
                break;
        }
    }
}

