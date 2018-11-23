package com.software.thincnext.kawasaki.ServiceDetails;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.software.thincnext.kawasaki.R;
import com.software.thincnext.kawasaki.Services.API;
import com.software.thincnext.kawasaki.Services.ConnectionDetector;
import com.software.thincnext.kawasaki.Services.Constants;
import java.util.concurrent.TimeUnit;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;

public class ServiceHistoryFragment extends Fragment {


    //Connection detector class
    private ConnectionDetector connectionDetector;

    private LinearLayout progressDialog;
    private LinearLayout errorDisplayAlternate;


    private ImageView errorDisplayIconAlternate;
    private TextView errorDisplayTextAlternate;
    private Button errorDisplayTryClickAlternate;

    @BindView(R.id.listView)
    ListView listView;

    //Declaring progress dialog
    private ProgressDialog mProgress;

    private SharedPreferences sharedPreferences;

    private static OkHttpClient.Builder builder;

    private ServiceHistroryAdapter mAdapter;



    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.service_history, container, false);
        ButterKnife.bind(this, view);

        //Initialising connection detector
        connectionDetector = new ConnectionDetector(getContext());

        sharedPreferences = getActivity().getSharedPreferences(Constants.PREFERENCE_NAME, MODE_PRIVATE);


        //Initialising progress dialog
        mProgress = new ProgressDialog(getContext());


        //Hiding Keyboard
       getActivity(). getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //Progress Dialog and Error views
        progressDialog = (LinearLayout) view.findViewById(R.id.ll_custom_dialog);
        errorDisplayAlternate = (LinearLayout)view. findViewById(R.id.ll_errorAlternative_layout);
        errorDisplayIconAlternate = (ImageView) view.findViewById(R.id.iv_errorAlternative_errorIcon);
        errorDisplayTextAlternate = (TextView) view.findViewById(R.id.tv_errorAlternative_errorText);
        errorDisplayTryClickAlternate = (Button) view.findViewById(R.id.btn_errorAlternative_errorTryAgain);


        //Hiding views
       errorDisplayAlternate.setVisibility(View.GONE);

        listView.setVisibility(View.GONE);
        progressDialog.setVisibility(View.GONE);


        mAdapter = new ServiceHistroryAdapter(getContext());

        //getServiceHistoryDetails
        getServiceHistoryDetails();

        listView.setAdapter(mAdapter);

        listView.setDivider(null);


        return view;
    }

    private void getServiceHistoryDetails() {

        //Checking internet connection
        boolean isInternetPresent = connectionDetector.isConnectingToInternet();

        if (isInternetPresent) {
            errorDisplayAlternate.setVisibility(View.GONE);
            progressDialog.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);


            //Calling service
            callServiceHistory();

        } else {

            listView.setVisibility(View.GONE);
            progressDialog.setVisibility(View.GONE);
            errorDisplayAlternate.setVisibility(View.VISIBLE);
            errorDisplayIconAlternate.setImageResource(R.drawable.ic_error_network);
            errorDisplayTextAlternate.setText("Slow or no internet connection \n Please check your internet settings");

        }

    }

    private void callServiceHistory() {

        String registerNumber=sharedPreferences.getString(Constants.REGISTER_NUMBER,"");

        //progress dialog
       // showProgressDialog(getResources().getString(R.string.please_wait));


        ServiceRequest serviceRequest = new ServiceRequest();
        serviceRequest.setmRegistrationNumber(registerNumber);


        builder = getHttpClient();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create()).client(builder.build()).build();
        API gi = retrofit.create(API.class);



        Call<JsonArray> call = (Call<JsonArray>) gi.getServiceHistory(serviceRequest);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> jsonResponse) {


                //Checking for response code
                if (jsonResponse != null) {
                    if (jsonResponse.code() == 200) {
                        for (int i = 0; i < jsonResponse.body().size(); i++) {
                            JsonObject object = jsonResponse.body().get(i).getAsJsonObject();

                            Log.e("Service Details", jsonResponse.body() + "");

                            progressDialog.setVisibility(View.GONE);
                            listView.setVisibility(View.VISIBLE);

                            mAdapter.listUpdate(jsonResponse.body());
                            mAdapter.notifyDataSetChanged();

                        }


                    } else {



                            Toast.makeText(getActivity(), "Something went wrong! Error :" + jsonResponse.code(), Toast.LENGTH_SHORT).show();





                    }
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Toast.makeText(getActivity(), "Something went wrong!  Try again", Toast.LENGTH_SHORT).show();
            }


        });

    }

    private OkHttpClient.Builder getHttpClient() {
        if (builder == null) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder client = new OkHttpClient.Builder();
            client.addInterceptor(loggingInterceptor);
            client.writeTimeout(60000, TimeUnit.MILLISECONDS);
            client.readTimeout(60000, TimeUnit.MILLISECONDS);
            client.connectTimeout(60000, TimeUnit.MILLISECONDS);
            return client;
        }
        return builder;
    }
}