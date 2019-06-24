package com.shashank.seconpart.svrecorder;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    ImageView splash1;
    private static int SPLASH_TIME_OUT= 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        splash1=findViewById(R.id.SplashScreen);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i= new Intent(MainActivity.this,RegisterLoginPage.class);
                startActivity(i);
                finish();
            }
        },SPLASH_TIME_OUT);
    }
}
