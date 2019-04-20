package com.example.behere.register;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TimeFormatException;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.behere.LoginActivity;
import com.example.behere.R;
import com.example.behere.actor.User;
import com.example.behere.utils.ApiUsage;

import org.json.simple.JSONObject;

public class RegisterFirstStep extends Activity {

    private EditText name;
    private EditText surname;
    private EditText email;
    private EditText birthdate;
    private EditText password;
    private EditText checkPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register_first_step);

        name = findViewById(R.id.name);
        surname = findViewById(R.id.surname);
        email = findViewById(R.id.email);
        birthdate = findViewById(R.id.birthDate);
        password = findViewById(R.id.password);
        checkPassword = findViewById(R.id.checkpassword);


        Button continueStep = findViewById(R.id.continueStep);

        continueStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name.getText() != null && surname.getText() != null && email.getText() != null && birthdate.getText() != null
                && password.getText() != null && checkPassword.getText() != null)
                {
                    User newUser = new User();
                    newUser.setName(name.getText().toString());
                    newUser.setSurname(surname.getText().toString());
                    newUser.setEmail(email.getText().toString());
                    newUser.setBirthDate(birthdate.getText().toString());
                    newUser.setPassword(password.getText().toString());
                    newUser.setCheckPassword(checkPassword.getText().toString());
                    Intent nextStep = new Intent(RegisterFirstStep.this, RegisterSecondStep.class);
                    nextStep.putExtra("User", newUser);
                    startActivity(nextStep);
                }
            }
        });
    }
}
