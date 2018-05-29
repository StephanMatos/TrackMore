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
    private static TrackingGroupActivity instance = null;

    // google map
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private GoogleMap mMap;
    private final String TAG = TrackingGroupActivity.class.getSimpleName();
    private Location mLastKnownLocation;
    private float zoom = 17;
    Handler h = new Handler();
    int delay = 10 * 500;


    // Location markers
    private ArrayList<Marker> red = new ArrayList<>();
    private ArrayList<Marker> yellow = new ArrayList<>();
    private ArrayList<Marker> green = new ArrayList<>();
    private ArrayList<Marker> blue = new ArrayList<>();
    private LatLng CurrentPosition, markerPosition;
    public LocationManager lm;
    public Location location;

    // Device ID
    private ArrayList<String> macID = new ArrayList<>();
    private int internalID;

    // image Button
    private ImageButton dropDownButton;

    // Timeout for LoRa
    public int countRED,countYellow,countBLUE,countGreen;
    public boolean RED,GREEN,YELLOW,BLUE;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_individual);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        new AsyncTCP().execute();
        new AsyncRead().execute();

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
        /* Try without handler
        h.postDelayed(new Runnable() {
            public void run() {
                if (action) {
                    h.postDelayed(this, delay);
                    action = false;
                    System.out.println("redraw");
                }
            }
        }, delay);
        */

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


    public void makeMarker(String lat, String lon, String ID){

        double latitude = Double.parseDouble(lat);
        double longitude = Double.parseDouble(lon);
        LatLng position = new LatLng(latitude,longitude);

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

        new AsyncRead().execute();
    }

    private int translateID(String foreignID){

        if(!macID.contains(foreignID)){
            macID.add(foreignID);
            internalID = macID.indexOf(foreignID)+1;
        }else{
            internalID = macID.indexOf(foreignID)+1;
        }
        return internalID;
    }

    private void count(int id){
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

    public static TrackingGroupActivity getInstance() {
        if (instance == null) {
            instance = new TrackingGroupActivity();
        }
        return(instance);
    }
}
