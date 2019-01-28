package com.software.thincnext.kawasaki.Dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.software.thincnext.kawasaki.Activity.HomeActivity;
import com.software.thincnext.kawasaki.R;

public class FeedbackDialog  extends DialogFragment implements View.OnClickListener {

    private LinearLayout rateOneLayout, rateTwoLayout, rateThreeLayout, rateFourLayout, rateFiveLayout;
    private ImageView rateOneIcon, rateTwoIcon, rateThreeIcon, rateFourIcon, rateFiveIcon;
    private TextView rateOneText, rateTwoText, rateThreeText, rateFourText, rateFiveText;

    private EditText rating;
    private Button submit;


    private int selectedRating = 0;

    public FeedbackDialog() {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //Creating dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View rootView = inflater.inflate(R.layout.layout_feedback_dialog, null);

        rateOneLayout = rootView.findViewById(R.id.ll_feedBackDialog_rateOne);
        rateOneIcon = rootView.findViewById(R.id.iv_feedBackDialog_rateOne);
        rateOneText = rootView.findViewById(R.id.tv_feedBackDialog_rateOne);

        rateTwoLayout = rootView.findViewById(R.id.ll_feedBackDialog_rateTwo);
        rateTwoIcon = rootView.findViewById(R.id.iv_feedBackDialog_rateTwo);
        rateTwoText = rootView.findViewById(R.id.tv_feedBackDialog_rateTwo);

        rateThreeLayout = rootView.findViewById(R.id.ll_feedBackDialog_rateThree);
        rateThreeIcon = rootView.findViewById(R.id.iv_feedBackDialog_rateThree);
        rateThreeText = rootView.findViewById(R.id.tv_feedBackDialog_rateThree);

        rateFourLayout = rootView.findViewById(R.id.ll_feedBackDialog_rateFour);
        rateFourIcon = rootView.findViewById(R.id.iv_feedBackDialog_rateFour);
        rateFourText = rootView.findViewById(R.id.tv_feedBackDialog_rateFour);

        rateFiveLayout = rootView.findViewById(R.id.ll_feedBackDialog_rateFive);
        rateFiveIcon = rootView.findViewById(R.id.iv_feedBackDialog_rateFive);
        rateFiveText = rootView.findViewById(R.id.tv_feedBackDialog_rateFive);

        rating = rootView.findViewById(R.id.et_feedBackDialog_rateBox);
        submit = rootView.findViewById(R.id.btn_feedBackDialog_submit);


        //Setting onclick listner
        rateOneLayout.setOnClickListener(this);
        rateTwoLayout.setOnClickListener(this);
        rateThreeLayout.setOnClickListener(this);
        rateFourLayout.setOnClickListener(this);
        rateFiveLayout.setOnClickListener(this);
        submit.setOnClickListener(this);


        builder.setView(rootView);
        setCancelable(false);
        return builder.create();
    }

