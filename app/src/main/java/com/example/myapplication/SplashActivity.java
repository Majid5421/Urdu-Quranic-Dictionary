package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DURATION = 3000; // Splash screen duration in milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        // Set the navigation bar color to green
        getWindow().setNavigationBarColor(getResources().getColor(R.color.green));


        // Delay for SPLASH_DURATION milliseconds and then start MainActivity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start MainActivity
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                // Finish this activity
                finish();
            }
        }, SPLASH_DURATION);
    }
}
