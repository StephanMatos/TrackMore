package com.example.matos.trackmore;


import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;


public class AsyncRead extends AsyncTask<Integer,Void,String[]> {
    @Override
    protected String[] doInBackground(Integer... integers) {
        System.out.println("in read");
        Network network = Network.getInstance();
        BufferedReader bir = network.getBir();
        String message = null;
        try {
            message = bir.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Message is : "+message);
        if(message == null){
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String function = integers[0].toString();
        return new String[]{message,function};

    }

    @Override
    protected void onPostExecute(String[] strings) {
        System.out.println("on post");

        int function = Integer.parseInt(strings[1]);
        if(strings[0] == null){
            new AsyncTCP().execute(function);
        }else{
            new AsyncJSON().execute(strings[0]);
        }
    }
}



