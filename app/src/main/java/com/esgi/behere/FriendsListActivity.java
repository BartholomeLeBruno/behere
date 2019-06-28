package com.esgi.behere;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.esgi.behere.actor.User;
import com.esgi.behere.adapter.FriendAdpater;
import com.esgi.behere.utils.ApiUsage;
import com.esgi.behere.utils.CacheContainer;
import com.esgi.behere.utils.VolleyCallback;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;


public class FriendsListActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private VolleyCallback mResultCallback = null;
    private ApiUsage mVolleyService;
    private ListView listFriends;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_friends);
        listFriends = findViewById(R.id.listFriends);
        sharedPreferences = getBaseContext().getSharedPreferences(getString(R.string.prefs), MODE_PRIVATE);
        BottomNavigationView navigationView = findViewById(R.id.footerpub);
        navigationView.setOnNavigationItemSelectedListener(this::onOptionsItemSelected);
        CacheContainer.getInstance().getFriends().clear();
        if(getIntent().getExtras() != null) {
            if (sharedPreferences.getLong(getString(R.string.prefs_id), 0) == (long) getIntent().getExtras().get("entityID")) {
                prepareGetAllFriends();
                mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
                mVolleyService.getAllFriends(getIntent().getExtras().getLong("entityID"));
            } else {
                prepareGetAllFriends();
                mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
                mVolleyService.getAllFriends(getIntent().getExtras().getLong("entityID"));
            }

        }
    }
    @Override
    public void onResume() {
        super.onResume();
        CacheContainer.initializeQueue();
        CacheContainer.getInstance().getFriends().clear();
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
    public void onBackPressed() {
        Intent next;
        next = new Intent(getApplicationContext(), MapActivity.class);
        startActivity(next);
        finish();
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
                        JSONObject objres;
                        if (!resFriend.isEmpty()) {
                            for (Object unres : resFriend) {
                                objres = (JSONObject) new JSONTokener(unres.toString()).nextValue();
                                if(getIntent().getExtras() != null) {
                                    if (sharedPreferences.getLong(getString(R.string.prefs_id), 0) == (long) getIntent().getExtras().get("entityID")) {
                                        prepareGetUser();
                                        mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
                                        mVolleyService.getUser(objres.getInt("user_id"));
                                    } else {
                                        prepareGetUser();
                                        mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
                                        mVolleyService.getUser(objres.getInt("user_friend_id"));
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            @Override
            public void onError(VolleyError error) { }
        };
    }

    private void prepareGetUser() {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    String name;
                    String surname;
                    long id;
                    if (!(boolean) response.get("error")) {
                        JSONObject objres = (JSONObject) new JSONTokener(response.get("user").toString()).nextValue();
                        name = objres.getString("name");
                        surname = objres.getString("surname");
                        id = objres.getLong("id");
                        // todo ajouter la photo
                        User friend = new User();
                        friend.setName(name);
                        friend.setSurname(surname);
                        friend.setId(id);
                        CacheContainer.getInstance().getFriends().add(friend);
                        listFriends.setAdapter(new FriendAdpater(getApplicationContext(), CacheContainer.getInstance().getFriends()));

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
