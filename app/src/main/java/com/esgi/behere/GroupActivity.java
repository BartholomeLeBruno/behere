package com.esgi.behere;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.esgi.behere.utils.ApiUsage;
import com.esgi.behere.utils.VolleyCallback;

import org.json.JSONObject;
import org.json.JSONTokener;

public class GroupActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private VolleyCallback mResultCallback = null;
    private TextView tvNameGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        Button btnAdd = findViewById(R.id.btnJoinOrAdd);
        tvNameGroup = findViewById(R.id.tvNameGroup);
        sharedPreferences = getBaseContext().getSharedPreferences(getString(R.string.prefs), MODE_PRIVATE);
        btnAdd.setText(getString(R.string.join_upercase));
        if(getIntent().getExtras() != null) {
            long entityID = getIntent().getExtras().getLong("entityID");
            prepareGetGroup();
            ApiUsage mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
            mVolleyService.getGroup(entityID);

        }

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
        }
        return super.onOptionsItemSelected(item);
    }


    private void prepareGetGroup() {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    String name;
                    if (!(boolean) response.get("error")) {
                        JSONObject objres = (JSONObject) new JSONTokener(response.get("group").toString()).nextValue();
                        name = objres.getString("name");
                        tvNameGroup.setText(name);

                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            @Override
            public void onError(VolleyError error) { }
        };
    }
}
