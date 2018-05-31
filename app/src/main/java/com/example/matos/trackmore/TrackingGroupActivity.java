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
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

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
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;


public class TrackingGroupActivity extends AppCompatActivity implements OnMapReadyCallback {

    // google map
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static GoogleMap mMap;
    private float zoom = 17;

    // Location markers
    private static Marker RedMarkerC, YellowMarkerC,  GreenMarkerC, BlueMarkerC;
    private static LatLng CurrentPosition, markerPosition;
    public static LocationManager lm;
    public static Location location;

    // Distance
    private static double RedCurrent = 0.0, RedPrev = 0.0, YellowCurrent = 0.0, YellowPrev = 0.0, GreenCurrent = 0.0, GreenPrev = 0.0, BlueCurrent = 0.0, BluePrev = 0.0;
    private static TextView currentDistance, previousDistance, direction;

    // Device ID
    private static ArrayList<String> macID = new ArrayList<>();
    private static int internalID;

    // Timeout for LoRa
    public static int countRED,countYellow,countBLUE,countGreen;
    public static boolean RED,GREEN,YELLOW,BLUE;
    private static Context mContext;


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
    }

    @Override
    public void onBackPressed(){
        new AlertDialog.Builder(this)
                .setTitle("EXIT")
                .setMessage("Exit will delete data and end connection")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        TrackingGroupActivity.super.onBackPressed();

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

        // Location of device, zoom to location of device
        try{
            mMap.setMyLocationEnabled(true);
            lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            CurrentPosition = latLng;
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        } catch (NullPointerException e){
            e.printStackTrace();
        }


        new AsyncTCP().execute(Integer.valueOf('2'));
    }

    public static void makeMarker(String lat, String lon, String ID){

        double latitude = Double.parseDouble(lat);
        double longitude = Double.parseDouble(lon);
        markerPosition = new LatLng(latitude,longitude);

        double distance = SphericalUtil.computeDistanceBetween(CurrentPosition, markerPosition);
        if(distance > 1000){
            new AsyncRead().execute();
            return;
        }
            internalID = translateID(ID);
            count(internalID);

            if (internalID == 1) {
                RED = true;
                if(RedMarkerC != null){
                    RedMarkerC.remove();
                }
                RedMarkerC = mMap.addMarker(new MarkerOptions().position(markerPosition).title(String.valueOf(internalID)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                MarkerClick(RedMarkerC);

                RedPrev = RedCurrent;
                RedCurrent = distance;


            } else if (internalID == 2) {
                YELLOW = true;

                if(YellowMarkerC != null){
                    YellowMarkerC.remove();
                }

                YellowMarkerC = mMap.addMarker(new MarkerOptions().position(markerPosition).title(String.valueOf(internalID)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                MarkerClick(YellowMarkerC);

                YellowPrev = YellowCurrent;
                YellowCurrent = distance;

            } else if (internalID == 3) {
                GREEN = true;

                if(GreenMarkerC != null){
                    GreenMarkerC.remove();
                }

                GreenMarkerC = mMap.addMarker(new MarkerOptions().position(markerPosition).title(String.valueOf(internalID)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                MarkerClick(GreenMarkerC);

                GreenPrev = GreenCurrent;
                GreenCurrent = distance;

            } else if (internalID == 4) {
                BLUE = true;
                if(BlueMarkerC != null){
                    BlueMarkerC.remove();
                }

                BlueMarkerC = mMap.addMarker(new MarkerOptions().position(markerPosition).title(String.valueOf(internalID)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                MarkerClick(BlueMarkerC);

                BluePrev = BlueCurrent;
                BlueCurrent = distance;
            }

            new AsyncRead().execute();
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

    public static void stop(){
        Intent newIntent = new Intent(mContext,HomeActivity.class);
        mContext.startActivity(newIntent);
        ((Activity) mContext).finish();
    }

    public static void MarkerClick(Marker marker){

        marker.setTag(markerPosition);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {

                // Create custom dialog object
                final Dialog dialog = new Dialog(mContext);
                // Include dialog.xml file
                dialog.setContentView(R.layout.dialog_group_individuel);

                currentDistance = dialog.findViewById(R.id.curDis_Marker);
                previousDistance = dialog.findViewById(R.id.preDis_Marker);
                direction = dialog.findViewById(R.id.direction_Marker);


                if(marker.getTitle().equals("1")){
                    SetDialogTextView(RedCurrent, RedPrev);
                    System.out.println("inside equals 1");
                } else if(marker.getTitle().equals("2")){
                    SetDialogTextView(YellowCurrent, YellowPrev);
                    System.out.println("inside equals 2");
                } else if(marker.getTitle().equals("3")){
                    SetDialogTextView(GreenCurrent, GreenPrev);
                    System.out.println("inside equals 3");
                }else if(marker.getTitle().equals("4")){
                    SetDialogTextView(BlueCurrent, BluePrev);
                    System.out.println("inside equals 3");
                }
                dialog.show();
                return false;
            }
        });

    }

    public static void SetDialogTextView(double current, double prev){
        int c = (int) current;
        int p = (int) prev;
        currentDistance.setText(String.valueOf(c));
        previousDistance.setText(String.valueOf(p));
        direction.setText(String.valueOf(c - p));
    }

}
