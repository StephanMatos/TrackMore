package com.example.matos.trackmore;

import android.os.AsyncTask;
import java.io.PrintWriter;


public class AsyncTCP extends AsyncTask<Void,Void,Void>{
        protected Void doInBackground(Void... params) {
            Network network = Network.getInstance();
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
