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
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;


public class trackingIndividualActivity extends FragmentActivity implements OnMapReadyCallback {

    private static Marker RedMarkerC;
    static GoogleMap mMap;
    private static LatLng CurrentPosition, markerPosition;
    private static double RedCurrent = 0.0, RedPrev = 0.0;
    private static TextView RedcurrentDistance, RedpreviousDistance, RedMarker;
    private static Context mContext;
    LocationManager lm;
    Location location;
    LatLng latLng;
    Handler h = new Handler();
    int delay = 30000;
    float zoom;



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

        //new asyncTCP().execute(Integer.valueOf('1'));
        new asyncGETLoRa().execute("function1");
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        zoom = 11;

        // Android needs to peform this check, otherwise location will not be shown
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);

        h.postDelayed(new Runnable(){
            @SuppressLint("MissingPermission")
            public void run(){

                h.postDelayed(this, delay);
                lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                latLng = new LatLng(location.getLatitude(), location.getLongitude());
                CurrentPosition = latLng;
                System.out.println("redraw");
            }
        }, delay);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));


    }

    public static void makeMarker(String ID,String lat, String lon){

        double latitude = Double.parseDouble(lat);
        double longitude = Double.parseDouble(lon);
        markerPosition = new LatLng(latitude,longitude);

        double distance = SphericalUtil.computeDistanceBetween(CurrentPosition, markerPosition);

        if(RedMarkerC != null){
            RedMarkerC.remove();
        }
        RedMarkerC = mMap.addMarker(new MarkerOptions().position(markerPosition).title(ID).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        RedPrev = RedCurrent;
        RedCurrent = distance;

        markerClick(RedMarkerC);



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

                    int c = (int) RedCurrent;
                    int p = (int) RedPrev;
                    RedcurrentDistance.setText(String.valueOf(c) + " meter");
                    RedpreviousDistance.setText(String.valueOf(p)+ " meter");
                    RedMarker.setText("Red");

                dialog.show();
                return false;
            }
        });

        System.out.println("run is over");
        new asyncGETLoRa().execute("function1");

    }

    public static void stop(){
        Intent newIntent = new Intent(mContext,HomeActivity.class);
        mContext.startActivity(newIntent);
        ((Activity) mContext).finish();
    }
    @Override
    public void onBackPressed(){
        new AlertDialog.Builder(this)
                .setTitle("EXIT")
                .setMessage("Exit will delete data and end connection")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        trackingIndividualActivity.super.onBackPressed();

                    }
                }).create().show();

    }

}