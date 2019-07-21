package com.esgi.behere;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.error.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.ui.NetworkImageView;
import com.esgi.behere.actor.Notification;
import com.esgi.behere.fragment.FriendOrGroupAdapterProfile;
import com.esgi.behere.utils.ApiUsage;
import com.esgi.behere.utils.CacheContainer;
import com.esgi.behere.utils.PopupAchievement;
import com.esgi.behere.utils.VolleyCallback;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.text.MessageFormat;
import java.util.ArrayList;

public class GroupActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private VolleyCallback mResultCallback = null;
    private TextView tvNameGroup, tvMembers;
    private ApiUsage mVolleyService;
    private long entityID;
    private Button btnAdd;
    private Button btnCommentWall;
    private long adminID;
    private ArrayList<Long> memberList;
    private NetworkImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        btnAdd = findViewById(R.id.btnJoin);
        tvNameGroup = findViewById(R.id.tvNameGroup);
        btnCommentWall = findViewById(R.id.btnCommentWall);
        tvMembers = findViewById(R.id.tvMembers);
        imageView = findViewById(R.id.ivGroup);
        FriendOrGroupAdapterProfile mSectionsPagerAdapter = new FriendOrGroupAdapterProfile(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = findViewById(R.id.activity_main_viewpager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(mViewPager);
        sharedPreferences = getBaseContext().getSharedPreferences(getString(R.string.prefs), MODE_PRIVATE);
        BottomNavigationView navigationView = findViewById(R.id.footerpub);
        navigationView.setSelectedItemId(R.id.navigation_mygroups);
        navigationView.setOnNavigationItemSelectedListener(this::onOptionsItemSelected);
        btnAdd.setText(getString(R.string.join_upercase));
        if (getIntent().getExtras() != null) {
            entityID = getIntent().getExtras().getLong("entityID");
            prepareGetGroup();
            ApiUsage mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
            mVolleyService.getGroup(entityID);
        }
        prepareGetMembers();
        mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
        mVolleyService.getAllGroups(sharedPreferences.getLong(getString(R.string.prefs_id), 0));
        prepareGetOtherNotification();
        mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
        mVolleyService.getNotification(entityID, sharedPreferences.getString(getString(R.string.access_token), ""));
        if (btnAdd.getText().toString().equals(getString(R.string.join_upercase))) {
            btnAdd.setEnabled(true);
            btnAdd.setOnClickListener(v -> {
                prepareSendNotification();
                mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
                mVolleyService.createNotification(new Notification("Asking to join the group " + tvNameGroup.getText().toString(), "Groups", adminID,
                        sharedPreferences.getLong(getString(R.string.prefs_id), 0), entityID), sharedPreferences.getString(getString(R.string.access_token), ""));
            });
        }
        // get All friends of user you are on to update number of friends he has
        prepareGetAllMembers();
        mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
        mVolleyService.getAllGroups(entityID);
        tvMembers.setOnClickListener(v -> {
            Intent listFriend = new Intent(getApplicationContext(), FriendsListActivity.class);
            listFriend.putExtra("entityID", entityID);
            listFriend.putExtra("group", "");
            startActivity(listFriend);
        });
        if (btnAdd.getText().toString().equals(getString(R.string.leave_uppercase)))
            btnCommentWall.setVisibility(View.VISIBLE);
        else
            btnCommentWall.setVisibility(View.INVISIBLE);

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


    private void prepareGetGroup() {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    String name;
                    if (!(boolean) response.get("error")) {
                        JSONParser parser = new JSONParser();
                        JSONObject objres = (JSONObject) new JSONTokener(response.get("group").toString()).nextValue();
                        name = objres.getString("name");
                        tvNameGroup.setText(name);
                        adminID = objres.getLong("admin_id");
                        if (JSONObject.NULL != objres.getString("pathPicture"))
                            imageView.setImageUrl(objres.getString("pathPicture"), new ImageLoader(CacheContainer.getQueue()));
                        else
                            imageView.setBackground(getDrawable(R.drawable.default_image));
                        JSONArray sizeOF = (JSONArray) parser.parse(response.getJSONObject("group").getJSONArray("user").toString());
                        tvMembers.setText(MessageFormat.format("{0} {1}", sizeOF.size(), getString(R.string.members)));


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

    private void prepareSendNotification() {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (!(boolean) response.get("error")) {
                        btnAdd.setText(getString(R.string.waiting_uppercase));
                        btnAdd.setOnClickListener(v -> {
                        });
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

    private void prepareGetMembers() {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (!(boolean) response.get("error")) {
                        Log.d("responseICI", response.toString());
                        JSONParser parser = new JSONParser();
                        JSONObject objres;
                        JSONArray group = (JSONArray) parser.parse(response.get("group").toString());
                        if (!group.isEmpty()) {
                            for (Object unmembre : group) {
                                objres = (JSONObject) new JSONTokener(unmembre.toString()).nextValue();
                                adminID = objres.getLong("admin_id");
                                if (objres.getLong("id") == entityID) {
                                    btnAdd.setText(getString(R.string.leave_uppercase));
                                    break;
                                }

                            }
                        }
                        if (btnAdd.getText().toString().equals(getString(R.string.leave_uppercase))) {
                            btnCommentWall.setVisibility(View.VISIBLE);
                            btnAdd.setOnClickListener(v -> {
                                btnCommentWall.setVisibility(View.INVISIBLE);
                                prepareDeleteMember();
                                mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
                                mVolleyService.deleteUserInGroup(sharedPreferences.getLong(getString(R.string.prefs_id), 0), (int) entityID, sharedPreferences.getString(getString(R.string.access_token), ""));
                                prepareGetAllMembers();
                                mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
                                mVolleyService.getAllGroups(entityID);
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
            public void onError(VolleyError error) {
            }
        };
    }

    private void prepareDeleteMember() {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (!(boolean) response.get("error")) {
                        btnAdd.setText(getString(R.string.join_upercase));

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

    private void prepareGetAllMembers() {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (!(boolean) response.get("error")) {
                        memberList = new ArrayList<>();
                        JSONParser parser = new JSONParser();
                        JSONArray group = (JSONArray) parser.parse(response.get("group").toString());
                        JSONArray resmembres = new JSONArray();
                        JSONObject objres;
                        for (Object unmembre : group) {
                            objres = (JSONObject) new JSONTokener(unmembre.toString()).nextValue();
                            resmembres = (JSONArray) parser.parse(objres.get("user").toString());

                        }
                        if (!resmembres.isEmpty()) {
                            for (Object unres : resmembres) {
                                objres = (JSONObject) new JSONTokener(unres.toString()).nextValue();
                                memberList.add(objres.getLong("id"));
                            }
                        }
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

    private void prepareGetOtherNotification() {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (!(boolean) response.get("error")) {
                        JSONParser parser = new JSONParser();
                        JSONArray resNotification = (JSONArray) parser.parse(response.get("notification").toString());
                        JSONObject objres;
                        if (!resNotification.isEmpty()) {
                            for (Object unres : resNotification) {
                                objres = (JSONObject) new JSONTokener(unres.toString()).nextValue();
                                if (objres.getString("type").equals("Groups") && sharedPreferences.getLong(getString(R.string.prefs_id), 0) == objres.getLong("other_user_id")) {
                                    btnAdd.setText(getString(R.string.waiting_uppercase));
                                    btnAdd.setOnClickListener(v -> {
                                    });
                                    break;
                                }
                            }
                        }
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

    @Override
    public void onBackPressed() {
        Intent next;
        next = new Intent(getApplicationContext(), MapActivity.class);
        startActivity(next);
        finish();
    }

    public void onButtonShowPopupWindowClick(View view) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.activity_comment, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        Button btnSendComment = popupView.findViewById(R.id.btnSendComment);
        EditText tvComment = popupView.findViewById(R.id.tvComment);
        tvComment.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) tvComment.setHint("");
            else tvComment.setHint("Your Comments...");
        });
        btnSendComment.setOnClickListener((View v) -> {
            if (!tvComment.getText().toString().isEmpty()) {
                prepareAddComment();
                sharedPreferences = getBaseContext().getSharedPreferences(getString(R.string.prefs), MODE_PRIVATE);
                mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
                mVolleyService.addCommentsToGroup(tvComment.getText().toString(), entityID, sharedPreferences.getString(getString(R.string.access_token), ""));
                prepareSendEmpty();
                mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
                for (Long memberID : memberList) {
                    if (memberID != sharedPreferences.getLong(getString(R.string.prefs_id), 0)) {
                        mVolleyService.createNotification(new Notification("Commentary from " + sharedPreferences.getString("USERNAME", ""), "Comments", memberID,
                                        0, entityID),
                                sharedPreferences.getString(getString(R.string.access_token), ""));
                    }
                    popupWindow.dismiss();
                }
                view.getContext().startActivity(getIntent());
                finish();
            }
        });
    }

    private void prepareAddComment() {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
            }

            @Override
            public void onError(VolleyError error) {
                if (error.networkResponse.statusCode == 500) {
                    new PopupAchievement().popupAuthentification(getWindow().getDecorView().getRootView());
                }
            }
        };
    }

    private void prepareSendEmpty() {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
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
