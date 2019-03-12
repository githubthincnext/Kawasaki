package com.software.thincnext.kawasaki.ApiRequest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CancelServiceRequest {

    @SerializedName("RegistrationNo")
    @Expose
    private String mRegisterNumber;


    public String getmRegisterNumber() {
        return mRegisterNumber;
    }

    public void setmRegisterNumber(String mRegisterNumber) {
        this.mRegisterNumber = mRegisterNumber;
    }

    public String getmCancelationReason() {
        return mCancelationReason;
    }

    public void setmCancelationReason(String mCancelationReason) {
        this.mCancelationReason = mCancelationReason;
    }

    @SerializedName("CancelReason")
    @Expose
    private String mCancelationReason;


}
