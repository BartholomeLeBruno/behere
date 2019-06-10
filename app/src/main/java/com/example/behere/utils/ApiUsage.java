package com.example.behere.utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.behere.actor.User;

import org.json.JSONObject;


import java.util.HashMap;
import java.util.Map;

public class ApiUsage {

    private final static String PATH_API = "http://31.220.61.74:8081/";

    private VolleyCallback mResultCallback;

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
            postData(params,PATH_API+"authentificate");
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
            postData(params,PATH_API+"users/create");
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
            postDataWithAccessToken(params, PATH_API+"users/" + user_ID + "/addTypeOfBeer", acces_token);
        }catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public void addCommentsToBar(String text, int bar_id, String acces_token)
    {
        try {
            JSONObject params = new JSONObject();
            params.put("text", text);
            params.put("bar_id", bar_id);
            postDataWithAccessToken(params, PATH_API+"commentsBars/create", acces_token);
        }catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public void getAllCommentsBar(int bar_id, int user_id)
    {
        try {
            getData(PATH_API+"commentsBars/?bar_id= " + bar_id + "&user_id=" + user_id + "");
        }catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public void getAllCommentsBeer(int beer_id, int user_id)
    {
        try {
            getData(PATH_API+"commentsBeers/?beer_id= " + beer_id + "&user_id=" + user_id + "");
        }catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public void getAllCommentsBrewery(int brewery_id, int user_id)
    {
        try {
            getData(PATH_API+"commentsBrewerys/?brewery_id= " + brewery_id + "&user_id=" + user_id + "");
        }catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }



    public void getUser(long idUser)
    {
        try {
            getData(PATH_API+"users/" + idUser);
        }catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public void getAllComments(long idUser)
    {
        try {
            getData(PATH_API+"/generals/commentsUser/" + idUser);
        }catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public void getAllTypeOfBeer()
    {
        try {
            getData(PATH_API+"typeOfBeers");
        }catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public void getAllBar()
    {
        try {
            getData(PATH_API+"bars");
        }catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }


    private void getData(String url) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
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
                        Log.d("Error.Response", " " + error.getMessage());
                        error.printStackTrace();
                        if(mResultCallback != null)
                            mResultCallback.onError(error);

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
    }

    private void postData(JSONObject params, String url) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params,
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
                        Log.d("Error.Response", error.getMessage() +" ");
                        if(mResultCallback != null)
                            mResultCallback.onError(error);
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
    }

    private void postDataWithAccessToken(JSONObject params, String url, String acces_token) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params,
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
                        Log.d("Error.Response", error.getMessage() +" ");
                        if(mResultCallback != null)
                            mResultCallback.onError(error);
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
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

}
