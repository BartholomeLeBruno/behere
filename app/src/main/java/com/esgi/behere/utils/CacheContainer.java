package com.esgi.behere.utils;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.esgi.behere.actor.Market;
import com.esgi.behere.actor.ResultSearch;
import com.esgi.behere.actor.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

public class CacheContainer {

    public static RequestQueue getQueue() {
        return queue;
    }

    private static RequestQueue queue;
    private static HashMap<String, Market> marketHashMap;
    private static List<ResultSearch> resultSearches;


    private static List<User> friends;

    private CacheContainer()
    {
        marketHashMap = new HashMap<>();
        friends = new ArrayList<>();
        resultSearches = new ArrayList<>();
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

    public  List<ResultSearch> getResultSearches() {
        return resultSearches;
    }

    public HashMap<String,Market> getMarketHashMap()
    {
        return marketHashMap;
    }

    public List<User> getFriends() { return friends; }

    public String getStringEntities() {
        return stringEntities;
    }

    public void setStringEntities(String stringEntities) {
        CacheContainer.stringEntities = stringEntities;
    }

    private static String stringEntities;


}
