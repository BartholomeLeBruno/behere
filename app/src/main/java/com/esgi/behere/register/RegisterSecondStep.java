package com.esgi.behere.register;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.esgi.behere.LoginActivity;
import com.esgi.behere.R;
import com.esgi.behere.actor.BeerType;
import com.esgi.behere.actor.User;
import com.esgi.behere.adapter.BeerTypeAdapter;
import com.esgi.behere.utils.ApiUsage;
import com.esgi.behere.utils.VolleyCallback;

import org.json.JSONTokener;
import org.json.simple.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RegisterSecondStep extends Activity {

    private ListView lvBeerType;
    public static List<Integer> finallistBeerType = new ArrayList<>();
    private VolleyCallback mResultCallback = null;
    private ApiUsage mVolleyService;
    private int idUser;
    List<BeerType> beerTypeList;
    SharedPreferences sharedPreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register_second_step);
        lvBeerType = findViewById(R.id.lvBeerType);
        Button btnRegister = findViewById(R.id.btnRegisterLastStep);
        implementList();
        btnRegister.setOnClickListener((View v) ->{
                    User newUser = (User) Objects.requireNonNull(getIntent().getExtras()).get("User");
                    Log.d("user", newUser.getEmail());
                    prepareCreateAccount();
                    assert newUser != null;
                    newUser.setPhone_id(getIdPhone());
                    newUser.setEmail(newUser.getEmail().trim());
                    mVolleyService = new ApiUsage(mResultCallback,getApplicationContext());
                    mVolleyService.createAccount(newUser);
        });
    }

    private void implementList()
    {
        try {
            prepareGetAllTypeOfBeer();
            mVolleyService = new ApiUsage(mResultCallback,getApplicationContext());
            mVolleyService.getAllTypeOfBeer();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

    }

    private void prepareGetAllTypeOfBeer(){
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if(!(boolean) response.get("error")) {
                        beerTypeList = new ArrayList<>();
                        JSONParser parser = new JSONParser();
                        JSONArray res = (JSONArray) parser.parse(response.get("typeOfBeer").toString());
                        for (Object unres : res) {
                            JSONObject objres = (JSONObject) new JSONTokener(unres.toString()).nextValue();
                            BeerType beerType = new BeerType((String) objres.get("name"));
                            beerType.setId((int) objres.get("id"));
                            beerTypeList.add(beerType);
                        }
                        BeerTypeAdapter beerTypeAdapter = new BeerTypeAdapter(getApplicationContext(), beerTypeList);
                        lvBeerType.setAdapter(beerTypeAdapter);
                    }
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }

            }
            @Override
            public void onError(VolleyError error) { }
        };
    }

    private String getIdPhone() {
        sharedPreferences = getApplicationContext().getSharedPreferences("PREFS", MODE_PRIVATE);
        Log.d("voila",sharedPreferences.getString("FCM_ID","0"));
        if(sharedPreferences.getString("FCM_ID","0").equals("0"))
        {
            return "empty";
        }
        else
        return sharedPreferences.getString("FCM_ID","0");
    }

    private void prepareCreateAccount(){
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (!(boolean) response.get("error")) {
                        Log.d("prepareAccount", response.toString());
                        User newUser = (User) Objects.requireNonNull(getIntent().getExtras()).get("User");
                        assert newUser != null;
                        JSONObject objres = (JSONObject) new JSONTokener(response.get("user").toString()).nextValue();
                        idUser = objres.getInt("id");
                        prepareAuthentification();
                        mVolleyService = new ApiUsage(mResultCallback,getApplicationContext());
                        mVolleyService.authentificate(newUser.getEmail(),newUser.getPassword());
                    } else {

                        Toast.makeText(getApplicationContext(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }

            }
            @Override
            public void onError(VolleyError error) {
                Log.d("error",error.getMessage() + "");
            }
        };
    }
    private void prepareAuthentification() {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (!(boolean) response.get("error")) {
                        Log.d("prepareAtuh", response.toString());
                        JSONObject objres = (JSONObject) new JSONTokener(response.get("user").toString()).nextValue();
                        String token = objres.getString("token");
                        prepareEmpty();
                        mVolleyService = new ApiUsage(mResultCallback,getApplicationContext());
                        for (int item : finallistBeerType) {
                            mVolleyService.addLinkBetweenBeerAndUser(idUser, item, token);
                        }
                        Intent nextStep = new Intent(RegisterSecondStep.this, LoginActivity.class);
                        //Mail mail = new Mail();
                        //assert newUser != null;
                        //mail.send(newUser.getEmail(), newUser.getName());
                        startActivity(nextStep);
                    }
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }

            }
            @Override
            public void onError(VolleyError error) { }
        };
    }
    void prepareEmpty(){
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            }
            @Override
            public void onError(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Erreur lors de l'authentification", Toast.LENGTH_SHORT).show();
            }
        };
    }
}

