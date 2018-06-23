package com.example.matos.trackmore;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

public class trackingSportActivity extends AppCompatActivity {

    private static ArrayList<LatLng> red = new ArrayList<>();
    private static ArrayList<LatLng> yellow = new ArrayList<>();
    private static ArrayList<LatLng> green = new ArrayList<>();
    private static ArrayList<LatLng> blue = new ArrayList<>();
    private static ArrayList<String> macID = new ArrayList<>();

    private ImageButton topleftcorner, lowleftcorner, lowrightcorner;
    private static ImageView bluetop,redtop, yellowtop, greentop, dropdownmenu;
    private static LatLng firstcorner, secondcorner, position;
    private static LatLng topLeft, lowLeft, lowRight;

    static int  maxDpY, maxDpX;
    static double WidthRatio, LenghtRatio, MaxCordinateY, MaxCordinateX, OrigoY, OrigoX, FieldLenght, FieldWidth ;
    static int internalID;
    static Context mContext;
    static vector vectorA = new vector(55.791229,12.521473);
    static vector vectorB = new vector(55.790426, 12.521023);



    LatLng corner1 = new LatLng(55.7912302,12.52147017);
    LatLng corner2 = new LatLng(55.79042045,12.52102478);
    LatLng corner3 = new LatLng(55.79026187,12.52191608);


    static boolean Origoclick = false;
    static boolean MaxCordinateclick = false;
    static int toprightclick = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_sport);
        mContext = this;
        topleftcorner = findViewById(R.id.topleftcorner);
        lowleftcorner = findViewById(R.id.lowleftcorner);
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

                // you clicked top left corner first, Origo is then on the first click.
                if (Origoclick == false && MaxCordinateclick == false){
                    Origoclick = true;
                }
                topLeft = corner1;


            }
        });

        lowleftcorner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                lowleftcorner.setVisibility(View.GONE);

                // Check whether this button was clicked secondly or first
                if (toprightclick == 0){
                    toprightclick = 2;
                }
               lowLeft = corner2;
            }
        });

        lowrightcorner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lowrightcorner.setVisibility(View.GONE);
                // you clicked low right corner first, Origo is then on the second click.

                lowRight = corner3;
                CalFieldPixels();

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
        System.out.println("in calibrate");

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        maxDpY = metrics.heightPixels - 305;
        maxDpX = metrics.widthPixels - 90;

        System.out.println("maksY: "+ maxDpY + " maksX: " + maxDpX);

        //calculate field dimensions
        FieldLenght = SphericalUtil.computeDistanceBetween(topLeft,lowLeft);
        FieldWidth = SphericalUtil.computeDistanceBetween(lowLeft,lowRight);
        System.out.println("legth is : " + FieldLenght + "width is : " + FieldWidth);




        addPosition("55.79073614","12.52171057", "AE12C212");
        //new asyncTCP().execute(Integer.valueOf('3'));
    }



    // To Calculate the players position on the field and place the position
    public static void SetPlayers(LatLng position){

        double C = SphericalUtil.computeDistanceBetween(topLeft,position);
        vectorB.sub(vectorA);
        vector vectorC = new vector(position.latitude,position.longitude);
        vectorC.sub(vectorA);

        double angle = vectorB.angle(vectorC);
        System.out.println("angle is : " + angle);

        double sidea = Math.sin(angle)*C;
        double sideb = Math.cos(angle)*C;

        System.out.println("Side a is :  " + sidea + " side b is : " + sideb);

        System.out.println("   RatioW   " + sidea/FieldWidth + "  RatioL   " + sideb/FieldLenght);

        float playerposX = (float)(sidea/FieldWidth)*maxDpX;
        float playerposY = (float) (sideb/FieldLenght)*maxDpY;

        System.out.println("player x is : " + playerposX + "playerposY : " + playerposY);


        if (internalID == 1){
            System.out.println("in setplayers");
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

       // new asyncRead().execute();

    }

    public static void addPosition(String lat, String lon, String ID){
        System.out.println("in add");
        double Latitude = Double.parseDouble(lat);
        double Longitude = Double.parseDouble(lon);
        position = new LatLng(Latitude,Longitude);

        /*
        if(distance > 1000){
            new asyncRead().execute();
            return;
        }
        */

        internalID = translateID(ID);
        if(internalID == 1){
            red.add(position);
            redtop.setVisibility(View.VISIBLE);
            System.out.println("in red");
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

    public static void stop(){
        Intent newIntent = new Intent(mContext,HomeActivity.class);
        mContext.startActivity(newIntent);
        ((Activity) mContext).finish();
    }
}
