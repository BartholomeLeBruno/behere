package com.example.behere;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.example.behere.utils.ApiUsage;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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
                    JSONObject jsonObject = ApiUsage.getUser(sharedPreferences.getLong(PREFS_ID,0));
                    if(!(boolean) jsonObject.get("error"))
                    {
                        try {
                            JSONParser parser = new JSONParser();
                            Object obj = parser.parse(jsonObject.get("user").toString());
                            JSONObject objres = (JSONObject) parser.parse(jsonObject.get("user").toString());
                            mapActivity.putExtra("userID", sharedPreferences.getLong(PREFS_ID, 0));
                            startActivity(mapActivity);
                        }
                        catch (Exception e)
                        {
                            throw new RuntimeException(e);
                        }
                    }
                    else
                    {
                        Intent loginActivity = new Intent(SplashScreen.this, LoginActivity.class);
                        startActivity(loginActivity);
                    }
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
