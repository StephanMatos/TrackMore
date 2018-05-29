package com.example.matos.trackmore;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
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
import com.google.maps.android.SphericalUtil;
import java.text.DecimalFormat;
import java.util.ArrayList;


public class TrackingGroupActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static TrackingGroupActivity instance = null;

    // google map
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static GoogleMap mMap;
    private final String TAG = TrackingGroupActivity.class.getSimpleName();
    private static Location mLastKnownLocation;
    private float zoom = 17;
    Handler h = new Handler();
    int delay = 10 * 500;

    // Location markers
    private static ArrayList<Marker> red = new ArrayList<>();
    private static ArrayList<Marker> yellow = new ArrayList<>();
    private static ArrayList<Marker> green = new ArrayList<>();
    private static ArrayList<Marker> blue = new ArrayList<>();
    private static LatLng CurrentPosition, markerPosition;
    public static LocationManager lm;
    public static Location location;
    private static boolean action;
    // Device ID
    private static ArrayList<String> macID = new ArrayList<>();
    private static int internalID;

    // image Button
    private ImageButton dropDownButton;

    // Timeout for LoRa
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
    public void onMapReady(GoogleMap googleMap) {
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

        new AsyncTCP().execute(Integer.valueOf('2'));
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


    public static void makeMarker(String lat, String lon, String ID){

        double latitude = Double.parseDouble(lat);
        double longitude = Double.parseDouble(lon);
        markerPosition = new LatLng(latitude,longitude);

        internalID = translateID(ID);
        int size;
        count(internalID);
        action = true;

        System.out.println(markerPosition + ID);
        if(internalID == 1){
            RED = true;
            Marker redMarker = mMap.addMarker(new MarkerOptions().position(markerPosition).title(ID).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
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
            Marker yellowMarker = mMap.addMarker(new MarkerOptions().position(markerPosition).title(ID).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
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
            Marker greenMarker = mMap.addMarker(new MarkerOptions().position(markerPosition).title(ID).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
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
            Marker blueMarker = mMap.addMarker(new MarkerOptions().position(markerPosition).title(ID).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
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
