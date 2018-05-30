package com.example.matos.trackmore;

import android.app.Dialog;
import android.os.AsyncTask;


import java.io.PrintWriter;


public class AsyncTCP extends AsyncTask<Integer,Void,Void>{
    Network network = Network.getInstance();

    protected Void doInBackground(Integer... integers) {
            int retry = 0;
                while(!network.Init()){
                    System.out.println("inside loop tcp");
                    try {
                    Thread.sleep(10000);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    if(retry> 2){
                        TrackingGroupActivity.stop();
                        break;
                    }
                    retry++;
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
            }else{
                cancel(true);
                System.out.println(isCancelled());

            }

          return null;
        }

    @Override
    protected void onPostExecute(Void Void) {
            new AsyncRead().execute();
    }
}
