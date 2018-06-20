package com.example.matos.trackmore;

import android.app.Application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class Network extends Application {

    private static Network instance = null;
    private static Socket sock;
    private static InputStream inputStream;
    private static InputStreamReader inputStreamReader;
    private static BufferedReader bir;
    private static PrintWriter pw;
    private boolean succes;

    public Network(){


    }

    public static Network getInstance() {
        if (instance == null) {
            instance = new Network();
        }
        return(instance);
    }

    public boolean Init (){

        String ip = "192.168.1.2";
        try {
            sock = new Socket(ip, 8888);
            inputStream = sock.getInputStream();
            bir = new BufferedReader(new InputStreamReader(inputStream));
            pw = new PrintWriter(sock.getOutputStream());

        }catch (IOException e){
            e.printStackTrace();
            System.out.println("init failed");
            succes = false;
            return succes;
        }
        succes = true;
        return succes;
    }

    public InputStream getInputStream(){
        return inputStream;

    }
    public BufferedReader getBir(){
        return bir;
    }
    public PrintWriter getPw(){
        return pw;
    }

}

