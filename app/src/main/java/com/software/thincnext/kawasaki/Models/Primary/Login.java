package com.software.thincnext.kawasaki.Models.Primary;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Login {

    @SerializedName("PhoneNo")
    @Expose
    private String mMobileNumber;




    public String getmMobileNumber() {
        return mMobileNumber;
    }

    public void setmMobileNumber(String mMobileNumber) {
        this.mMobileNumber = mMobileNumber;
    }
}
