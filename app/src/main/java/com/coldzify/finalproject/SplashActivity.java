package com.coldzify.finalproject;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {
    Handler handler;
    Runnable runnable;
    long delay_time;
    long time = 3000L;
    private boolean isLoggedIn= false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        isLoggedIn =  mAuth.getCurrentUser() != null;
        handler = new Handler();

        runnable = new Runnable() {
            public void run() {

                if(isLoggedIn){
                    Intent intent = new Intent(SplashActivity.this, ReportActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        };

    }
    public void onResume() {
        super.onResume();
        delay_time = time;
        handler.postDelayed(runnable, delay_time);
        time = System.currentTimeMillis();
    }

    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
        time = delay_time - (System.currentTimeMillis() - time);
    }
}
