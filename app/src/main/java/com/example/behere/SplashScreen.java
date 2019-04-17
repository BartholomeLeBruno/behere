package com.example.behere;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends Activity {

    private static int SPLASH_TIME_OUT = 1000;
    private static final String PREFS = "PREFS";
    private static final String PREFS_ID = "USER_ID";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sharedPreferences = getBaseContext().getSharedPreferences(PREFS, MODE_PRIVATE);

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                if (sharedPreferences.contains(PREFS_ID)) {
                    Intent mapActivity = new Intent(SplashScreen.this, MapActivity.class);
                    startActivity(mapActivity);
                }
                else{
                    Intent loginActivity = new Intent(SplashScreen.this, LoginActivity.class);
                    startActivity(loginActivity);
                }
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
