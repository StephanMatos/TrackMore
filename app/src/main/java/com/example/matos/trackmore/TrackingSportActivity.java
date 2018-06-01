package com.example.matos.trackmore;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class TrackingSportActivity extends AppCompatActivity {

    private static ArrayList<LatLng> red = new ArrayList<>();
    private static ArrayList<LatLng> yellow = new ArrayList<>();
    private static ArrayList<LatLng> green = new ArrayList<>();
    private static ArrayList<LatLng> blue = new ArrayList<>();
    private static ArrayList<String> macID = new ArrayList<>();

    private ImageButton topleftcorner, lowleftcorner, toprightcorner, lowrightcorner;
    private static ImageView bluetop,redtop, yellowtop, greentop, dropdownmenu;
    private static LatLng firstcorner, secondcorner, Origo, MaxCordinates, position;

    static int  maxDpY, maxDpX;
    static double SizeRatioY, SizeRatioX;
    static int internalID;

    static boolean firstclick = false;
    static boolean secondclick = false;
    static int toprightclick = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_sport);

        topleftcorner = findViewById(R.id.topleftcorner);
        lowleftcorner = findViewById(R.id.lowleftcorner);
        toprightcorner = findViewById(R.id.toprightcorner);
        lowrightcorner = findViewById(R.id.lowrightcorner);
        dropdownmenu = findViewById(R.id.dropdownButton);

        bluetop = findViewById(R.id.BlueShirt);
        redtop = findViewById(R.id.RedShirt);
        yellowtop = findViewById(R.id.YellowShirt);
        greentop = findViewById(R.id.GreenShirt);

        topleftcorner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                topleftcorner.setVisibility(View.GONE);
                toprightcorner.setVisibility(View.GONE);
                lowleftcorner.setVisibility(View.GONE);
                if (firstclick == false){
                    firstclick = true;
                } else {
                    secondclick = true;
                }
                GetOwnLocation();
            }
        });

        toprightcorner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                topleftcorner.setVisibility(View.GONE);
                toprightcorner.setVisibility(View.GONE);
                lowrightcorner.setVisibility(View.GONE);
                // check whether this button was clicked secondly or first
                if (toprightclick == 0){
                    toprightclick = 1;
                } else{
                    toprightclick = 2;
                }
                GetOwnLocation();
            }
        });

        lowleftcorner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                topleftcorner.setVisibility(View.GONE);
                lowleftcorner.setVisibility(View.GONE);
                lowrightcorner.setVisibility(View.GONE);
                // Check whether this button was clicked secondly or first
                if (toprightclick == 0){
                    toprightclick = 3;
                }
                GetOwnLocation();
            }
        });

        lowrightcorner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toprightcorner.setVisibility(View.GONE);
                lowleftcorner.setVisibility(View.GONE);
                lowrightcorner.setVisibility(View.GONE);
                if (firstclick == false){
                    firstclick = true;
                }
                GetOwnLocation();
            }
        });
    }

    // To get the phones location
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
            dropdownmenu.setVisibility(View.VISIBLE);
            CalFieldPixels();
        }
    }

    // To calculate the field in dp
    public void CalFieldPixels() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        maxDpY = metrics.heightPixels - 305;
        maxDpX = metrics.widthPixels - 90;

        System.out.println("maksY: "+ maxDpY + " maksX: " + maxDpX);

        if (firstclick){
            Origo = firstcorner;
            MaxCordinates = secondcorner;
            System.out.println("Origo: "+ Origo + " MaxCordinates: " +MaxCordinates);
            bluetop.setVisibility(View.VISIBLE);
            bluetop.setX(0);
            bluetop.setY(0);
        } else if (secondclick){
            Origo = secondcorner;
            MaxCordinates = firstcorner;
            System.out.println("Origo: "+ Origo + " MaxCordinates: " +MaxCordinates);
        } else {
            if (toprightclick == 2){
                Origo =  new LatLng(firstcorner.latitude,secondcorner.longitude);
                MaxCordinates =  new LatLng(firstcorner.longitude,secondcorner.latitude);
                System.out.println("Origo: "+ Origo + " MaxCordinates: " +MaxCordinates);
            }else {
                Origo =  new LatLng(firstcorner.longitude,secondcorner.latitude);
                MaxCordinates =  new LatLng(firstcorner.latitude,secondcorner.longitude);
                System.out.println("Origo: "+ Origo + " MaxCordinates: " +MaxCordinates);
            }
        }
    }


    // To Calculate the players position on the field
    public static void SetPlayers(LatLng position){

       double MaxCordinateY = MaxCordinates.latitude;
       double MaxCordinateX = MaxCordinates.longitude;
       double OrigoY = Origo.latitude;
       double OrigoX = Origo.longitude;
       double X_Cordinate = position.longitude;
       double Y_Cordinate = position.latitude;


       // Check if the coordinates is negative, then multiply with -1
        if (MaxCordinateX < 0){
            MaxCordinateX *= -1;
        }if (MaxCordinateY < 0) {
            MaxCordinateY *= -1;
        }if (OrigoX < 0) {
            OrigoX *=-1;
        }if (OrigoY < 0) {
            OrigoY *= -1;
        }if (X_Cordinate < 0) {
            X_Cordinate *= -1;
        }if (Y_Cordinate < 0) {
            Y_Cordinate *= -1;
        }

       SizeRatioY = maxDpY/MaxCordinateY;
       SizeRatioX = maxDpX/MaxCordinateX;

       //Check if the given position is on the field
       if (X_Cordinate > OrigoX && X_Cordinate < MaxCordinateX && Y_Cordinate > OrigoY && Y_Cordinate < MaxCordinateY){

           //Calculate the lat/lng to dp
           float xPosition = (float) ((float) X_Cordinate * SizeRatioX);
           float yPosition = (float) ((float) Y_Cordinate * SizeRatioY);

           //check which shirt to move to the given position
           if (internalID == 1){
               redtop.setX(xPosition);
               redtop.setY(yPosition);

           }else if (internalID == 2){
               yellowtop.setX(xPosition);
               yellowtop.setY(yPosition);
           }else if (internalID == 3){
               greentop.setX(xPosition);
               greentop.setY(yPosition);
           }else {
               bluetop.setX(xPosition);
               bluetop.setY(yPosition);
           }
       }
    }

    public static void addPosition(String lat, String lon, String ID){
        double Latitude = Double.parseDouble(lat);
        double Longitude = Double.parseDouble(lon);
        position = new LatLng(Latitude,Longitude);

        double distance = SphericalUtil.computeDistanceBetween(Origo, position);
        if(distance > 1000){
            new AsyncRead().execute();
            return;
        }
        internalID = translateID(ID);
        if(internalID == 1){
            red.add(position);
            redtop.setVisibility(View.VISIBLE);
            SetPlayers(position);
        } else if(internalID == 2){
            yellow.add(position);
            yellowtop.setVisibility(View.VISIBLE);
            SetPlayers(position);
        }else if(internalID == 3){
            green.add(position);
            greentop.setVisibility(View.VISIBLE);
            SetPlayers(position);
        }else if(internalID == 4){
            blue.add(position);
            bluetop.setVisibility(View.VISIBLE);
            SetPlayers(position);
        }

    }

    private static int translateID(String foreignID){

        if(!macID.contains(foreignID)){
            macID.add(foreignID);
            internalID = macID.indexOf(foreignID)+1;
        }else{
            internalID = macID.indexOf(foreignID)+1;
        }
        return internalID;
    }
}