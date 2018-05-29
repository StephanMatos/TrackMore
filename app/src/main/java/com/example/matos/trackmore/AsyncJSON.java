package com.example.matos.trackmore;
import android.os.AsyncTask;
import org.json.JSONException;
import org.json.JSONObject;

public class AsyncJSON extends AsyncTask<String, String, String[]> {

    @Override
    protected String[] doInBackground(String... strings) {
        int SYSTEM = 0;
        String ID = "";
        String lat = "";
        String lon = "";
        JSONObject json = null;

        try {
            json = new JSONObject(strings[0]);
            SYSTEM = (json.getInt("SYSTEM"));
        } catch (NullPointerException | JSONException e) {
            e.printStackTrace();
            System.out.println("json object failed");
        }

        if (SYSTEM == 2) {

            try {
                ID = json.getString("ID");
                lat = json.getString("LATITUDE");
                lon = json.getString("LONGITUDE");



                } catch (JSONException | NullPointerException e) {
                    e.printStackTrace();
                }

            }
            return new String[]{lat,lon,ID};
        }

        @Override
        protected void onPostExecute(String[] strings) {
        String lat = strings[0];
        String lon = strings[1];
        String ID = strings[2];

            if(strings[0].equals("")){
                new AsyncRead().execute();
            } else {
                TrackingGroupActivity.makeMarker(lat,lon,ID);

            }

            System.out.println("OnPostExecute in MakeJsonObject");
        }
    }


