package com.example.behere.register;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.behere.LoginActivity;
import com.example.behere.R;
import com.example.behere.actor.User;
import com.example.behere.utils.ApiUsage;
import com.example.behere.utils.Mail;
import com.example.behere.utils.VolleyCallback;

import org.json.simple.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.List;

public class RegisterSecondStep extends Activity {

    private ListView lvBeerType;
    Button btnRegister;
    private List<String> listBeerType = new ArrayList<>();
    private String TAG = "RegisterSecondStep";
    private VolleyCallback mResultCallback = null;
    private ApiUsage mVolleyService;
    private Long idUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register_second_step);

        lvBeerType = findViewById(R.id.lvBeerType);
        btnRegister = findViewById(R.id.btnRegisterLastStep);
        implementList();
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    User newUser = (User) getIntent().getExtras().get("User");
                    prepareCreateAccount();
                    mVolleyService = new ApiUsage(mResultCallback,getApplicationContext());
                    mVolleyService.createAccount(newUser);
                }
                catch(Exception e)
                {
                    Log.e("erroronclcoj,d",e.getMessage());
                }
            }
        });
        lvBeerType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                Object clickItemObj = adapterView.getAdapter().getItem(index);
                Toast.makeText(RegisterSecondStep.this, "You clicked " + clickItemObj.toString(), Toast.LENGTH_SHORT).show();
            }
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

    void prepareGetAllTypeOfBeer(){
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if(!(boolean) response.get("error")) {
                        JSONParser parser = new JSONParser();
                        JSONArray res = (JSONArray) parser.parse(response.get("typeOfBeer").toString());
                        for (Object unres : res) {
                            JSONObject objres = (JSONObject) parser.parse(unres.toString());
                            listBeerType.add((String) objres.get("name"));
                        }
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice, listBeerType) {
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                // Get the Item from ListView
                                View view = super.getView(position, convertView, parent);

                                // Initialize a TextView for ListView each Item
                                TextView tv = view.findViewById(android.R.id.text1);

                                // Set the text color of TextView (ListView Item)
                                tv.setTextColor(Color.WHITE);

                                // Generate ListView Item using TextView
                                return view;
                            }
                        };
                        lvBeerType.setAdapter(arrayAdapter);
                    }
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }

            }
        };
    }
    void prepareCreateAccount(){
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (!(boolean) response.get("error")) {
                        User newUser = (User) getIntent().getExtras().get("User");
                        JSONObject acessUser = (JSONObject) response.get("user");
                        idUser = (Long) acessUser.get("id");
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
        };
    }
    void prepareAuthentification() {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    User newUser = (User) getIntent().getExtras().get("User");
                    JSONObject userData = (JSONObject) response.get("user");
                    String token = (String) userData.get("token");
                    SparseBooleanArray checked = lvBeerType.getCheckedItemPositions();
                    mVolleyService = new ApiUsage(mResultCallback,getApplicationContext());
                    for (int i = 0; i < lvBeerType.getCount(); i++)
                        if (checked.get(i)) {
                            mVolleyService.addLinkBetweenBeerAndUser(idUser, i + 1, token);
                        }

                    Intent nextStep = new Intent(RegisterSecondStep.this, LoginActivity.class);
                    Mail mail = new Mail();
                    mail.send(newUser.getEmail(), newUser.getName());
                    startActivity(nextStep);
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }

            }
        };
    }
}

