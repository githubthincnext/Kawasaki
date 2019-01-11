package com.software.thincnext.kawasaki.SearchActivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.software.thincnext.kawasaki.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchCity extends AppCompatActivity {


    @BindView(R.id.cityList)
    RecyclerView mCityList;

    @BindView(R.id.search_bar)
    EditText mSearchBar;


    private CitylistAdapter citylistAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_city);
        ButterKnife.bind(this);

        //Hiding Keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        mCityList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mCityList.setLayoutManager(linearLayoutManager);

        //Set cityList Data
        setCityList();

        //Search City
        mSearchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (citylistAdapter!= null)
                {

                    citylistAdapter.getFilter().filter(charSequence);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });






    }

    private void setCityList() {

        ArrayList<CityItem> cityItems = new ArrayList<>();

        CityItem cityItem1 = new CityItem();
        cityItem1.setrId(1);
        cityItem1.setrName("Hyderabad");
        cityItems.add(cityItem1);


        CityItem cityItem2 = new CityItem();
        cityItem2.setrId(2);
        cityItem2.setrName("Mumbai");
        cityItems.add(cityItem2);


        CityItem cityItem3 = new CityItem();
        cityItem3.setrId(3);
        cityItem3.setrName("Delhi");
        cityItems.add(cityItem3);


        CityItem cityItem4 = new CityItem();
        cityItem4.setrId(4);
        cityItem4.setrName("Ahmedabad");
        cityItems.add(cityItem4);

        CityItem cityItem5 = new CityItem();
        cityItem5.setrId(5);
        cityItem5.setrName("Chennai");
        cityItems.add(cityItem5);

        CityItem cityItem6 = new CityItem();
        cityItem6.setrId(6);
        cityItem6.setrName("Kolkata");
        cityItems.add(cityItem6);

        CityItem cityItem7 = new CityItem();
        cityItem7.setrId(7);
        cityItem7.setrName("Surat");
        cityItems.add(cityItem7);

        CityItem cityItem8 = new CityItem();
        cityItem8.setrId(8);
        cityItem8.setrName("Pune");
        cityItems.add(cityItem8);


        CityItem cityItem9 = new CityItem();
        cityItem9.setrId(9);
        cityItem9.setrName("Jaipur");
        cityItems.add(cityItem9);


        CityItem cityItem10 = new CityItem();
        cityItem10.setrId(10);
        cityItem10.setrName("Lucknow");
        cityItems.add(cityItem10);


        CityItem cityItem11 = new CityItem();
        cityItem11.setrId(11);
        cityItem11.setrName("Nagpur");
        cityItems.add(cityItem11);

        citylistAdapter = new CitylistAdapter(SearchCity.this, cityItems);
        mCityList.setAdapter(citylistAdapter);
        citylistAdapter.notifyDataSetChanged();


    }

    @OnClick({ R.id.back_button})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_button:
                finish();
                break;

        }
    }

}
