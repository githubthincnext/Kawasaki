package com.software.thincnext.kawasaki.Models.SosRequest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SOSRequest {

    public String getmRegistrationNumber() {
        return mRegistrationNumber;
    }

    public void setmRegistrationNumber(String mRegistrationNumber) {
        this.mRegistrationNumber = mRegistrationNumber;
    }

    @SerializedName("RegistrationNo")
    @Expose
    private String mRegistrationNumber;
}
