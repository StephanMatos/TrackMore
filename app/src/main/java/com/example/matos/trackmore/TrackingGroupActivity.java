package com.example.matos.trackmore;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


import com.google.android.gms.location.FusedLocationProviderClient;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.maps.android.SphericalUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.io.FileWriter;

public class TrackingGroupActivity extends AppCompatActivity implements OnMapReadyCallback {
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private static GoogleMap mMap;
    private static final String TAG = TrackingGroupActivity.class.getSimpleName();
    private Location mLastKnownLocation;
    private float zoom = 15;
    private static ArrayList<Marker> red = new ArrayList<>();
    private static ArrayList<Marker> yellow = new ArrayList<>();
    private static ArrayList<Marker> green = new ArrayList<>();
    private static ArrayList<Marker> blue = new ArrayList<>();
    private static ArrayList<String> macID = new ArrayList<>();
    private static ArrayList<Integer> RSSI = new ArrayList<>();
    private static ArrayList<Double> Offset = new ArrayList<>();
    private static LatLng CurrentPosition, markerPosition;
    Handler h = new Handler();
    private static String ID;
    private static int internalID;
    int delay = 10 * 500;
    private static int SYSTEM = 0;
    private ImageButton dropDownButton;
    private static boolean action = false;
    static Network network = Network.getInstance();
    private static BufferedReader bir = network.getBir();
    public static Activity activity;
    LocationManager lm;
    static Location location;
    private static Context mContext;
    public static int countRED,countYellow,countBLUE,countGreen;
    public static boolean RED,GREEN,YELLOW,BLUE;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_individual);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mContext = this;
        new tcp().execute();
        new readBuffer().execute();
        activity = this;


        dropDownButton = findViewById(R.id.dropdownButton);
        dropDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(TrackingGroupActivity.this, dropDownButton);

                // Inflating the popup using xml file
                popup.getMenuInflater().inflate(R.menu.popup_menu_group_1device, popup.getMenu());
                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        if (menuItem.getGroupId() == R.id.ShowRed && markerPosition != null) {

                            // lav testing her!

                            DecimalFormat twodecimalDistance = new DecimalFormat("0.00");

                            double distance = SphericalUtil.computeDistanceBetween(CurrentPosition, markerPosition) / 1000;


                            // laver kun en toast, dvs den forsvinder igen efter lidt tid

                            Toast.makeText(TrackingGroupActivity.this, menuItem.getTitle() + " to marker: " + twodecimalDistance.format(distance) + " Km", Toast.LENGTH_LONG).show();

                            // Fordi der er to "knapper" du kan finde menuen under app->res->menu->popup_menu_group_1device
                        } else if (menuItem.getGroupId() == R.id.ShowEdit && markerPosition != null) {

                            System.out.print("Speed");

                        }

                        return true;
                    }
                });

                popup.show();
            }
        });


    }

    @Override
    public void onMapReady(GoogleMap googleMap) throws NullPointerException {
        mMap = googleMap;


        // Android needs to peform this check, otherwise location will not be shown
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Redraws map every delay's time
        h.postDelayed(new Runnable() {
            public void run() {
                if (action) {
                    h.postDelayed(this, delay);
                    action = false;
                    System.out.println("redraw");
                }
            }
        }, delay);


        // Location of device, zoom to location of device
        mMap.setMyLocationEnabled(true);
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CurrentPosition = latLng;
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

    }

    private void getDeviceLocation() {

        try {

                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), zoom));
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                        }
                    }
                });

        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    public static class tcp extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... params) {
            int retry = 0;
            boolean connection = true;
            while(!network.Init()){

                System.out.println("inside loop tcp");
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                retry++;
                if(retry > 3){
                    connection = false;
                    break;
                }
            }
            if(connection) {
                PrintWriter pw = network.getPw();
                System.out.println("this is pw" + pw);
                if (pw != null) {
                    pw.println("{\"ID\":0,\"SYSTEM\":9,\"RSSI\":0,\"NumberOfStations\":0,\"LATITUDE\":0,\"LONGITUDE\":0}");
                    pw.println("{\"ID\":0,\"SYSTEM\":2,\"RSSI\":0,\"NumberOfStations\":0,\"LATITUDE\":0,\"LONGITUDE\":0}");
                    pw.flush();
                }
            }else{
                Intent home = new Intent(mContext, HomeActivity.class);
                mContext.startActivity(home);
                activity.finish();
            }
            return null;
        }
    }


    public static class readBuffer extends AsyncTask<Void, Void,String>{

        @Override
        protected String doInBackground(Void... voids) {

            String message = "Null";
            try {
                message = bir.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Message is : "+message);
            if(message.equals("Null")){
                try {
                    Thread.sleep(20000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return message;
        }

        @Override
        protected void onPostExecute(String s) {
            if(s.equals("Null")){
                new readBuffer().execute();
            }else{
                new makeJsonObject().execute(s);
            }
        }
    }

    public static class makeJsonObject extends AsyncTask<String, Void, LatLng>{

        @Override
        protected LatLng doInBackground(String... strings) {
            JSONObject json = null;
            markerPosition = null;
            try {
                json = new JSONObject(strings[0]);
                SYSTEM = (json.getInt("SYSTEM"));
            } catch (NullPointerException | JSONException e) {
                e.printStackTrace();
                System.out.println("json object failed");
            }

            if(SYSTEM == 2){
                try {
                    ID = json.getString("ID");
                    String lat = json.getString("LATITUDE");
                    double latitude = Double.parseDouble(lat);
                    String lon = json.getString("LONGITUDE");
                    double longitude = Double.parseDouble(lon);

                    // test af rssi
                    int rssi = json.getInt("RSSI");
                    RSSI.add(rssi);
                    System.out.println(rssi);
                    markerPosition = new LatLng(latitude,longitude);

                    // test af distance
                    double m = 100.0;
                    double distance = SphericalUtil.computeDistanceBetween(CurrentPosition, markerPosition);
                    System.out.println(distance);
                    if(m > distance){
                        m = m - distance;
                    }else {
                        m = distance - m;
                    }
                    System.out.println("Offset is : "+m);
                    Offset.add(m);

                } catch (JSONException | NullPointerException e) {
                    e.printStackTrace();
                }

            }
            return markerPosition;
        }

        @Override
        protected void onPostExecute(LatLng markerPosistion) {
            if(markerPosistion != null){
                makeMarker(markerPosistion);
            } else {
                new readBuffer().execute();
            }

            System.out.println("OnPostExecute in MakeJsonObject");
        }
    }

    private static void makeMarker(LatLng position){

        internalID = translateID(ID);
        int size;
        count(internalID);

        if(internalID == 1){
            RED = true;
            Marker redMarker = mMap.addMarker(new MarkerOptions().position(position).title(ID).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            if(red.size() == 0){
                red.add(redMarker);

            } else{
                size = red.size();
                red.get(size-1).remove();
                red.clear();
                red.add(redMarker);
            }

        } else if(internalID == 2){
            YELLOW = true;
            Marker yellowMarker = mMap.addMarker(new MarkerOptions().position(position).title(ID).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            if(yellow.size() == 0){
                yellow.add(yellowMarker);
            } else{
                size = yellow.size();
                yellow.get(size-1).remove();
                yellow.clear();
                yellow.add(yellowMarker);
            }

        }else if(internalID == 3){
            GREEN = true;
            Marker greenMarker = mMap.addMarker(new MarkerOptions().position(position).title(ID).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            if(green.size() == 0){
                green.add(greenMarker);
            } else{
                size = green.size();
                green.get(size-1).remove();
                green.clear();
                green.add(greenMarker);
            }

        }else if(internalID == 4){
            BLUE = true;
            Marker blueMarker = mMap.addMarker(new MarkerOptions().position(position).title(ID).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            if(blue.size() == 0){
                blue.add(blueMarker);
            } else{
                size = blue.size();
                blue.get(size-1).remove();
                blue.clear();
                blue.add(blueMarker);
            }
        }

        action = true;
        System.out.println(RSSI.size());
        if(RSSI.size() < 100){
            new readBuffer().execute();
        }else{
            double sumrssi = 0;
            double sumMeter = 0;
            int badRSSI = -30;
            int bestRSSI = -100;

                for (Integer rssi : RSSI) {
                    if(rssi>bestRSSI){
                        bestRSSI = rssi;
                    }
                    if(rssi< badRSSI){
                        badRSSI = rssi;
                    }

                    sumrssi += rssi;
                }
                for(Double meter : Offset){
                    sumMeter += meter;
                    System.out.println(meter);
                }
            System.out.println("Avereage result is of rssi is :   " + sumrssi/RSSI.size());
            System.out.println("The best RSSI value was : " + bestRSSI);
            System.out.println("The worst RSSI value was : " + badRSSI);
            System.out.println("Avereage result is :   " + sumMeter/Offset.size());
            System.out.println("all done ");
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

    private static void count(int id){
        if(id == 1){
            countRED = 0;
            countYellow++;
            countGreen++;
            countBLUE++;
        }else if( id == 2){
            countRED++;
            countYellow = 0;
            countGreen++;
            countBLUE++;
        }else if (id == 3){
            countRED++;
            countYellow++;
            countGreen = 0;
            countBLUE++;
        }else if (id == 4){
            countRED++;
            countYellow++;
            countGreen++;
            countBLUE = 0;
        }

        if(countRED > 10 && RED || countYellow > 10 && YELLOW || countGreen > 10 && GREEN || countBLUE > 10 && BLUE){
            // do Lora

        }

    }

}
