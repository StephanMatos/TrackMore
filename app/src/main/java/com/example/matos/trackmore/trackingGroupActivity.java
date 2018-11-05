package com.example.matos.trackmore;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;

public class trackingGroupActivity extends AppCompatActivity implements OnMapReadyCallback {

    // google map
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static GoogleMap mMap;
    private float zoom = 17;

    // Location markers
    private static Marker RedMarkerC, YellowMarkerC,  GreenMarkerC, BlueMarkerC;
    private static LatLng CurrentPosition, markerPosition;

    // Distance
    private static double RedCurrent = 0.0, RedPrev = 0.0, YellowCurrent = 0.0, YellowPrev = 0.0, GreenCurrent = 0.0, GreenPrev = 0.0, BlueCurrent = 0.0, BluePrev = 0.0;
    private static TextView RedcurrentDistance, RedpreviousDistance, RedMarker,
            YellowcurrentDistance, YellowpreviousDistance, YellowMarker,
            GreencurrentDistance, GreenpreviousDistance, GreenMarker,
            BluecurrentDistance, BluepreviousDistance, BlueMarker;

    LocationManager lm;
    Location location;
    LatLng latLng;

    // Device ID
    private static ArrayList<String> macID = new ArrayList<>();
    private static int internalID;

    // Timeout for LoRa
    public static int countRED,countYellow,countBLUE,countGreen;
    public static boolean RED,GREEN,YELLOW,BLUE;


    // handler
    private static Context mContext;
    private Context context;
    Handler h = new Handler();
    int delay = 5000;
    static boolean distanceToGreat = false;
    static boolean fromLoRa = false;
    static boolean stop = false;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mContext = this;
        context = this;
    }

    @Override
    public void onBackPressed(){
        new AlertDialog.Builder(this)
                .setTitle("EXIT")
                .setMessage("Exit will delete data and end connection")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        trackingGroupActivity.super.onBackPressed();

                    }
                }).create().show();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Android needs to peform this check, otherwise location will not be shown
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        h.postDelayed(new Runnable(){
            @SuppressLint("MissingPermission")
            public void run(){

                h.postDelayed(this, delay);
                lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                latLng = new LatLng(location.getLatitude(), location.getLongitude());
                CurrentPosition = latLng;

                if(distanceToGreat){
                    new AlertDialog.Builder(context)
                            .setTitle("WARNING")
                            .setMessage("Client exceeds 75 meter distance")
                            .setPositiveButton("OK", null).create().show();
                    distanceToGreat = false;
                }
                if(fromLoRa){
                    new AlertDialog.Builder(context)
                            .setTitle("LoRa")
                            .setMessage("Data received using LoRa")
                            .setPositiveButton("OK", null).create().show();
                    distanceToGreat = false;
                }
                if(stop){
                    delay = 2000000000;
                    finish();

                }

                System.out.println("redraw");
            }
        }, delay);

        // Location of device, zoom to location of device
        try{
            mMap.setMyLocationEnabled(true);
            lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
            CurrentPosition = latLng;
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }


    public static void makeMarker(String lat, String lon, String ID, boolean LoRa){

        double latitude = Double.parseDouble(lat);
        double longitude = Double.parseDouble(lon);
        markerPosition = new LatLng(latitude,longitude);

        double distance = SphericalUtil.computeDistanceBetween(CurrentPosition, markerPosition);

                RED = true;

                RedMarkerC.remove();

                RedMarkerC = mMap.addMarker(new MarkerOptions().position(markerPosition).title(String.valueOf(internalID)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                markerClick(RedMarkerC);

                RedPrev = RedCurrent;
                RedCurrent = distance;





    }

    public void initMarkers(ArrayList<String> ID, ArrayList Colour){

        for(int i = 0; i < ID.size(); i++){
            String txt = ID.get(i);
            Marker newMarker;


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

    private static void count(int id, boolean LoRa){
        if(id == 1 && !LoRa){
            countRED = 0;
            countYellow++;
            countGreen++;
            countBLUE++;
        }else if( id == 2 && !LoRa){
            countRED++;
            countYellow = 0;
            countGreen++;
            countBLUE++;
        }else if (id == 3 && !LoRa){
            countRED++;
            countYellow++;
            countGreen = 0;
            countBLUE++;
        }else if (id == 4 && !LoRa){
            countRED++;
            countYellow++;
            countGreen++;
            countBLUE = 0;
        }

        if(countRED > 5 && RED || countYellow > 5 && YELLOW || countGreen > 10 && GREEN || countBLUE > 10 && BLUE){


        }

    }



    public  static void markerClick(Marker marker){

        marker.setTag(markerPosition);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {

                // Create custom dialog object
                final Dialog dialog = new Dialog(mContext);
                // Include dialog.xml file
                dialog.setContentView(R.layout.dialog_group_individuel);

                RedcurrentDistance = dialog.findViewById(R.id.curDis_redMarker);
                RedpreviousDistance = dialog.findViewById(R.id.preDis_redMarker);
                RedMarker = dialog.findViewById(R.id.redMarker);

                YellowcurrentDistance = dialog.findViewById(R.id.curDis_yellowMarker);
                YellowpreviousDistance = dialog.findViewById(R.id.preDis_yellowMarker);
                YellowMarker = dialog.findViewById(R.id.yellowMarker);

                GreencurrentDistance = dialog.findViewById(R.id.curDis_greenMarker);
                GreenpreviousDistance = dialog.findViewById(R.id.preDis_greenMarker);
                GreenMarker = dialog.findViewById(R.id.greenMarker);

                BluecurrentDistance = dialog.findViewById(R.id.curDis_blueMarker);
                BluepreviousDistance = dialog.findViewById(R.id.preDis_blueMarker);
                BlueMarker = dialog.findViewById(R.id.blueMarker);

                if(RED){
                    int c = (int) RedCurrent;
                    int p = (int) RedPrev;
                    RedcurrentDistance.setText(String.valueOf(c) + " meter");
                    RedpreviousDistance.setText(String.valueOf(p)+ " meter");
                    RedMarker.setText("Red");
                } if(YELLOW){
                    int c = (int) YellowCurrent;
                    int p = (int) YellowPrev;
                    YellowcurrentDistance.setText(String.valueOf(c)+ " meter");
                    YellowpreviousDistance.setText(String.valueOf(p)+ " meter");
                    YellowMarker.setText("Yellow");
                } if(GREEN){
                    int c = (int) GreenCurrent;
                    int p = (int) GreenPrev;
                    GreencurrentDistance.setText(String.valueOf(c)+ " meter");
                    GreenpreviousDistance.setText(String.valueOf(p)+ " meter");
                    GreenMarker.setText("Green");
                } if(BLUE){
                    int c = (int) YellowCurrent;
                    int p = (int) YellowPrev;
                    BluecurrentDistance.setText(String.valueOf(c)+ " meter");
                    BluepreviousDistance.setText(String.valueOf(p)+ " meter");
                    BlueMarker.setText("Blue");
                }
                dialog.show();
                return false;
            }
        });

    }


}
