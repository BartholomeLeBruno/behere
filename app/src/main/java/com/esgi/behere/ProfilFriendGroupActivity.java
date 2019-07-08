package com.esgi.behere;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.esgi.behere.actor.Notification;
import com.esgi.behere.fragment.FriendOrGroupAdapterProfile;
import com.esgi.behere.utils.ApiUsage;
import com.esgi.behere.utils.InformationMessage;
import com.esgi.behere.utils.PopupAchievement;
import com.esgi.behere.utils.VolleyCallback;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.text.MessageFormat;
import java.util.Objects;

public class ProfilFriendGroupActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private VolleyCallback mResultCallback = null;
    private ApiUsage mVolleyService;
    private long entityId;
    private TextView tvNameEntity;
    private Button btnJoinORAdd, btnDenied;
    private TextView tvFriendsOrMembers;
    private long notifID;
    private Button btnCommentWall;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile_groups);

        tvNameEntity = findViewById(R.id.tvNamePersonOrGroup);
        tvFriendsOrMembers = findViewById(R.id.tvFriendsOrMembers);
        sharedPreferences = getBaseContext().getSharedPreferences(getString(R.string.prefs), MODE_PRIVATE);
        btnJoinORAdd = findViewById(R.id.btnJoinOrAdd);
        btnDenied = findViewById(R.id.btnDenied);
        btnCommentWall = findViewById(R.id.btnCommentWall);
        FriendOrGroupAdapterProfile mSectionsPagerAdapter = new FriendOrGroupAdapterProfile(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = findViewById(R.id.activity_main_viewpager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(mViewPager);
        if (Objects.requireNonNull(getIntent().getExtras()).get("entityID") != null) {
            entityId = (long) getIntent().getExtras().get("entityID");
        } else {
            Intent goback = new Intent(getApplicationContext(), MapActivity.class);
            startActivity(goback);
        }
        // get information about actual user
        prepareGetUser();
        mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
        mVolleyService.getUser(entityId);
        btnJoinORAdd.setText(getString(R.string.add_upercase));

        // get all friends
        prepareGetAllPersonnalFriends();
        mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
        mVolleyService.getAllFriends(sharedPreferences.getLong(getString(R.string.prefs_id), 0));
        prepareGetNotification();
        mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
        mVolleyService.getNotification(sharedPreferences.getLong(getString(R.string.prefs_id), 0), sharedPreferences.getString(getString(R.string.access_token), ""));
        prepareGetOtherNotification();
        mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
        mVolleyService.getNotification(entityId, sharedPreferences.getString(getString(R.string.access_token), ""));
        if (btnJoinORAdd.getText().toString().equals(getString(R.string.add_upercase))) {
            btnJoinORAdd.setEnabled(true);
            btnJoinORAdd.setOnClickListener(v -> {
                prepareSendNotification();
                mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
                mVolleyService.createNotification(new Notification("Demande ami de " + sharedPreferences.getString("USERNAME", ""), "Friends", entityId,
                                sharedPreferences.getLong(getString(R.string.prefs_id), 0), 0),
                        sharedPreferences.getString(getString(R.string.access_token), ""));
            });
        }
        // get All friends of user you are on to update number of friends he has
        prepareGetAllFriends();
        mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
        mVolleyService.getAllFriends(entityId);
        tvFriendsOrMembers.setOnClickListener(v -> {
            Intent listFriend = new Intent(getApplicationContext(), FriendsListActivity.class);
            listFriend.putExtra("entityID", entityId);
            startActivity(listFriend);
        });
        BottomNavigationView navigationView = findViewById(R.id.footerpub);
        navigationView.setOnNavigationItemSelectedListener(this::onOptionsItemSelected);
        if(btnJoinORAdd.getText().toString().equals(getString(R.string.delete_upercase)))
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
                        tvNameEntity = findViewById(R.id.tvNamePersonOrGroup);
                        tvNameEntity.setText(String.format("%s %s", name, surname));
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

    private void prepareGetAllPersonnalFriends() {
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
                                if (Long.parseLong(objres.get("user_friend_id").toString()) == entityId || Long.parseLong(objres.get("user_id").toString()) == entityId) {
                                    btnJoinORAdd.setText(getString(R.string.delete_upercase));
                                    break;
                                }
                            }
                        }
                        if (btnJoinORAdd.getText().toString().equals("DELETE")) {
                            btnCommentWall.setVisibility(View.VISIBLE);
                            btnJoinORAdd.setOnClickListener(v -> {
                                btnCommentWall.setVisibility(View.INVISIBLE);
                                prepareDeleteFriend();
                                mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
                                mVolleyService.deleteFriend(sharedPreferences.getLong(getString(R.string.prefs_id), 0), (int) entityId, sharedPreferences.getString(getString(R.string.access_token), ""));
                                prepareGetAllFriends();
                                mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
                                mVolleyService.getAllFriends(entityId);
                                finish();
                                startActivity(new Intent(getApplicationContext(), ProfilFriendGroupActivity.class));
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

    private void prepareGetAllFriends() {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (!(boolean) response.get("error")) {
                        JSONParser parser = new JSONParser();
                        JSONArray resFriend = (JSONArray) parser.parse(response.get("friend").toString());
                        Log.d("voila", resFriend.size() + "");
                        tvFriendsOrMembers.setText(MessageFormat.format("{0} {1}", resFriend.size(), getString(R.string.friends)));
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

    private void prepareEmpty() {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (!(boolean) response.get("error")) {
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
            }
        };
    }

    private void prepareDeleteFriend() {
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
                if (error.networkResponse.statusCode == 500) {
                    new PopupAchievement().popupAuthentification(getWindow().getDecorView().getRootView());
                }
            }
        };
    }

    private void prepareSendNotification() {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (!(boolean) response.get("error")) {
                        btnJoinORAdd.setText(getString(R.string.waiting_uppercase));
                        btnJoinORAdd.setOnClickListener(v -> {
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

    private void prepareSendEmpty() {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {}
            @Override
            public void onError(VolleyError error) {
                if (error.networkResponse.statusCode == 500) {
                    new PopupAchievement().popupAuthentification(getWindow().getDecorView().getRootView());
                }
            }
        };
    }

    private void prepareGetNotification() {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (!(boolean) response.get("error")) {
                        JSONParser parser = new JSONParser();
                        JSONArray resNotification = (JSONArray) parser.parse(response.get("notification").toString());
                        JSONObject objres;
                        Log.d("notifi", response.toString());
                        if (!resNotification.isEmpty()) {
                            for (Object unres : resNotification) {
                                objres = (JSONObject) new JSONTokener(unres.toString()).nextValue();
                                if (objres.getString("type").equals("Friends") && entityId == objres.getLong("other_user_id")) {
                                    btnJoinORAdd.setText(getString(R.string.accept_uppercase));
                                    btnDenied.setVisibility(View.VISIBLE);
                                    notifID = objres.getLong("id");
                                    //btnJoinORAdd.setEnabled(false);
                                    break;
                                }
                            }
                        }
                        if (btnJoinORAdd.getText().toString().equals(getString(R.string.accept_uppercase))) {
                            btnJoinORAdd.setOnClickListener(v -> {
                                prepareEmpty();
                                mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
                                mVolleyService.addFriend(sharedPreferences.getLong(getString(R.string.prefs_id), 0), (int) entityId, sharedPreferences.getString(getString(R.string.access_token), ""));
                                prepareDeleteNotification();
                                mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
                                mVolleyService.deleteNotification(notifID, sharedPreferences.getString(getString(R.string.access_token), ""));
                                prepareGetAllFriends();
                                mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
                                mVolleyService.getAllFriends(entityId);
                            });
                        }
                        if (btnDenied.getVisibility() == View.VISIBLE) {
                            btnDenied.setOnClickListener(v -> {
                                prepareDeleteNotification();
                                mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
                                mVolleyService.deleteNotification(notifID, sharedPreferences.getString(getString(R.string.access_token), ""));
                            });

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
                                if (objres.getString("type").equals("Friends") && sharedPreferences.getLong(getString(R.string.prefs_id), 0) == objres.getLong("other_user_id")) {
                                    btnJoinORAdd.setText(getString(R.string.waiting_uppercase));
                                    btnJoinORAdd.setOnClickListener(v -> {
                                    });
                                    //btnJoinORAdd.setEnabled(false);
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

    private void prepareDeleteNotification() {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (!(boolean) response.get("error")) {
                        Intent refresh = new Intent(getApplicationContext(), ProfilFriendGroupActivity.class);
                        refresh.putExtra("entityID", entityId);
                        startActivity(refresh);
                        finish();
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
        btnSendComment.setOnClickListener((View v) -> {
            prepareAddComment();
            sharedPreferences = getBaseContext().getSharedPreferences(getString(R.string.prefs), MODE_PRIVATE);
            mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
            mVolleyService.addCommentsToUser(tvComment.getText().toString(), entityId, sharedPreferences.getString(getString(R.string.access_token), ""));
            prepareSendEmpty();
            mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
            mVolleyService.createNotification(new Notification("Commentaire de " + sharedPreferences.getString("USERNAME", ""), "Comments", entityId,
                            sharedPreferences.getLong(getString(R.string.prefs_id), 0), 0),
                    sharedPreferences.getString(getString(R.string.access_token), ""));
            popupWindow.dismiss();
        });
    }

    private void prepareAddComment() {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    InformationMessage.createToastInformation(ProfilFriendGroupActivity.this, getLayoutInflater(), getApplicationContext(), R.drawable.ic_insert_emoticon_blue_24dp,
                            "We love you my love");
                    if ((boolean) response.get("error")) {
                        Toast.makeText(getApplicationContext(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
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
