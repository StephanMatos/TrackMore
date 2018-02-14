package com.example.matos.trackmore;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {

    private ViewPager mSlideViewPager;
    private LinearLayout mDotLayout;
    private SliderAdapter sliderAdapter;

    private TextView[] mdots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mSlideViewPager = (ViewPager) findViewById(R.id.SlideView);
        mDotLayout = (LinearLayout) findViewById(R.id.dotsLayout);

        sliderAdapter = new SliderAdapter(this);

        mSlideViewPager.setAdapter(sliderAdapter);

        addDotsIndicator();
    }

    public void addDotsIndicator() {

        mdots = new  TextView[3];

        for (int i = 0; i < mdots.length ; i++){

            mdots[i] = new TextView(this);
            mdots[i].setText(Html.fromHtml("&#8226"));
            mdots[i].setTextSize(30);
            mdots[i].setTextColor(getResources().getColor(R.color.colorPrimary));

            mDotLayout.addView(mdots[i] );
        }
    }
}
