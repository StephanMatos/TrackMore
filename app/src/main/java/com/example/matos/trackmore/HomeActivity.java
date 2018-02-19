package com.example.matos.trackmore;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {

    private ViewPager mSlideViewPager;
    private LinearLayout mDotLayout;
    private SliderAdapter sliderAdapter;
    private Button mNextActivityBtn;
    private int mCurrentPage;

    private TextView[] mdots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mSlideViewPager = (ViewPager) findViewById(R.id.SlideView);
        mDotLayout = (LinearLayout) findViewById(R.id.dotsLayout);

        mNextActivityBtn = (Button) findViewById(R.id.nxtActivityBnt);

        sliderAdapter = new SliderAdapter(this);

        mSlideViewPager.setAdapter(sliderAdapter);

        addDotsIndicator(0);

        mSlideViewPager.addOnPageChangeListener(dotListner );

        mNextActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mCurrentPage == 0) {
                    IndividualActivity();
                } else if (mCurrentPage == 1) {
                    GroupActivity();
                } else {
                     SportActivity();
                }

            }
        });


    }

    public void IndividualActivity() {
        Intent intent =  new Intent(this, TrackingIndividualActivity.class);
        startActivity(intent);
    }

    public void GroupActivity() {
        Intent intent =  new Intent(this, TrackingGroupActivity.class);
        startActivity(intent);
    }

    public void SportActivity() {
        Intent intent =  new Intent(this, TrackingSportActivity.class);
        startActivity(intent);
    }



    public void addDotsIndicator(int position) {
        mdots = new TextView[3];
        mDotLayout.removeAllViews();

        for (int i = 0; i < mdots.length; i++){

            mdots[i] = new TextView(this);
            mdots[i].setText(Html.fromHtml("&#8226"));
            mdots[i].setTextSize(40);
            mdots[i].setTextColor(getResources().getColor(R.color.TransparentWhite));

            mDotLayout.addView(mdots[i]);
        }



        if (mdots.length >   0) {
            mdots[position].setTextColor(getResources().getColor(R.color.White));
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
