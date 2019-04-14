package com.example.behere.utils;

import android.os.StrictMode;

import com.example.behere.actor.User;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ApiUsage {

    final static String PATH_API = "http://10.0.2.2:8081/";

    public static JSONObject authentificate(String email, String password){
        try{
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

            StrictMode.setThreadPolicy(policy);
            JSONObject body = new JSONObject();
            body.put("email", email);
            body.put("password", password);


            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost httpPost = new HttpPost(PATH_API+"authentificate/");
            httpPost.setHeader("Content-type", "application/json");
            //httpPut.setHeader("x-access-token", user.getAccessToken());

            StringEntity stringEntity = new StringEntity(body.toString());
            httpPost.setEntity(stringEntity);
            HttpResponse httpResponse = httpClient.execute(httpPost);

            String content = EntityUtils.toString(httpResponse.getEntity());
            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(content);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static JSONObject createAccount(User user){
        try{
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

            StrictMode.setThreadPolicy(policy);
            JSONObject body = new JSONObject();
            body.put("email", user.getEmail());
            body.put("name", user.getName());
            body.put("surname", user.getSurname());
            body.put("password", user.getPassword());
            body.put("checkPassword", user.getCheckPassword());
            body.put("birthDate", user.getBirthDate());

            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost httpPost = new HttpPost(PATH_API+"users/create/");
            httpPost.setHeader("Content-type", "application/json");
            //httpPut.setHeader("x-access-token", user.getAccessToken());

            StringEntity stringEntity = new StringEntity(body.toString());
            httpPost.setEntity(stringEntity);
            HttpResponse httpResponse = httpClient.execute(httpPost);

            String content = EntityUtils.toString(httpResponse.getEntity());
            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(content);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
