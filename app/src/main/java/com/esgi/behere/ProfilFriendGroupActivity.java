package com.esgi.behere;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.esgi.behere.fragment.FriendOrGroupAdapterProfile;
import com.esgi.behere.utils.ApiUsage;
import com.esgi.behere.utils.InformationMessage;
import com.esgi.behere.utils.VolleyCallback;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.util.Objects;

public class ProfilFriendGroupActivity  extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private static final String PREFS = "PREFS";
    private VolleyCallback mResultCallback = null;
    private static final String PREFS_ID = "USER_ID";
    private ApiUsage mVolleyService;
    private long entityId;
    private String entityType;
    private TextView tvNameEntity;
    private Button btnJoinORAdd;
    private TextView tvFriendsOrMembers;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile_groups);

        tvNameEntity = findViewById(R.id.tvNamePersonOrGroup);
        tvFriendsOrMembers = findViewById(R.id.tvFriendsOrMembers);
        sharedPreferences = getBaseContext().getSharedPreferences(PREFS, MODE_PRIVATE);
        btnJoinORAdd = findViewById(R.id.btnJoinOrAdd);
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
            btnJoinORAdd.setText("ADD");
            prepareGetAllPersonnalFriends();
            mVolleyService = new ApiUsage(mResultCallback,getApplicationContext());
            mVolleyService.getAllFriends(sharedPreferences.getLong(PREFS_ID,0));
            if(btnJoinORAdd.getText().toString().equals("ADD"))
            {
                Log.d("onCreate",btnJoinORAdd.getText().toString());
                btnJoinORAdd.setOnClickListener(v -> {
                    prepareEmpty();
                    mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
                    mVolleyService.addFriend(sharedPreferences.getLong(PREFS_ID, 0), (int) entityId, sharedPreferences.getString("ACESS_TOKEN", ""));
                    prepareGetAllFriends();
                    mVolleyService = new ApiUsage(mResultCallback,getApplicationContext());
                    mVolleyService.getAllFriends(entityId);
                });
            }
            prepareGetAllFriends();
            mVolleyService = new ApiUsage(mResultCallback,getApplicationContext());
            mVolleyService.getAllFriends(entityId);
            tvFriendsOrMembers.setOnClickListener(v -> {
                Intent listFriend = new Intent(getApplicationContext(), FriendsListActivity.class);
                listFriend.putExtra("entityID", entityId);
                startActivity(listFriend);
            });
        }
        BottomNavigationView navigationView = findViewById(R.id.footerpub);
        navigationView.setOnNavigationItemSelectedListener(this::onOptionsItemSelected);

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
                        tvNameEntity = findViewById(R.id.tvNamePersonOrGroup);
                        tvNameEntity.setText(String.format("%s %s", name, surname));
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

    private void prepareGetAllPersonnalFriends()
    {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (!(boolean) response.get("error")) {
                        JSONParser parser = new JSONParser();
                        JSONArray resFriends = (JSONArray) parser.parse(response.get("friend").toString());
                        if (!resFriends.isEmpty()) {
                            for (Object unres : resFriends) {
                                JSONObject objres = (JSONObject) new JSONTokener(unres.toString()).nextValue();
                                if(Long.parseLong(objres.get("user_friend_id").toString()) == entityId)
                                {
                                    btnJoinORAdd.setText("DELETE");
                                    break;
                                }
                            }
                        }
                        if(btnJoinORAdd.getText().toString().equals("DELETE"))
                        {
                            btnJoinORAdd.setOnClickListener(v -> {
                                prepareDeleteFriend();
                                mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
                                mVolleyService.deleteFriend(sharedPreferences.getLong(PREFS_ID, 0), (int) entityId, sharedPreferences.getString("ACESS_TOKEN", ""));
                                prepareGetAllFriends();
                                mVolleyService = new ApiUsage(mResultCallback,getApplicationContext());
                                mVolleyService.getAllFriends(entityId);
                            });
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

    private void prepareGetAllFriends()
    {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (!(boolean) response.get("error")) {
                        JSONParser parser = new JSONParser();
                        JSONArray resFriend = (JSONArray) parser.parse(response.get("friend").toString());
                        Log.d("voila",resFriend.size()+"");
                        tvFriendsOrMembers.setText(resFriend.size() + " Friends");
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            @Override
            public void onError(VolleyError error) { }
        };
    }

    private void prepareEmpty()
    {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (!(boolean) response.get("error")) {
                       //todo handle error response
                        InformationMessage.createToastInformation(ProfilFriendGroupActivity.this, getLayoutInflater(), getApplicationContext(), R.drawable.ic_insert_emoticon_blue_24dp,
                                "Added to Friend");
                        btnJoinORAdd.setText("DELETE");

                        }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            @Override
            public void onError(VolleyError error) { }
        };
    }

    private void prepareDeleteFriend()
    {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (!(boolean) response.get("error")) {
                        //todo handle error response
                        InformationMessage.createToastInformation(ProfilFriendGroupActivity.this, getLayoutInflater(), getApplicationContext(), R.drawable.ic_highlight_off_red_24dp,
                                "Deleted from friend");
                        btnJoinORAdd.setText("ADD");

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
