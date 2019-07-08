package com.esgi.behere;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.esgi.behere.fragment.SectionsAdapterProfile;
import com.esgi.behere.utils.ApiUsage;
import com.esgi.behere.utils.VolleyCallback;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;


public class DefaultProfileActivity extends AppCompatActivity {

    private TextView tvNamePerson;
    private SharedPreferences sharedPreferences;
    private static final String PREFS = "PREFS";
    private VolleyCallback mResultCallback = null;
    private static final String PREFS_ID = "USER_ID";
    private TextView tvGroups;
    private static TextView tvNbFriends;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_profile);

        TextView tvFriends = findViewById(R.id.tvFriends);
        tvNbFriends = findViewById(R.id.tvNbFriends);
        tvGroups = findViewById(R.id.tvGroups);
        tvNamePerson = findViewById(R.id.tvNamePerson);
        sharedPreferences = getBaseContext().getSharedPreferences(PREFS, MODE_PRIVATE);

        SectionsAdapterProfile mSectionsPagerAdapter = new SectionsAdapterProfile(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = findViewById(R.id.activity_main_viewpager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(mViewPager);
        BottomNavigationView navigationView = findViewById(R.id.footerpub);
        navigationView.setOnNavigationItemSelectedListener(this::onOptionsItemSelected);
        prepareGetUser();
        ApiUsage mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
        mVolleyService.getUser(sharedPreferences.getLong(PREFS_ID,0));
        tvFriends.setOnClickListener(this::onFriendListCLick);
        tvGroups.setOnClickListener(this::onGroupListCLick);
        prepareGetAllFriends();
        mVolleyService = new ApiUsage(mResultCallback,getApplicationContext());
        mVolleyService.getAllFriends(sharedPreferences.getLong(PREFS_ID,0));
        prepareGetAllGroups();
        mVolleyService = new ApiUsage(mResultCallback,getApplicationContext());
        mVolleyService.getAllGroups(sharedPreferences.getLong(PREFS_ID,0));

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Intent next;
        switch (item.getItemId()) {
            case R.id.disconnected:
                next = new Intent(getApplicationContext(), LoginActivity.class);
                sharedPreferences.edit().clear().apply();
                next.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(next);
                finish();
                return true;
            case R.id.navigation_mygroups:
                next = new Intent(getApplicationContext(), MyGroupActivity.class);
                next.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(next);
                finish();
                return true;
            case R.id.navigation_home:
                next = new Intent(getApplicationContext(), MapActivity.class);
                next.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(next);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent next;
        next = new Intent(getApplicationContext(), MapActivity.class);
        startActivity(next);
        finish();
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
                        tvNamePerson.setText(String.format("%s %s", name, surname));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            @Override
            public void onError(VolleyError error) { }
        };
    }

    public void onFriendListCLick(View view)
    {
        Intent listFriend = new Intent(getApplicationContext(), FriendsListActivity.class);
        listFriend.putExtra("entityID", sharedPreferences.getLong(PREFS_ID,0));
        startActivity(listFriend);
    }
    public void onGroupListCLick(View view)
    {
        Intent listGroup = new Intent(getApplicationContext(), MyGroupActivity.class);
        startActivity(listGroup);
    }


    private void prepareGetAllFriends()
    {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (!(boolean) response.get("error")) {
                        JSONParser parser = new JSONParser();
                        JSONArray resFriend = (JSONArray) parser.parse(response.get("friend").toString());
                        tvNbFriends.setText(String.format("%d", resFriend.size()));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            @Override
            public void onError(VolleyError error) { }
        };
    }


    private void prepareGetAllGroups()
    {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (!(boolean) response.get("error")) {
                        JSONParser parser = new JSONParser();
                        JSONArray resGroup = (JSONArray) parser.parse(response.get("group").toString());
                        tvGroups.setText(resGroup.size() + " Groups");
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
