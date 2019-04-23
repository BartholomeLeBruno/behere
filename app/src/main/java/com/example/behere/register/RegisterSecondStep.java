package com.example.behere.register;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.behere.LoginActivity;
import com.example.behere.R;
import com.example.behere.actor.User;
import com.example.behere.utils.ApiUsage;
import com.example.behere.utils.Mail;
import com.google.android.gms.common.api.Api;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.concurrent.ExecutionException;

public class RegisterSecondStep extends Activity {

    private ListView lvBeerType;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register_second_step);

        lvBeerType = findViewById(R.id.lvBeerType);
        btnRegister = findViewById(R.id.btnRegisterLastStep);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User newUser =  (User) getIntent().getExtras().get("User");
                JSONObject result = ApiUsage.createAccount(newUser);
                Intent nextStep = new Intent(RegisterSecondStep.this, LoginActivity.class);
                if(!(boolean) result.get("error")) {
                    Mail mail = new Mail();
                    mail.send(newUser.getEmail(), newUser.getName());
                    startActivity(nextStep);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), result.get("message").toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void implementList()
    {
        try {
            JSONObject jsonObject = ApiUsage.getAllTypeOfBeer();
            JSONParser parser = new JSONParser();
            //Object obj  = parser.parse(jsonObject.get("typeOfBeer").toString());
            JSONObject res = (JSONObject)  parser.parse(jsonObject.get("typeOfBeer").toString());
            JSONObject objres = (JSONObject)  parser.parse(res.get("name").toString());
            Toast.makeText(getApplicationContext(),objres.toJSONString(), Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

    }
}

