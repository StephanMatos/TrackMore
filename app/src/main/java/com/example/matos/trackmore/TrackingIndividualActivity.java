package com.example.matos.trackmore;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import com.google.android.gms.maps.model.PolylineOptions;
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

    private ArrayList<Marker> markerArray1 = new ArrayList<Marker>();
    private ArrayList<Marker> markerArray2 = new ArrayList<Marker>();
    LatLng CurrentPosition;
    LatLng MarkerPosition;
    Handler h = new Handler();
    int delay = 10 * 500;
    int count = 1;
    boolean ShowMarker = false;
    private ImageButton dropDownButton;


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
                PopupMenu popup = new PopupMenu(TrackingIndividualActivity.this, dropDownButton);
                // Inflating the popup using xml file
                popup.getMenuInflater().inflate(R.menu.popup_menu_individual, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        if (menuItem.getGroupId() == R.id.ShowDistance){

                            DecimalFormat twodecimalDistance = new DecimalFormat("0.00");

                            double distance = SphericalUtil.computeDistanceBetween(CurrentPosition, MarkerPosition)/1000;

                            Toast.makeText(TrackingIndividualActivity.this, menuItem.getTitle() + " to marker: " + twodecimalDistance.format(distance) + " Km", Toast.LENGTH_LONG).show();

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
                recieveGPS();
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


    public void addMarker(LatLng location, String id){

        if(id == "1") {
            MakeMarker(location,id, markerArray1,Color.RED);
        }

        if(id == "2") {
            MakeMarker(location,id, markerArray2,Color.BLUE);
        }
    }

    public void MakeMarker(LatLng location,String id,ArrayList<Marker> list, int color){

        Marker newMarker = mMap.addMarker(new MarkerOptions().position(location).title(id));
        list.add(newMarker);

        if(list.size() > 1){
            Marker lastPosition = list.get(0);
            mMap.addPolyline(new PolylineOptions().add(newMarker.getPosition(), lastPosition.getPosition()).width(5).color(color));
            lastPosition.remove();
            list.remove(0);
        }

    }

    public void recieveGPS(){
        LatLng DTU = new LatLng(55.786080, 12.519635);
        LatLng DTU1 = new LatLng(55.786070, 12.519635);
        MarkerPosition = DTU;


        LatLng DTU2 = new LatLng(55.786090, 12.519631);
        LatLng DTU3 = new LatLng(55.786100, 13.519635);


        LatLng DTU4 = new LatLng(55.786320, 12.539635);
        LatLng DTU5 = new LatLng(55.786110, 14.519635);

        LatLng DTU6 = new LatLng(55.786123, 12.512635);
        LatLng DTU7 = new LatLng(55.786140, 15.519615);

        LatLng DTU8 = new LatLng(55.723160, 12.513635);
        LatLng DTU9 = new LatLng(55.782180, 16.519445);




        if(count == 1){
            addMarker(DTU,"1");
           // addMarker(DTU1,"2");
        }
        if(count == 2){
            addMarker(DTU2,"1");
           // addMarker(DTU3,"2");
        }
        if(count == 3){
            addMarker(DTU4,"1");
           // addMarker(DTU5,"2");
        }
        if(count == 4){
            addMarker(DTU6,"1");
           // addMarker(DTU7,"2");
        }
        if(count == 5){
            addMarker(DTU8,"1");
           // addMarker(DTU9,"2");
        }

        count = count + 1;
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

    public void readJsonObject(JSONObject json) throws JSONException {

        int SYSTEM = (json.getInt("SYSTEM"));

        if(SYSTEM == 7){
            System.out.println("json object read");
        }


    }


    public class tcp extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... params) {
            Network net = Network.getInstance();
            net.Init();

            PrintWriter pw = net.getPw();
            System.out.println("Inside Async");
            pw.println("{\"ID\":0,\"SYSTEM\":9,\"RSSI\":0,\"NumberOfStations\":0,\"LATITUDE\":0,\"LONGITUDE\":0}");
            pw.flush();
            return null;
        }


    }

    public class readBuffer extends AsyncTask<Void, Void,String>{

        @Override
        protected String doInBackground(Void... voids) {
            Network net = Network.getInstance();
            BufferedReader bir = net.getBir();
            String message = null;
            try {
                if(bir.ready()){
                    message = bir.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(message);
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

    public class makeJsonObject extends AsyncTask<String, Void, JSONObject>{

        @Override
        protected JSONObject doInBackground(String... strings) {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(strings[0]);
            } catch (JSONException e) {
                e.printStackTrace();
                System.out.println("json object failed");
            }

            return  jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {

            try {
                readJsonObject(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


}