    @Override
    public void onClick(View v) {


        switch (v.getId()) {

            case R.id.ll_feedBackDialog_rateOne:

                //Setting values
                rateOneIcon.setImageResource(R.drawable.ic_rate_one_yellow);
                rateOneText.setTextColor(getResources().getColor(R.color.colorRed));

                rateTwoIcon.setImageResource(R.drawable.ic_rate_two_grey);
                rateTwoText.setTextColor(getResources().getColor(R.color.colorGrey));

                rateThreeIcon.setImageResource(R.drawable.ic_rate_three_grey);
                rateThreeText.setTextColor(getResources().getColor(R.color.colorGrey));

                rateFourIcon.setImageResource(R.drawable.ic_rate_four_grey);
                rateFourText.setTextColor(getResources().getColor(R.color.colorGrey));

                rateFiveIcon.setImageResource(R.drawable.ic_rate_five_grey);
                rateFiveText.setTextColor(getResources().getColor(R.color.colorGrey));

                selectedRating = 1;


                break;

            case R.id.ll_feedBackDialog_rateTwo:

                //Setting values
                rateTwoIcon.setImageResource(R.drawable.ic_rate_two_yellow);
                rateTwoText.setTextColor(getResources().getColor(R.color.colorOrange));

                rateOneIcon.setImageResource(R.drawable.ic_rate_one_grey);
                rateOneText.setTextColor(getResources().getColor(R.color.colorGrey));

                rateThreeIcon.setImageResource(R.drawable.ic_rate_three_grey);
                rateThreeText.setTextColor(getResources().getColor(R.color.colorGrey));

                rateFourIcon.setImageResource(R.drawable.ic_rate_four_grey);
                rateFourText.setTextColor(getResources().getColor(R.color.colorGrey));

                rateFiveIcon.setImageResource(R.drawable.ic_rate_five_grey);
                rateFiveText.setTextColor(getResources().getColor(R.color.colorGrey));

                selectedRating = 2;

                break;

            case R.id.ll_feedBackDialog_rateThree:

                //Setting values
                rateThreeIcon.setImageResource(R.drawable.ic_rate_three_yellow);
                rateThreeText.setTextColor(getResources().getColor(R.color.colorYellow));

                rateOneIcon.setImageResource(R.drawable.ic_rate_one_grey);
                rateOneText.setTextColor(getResources().getColor(R.color.colorGrey));

                rateTwoIcon.setImageResource(R.drawable.ic_rate_two_grey);
                rateTwoText.setTextColor(getResources().getColor(R.color.colorGrey));

                rateFourIcon.setImageResource(R.drawable.ic_rate_four_grey);
                rateFourText.setTextColor(getResources().getColor(R.color.colorGrey));

                rateFiveIcon.setImageResource(R.drawable.ic_rate_five_grey);
                rateFiveText.setTextColor(getResources().getColor(R.color.colorGrey));

                selectedRating = 3;

                break;

            case R.id.ll_feedBackDialog_rateFour:

                //Setting values
                rateFourIcon.setImageResource(R.drawable.ic_rate_four_yellow);
                rateFourText.setTextColor(getResources().getColor(R.color.colorGreen));

                rateOneIcon.setImageResource(R.drawable.ic_rate_one_grey);
                rateOneText.setTextColor(getResources().getColor(R.color.colorGrey));

                rateTwoIcon.setImageResource(R.drawable.ic_rate_two_grey);
                rateTwoText.setTextColor(getResources().getColor(R.color.colorGrey));

                rateThreeIcon.setImageResource(R.drawable.ic_rate_three_grey);
                rateThreeText.setTextColor(getResources().getColor(R.color.colorGrey));

                rateFiveIcon.setImageResource(R.drawable.ic_rate_five_grey);
                rateFiveText.setTextColor(getResources().getColor(R.color.colorGrey));

                selectedRating = 4;

                break;

            case R.id.ll_feedBackDialog_rateFive:

                //Setting values
                rateFiveIcon.setImageResource(R.drawable.ic_rate_five_yellow);
                rateFiveText.setTextColor(getResources().getColor(R.color.colorDarkGreen));

                rateOneIcon.setImageResource(R.drawable.ic_rate_one_grey);
                rateOneText.setTextColor(getResources().getColor(R.color.colorGrey));

                rateTwoIcon.setImageResource(R.drawable.ic_rate_two_grey);
                rateTwoText.setTextColor(getResources().getColor(R.color.colorGrey));

                rateThreeIcon.setImageResource(R.drawable.ic_rate_three_grey);
                rateThreeText.setTextColor(getResources().getColor(R.color.colorGrey));

                rateFourIcon.setImageResource(R.drawable.ic_rate_four_grey);
                rateFourText.setTextColor(getResources().getColor(R.color.colorGrey));

                selectedRating = 5;

                break;

            case R.id.btn_feedBackDialog_submit:

                String ratingText = rating.getText().toString().trim();

                if (selectedRating == 0) {
                    Toast.makeText(getActivity(), "Select rating", Toast.LENGTH_SHORT).show();
                } else {

                    ((HomeActivity) getActivity()).saveRating(ratingText, selectedRating);

                    dismiss();
                }

                break;


        }


    }


}