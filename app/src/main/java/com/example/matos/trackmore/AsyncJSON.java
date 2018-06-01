package com.example.matos.trackmore;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

public class AsyncJSON extends AsyncTask<String, String, String[]> {

    @Override
    protected String[] doInBackground(String... strings) {
        int SYSTEM = 0;
        String ID = "";
        String lat = "";
        String lon = "";
        int RSSI = 0;
        JSONObject json;
        try {
        json = new JSONObject(strings[0]);
        SYSTEM = (json.getInt("SYSTEM"));
        ID = json.getString("ID");
        lat = json.getString("LATITUDE");
        lon = json.getString("LONGITUDE");

        if (SYSTEM == 2) {
            RSSI = json.getInt("RSSI");

            }

        }catch(JSONException | NullPointerException e){
                e.printStackTrace(); }

            return new String[]{lat,lon,ID,Integer.toString(SYSTEM)};
        }


        @Override
        protected void onPostExecute(String[] strings) {
        String lat = strings[0];
        String lon = strings[1];
        String ID = strings[2];
        String sys = strings[3];
        System.out.println(sys);

            if(sys.equals("2")) {
                TrackingGroupActivity.makeMarker(lat,lon,ID,false);

            } else if(sys.equals("3")){
                TrackingSportActivity.addPosition(lat,lon,ID);
            }else {
                new AsyncRead().execute();
            }
        }
    }


