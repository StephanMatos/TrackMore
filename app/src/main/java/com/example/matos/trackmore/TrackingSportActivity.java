package com.example.matos.trackmore;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.maps.model.LatLng;

public class TrackingSportActivity extends AppCompatActivity {

    private static ImageButton topleftcorner;
    private static ImageButton lowleftcorner;
    private static ImageButton toprightcorner;
    private static ImageButton lowrightcorner;
    private static LatLng firstcorner;
    private static LatLng secondcorner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_sport);


        topleftcorner = (ImageButton) findViewById(R.id.topleftcorner);
        lowleftcorner = (ImageButton) findViewById(R.id.lowleftcorner);
        toprightcorner = (ImageButton) findViewById(R.id.toprightcorner);
        lowrightcorner = (ImageButton) findViewById(R.id.lowrightcorner);

        topleftcorner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                topleftcorner.setVisibility(View.GONE);
                toprightcorner.setVisibility(View.GONE);
                lowleftcorner.setVisibility(View.GONE);
                GetOwnLocation();
            }

        });

        toprightcorner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                topleftcorner.setVisibility(View.GONE);
                toprightcorner.setVisibility(View.GONE);
                lowrightcorner.setVisibility(View.GONE);
                GetOwnLocation();
            }

        });

        lowleftcorner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                topleftcorner.setVisibility(View.GONE);
                lowleftcorner.setVisibility(View.GONE);
                lowrightcorner.setVisibility(View.GONE);
                GetOwnLocation();
            }

        });

        lowrightcorner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toprightcorner.setVisibility(View.GONE);
                lowleftcorner.setVisibility(View.GONE);
                lowrightcorner.setVisibility(View.GONE);
                GetOwnLocation();
            }

        });
    }

    public void GetOwnLocation(){
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        @SuppressLint("MissingPermission")
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());

        if (firstcorner == null){
            firstcorner = latlng;
            System.out.println("Firstcorner: " + firstcorner);
        } else {
            secondcorner = latlng;
            System.out.println("Secondcorner: " + secondcorner);
        }
    }
}
