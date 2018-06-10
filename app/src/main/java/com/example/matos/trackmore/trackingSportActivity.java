package com.example.matos.trackmore;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

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
    static double WidthRatio, LenghtRatio, MaxCordinateY, MaxCordinateX, OrigoY, OrigoX, FieldLenght, FieldWidth ;
    static int internalID;


    static boolean Origoclick = false;
    static boolean MaxCordinateclick = false;
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
                // you clicked top left corner first, Origo is then on the first click.
                if (Origoclick == false && MaxCordinateclick == false){
                    Origoclick = true;
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
                    toprightclick = 2;
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
                // you clicked low right corner first, Origo is then on the second click.
                if (MaxCordinateclick == false && Origoclick == false){
                    MaxCordinateclick = true;
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

        if (Origoclick){
            Origo = firstcorner;
            MaxCordinates = secondcorner;
            System.out.println("Origo: "+ Origo + " MaxCordinates: " +MaxCordinates);
        } else if (MaxCordinateclick){
            Origo = secondcorner;
            MaxCordinates = firstcorner;
            System.out.println("Origo: "+ Origo + " MaxCordinates: " +MaxCordinates);
        } else {
            if (toprightclick == 2){
                Origo =  new LatLng(secondcorner.longitude, firstcorner.latitude);
                MaxCordinates =  new LatLng(firstcorner.longitude,secondcorner.latitude);
                System.out.println("Origo: "+ Origo + " MaxCordinates: " +MaxCordinates);
            }else {
                Origo =  new LatLng(firstcorner.longitude,secondcorner.latitude);
                MaxCordinates =  new LatLng(firstcorner.latitude,secondcorner.longitude);
                System.out.println("Origo: "+ Origo + " MaxCordinates: " +MaxCordinates);
            }
        }

        MaxCordinateY = MaxCordinates.latitude;
        MaxCordinateX = MaxCordinates.longitude;
        OrigoY = Origo.latitude;
        OrigoX = Origo.longitude;
        LatLng calLenght = new LatLng(OrigoX, MaxCordinateY);
        LatLng calWidth = new LatLng(MaxCordinateX, OrigoY);

        //calculate field dimensions
        FieldLenght = SphericalUtil.computeDistanceBetween(Origo, calLenght);
        FieldWidth = SphericalUtil.computeDistanceBetween(Origo, calWidth);



    }


    // To Calculate the players position on the field and place the position
    public static void SetPlayers(LatLng position){

        LatLng X_Cordinate =  new LatLng(position.longitude, OrigoY);
        LatLng Y_Cordinate = new LatLng(OrigoX, position.latitude);

        double playerDistanceX = SphericalUtil.computeDistanceBetween(Origo, X_Cordinate);
        double playerDistanceY = SphericalUtil.computeDistanceBetween(Origo, Y_Cordinate);

        WidthRatio = FieldLenght/playerDistanceX;
        LenghtRatio = FieldWidth/playerDistanceY;

        float playerposX = (float) WidthRatio * maxDpX;
        float playerposY = (float) LenghtRatio * maxDpY;

        if (internalID == 1){
            redtop.setX(playerposX);
            redtop.setY(playerposY);
        }else if (internalID == 2) {
            yellowtop.setX(playerposX);
            yellowtop.setY(playerposY);
        }else if (internalID == 3) {
            greentop.setX(playerposX);
            greentop.setY(playerposY);
        }else if (internalID == 4) {
            bluetop.setX(playerposX);
            bluetop.setY(playerposY);
        }

    }

    public static void addPosition(String lat, String lon, String ID){
        double Latitude = Double.parseDouble(lat);
        double Longitude = Double.parseDouble(lon);
        position = new LatLng(Latitude,Longitude);

        double distance = SphericalUtil.computeDistanceBetween(Origo, position);
        if(distance > 1000){
            new asyncRead().execute();
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
