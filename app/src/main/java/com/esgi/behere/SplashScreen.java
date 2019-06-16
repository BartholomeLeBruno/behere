package com.esgi.behere;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.android.volley.VolleyError;
import com.esgi.behere.utils.ApiUsage;
import com.esgi.behere.utils.VolleyCallback;

import org.json.JSONObject;

import static com.esgi.behere.utils.CacheContainer.initializeQueue;

public class SplashScreen extends Activity {

    static int SPLASH_TIME_OUT = 1000;
    private static final String PREFS = "PREFS";
    private static final String PREFS_ID = "USER_ID";
    private SharedPreferences sharedPreferences;
    private String TAG = "SplashScreen";
    private VolleyCallback mResultCallback = null;
    private ApiUsage mVolleyService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sharedPreferences = getBaseContext().getSharedPreferences(PREFS, MODE_PRIVATE);
        initializeQueue();
        new Handler().postDelayed(() ->
        {
                try {
                    if (sharedPreferences.contains(PREFS_ID)) {
                        prepareGetUser();
                        mVolleyService = new ApiUsage(mResultCallback,getApplicationContext());
                        mVolleyService.getUser(sharedPreferences.getLong(PREFS_ID, 0));
                    } else {
                        Intent loginActivity = new Intent(SplashScreen.this, LoginActivity.class);
                        startActivity(loginActivity);
                    }
                    finish();
                }
                catch (Exception e)
                {
                    Intent loginActivity = new Intent(SplashScreen.this, LoginActivity.class);
                    startActivity(loginActivity);
                    Log.e("error splashScreen",e.getMessage());
                }
        }, SPLASH_TIME_OUT);
    }

    void prepareGetUser(){
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    Log.d(TAG, response.toString());
                    if (!(boolean) response.get("error")) {
                        try {
                            Intent mapActivity = new Intent(SplashScreen.this, MapActivity.class);
                            mapActivity.putExtra("userID", sharedPreferences.getLong(PREFS_ID, 0));
                            startActivity(mapActivity);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        Intent loginActivity = new Intent(SplashScreen.this, LoginActivity.class);
                        startActivity(loginActivity);
                    }
                }
                catch (Exception e)
                {

                    throw new RuntimeException(e);
                }

            }
            @Override
            public void onError(VolleyError error) {
                Intent loginActivity = new Intent(SplashScreen.this, LoginActivity.class);
                startActivity(loginActivity);
            }
        };
    }
}
