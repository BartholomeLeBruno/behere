package com.esgi.behere.utils;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.esgi.behere.actor.Market;

import java.util.HashMap;

import static com.facebook.FacebookSdk.getApplicationContext;

public class CacheContainer {

    static RequestQueue getQueue() {
        return queue;
    }

    private static RequestQueue queue;
    private static HashMap<String, Market> marketHashMap;

    private CacheContainer()
    {
        marketHashMap = new HashMap<>();
    }

    /** Instance unique non préinitialisée */
    private static CacheContainer INSTANCE = null;

    /** Point d'accès pour l'instance unique du singleton */
    public static synchronized CacheContainer getInstance()
    {
        if (INSTANCE == null)
        {   INSTANCE = new CacheContainer();
        }
        return INSTANCE;
    }

    public static void initializeQueue()
    {
        queue = Volley.newRequestQueue(getApplicationContext());
    }

    public HashMap<String,Market> getMarketHashMap()
    {
        return marketHashMap;
    }




}
