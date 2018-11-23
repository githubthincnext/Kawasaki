package com.software.thincnext.kawasaki.ServiceDetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ServiceRequest {

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
