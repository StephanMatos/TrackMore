package com.example.matos.trackmore;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.JsonReader;
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
import com.google.maps.android.SphericalUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import javax.net.ssl.HttpsURLConnection;

public class TrackingIndividualActivity extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap mMap;
    LatLng CurrentPosition;
    LatLng markerPosition;
    Handler h = new Handler();
    int delay = 10 * 500;
    private ImageButton dropDownButton;
    static Network network = Network.getInstance();
    private static boolean action;
    String httpsValue = "";


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_individual);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //new tcp().execute();

        new HttpsGetRequest().execute();

        dropDownButton = findViewById(R.id.dropdownButton);
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

                        if (menuItem.getGroupId() == R.id.ShowDistance && markerPosition != null  ){

                            double distance = SphericalUtil.computeDistanceBetween(CurrentPosition, markerPosition);

                            if (distance < 10.00 && distance > 1.00) {
                                DecimalFormat twodecimalDistance = new DecimalFormat("0.00");
                                distance /= 1000;
                                Toast.makeText(TrackingIndividualActivity.this, menuItem.getTitle() + " to marker: " + twodecimalDistance.format(distance) + " km", Toast.LENGTH_LONG).show();

                            } else if (distance < 1.00){
                                DecimalFormat onedecimalDistance = new DecimalFormat("0.0");
                                Toast.makeText(TrackingIndividualActivity.this, menuItem.getTitle() + " to marker: " + onedecimalDistance.format(distance) + " m", Toast.LENGTH_LONG).show();
                            }
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
    public void onMapReady(GoogleMap googleMap) throws NullPointerException {
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
                }
                action = false;
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
                pw.println("{\"ID\":0,\"SYSTEM\":1,\"RSSI\":0,\"NumberOfStations\":0,\"LATITUDE\":0,\"LONGITUDE\":0}");
                pw.flush();
            }
            return null;
        }
    }

    // Access the LoRa data through http-request
    public class HttpsGetRequest extends AsyncTask<Void, Void ,Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                URL url = new URL("https://trackmore.data.thethingsnetwork.org/#!/query/get_api_v2_query");
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                HttpsURLConnection.setDefaultRequestProperty("Authorization", "ttn-account-v2.Cr9EGHQeqfyvMb0oB8gYNIwIBVJi4hPBg2i5fsYLUmI");

                if (httpsURLConnection.getResponseCode() == 200) {
                    InputStream inputStream = httpsURLConnection.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                    BufferedReader bir = new BufferedReader(inputStreamReader);

                    String str;
                    while ((str = bir.readLine()) != null) {
                        System.out.println(str);
                    }

                } else {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Could not fetch data from https");
                    new HttpsGetRequest().execute();
                }



            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}