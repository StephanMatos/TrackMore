package com.example.matos.trackmore;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
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


public class TrackingIndividualActivity extends FragmentActivity implements OnMapReadyCallback {

    private static Marker RedMarkerC;
    static GoogleMap mMap;
    private static LatLng CurrentPosition, markerPosition;
    private static double RedCurrent = 0.0, RedPrev = 0.0;
    private static TextView RedcurrentDistance, RedpreviousDistance, RedMarker;
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


        new AsyncGETLoRa().execute("function1", "FirstRun");
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        float zoom = 13;

        // Android needs to peform this check, otherwise location will not be shown
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Location of device, zoom to location of device
        mMap.setMyLocationEnabled(true);
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CurrentPosition = latLng;
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
        RedMarkerC = mMap.addMarker(new MarkerOptions().position(markerPosition).title("Device 1").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        MarkerClick(RedMarkerC);

        RedPrev = RedCurrent;
        RedCurrent = distance;

    }
    public  static void MarkerClick(Marker marker){

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
                    RedcurrentDistance.setText(String.valueOf(c));
                    RedpreviousDistance.setText(String.valueOf(p));
                    RedMarker.setText("Red");

                dialog.show();
                return false;
            }
        });

        System.out.println("run is over");
        new AsyncGETLoRa().execute("function1", "Not first run");


    }

}