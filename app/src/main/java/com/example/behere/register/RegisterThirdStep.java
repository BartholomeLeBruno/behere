package com.example.behere.register;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import com.example.behere.R;

public class RegisterThirdStep extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register_third_step);

        Toast.makeText(getApplicationContext(), getIntent().getSerializableExtra("User").toString(),Toast.LENGTH_SHORT).show();

    }
}
