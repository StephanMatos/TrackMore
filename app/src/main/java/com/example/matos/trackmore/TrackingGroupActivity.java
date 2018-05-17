package com.example.matos.trackmore;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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

public class TrackingGroupActivity extends FragmentActivity implements OnMapReadyCallback {

    private static GoogleMap mMap;
    private static ArrayList<Marker> red = new ArrayList<>();
    private static ArrayList<Marker> yellow = new ArrayList<>();
    private static ArrayList<Marker> green = new ArrayList<>();
    private static ArrayList<Marker> blue = new ArrayList<>();
    private static ArrayList<String> macID = new ArrayList<>();
    private static ArrayList<Integer> RSSI = new ArrayList<>();
    private static ArrayList<Double> Offset = new ArrayList<>();


    private static int i = 1;
    private static LatLng CurrentPosition, markerPosition;
    Handler h = new Handler();
    private static String ID;
    private static int internalID;
    int delay = 10 * 500;
    private static int SYSTEM = 0;
    private ImageButton dropDownButton;
    private static boolean action = false;
    static Network network = Network.getInstance();
    private static BufferedReader bir;
    static double meter = 5;
    LocationManager lm;
    static Location location;



    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_individual);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        new tcp().execute();
        new readBuffer().execute();


        dropDownButton = (ImageButton) findViewById(R.id.dropdownButton);
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

                        if (menuItem.getGroupId() == R.id.ShowRed && markerPosition != null){

                            DecimalFormat twodecimalDistance = new DecimalFormat("0.00");

                            double distance = SphericalUtil.computeDistanceBetween(CurrentPosition, markerPosition)/1000;

                            Toast.makeText(TrackingGroupActivity.this, menuItem.getTitle() + " to marker: " + twodecimalDistance.format(distance) + " Km", Toast.LENGTH_LONG).show();

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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        float zoom = 13;

        // Android needs to peform this check, otherwise location will not be shown
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Redraws map every delay's time
        h.postDelayed(new Runnable(){
            public void run(){
                if(action){
                    h.postDelayed(this, delay);
                    action = false;
                    System.out.println("redraw");
                }
            }
        }, delay);



        // Location of device, zoom to location of device
        mMap.setMyLocationEnabled(true);
        lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CurrentPosition = latLng;
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

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
            if(message == null){
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
            if(s == null){
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
                    double distance = SphericalUtil.computeDistanceBetween(CurrentPosition, markerPosition);
                    System.out.println(distance);
                    if(distance > meter){
                        distance = distance-meter;
                        Offset.add(distance);
                    }else{
                        meter = meter - distance;
                        Offset.add(meter);
                    }

                } catch (JSONException e) {
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
        int size = 0;

        if(internalID == 1){
            Marker redMarker = mMap.addMarker(new MarkerOptions().position(position).title(ID).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            if(red.size() == 0){
                red.add(redMarker);
                //red.clear();
            } else{
                size = red.size();
                red.get(size-1).remove();
                red.add(redMarker);
            }


        } else if(internalID == 2){

            Marker yellowMarker = mMap.addMarker(new MarkerOptions().position(position).title(ID).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            if(yellow.size() == 0){

                yellow.add(yellowMarker);

                //red.clear();
            } else{
                size = yellow.size();
                yellow.get(size-1).remove();
                yellow.add(yellowMarker);
            }

        }else if(internalID == 3){
            if(green.size() > 0){
                green.get(0).remove();
                green.clear();
            }
            Marker greenMarker = mMap.addMarker(new MarkerOptions().position(position).title(ID).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            green.clear();
            green.add(greenMarker);
        }else if(internalID == 4){
            if(blue.size() > 0){
                blue.get(0).remove();
                blue.clear();
            }
            Marker blueMarker = mMap.addMarker(new MarkerOptions().position(position).title(ID).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            blue.clear();
            blue.add(blueMarker);
        }

        action = true;
        System.out.println(action);
        System.out.println(RSSI.size());
        if(RSSI.size() < 20){
            new readBuffer().execute();
        }else{
            double sumrssi = 0;
            double sumMeter = 0;

                for (Integer rssi : RSSI) {
                    sumrssi += rssi;
                }
                for(Double meter : Offset){
                    sumMeter += meter;
                }
            System.out.println("Avereage result is :   " + sumrssi/RSSI.size());
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



}
