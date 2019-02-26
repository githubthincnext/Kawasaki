package com.software.thincnext.kawasaki.ApiRequest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FeedBackRequest {

    @SerializedName("RegistrationNo")
    @Expose
    private String mRegNumber;


    @SerializedName("Rating")
    @Expose
    private String mRating;


    public String getmRegNumber() {
        return mRegNumber;
    }

    public void setmRegNumber(String mRegNumber) {
        this.mRegNumber = mRegNumber;
    }

    public String getmRating() {
        return mRating;
    }

    public void setmRating(String mRating) {
        this.mRating = mRating;
    }

    public String getmFeedBack() {
        return mFeedBack;
    }

    public void setmFeedBack(String mFeedBack) {
        this.mFeedBack = mFeedBack;
    }

    @SerializedName("Feedback")
    @Expose
    private String mFeedBack;


}
