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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.List;

public class RegisterSecondStep extends Activity {

    private ListView lvBeerType;
    private Button btnRegister;
    private List<String> listBeerType = new ArrayList<>();


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
                User newUser =  (User) getIntent().getExtras().get("User");
                JSONObject result = ApiUsage.createAccount(newUser);
                if(!(boolean) result.get("error")) {
                    JSONObject acessUser = (JSONObject) result.get("user");
                    Long idUSER = (Long) acessUser.get("id");
                    JSONObject auth = ApiUsage.authentificate(newUser.getEmail(), newUser.getPassword());
                    JSONObject userData = (JSONObject) auth.get("user");
                    String token = (String) userData.get("token");
                    SparseBooleanArray checked = lvBeerType.getCheckedItemPositions();
                    for (int i = 0; i < lvBeerType.getCount(); i++)
                        if (checked.get(i)) {
                           JSONObject addBeerType = ApiUsage.addLinkBetweenBeerAndUser(idUSER, i+1, token);
                            if((boolean) addBeerType.get("error")) {
                                Log.i("erreur", addBeerType.get("message").toString());
                                Log.i("token", token);
                                //Toast.makeText(getApplicationContext(), addBeerType.get("message").toString(), Toast.LENGTH_SHORT).show();
                            }
                        }

                    Intent nextStep = new Intent(RegisterSecondStep.this, LoginActivity.class);
                    Mail mail = new Mail();
                    mail.send(newUser.getEmail(), newUser.getName());
                    startActivity(nextStep);
                }else {

                    Toast.makeText(getApplicationContext(), result.get("message").toString(), Toast.LENGTH_SHORT).show();
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
            JSONObject jsonObject = ApiUsage.getAllTypeOfBeer();
            JSONParser parser = new JSONParser();
            JSONArray res = (JSONArray)  parser.parse(jsonObject.get("typeOfBeer").toString());
            for (Object unres : res) {
                JSONObject objres = (JSONObject)  parser.parse(unres.toString());
                listBeerType.add((String) objres.get("name"));
            }
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, listBeerType){
                @Override
                public View getView(int position, View convertView, ViewGroup parent){
                    // Get the Item from ListView
                    View view = super.getView(position, convertView, parent);

                    // Initialize a TextView for ListView each Item
                    TextView tv =  view.findViewById(android.R.id.text1);

                    // Set the text color of TextView (ListView Item)
                    tv.setTextColor(Color.WHITE);

                    // Generate ListView Item using TextView
                    return view;
                }
            };
            lvBeerType.setAdapter(arrayAdapter);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

    }
}

