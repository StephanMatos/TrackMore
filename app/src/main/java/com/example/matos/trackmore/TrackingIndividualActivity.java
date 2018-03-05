package com.example.matos.trackmore;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import java.io.PrintWriter;
import java.util.ArrayList;

public class TrackingIndividualActivity extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap mMap;

    private ArrayList<Marker> markerArray1 = new ArrayList<Marker>();
    private ArrayList<Marker> markerArray2 = new ArrayList<Marker>();
    Handler h = new Handler();
    int delay = 10 * 500;
    int count = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_individual);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        new tcp().execute();

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

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        // Redraws map every delay's time
        h.postDelayed(new Runnable(){
            public void run(){
                recieveGPS();
                h.postDelayed(this, delay);
            }
        }, delay);

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

    private class makeMark extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {

            return null;
        }
    }




    public class tcp extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... params) {

            Network net = Network.getInstance();
            net.Init();
            PrintWriter pw = net.getPw();
            System.out.println("Inside Async");

            pw.println("Hello");
            pw.flush();

            return null;
        }


    }
}
