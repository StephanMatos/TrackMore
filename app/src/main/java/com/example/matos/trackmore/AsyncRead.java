package com.example.matos.trackmore;
import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.IOException;

public class AsyncRead extends AsyncTask<Void,Void,String> {
    @Override
    protected String doInBackground(Void... Voids) {

        Network network = Network.getInstance();
        BufferedReader bir = network.getBir();
        String message = null;
        try {

            System.out.println(bir);
            if(network.getInputStream().available() > 0){
                message = bir.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Message is : "+message);
        if(message == null){
            try {
                Thread.sleep(1000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return message;
    }
    @Override
    protected void onPostExecute(String strings) {
        System.out.println("on post");

        if(strings == null){
            new AsyncRead().execute();
        }else{
            new AsyncJSON().execute(strings);
        }
        System.out.println("End of read");
    }
}



