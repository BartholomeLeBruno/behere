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

import com.android.volley.VolleyError;
import com.esgi.behere.fragment.FriendOrGroupAdapterProfile;
import com.esgi.behere.utils.ApiUsage;
import com.esgi.behere.utils.InformationMessage;
import com.esgi.behere.utils.PopupAchievement;
import com.esgi.behere.utils.VolleyCallback;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.text.MessageFormat;
import java.util.Objects;

public class ProfilFriendGroupActivity  extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private VolleyCallback mResultCallback = null;
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
        sharedPreferences = getBaseContext().getSharedPreferences(getString(R.string.prefs), MODE_PRIVATE);
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
            btnJoinORAdd.setText(getString(R.string.add_upercase));
            prepareGetAllPersonnalFriends();
            mVolleyService = new ApiUsage(mResultCallback,getApplicationContext());
            mVolleyService.getAllFriends(sharedPreferences.getLong(getString(R.string.prefs_id),0));
            if(btnJoinORAdd.getText().toString().equals(getString(R.string.add_upercase)))
            {
                btnJoinORAdd.setOnClickListener(v -> {
                    prepareEmpty();
                    mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
                    mVolleyService.addFriend(sharedPreferences.getLong(getString(R.string.prefs_id), 0), (int) entityId, sharedPreferences.getString(getString(R.string.access_token), ""));
                    prepareGetAllFriends();
                    mVolleyService = new ApiUsage(mResultCallback,getApplicationContext());
                    mVolleyService.getAllFriends(entityId);
                    finish();
                    startActivity(getIntent());
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
    public void onBackPressed() {
        Intent next;
        next = new Intent(getApplicationContext(), MapActivity.class);
        startActivity(next);
        finish();
    }

    // todo recueperer l'id du friend dans le personnal, si l'id est user_rf machin

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
                        JSONObject objres;
                        if (!resFriends.isEmpty()) {
                            for (Object unres : resFriends) {
                                 objres = (JSONObject) new JSONTokener(unres.toString()).nextValue();
                                if(Long.parseLong(objres.get("user_id").toString()) == entityId)
                                {
                                    btnJoinORAdd.setText(getString(R.string.delete_upercase));
                                    break;
                                }
                            }
                        }
                        if(btnJoinORAdd.getText().toString().equals("DELETE"))
                        {
                            btnJoinORAdd.setOnClickListener(v -> {
                                prepareDeleteFriend();
                                mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
                                mVolleyService.deleteFriend(sharedPreferences.getLong(getString(R.string.prefs_id), 0), (int) entityId, sharedPreferences.getString(getString(R.string.access_token), ""));
                                prepareGetAllFriends();
                                mVolleyService = new ApiUsage(mResultCallback,getApplicationContext());
                                mVolleyService.getAllFriends(entityId);
                                finish();
                                startActivity(getIntent());
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
                        tvFriendsOrMembers.setText(MessageFormat.format("{0}{1}", resFriend.size(), getString(R.string.friends)));
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
                        btnJoinORAdd.setText(getString(R.string.delete_upercase));

                        }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            @Override
            public void onError(VolleyError error) {
                if(error.networkResponse.statusCode == 500)
                {
                    new PopupAchievement().popupAuthentification(getWindow().getDecorView().getRootView());
                }
            }
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
                        btnJoinORAdd.setText(getString(R.string.add_upercase));

                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            @Override
            public void onError(VolleyError error) {
                if(error.networkResponse.statusCode == 500)
                {
                    new PopupAchievement().popupAuthentification(getWindow().getDecorView().getRootView());
                }
            }
        };
    }
}
