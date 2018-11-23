
package com.software.thincnext.kawasaki.Models.DealerLsit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DealerListResponse {

    @SerializedName("DealerSlno")
    @Expose
    private String dealerSlno;
    @SerializedName("DealerName")
    @Expose
    private String dealerName;
    @SerializedName("Message")
    @Expose
    private String message;



    public String getDealerSlno() {
        return dealerSlno;
    }

    public void setDealerSlno(String dealerSlno) {
        this.dealerSlno = dealerSlno;
    }

    public String getDealerName() {
        return dealerName;
    }

    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
