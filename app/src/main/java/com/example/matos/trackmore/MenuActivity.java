package com.example.matos.trackmore;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MenuActivity extends AppCompatActivity {
    private static int SPLASH_TIMEOUT = 3000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent HomeIntent = new Intent(MenuActivity.this, HomeActivity.class);
                startActivity(HomeIntent);
                finish();
            }
        },SPLASH_TIMEOUT);
    }
}
