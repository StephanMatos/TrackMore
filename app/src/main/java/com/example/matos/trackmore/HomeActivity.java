package com.example.matos.trackmore;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity{

    private ViewPager mSlideViewPager;
    private LinearLayout mDotLayout;
    private SliderAdapter sliderAdapter;
    private Button mNextActivityBtn;
    private int mCurrentPage;
    private TextView[] mdots;
    private ProgressDialog progress;
    private boolean isConnected = false;
    private static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mContext = this;

        mSlideViewPager = findViewById(R.id.SlideView);
        mDotLayout = findViewById(R.id.dotsLayout);

        mNextActivityBtn = findViewById(R.id.nxtActivityBnt);

        sliderAdapter = new SliderAdapter(this);

        mSlideViewPager.setAdapter(sliderAdapter);

        addDotsIndicator(0);

        mSlideViewPager.addOnPageChangeListener(dotListner );

        mNextActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mCurrentPage == 0) {
                    try {
                        IndividualActivity();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else if (mCurrentPage == 1) {
                    GroupActivity();
                } else {
                     SportActivity();
                }

            }
        });


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1
                );
            }
        }

    }



    public void IndividualActivity() throws InterruptedException {
        Intent intent =  new Intent(this, trackingIndividualActivity.class);
        startActivity(intent);
    }

    public void GroupActivity() {
        Intent intent =  new Intent(this, trackingGroupActivity.class);
        startActivity(intent);
    }

    public void SportActivity() {
        Intent intent =  new Intent(this, trackingSportActivity.class);
        startActivity(intent);
    }

    public void addDotsIndicator(int position) {
        mdots = new TextView[3];
        mDotLayout.removeAllViews();

        for (int i = 0; i < mdots.length; i++){

            mdots[i] = new TextView(this);
            mdots[i].setText(Html.fromHtml("&#8226"));
            mdots[i].setTextSize(40);
            mdots[i].setTextColor(getResources().getColor(R.color.TransparentWhite, getTheme()));

            mDotLayout.addView(mdots[i]);
        }

        if (mdots.length >   0) {
            mdots[position].setTextColor(getResources().getColor(R.color.White,getTheme()));
        }
    }

    ViewPager.OnPageChangeListener dotListner = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int pageposition) {

            addDotsIndicator(pageposition);
            mCurrentPage = pageposition;
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };


}

