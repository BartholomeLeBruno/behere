package com.example.behere.utils;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import static com.facebook.FacebookSdk.getApplicationContext;

public class CacheContainer {

    static RequestQueue getQueue() {
        return queue;
    }

    private static RequestQueue queue;


    public static void initializeQueue()
    {
        queue = Volley.newRequestQueue(getApplicationContext());
    }

}
