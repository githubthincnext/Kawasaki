package com.software.thincnext.kawasaki.ApiRequest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DashBoardInfo {




    @SerializedName("RegistrationNo")
    @Expose
    private String mRegisterNumber;



    public String getmRegisterNumber() {
        return mRegisterNumber;
    }

    public void setmRegisterNumber(String mRegisterNumber) {
        this.mRegisterNumber = mRegisterNumber;
    }


}
