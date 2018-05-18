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

    private ImageButton topleftcorner;
    private ImageButton lowleftcorner;
    private ImageButton toprightcorner;
    private ImageButton lowrightcorner;
    private ImageView bluetop;
    private ImageView redtop;
    private ImageView yellowtop;
    private ImageView greentop;
    private static LatLng firstcorner;
    private static LatLng secondcorner;
    private static LatLng Origo;
    private static LatLng MaxCordinates;
    static LatLng position;

    static int SYSTEM, maksY, maksX;
    static String ID;
    static int internalID;
    static boolean start = false;
    static boolean firstclick = false;
    static boolean secondclick = false;
    static int toprightclick = 0;
    static Network network = Network.getInstance();
    static BufferedReader bir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_sport);

        topleftcorner = findViewById(R.id.topleftcorner);
        lowleftcorner = findViewById(R.id.lowleftcorner);
        toprightcorner = findViewById(R.id.toprightcorner);
        lowrightcorner = findViewById(R.id.lowrightcorner);
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
    // Method to get the phones location
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
            CalFieldPixels();
        }
    }

    // Method to calculate the field pixels in dp
    public void CalFieldPixels() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        maksY = metrics.heightPixels - 305;
        maksX = metrics.widthPixels - 90;

        System.out.println("maksY: "+ maksY + " maksX: " + maksX);

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









    public static class tcp extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... params) {

            while(!network.Init()){
                System.out.println("inside loop tcp");
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            PrintWriter pw = network.getPw();
            System.out.println(pw);
            if(pw != null){
                pw.println("{\"ID\":0,\"SYSTEM\":9,\"RSSI\":0,\"NumberOfStations\":0,\"LATITUDE\":0,\"LONGITUDE\":0}");
                pw.println("{\"ID\":0,\"SYSTEM\":2,\"RSSI\":0,\"NumberOfStations\":0,\"LATITUDE\":0,\"LONGITUDE\":0}");
                pw.flush();
            }

            return null;
        }

    }

    public static class readBuffer extends AsyncTask<Void, Void,String>{

        @Override
        protected String doInBackground(Void... voids) {
            bir = network.getBir();
            String message = null;
            try {
                message = bir.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(message);
            return message;
        }

        @Override
        protected void onPostExecute(String s) {
            // If communication has started only sleep for 5 seconds, else wait 10 so the application wont do to much before messages starts comming in
            if(start){
                if(s == null){
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    new readBuffer().execute();

                }else{
                    new makeJsonObject().execute(s);
                }
            } else{
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public static class makeJsonObject extends AsyncTask<String, Void, LatLng>{

        @Override
        protected LatLng doInBackground(String... strings) {
            JSONObject json = null;

            try {
                json = new JSONObject(strings[0]);
            } catch (JSONException e) {
                e.printStackTrace();
                System.out.println("json object failed");
            }
            try {
                SYSTEM = (json.getInt("SYSTEM"));
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException e1){
                e1.printStackTrace();
            }
            if(SYSTEM == 3){
                start = true;
                try {
                    ID = json.getString("ID");
                    String lat = json.getString("LATITUDE");
                    double latitude = Double.parseDouble(lat);
                    String lon = json.getString("LONGITUDE");
                    double longitude = Double.parseDouble(lon);
                    position = new LatLng(latitude,longitude);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else{
                System.out.println("incorrect message received");
            }
            return position;
        }

        @Override
        protected void onPostExecute(LatLng Posistion) {
            if(Posistion != null){
                addPosition(Posistion);
            } else {
                new readBuffer().execute();
            }

            System.out.println("OnPostExecute in MakeJsonObject");
        }
    }

    private static void addPosition(LatLng position){

        internalID = translateID(ID);

        if(internalID == 1){
            red.add(position);
        } else if(internalID == 2){
            yellow.add(position);
        }else if(internalID == 3){
            green.add(position);
        }else if(internalID == 4){
            blue.add(position);
        }
        new readBuffer().execute();
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