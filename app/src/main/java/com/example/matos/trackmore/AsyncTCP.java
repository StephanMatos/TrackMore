package com.example.matos.trackmore;

import android.os.AsyncTask;
import java.io.PrintWriter;

public class AsyncTCP extends AsyncTask<Integer,Void,Void>{
        Network network = Network.getInstance();
        protected Void doInBackground(Integer... integers) {
            System.out.println("in tcp");

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
                if(integers[0] == '1'){
                    pw.println("{\"ID\":0,\"SYSTEM\":1,\"RSSI\":0,\"NumberOfStations\":0,\"LATITUDE\":0,\"LONGITUDE\":0}");
                } else if(integers[0] == '2'){
                    pw.println("{\"ID\":0,\"SYSTEM\":2,\"RSSI\":0,\"NumberOfStations\":0,\"LATITUDE\":0,\"LONGITUDE\":0}");
                } else if (integers[0] == '3'){
                    pw.println("{\"ID\":0,\"SYSTEM\":3,\"RSSI\":0,\"NumberOfStations\":0,\"LATITUDE\":0,\"LONGITUDE\":0}");
                }


                pw.flush();
            }

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

    @Override
    protected void onPostExecute(Void aVoid) {
      new AsyncRead().execute();
    }
}
