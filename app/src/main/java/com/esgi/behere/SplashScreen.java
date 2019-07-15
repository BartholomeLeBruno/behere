package com.esgi.behere;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.android.volley.error.VolleyError;
import com.esgi.behere.utils.ApiUsage;
import com.esgi.behere.utils.VolleyCallback;

import org.json.JSONObject;

import static com.esgi.behere.utils.CacheContainer.initializeQueue;

public class SplashScreen extends Activity {

    private SharedPreferences sharedPreferences;
    private VolleyCallback mResultCallback = null;
    private ApiUsage mVolleyService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sharedPreferences = getBaseContext().getSharedPreferences(getString(R.string.prefs), MODE_PRIVATE);
        initializeQueue();
        int SPLASH_TIME_OUT = 1000;
        new Handler().postDelayed(() ->
        {
                try {
                    if (sharedPreferences.contains(getString(R.string.prefs_id))) {
                        prepareGetUser();
                        mVolleyService = new ApiUsage(mResultCallback,getApplicationContext());
                        mVolleyService.getUser(sharedPreferences.getLong(getString(R.string.prefs_id), 0));
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

    private void prepareGetUser(){
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (!(boolean) response.get("error")) {
                        try {
                            Intent mapActivity = new Intent(SplashScreen.this, MapActivity.class);
                            mapActivity.putExtra("userID", sharedPreferences.getLong(getString(R.string.prefs_id), 0));
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
