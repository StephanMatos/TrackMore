package com.example.matos.trackmore;


import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;


public class AsyncRead extends AsyncTask<Void,Void,String> {
    @Override
    protected String doInBackground(Void... voids) {
        System.out.println("in read");
        Network network = Network.getInstance();
        BufferedReader bir = network.getBir();
        String message = "Null";
        try {
            message = bir.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Message is : "+message);
        if(message.equals("Null")){
            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return message;
    }

    @Override
    protected void onPostExecute(String s) {
        if(s.equals("Null")){
            new AsyncRead().execute();
        }else{
            new AsyncJSON().execute(s);
        }
    }
}



