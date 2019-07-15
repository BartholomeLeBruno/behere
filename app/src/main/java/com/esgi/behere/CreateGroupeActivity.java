package com.esgi.behere;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.error.VolleyError;
import com.esgi.behere.utils.ApiUsage;
import com.esgi.behere.utils.InformationMessage;
import com.esgi.behere.utils.PopupAchievement;
import com.esgi.behere.utils.VolleyCallback;

import org.json.JSONObject;

public class CreateGroupeActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private static final String PREFS = "PREFS";
    private VolleyCallback mResultCallback = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        Button btnCreateGroup = findViewById(R.id.btnCreateGroup);
        EditText tvNameGroup = findViewById(R.id.tvNameGroup);
        EditText tvDescription = findViewById(R.id.tvDescription);
        sharedPreferences = getBaseContext().getSharedPreferences(PREFS, MODE_PRIVATE);
        BottomNavigationView navigationView = findViewById(R.id.footerpub);
        navigationView.setOnNavigationItemSelectedListener(this::onOptionsItemSelected);
        btnCreateGroup.setOnClickListener(v -> {
            if (!tvNameGroup.getText().toString().equals("")) {
                prepareCreateGroup();
                ApiUsage volleyService = new ApiUsage(mResultCallback, getApplicationContext());
                volleyService.createGroup(tvNameGroup.getText().toString(), tvDescription.getText().toString(), sharedPreferences.getString(getString(R.string.access_token), ""));
            }
        });
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        Intent next;
        switch (item.getItemId()) {
            case R.id.disconnected:
                next = new Intent(getApplicationContext(), LoginActivity.class);
                sharedPreferences.edit().clear().apply();
                startActivity(next);
                return true;
            case R.id.navigation_mygroups:
                next = new Intent(getApplicationContext(), MyGroupActivity.class);
                startActivity(next);
                return true;
            case R.id.navigation_home:
                next = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(next);
                return true;
            case R.id.navigation_myprofile:
                next = new Intent(getApplicationContext(), DefaultProfileActivity.class);
                startActivity(next);
                return true;
            case R.id.navigation_lexical:
                next = new Intent(getApplicationContext(), LexiconActivity.class);
                startActivity(next);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void prepareCreateGroup() {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (!(boolean) response.get("error")) {
                        InformationMessage.createToastInformation(CreateGroupeActivity.this, getLayoutInflater(), getApplicationContext(), R.drawable.ic_insert_emoticon_blue_24dp,
                                "Thanks now you can share your beer !");
                        Intent gotoAllGroups = new Intent(getApplicationContext(), MyGroupActivity.class);
                        finish();
                        startActivity(gotoAllGroups);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onError(VolleyError error) {
                if (error.networkResponse.statusCode == 500) {
                    new PopupAchievement().popupAuthentification(getWindow().getDecorView().getRootView());
                }
            }
        };
    }
}
