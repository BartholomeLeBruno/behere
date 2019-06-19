package com.esgi.behere;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.esgi.behere.fragment.FriendOrGroupAdapterProfile;
import com.esgi.behere.utils.ApiUsage;
import com.esgi.behere.utils.VolleyCallback;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Objects;

public class ProfilFriendGroupActivity  extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private static final String PREFS = "PREFS";
    private VolleyCallback mResultCallback = null;
    private ApiUsage mVolleyService;
    private long entityId;
    private String entityType;
    private TextView tvNameEntity;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile_groups);

        tvNameEntity = findViewById(R.id.tvNamePersonOrGroup);
        sharedPreferences = getBaseContext().getSharedPreferences(PREFS, MODE_PRIVATE);

        FriendOrGroupAdapterProfile mSectionsPagerAdapter = new FriendOrGroupAdapterProfile(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = findViewById(R.id.activity_main_viewpager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(mViewPager);
        if(Objects.requireNonNull(getIntent().getExtras()).get("entityID") != null && Objects.requireNonNull(getIntent().getExtras()).get("entityType") != null)
        {
            entityId = (long) getIntent().getExtras().get("entityID");
            entityType = (String) getIntent().getExtras().get("entityType");
        }
        else{
            Intent goback = new Intent(getApplicationContext(), MapActivity.class);
            startActivity(goback);
        }
        if(Objects.equals(entityType, "User"))
        {
            prepareGetUser();
            mVolleyService = new ApiUsage(mResultCallback,getApplicationContext());
            mVolleyService.getUser(entityId);
        }
        BottomNavigationView navigationView = findViewById(R.id.footerpub);
        navigationView.setOnNavigationItemReselectedListener(this::onOptionsItemSelected);

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

    @Override
    public void onResume()
    {
        super.onResume();
        prepareAuthentification();
        mVolleyService = new ApiUsage(mResultCallback,getApplicationContext());
        mVolleyService.authentificate(sharedPreferences.getString("USERNAME", ""), sharedPreferences.getString("PASSWORD",""));

    }

    private void prepareAuthentification(){
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (!(boolean) response.get("error")) {
                        JSONObject objres = (JSONObject) new JSONTokener(response.get("user").toString()).nextValue();
                        sharedPreferences.edit().putString("ACESS_TOKEN",objres.getString("token")).apply();
                    }
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

    private void prepareGetUser() {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    String name;
                    String surname;
                    if (!(boolean) response.get("error")) {
                        JSONObject objres = (JSONObject) new JSONTokener(response.get("user").toString()).nextValue();
                        name = objres.getString("name");
                        surname = objres.getString("surname");
                        tvNameEntity = findViewById(R.id.tvNamePersonOrGroup);
                        tvNameEntity.setText(name + " " + surname);
                        Button btnChat = findViewById(R.id.btnTestChat);
                        btnChat.setOnClickListener(v -> {
                            try {
                                Intent chatroul = new Intent(getApplicationContext(), Chat.class);
                                chatroul.putExtra("reponsderEmail",objres.getString("email"));
                                chatroul.putExtra("reponsderID",objres.getLong("id"));
                                startActivity(chatroul);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        });
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
