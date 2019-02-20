package com.software.thincnext.kawasaki.Dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import com.software.thincnext.kawasaki.Activity.HomeActivity;
import com.software.thincnext.kawasaki.R;

public class LogoutAppDialog  extends BottomSheetDialogFragment implements View.OnClickListener {

    //Declaring views
    Button okClick, cancelClick;


    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog,style);

        //Creating dialog
        // AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //LayoutInflater inflater = getActivity().getLayoutInflater();

        View rootView = View.inflate(getContext(),R.layout.layout_logout_app_dialog, null);



        //Initilising views
        okClick = (Button) rootView.findViewById(R.id.btn_logoutAppDialog_ok);
        cancelClick = (Button) rootView.findViewById(R.id.btn_logoutAppDialog_cancel);

        //Setting onclick listner
        okClick.setOnClickListener(this);
        cancelClick.setOnClickListener(this);

        dialog.setContentView(rootView);
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

