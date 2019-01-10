package com.software.thincnext.kawasaki.Services;



import com.google.gson.JsonArray;
import com.software.thincnext.kawasaki.ApiRequest.DashBoardInfo;
import com.software.thincnext.kawasaki.ApiRequest.OtpRequest;
import com.software.thincnext.kawasaki.Models.DealerLsit.DealerRequest;
import com.software.thincnext.kawasaki.Models.Primary.Login;
import com.software.thincnext.kawasaki.Models.SosRequest.SOSRequest;
import com.software.thincnext.kawasaki.ServiceDetails.ServiceRequest;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;



public interface API {

    @Headers({"Content-Type: application/json"})

   //To receive OTP
    @POST("CustomerIdentifier")
    Call<JsonArray> login (@Body Login login);


    // To verify OTP
    @POST("OTPVerification")
    Call<JsonArray> VerifyOtp (@Body OtpRequest otpRequest);



    // To Get DashBoardInfo
    @POST("GetDashboardInfo")
    Call<JsonArray> ServiceType (@Body DashBoardInfo dashBoardInfo);


    //send otp details
    @POST("Otp/sendOpt")
    Call<ResponseBody> sendOtp(@Query("sNumber") String mNumber);


    //Get Service History Details
    @POST("GetServiceHistory")
    Call<JsonArray> getServiceHistory (@Body ServiceRequest serviceRequest);

    //Get SOS Details
    @POST("LastServiceDealerAndSoldDealer")
    Call<JsonArray> getSosDetails(@Body SOSRequest sosRequest);


    //Get Service Health Details
    @POST("GetServiceHealth")
    Call<JsonArray> getServiceHealth(@Body ServiceRequest serviceRequest);


    //getDealerList
    @POST("GetDealerList")
    Call<JsonArray> getDealerList(@Body DealerRequest dealerRequest);




    //verify otp
  /*  @GET("Otp/verify")
    Call<OtpOutPut> verifyOtp(@Query("otp") String mOtp);*/


}
