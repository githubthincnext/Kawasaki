package com.software.thincnext.kawasaki;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import com.software.thincnext.kawasaki.ServiceDetails.ServiceAccuracyFragment;
import com.software.thincnext.kawasaki.ServiceDetails.ServiceHistoryFragment;
import com.software.thincnext.kawasaki.ServiceDetails.ViewPageAdapter;
import com.software.thincnext.kawasaki.Services.Constants;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ServiceHistory extends AppCompatActivity {

    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;
    @BindView(R.id.viewpager)
    ViewPager mViewpager;
    private ViewPageAdapter mAdapter;

    private SharedPreferences sharedPreferences;

    private static final String TAG = ServiceHistory.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_history);
        ButterKnife.bind(this);


        //Hiding Keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        sharedPreferences = getSharedPreferences(Constants.PREFERENCE_NAME, MODE_PRIVATE);


        mAdapter = new ViewPageAdapter(getSupportFragmentManager());
        setUpViewPager();
        mTabLayout.setupWithViewPager(mViewpager);

    }

    private void setUpViewPager() {
        Log.d(TAG, "!! Set up view pager function called..");
        mAdapter.addFragment(new ServiceHistoryFragment(), "History");
        mAdapter.addFragment(new ServiceAccuracyFragment(), "Accuracy");
        mViewpager.setAdapter(mAdapter);
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

