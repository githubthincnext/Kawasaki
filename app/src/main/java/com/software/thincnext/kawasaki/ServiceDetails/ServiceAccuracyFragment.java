package com.software.thincnext.kawasaki.ServiceDetails;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.software.thincnext.kawasaki.R;
import com.software.thincnext.kawasaki.Services.API;
import com.software.thincnext.kawasaki.Services.ConnectionDetector;
import com.software.thincnext.kawasaki.Services.Constants;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
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

public class ServiceAccuracyFragment extends Fragment {


    //Connection detector class
    private ConnectionDetector connectionDetector;

    private LinearLayout progressDialog;
    private LinearLayout errorDisplayAlternate;


    private ImageView errorDisplayIconAlternate;
    private TextView errorDisplayTextAlternate;
    private Button errorDisplayTryClickAlternate;

    @BindView(R.id.listView)
    ListView listView;

    @BindView(R.id.pieChart)
    PieChart mPieChart;

    //Declaring progress dialog
    private ProgressDialog mProgress;

    private JsonArray mOnTime;
    private JsonArray mLate;

    private SharedPreferences sharedPreferences;

    private static OkHttpClient.Builder builder;

    private ServiceHelathAdapter mAdapter;


    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.service_accuracy, container, false);


        ButterKnife.bind(this, view);

        //Initialising connection detector
        connectionDetector = new ConnectionDetector(getContext());

        mOnTime=new JsonArray();

        mLate=new JsonArray();

        sharedPreferences = getActivity().getSharedPreferences(Constants.PREFERENCE_NAME, MODE_PRIVATE);


        //Initialising progress dialog
        mProgress = new ProgressDialog(getContext());


        //Hiding Keyboard
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //Progress Dialog and Error views
        progressDialog = (LinearLayout) view.findViewById(R.id.ll_custom_dialog);
        errorDisplayAlternate = (LinearLayout) view.findViewById(R.id.ll_errorAlternative_layout);
        errorDisplayIconAlternate = (ImageView) view.findViewById(R.id.iv_errorAlternative_errorIcon);
        errorDisplayTextAlternate = (TextView) view.findViewById(R.id.tv_errorAlternative_errorText);
        errorDisplayTryClickAlternate = (Button) view.findViewById(R.id.btn_errorAlternative_errorTryAgain);


        //Hiding views
        errorDisplayAlternate.setVisibility(View.GONE);

        listView.setVisibility(View.GONE);
        progressDialog.setVisibility(View.GONE);


        mAdapter = new ServiceHelathAdapter(getContext());

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
            callServicHealth();

        } else {

            listView.setVisibility(View.GONE);
            progressDialog.setVisibility(View.GONE);
            errorDisplayAlternate.setVisibility(View.VISIBLE);
            errorDisplayIconAlternate.setImageResource(R.drawable.ic_error_network);
            errorDisplayTextAlternate.setText("Slow or no internet connection \n Please check your internet settings");

        }

    }

    private void callServicHealth() {

        String registerNumber = sharedPreferences.getString(Constants.REGISTER_NUMBER, "");

        //progress dialog
        // showProgressDialog(getResources().getString(R.string.please_wait));


        ServiceRequest serviceRequest = new ServiceRequest();
        serviceRequest.setmRegistrationNumber(registerNumber);


        builder = getHttpClient();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create()).client(builder.build()).build();
        API gi = retrofit.create(API.class);


        Call<JsonArray> call = (Call<JsonArray>) gi.getServiceHealth(serviceRequest);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> jsonResponse) {


                //Checking for response code
                if (jsonResponse != null) {
                    if (jsonResponse.code() == 200) {

                        int onTime = 0;
                        int late = 0;

                        for (int i = 0; i < jsonResponse.body().size(); i++) {

                            JsonObject object = jsonResponse.body().get(i).getAsJsonObject();

                            Log.e("Service Health Details", jsonResponse.body() + "");

                            String colorCode=object.get("OnTimeServiceColorCode").getAsString();

                            if (colorCode.equalsIgnoreCase("G")){
                                onTime=onTime+1;

                                Log.e("ON TIME",onTime+"");

                            }

                            else if (colorCode.equalsIgnoreCase("R")){
                                late=late+1;

                                Log.e("LATE COUNT",late+"");

                            }

                            drawPieChart(onTime, late);




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

    private void drawPieChart(int onTime, int late) {
        Description description = new Description();
        description.setText("");
        mPieChart.setRotationEnabled(true);
        mPieChart.setDescription(description);
        mPieChart.setUsePercentValues(false);

        mPieChart.setDrawSliceText(false);
        Legend l = mPieChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(5f);
        l.setYOffset(0f);

        if(onTime == 0 && late == 0)
        {
            Log.e("Values","All Zero");
        }
        else if (onTime == 0)
        {
            Log.e("Values","Ontime Zero");
            setRedPiecChart(late);

        }
        else if (late == 0)
        {
            Log.e("Values","Late Zero");
            setGreenPiecChart(onTime);
        }
        else
        {
            setGreenRedPiecChart(onTime,late);
        }


    }

    private void setGreenRedPiecChart(int onTime, int late) {
        ArrayList<PieEntry> yValues = new ArrayList<>();

        yValues.add(new PieEntry(onTime,"On Time"));
        yValues.add(new PieEntry(late,"Late"));

        int [] color = { Color.rgb(0,255,0),
                Color.rgb(255,0,0)
        };

        PieDataSet dataSet = new PieDataSet(yValues, "");
        dataSet.setSliceSpace(0f);
        dataSet.setValueFormatter(new DecimalRemover(new DecimalFormat("###,###,###")));
        dataSet.setSelectionShift(5f);
        dataSet.setColors(color);
        PieData pieData = new PieData((dataSet));
        pieData.setValueTextSize(10f);
        pieData.setValueTextColor(Color.WHITE);
        mPieChart.setData(pieData);
        mPieChart.invalidate();

        Legend l = mPieChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(5f);
        l.setYOffset(0f);
    }

    private void setGreenPiecChart(int onTime) {
        ArrayList<PieEntry> yValues = new ArrayList<>();

        yValues.add(new PieEntry(onTime,"On Time"));


        int [] color = { Color.rgb(0,255,0)

        };

        PieDataSet dataSet = new PieDataSet(yValues, "");
        dataSet.setSliceSpace(0f);
        dataSet.setValueFormatter(new DecimalRemover(new DecimalFormat("###,###,###")));
        dataSet.setSelectionShift(5f);
        dataSet.setColors(color);
        PieData pieData = new PieData((dataSet));
        pieData.setValueTextSize(10f);
        pieData.setValueTextColor(Color.WHITE);
        mPieChart.setData(pieData);
        mPieChart.invalidate();
        Legend l = mPieChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(5f);
        l.setYOffset(0f);
    }

    private void setRedPiecChart(int late) {

        ArrayList<PieEntry> yValues = new ArrayList<>();


        yValues.add(new PieEntry(late,"Late"));

        int [] color = {
                Color.rgb(255,0,0)
        };

        PieDataSet dataSet = new PieDataSet(yValues, "");
        dataSet.setSliceSpace(0f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(color);
        PieData pieData = new PieData((dataSet));
        dataSet.setValueFormatter(new DecimalRemover(new DecimalFormat("###,###,###")));
        pieData.setValueTextSize(10f);
        pieData.setValueTextColor(Color.WHITE);
        mPieChart.setData(pieData);
        mPieChart.invalidate();

        Legend l = mPieChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(5f);
        l.setYOffset(0f);

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

    private class DecimalRemover implements IValueFormatter
    {
        protected DecimalFormat mFormat;

        public DecimalRemover(DecimalFormat format) {
            this.mFormat = format;
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return mFormat.format(value) +"";
        }
    }
}