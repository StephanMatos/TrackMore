package com.example.matos.trackmore;
import android.os.AsyncTask;
import android.util.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class asyncGETLoRa extends AsyncTask<String,Void,String[]>{



    @Override
    protected String[] doInBackground(String... strings) {

        System.out.println("begin");

            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        String data ="";
        String ID = "";
        String lat = "";
        String lon = "";
        String coded;
        String decoded;
        String metadata = "";
        String time = "";


        try {
            // This block reads data from the specified URL
            URL url = new URL("https://api.myjson.com/bins/z99wm");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";

            while(line != null){
                line = bufferedReader.readLine();
                data = data + line;
            }

            // Creates a JSON object from the text read from URL
            JSONObject json;
            json = new JSONObject(data);
            coded = json.getString("payload_raw");
            metadata = json.getString("metadata");
            JSONObject json2;
            json2 = new JSONObject(metadata);
            time = json2.getString("time");
            System.out.println(time.substring(11, 16));
            time = time.substring(11,16) + " d. " +time.substring(8,10)+ "-" + time.substring(5,7);

            // Decodes raw payload
            byte[] valueDecoded;
            valueDecoded = Base64.decode(coded.getBytes("UTF-8"), Base64.DEFAULT);
            decoded = new String(valueDecoded);

            // Creates a JSON object with the decoded data and extract ID, Latitude and Longitude
            json = new JSONObject(decoded);
            ID = json.getString("ID");
            lat = json.getString("LATITUDE");
            lon = json.getString("LONGITUDE");
            System.out.println(ID + lat + lon);

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }




        String[] finalstrings = {strings[0],ID,lat,lon,time};
        return finalstrings;
    }

    @Override
    protected void onPostExecute(String[] s) {
        System.out.println(s[0] + "   " + s[1] + "    " + s[2] + "     "  + s[3]);
        if(s[0].equals("function1")){
            System.out.println("in 1");

            trackingIndividualActivity.update(s[1],s[2],s[3],s[4]);
        }else if (s[0].equals("function1")){
            System.out.println("in 2 ");
            trackingGroupActivity.makeMarker(s[2],s[3],s[1],true);

        } else{

            new asyncGETLoRa().execute(s[0]);
        }


    }
}
