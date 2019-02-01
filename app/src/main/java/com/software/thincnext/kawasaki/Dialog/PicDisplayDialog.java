package com.software.thincnext.kawasaki.Dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.software.thincnext.kawasaki.Activity.HomeActivity;
import com.software.thincnext.kawasaki.R;

public class PicDisplayDialog extends DialogFragment implements View.OnClickListener {

    //Declaring views
    private LinearLayout cameraClick, galleryClick, removeClick;
    private ImageView closeClick;

    public PicDisplayDialog() {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //Creating dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View rootView = inflater.inflate(R.layout.disply_pic_layout, null);



        builder.setView(rootView);
        return builder.create();
    }

    @Override
    public void onClick(View v) {


        switch (v.getId()) {


            case R.id.imageView:

                //Calling choose camera
                ((HomeActivity) getActivity()).displayImage();




        }


    }


}