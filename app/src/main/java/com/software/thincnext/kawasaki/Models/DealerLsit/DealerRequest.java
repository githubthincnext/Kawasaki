package com.software.thincnext.kawasaki.Models.DealerLsit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DealerRequest {

    public String getmCityName() {
        return mCityName;
    }

    public void setmCityName(String mCityName) {
        this.mCityName = mCityName;
    }

    @SerializedName("CityName")
    @Expose
    private String mCityName;
}
