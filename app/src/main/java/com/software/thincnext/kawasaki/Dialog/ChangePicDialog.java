package com.software.thincnext.kawasaki.Dialog;

import android.app.Dialog;

import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.software.thincnext.kawasaki.Activity.HomeActivity;
import com.software.thincnext.kawasaki.R;

public class ChangePicDialog extends BottomSheetDialogFragment implements View.OnClickListener {

    //Declaring views
    private LinearLayout cameraClick, galleryClick, removeClick;
    private ImageView closeClick;



    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog,style);

        //Creating dialog
       // AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //LayoutInflater inflater = getActivity().getLayoutInflater();

        View rootView = View.inflate(getContext(),R.layout.layout_change_pic_dialog, null);

        //Initialising views
        closeClick = (ImageView) rootView.findViewById(R.id.iv_changePicDialog_close);
        cameraClick = (LinearLayout) rootView.findViewById(R.id.ll_changePicDialog_camera);
        galleryClick = (LinearLayout) rootView.findViewById(R.id.ll_changePicDialog_gallery);

        removeClick=(LinearLayout)rootView.findViewById(R.id.ll_changePicDialog_remove);


        //Setting onclick listner
        closeClick.setOnClickListener(this);
        cameraClick.setOnClickListener(this);
        galleryClick.setOnClickListener(this);
        removeClick.setOnClickListener(this);



        dialog.setContentView(rootView);

    }

    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.iv_changePicDialog_close:

                dismiss();

                break;

            case R.id.ll_changePicDialog_camera:

                //Calling choose camera
                ((HomeActivity) getActivity()).chooseCamera();

                dismiss();

                break;

            case R.id.ll_changePicDialog_gallery:

                //Calling choose gallery
                ((HomeActivity) getActivity()).chooseGallery();

                dismiss();

                break;
            case R.id.ll_changePicDialog_remove:

                //Calling  remove pic
                ((HomeActivity) getActivity()).removePicture();

                dismiss();

                break;


        }


    }


}