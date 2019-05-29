package com.example.behere.utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.behere.actor.User;

import org.json.JSONObject;


import java.util.HashMap;
import java.util.Map;

public class ApiUsage {

    private final static String PATH_API = "http://31.220.61.74:8081/";

    private VolleyCallback mResultCallback = null;
    private Context mContext;

    public ApiUsage(VolleyCallback resultCallback, Context context){
        mResultCallback = resultCallback;
        mContext = context;
    }

    public void authentificate(String email, String password)
    {
        try {
            JSONObject params = new JSONObject();
            params.put("email", email);
            params.put("password", password);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, PATH_API+"authentificate", params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // response
                            if(mResultCallback != null)
                                mResultCallback.onSuccess(response);

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            Log.d("Error.Response", error.getMessage());
                            error.printStackTrace();
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };
            CacheContainer.getQueue().add(jsonObjectRequest);
        }catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }


    public void createAccount(User user)
    {
        try {
            JSONObject params = new JSONObject();
            params.put("email", user.getEmail());
            params.put("name", user.getName());
            params.put("surname", user.getSurname());
            params.put("password", user.getPassword());
            params.put("checkPassword", user.getCheckPassword());
            params.put("birthDate", user.getBirthDate());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, PATH_API+"users/create", params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // response
                            if(mResultCallback != null)
                                mResultCallback.onSuccess(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            Log.d("Error.Response", error.getMessage());
                            error.printStackTrace();
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };
            CacheContainer.getQueue().add(jsonObjectRequest);
        }catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public void getUser(long idUser)
    {
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, PATH_API+"users/"+idUser, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // response
                            if(mResultCallback != null)
                                mResultCallback.onSuccess(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            Log.d("Error.Response", error.getMessage());
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };
            CacheContainer.getQueue().add(jsonObjectRequest);
        }catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    public void getAllTypeOfBeer()
    {
        try {
            RequestQueue queue = Volley.newRequestQueue(mContext);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, PATH_API+"typeOfBeers", null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            if(mResultCallback != null)
                                mResultCallback.onSuccess(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            Log.d("Error.Response", error.getMessage());
                            error.printStackTrace();
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };
            queue.add(jsonObjectRequest);
        }catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public void getAllBar()
    {
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, PATH_API+"bars", null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // response
                            if(mResultCallback != null)
                                mResultCallback.onSuccess(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            Log.d("Error.Response", error.getMessage());
                            error.printStackTrace();
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };
            CacheContainer.getQueue().add(jsonObjectRequest);
        }catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public void addLinkBetweenBeerAndUser(long user_ID, int typeBeer_ID, String acces_token)
    {
        try {
            JSONObject params = new JSONObject();
            params.put("typeOfBeer_id", typeBeer_ID);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, PATH_API+"users/" + user_ID + "/addTypeOfBeer", params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // responses
                            if(mResultCallback != null)
                                mResultCallback.onSuccess(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            Log.d("Error.Response", error.getMessage());
                            error.printStackTrace();
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    headers.put("x-access-token", acces_token);

                    return headers;
                }
            };
            CacheContainer.getQueue().add(jsonObjectRequest);
        }catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

}
