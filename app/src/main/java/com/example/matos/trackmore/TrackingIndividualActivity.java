package com.example.matos.trackmore;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class TrackingIndividualActivity extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap mMap;

    Marker[] markers = new Marker[4];
    LatLng CurrentPosition;
    LatLng markerPosition;
    Handler h = new Handler();
    String ID;
    int foreignID;
    int delay = 10 * 500;
    int SYSTEM = 0;
    private ImageButton dropDownButton;
    Network network;
    BufferedReader bir;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_individual);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        new TrackingIndividualActivity.tcp().execute();
        new TrackingIndividualActivity.readBuffer().execute();


        dropDownButton = (ImageButton) findViewById(R.id.dropdownButton);
        dropDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(TrackingIndividualActivity.this, dropDownButton);
                // Inflating the popup using xml file
                popup.getMenuInflater().inflate(R.menu.popup_menu_individual, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        if (menuItem.getGroupId() == R.id.ShowDistance && markerPosition != null){

                            DecimalFormat twodecimalDistance = new DecimalFormat("0.00");

                           double distance = SphericalUtil.computeDistanceBetween(CurrentPosition, markerPosition)/1000;

                           Toast.makeText(TrackingIndividualActivity.this, menuItem.getTitle() + " to marker: " + twodecimalDistance.format(distance) + " Km", Toast.LENGTH_LONG).show();

                        } else if (menuItem.getGroupId() == R.id.ShowDistance && markerPosition != null) {

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
                h.postDelayed(this, delay);

            }
        }, delay);

        // Location of device, zoom to location of device
        mMap.setMyLocationEnabled(true);
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CurrentPosition = latLng;
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

    }

    public void wifi(){


        String ssid = "TrackMore-1";
        String key = "password";

        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", ssid);
        wifiConfig.preSharedKey = String.format("\"%s\"", key);

        WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);

        int netId = wifiManager.addNetwork(wifiConfig);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();
    }


    public class tcp extends AsyncTask<Void, Void, Boolean>{

        protected Boolean doInBackground(Void... params) {
            network = Network.getInstance();
            network.Init();

            PrintWriter pw = network.getPw();
            System.out.println("Inside Async");
            pw.println("{\"ID\":0,\"SYSTEM\":9,\"RSSI\":0,\"NumberOfStations\":0,\"LATITUDE\":0,\"LONGITUDE\":0}");
            pw.flush();
           return null;
        }
    }

    public class readBuffer extends AsyncTask<Void, Void,String>{

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
            if(s == null){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                new readBuffer().execute();

            }else{
                new makeJsonObject().execute(s);
            }
        }
    }

    public class makeJsonObject extends AsyncTask<String, Void, LatLng>{

        @Override
        protected LatLng doInBackground(String... strings) {
            JSONObject json = null;
            int id = 0;
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
            }

            if(SYSTEM == 1){

                try {
                    foreignID = json.getInt("ID");
                    ID = Integer.toString(foreignID);
                    String lat = json.getString("LATITUDE");
                    double latitude = Double.parseDouble(lat);
                    String lon = json.getString("LONGITUDE");
                    double longitude = Double.parseDouble(lon);
                    markerPosition = new LatLng(latitude,longitude);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            if(SYSTEM == 7){
                System.out.println("json object read");
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

    public void makeMarker(LatLng position){

            Marker newMarker = mMap.addMarker(new MarkerOptions().position(position).title(ID));
            new readBuffer().execute();
    }

}
