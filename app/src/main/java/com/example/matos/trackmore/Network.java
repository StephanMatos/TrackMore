package com.example.matos.trackmore;

import android.app.Application;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class Network extends Application {

    private static Network instance = null;
    private static Socket sock;
    private static BufferedReader bir;
    private static PrintWriter pw;
    private boolean succes = true;

    public Network(){

        sock = new Socket();
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
            bir = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            pw = new PrintWriter(sock.getOutputStream());
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("init failed");
            succes = false;
            return succes;
        }

        return succes;
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

