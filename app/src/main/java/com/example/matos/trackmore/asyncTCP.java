package com.example.matos.trackmore;

import android.os.AsyncTask;


import java.io.PrintWriter;


public class asyncTCP extends AsyncTask<Integer,Void,Integer>{
    private Network network = Network.getInstance();

    protected Integer doInBackground(Integer... integers) {
            int retry = 0;
                while(!network.Init()){
                    System.out.println("inside loop tcp");
                    try {
                    Thread.sleep(5000);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    if(retry > 2){
                        if(integers[0] == '1'){
                          trackingIndividualActivity.stop();
                        } else if(integers[0] == '2'){
                          trackingGroupActivity.stop();
                        } else if (integers[0] == '3'){
                            
                        }
                        break;
                    }
                    retry++;
                }



            PrintWriter pw = network.getPw();
            System.out.println(pw);
            if(pw != null){
                pw.println("{\"SYSTEM\":9}");
                if(integers[0] == '1'){
                    pw.println("{\"SYSTEM\":1}");
                } else if(integers[0] == '2'){
                    pw.println("{\"SYSTEM\":2}");
                } else if (integers[0] == '3'){
                    pw.println("{\"SYSTEM\":3}");
                }

                pw.flush();
            }else{
                cancel(true);
                System.out.println(isCancelled());

            }

          return integers[0];
        }

    @Override
    protected void onPostExecute(Integer integer) {

            if(integer == 1){
                new asyncGETLoRa().execute("function1");
            }else{
                new asyncRead().execute();
            }


    }
}
