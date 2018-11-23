package com.software.thincnext.kawasaki.ApiRequest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OtpRequest {

    @SerializedName("PhoneM")
    @Expose
    private String mPhoneNumber;


    @SerializedName("OTPStatus")
    @Expose
    private String mOtpStatus;


    @SerializedName("OTP")
    @Expose
    private String mOtp;



    public String getmPhoneNumber() {
        return mPhoneNumber;
    }

    public void setmPhoneNumber(String mPhoneNumber) {
        this.mPhoneNumber = mPhoneNumber;
    }

    public String getmOtpStatus() {
        return mOtpStatus;
    }

    public void setmOtpStatus(String mOtpStatus) {
        this.mOtpStatus = mOtpStatus;
    }

    public String getmOtp() {
        return mOtp;
    }

    public void setmOtp(String mOtp) {
        this.mOtp = mOtp;
    }



}
