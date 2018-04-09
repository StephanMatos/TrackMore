package com.example.matos.trackmore;

import android.app.Application;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.Socket;


public class Network extends Application {

    private static Network instance = null;
    private static Socket sock;
    private static BufferedReader bir;
    private static PrintWriter pw;

    public Network(){

        Socket sock;
        BufferedReader bir;
        PrintWriter pw;
    }

    public static Network getInstance() {
        if (instance == null) {
            instance = new Network();
        }
        return(instance);
    }

    public void Init (){
        try {
            sock = new Socket("192.168.1.2", 8888);
            System.out.println(sock);
            bir = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            pw = new PrintWriter(sock.getOutputStream());
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("init failed");
        }
    }

    public Socket getSock(){
        return sock;
    }
    public BufferedReader getBir(){
        return bir;
    }
    public PrintWriter getPw(){
        return pw;
    }


}

