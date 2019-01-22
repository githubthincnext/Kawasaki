package com.software.thincnext.Request.BookService;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ServiceBookingRequest {


    @SerializedName("RegistrationNo")
    @Expose
    private String mRegNumber;


    @SerializedName("ServiceDate")
    @Expose
    private String mSerDate;



    @SerializedName("ServiceType")
    @Expose
    private String mSerType;


    @SerializedName("DealerSlno")
    @Expose
    private String mDealerNumber;


    public String getmRegNumber() {
        return mRegNumber;
    }

    public void setmRegNumber(String mRegNumber) {
        this.mRegNumber = mRegNumber;
    }

    public String getmSerDate() {
        return mSerDate;
    }

    public void setmSerDate(String mSerDate) {
        this.mSerDate = mSerDate;
    }

    public String getmSerType() {
        return mSerType;
    }

    public void setmSerType(String mSerType) {
        this.mSerType = mSerType;
    }

    public String getmDealerNumber() {
        return mDealerNumber;
    }

    public void setmDealerNumber(String mDealerNumber) {
        this.mDealerNumber = mDealerNumber;
    }

    public String getmPickupRe() {
        return mPickupRe;
    }

    public void setmPickupRe(String mPickupRe) {
        this.mPickupRe = mPickupRe;
    }

    public String getmStandByVe() {
        return mStandByVe;
    }

    public void setmStandByVe(String mStandByVe) {
        this.mStandByVe = mStandByVe;
    }

    public String getmDoorStep() {
        return mDoorStep;
    }

    public void setmDoorStep(String mDoorStep) {
        this.mDoorStep = mDoorStep;
    }

    @SerializedName("PickupRequired")
    @Expose
    private String mPickupRe;


    @SerializedName("StandByVehicleRequired")
    @Expose
    private String mStandByVe;


    @SerializedName("DoorStepService")
    @Expose
    private String mDoorStep;





}
