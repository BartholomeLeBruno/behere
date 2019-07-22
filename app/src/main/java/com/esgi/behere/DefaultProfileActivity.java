package com.esgi.behere;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.error.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.ui.NetworkImageView;
import com.esgi.behere.fragment.SectionsAdapterProfile;
import com.esgi.behere.utils.ApiUsage;
import com.esgi.behere.utils.CacheContainer;
import com.esgi.behere.utils.VolleyCallback;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;


public class DefaultProfileActivity extends AppCompatActivity {

    private TextView tvNamePerson;
    private SharedPreferences sharedPreferences;
    private VolleyCallback mResultCallback = null;
    private TextView tvGroups;
    private static TextView tvNbFriends;
    private NetworkImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_profile);

        TextView tvFriends = findViewById(R.id.tvFriends);
        tvNbFriends = findViewById(R.id.tvNbFriends);
        tvGroups = findViewById(R.id.tvGroups);
        tvNamePerson = findViewById(R.id.tvNamePerson);
        imageView = findViewById(R.id.ivProfile);

        sharedPreferences = getBaseContext().getSharedPreferences(getString(R.string.prefs), MODE_PRIVATE);

        SectionsAdapterProfile mSectionsPagerAdapter = new SectionsAdapterProfile(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = findViewById(R.id.activity_main_viewpager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(mViewPager);
        BottomNavigationView navigationView = findViewById(R.id.footerpub);
        navigationView.setSelectedItemId(R.id.navigation_myprofile);
        navigationView.setOnNavigationItemSelectedListener(this::onOptionsItemSelected);
        prepareGetUser();
        ApiUsage mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
        mVolleyService.getUser(sharedPreferences.getLong(getString(R.string.prefs_id), 0));
        tvFriends.setOnClickListener(this::onFriendListCLick);
        tvGroups.setOnClickListener(this::onGroupListCLick);
        prepareGetAllFriends();
        mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
        mVolleyService.getAllFriends(sharedPreferences.getLong(getString(R.string.prefs_id), 0));
        prepareGetAllGroups();
        mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
        mVolleyService.getAllGroups(sharedPreferences.getLong(getString(R.string.prefs_id), 0));
        navigationView.removeBadge(navigationView.getMenu().getItem(1).getItemId());

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
            case R.id.navigation_lexical:
                next = new Intent(getApplicationContext(), LexiconActivity.class);
                startActivity(next);
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
                        tvNamePerson.setText(String.format("%s %s %s", name, System.lineSeparator(), surname));
                        if (JSONObject.NULL != objres.getString("pathPicture"))
                            imageView.setImageUrl(objres.getString("pathPicture"), new ImageLoader(CacheContainer.getQueue()));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onError(VolleyError error) {
            }
        };
    }

    public void onFriendListCLick(View view) {
        Intent listFriend = new Intent(getApplicationContext(), FriendsListActivity.class);
        listFriend.putExtra("entityID", sharedPreferences.getLong(getString(R.string.prefs_id), 0));
        startActivity(listFriend);
    }

    public void onGroupListCLick(View view) {
        Intent listGroup = new Intent(getApplicationContext(), MyGroupActivity.class);
        startActivity(listGroup);
    }


    private void prepareGetAllFriends() {
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
            public void onError(VolleyError error) {
            }
        };
    }


    private void prepareGetAllGroups() {
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
            public void onError(VolleyError error) {
            }
        };
    }
}
