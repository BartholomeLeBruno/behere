package com.esgi.behere;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
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
    private ApiUsage mVolleyService;
    private long entityID;
    private Button btnAdd, btnDenied;
    private long notifID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        btnAdd = findViewById(R.id.btnJoin);
        btnDenied = findViewById(R.id.btnDenied);
        tvNameGroup = findViewById(R.id.tvNameGroup);
        sharedPreferences = getBaseContext().getSharedPreferences(getString(R.string.prefs), MODE_PRIVATE);
        BottomNavigationView navigationView = findViewById(R.id.footerpub);
        navigationView.setOnNavigationItemSelectedListener(this::onOptionsItemSelected);
        btnAdd.setText(getString(R.string.join_upercase));
        if(getIntent().getExtras() != null) {
            entityID = getIntent().getExtras().getLong("entityID");
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

   /* private void prepareAddComment() {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    InformationMessage.createToastInformation(GroupActivity.this, getLayoutInflater(), getApplicationContext(), R.drawable.ic_insert_emoticon_blue_24dp,
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
            mVolleyService.addCommentsToUser(tvComment.getText().toString(), entityID, sharedPreferences.getString(getString(R.string.access_token), ""));
            prepareSendEmpty();
            mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
            mVolleyService.createNotification(new Notification("Commentaire de " + sharedPreferences.getString("USERNAME", ""), "Comments", entityID,
                            sharedPreferences.getLong(getString(R.string.prefs_id), 0), 0),
                    sharedPreferences.getString(getString(R.string.access_token), ""));
            popupWindow.dismiss();
        });
    }

    private void prepareDeleteNotification() {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (!(boolean) response.get("error")) {
                        Intent refresh = new Intent(getApplicationContext(), GroupActivity.class);
                        refresh.putExtra("entityID", entityID);
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
                                    btnAdd.setText(getString(R.string.waiting_uppercase));
                                    btnAdd.setOnClickListener(v -> {
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
                                if (objres.getString("type").equals("Friends") && entityID == objres.getLong("other_user_id")) {
                                    btnAdd.setText(getString(R.string.accept_uppercase));
                                    btnDenied.setVisibility(View.VISIBLE);
                                    notifID = objres.getLong("id");
                                    //btnJoinORAdd.setEnabled(false);
                                    break;
                                }
                            }
                        }
                        if (btnAdd.getText().toString().equals(getString(R.string.accept_uppercase))) {
                            btnAdd.setOnClickListener(v -> {
                                prepareEmpty();
                                mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
                                mVolleyService.addFriend(sharedPreferences.getLong(getString(R.string.prefs_id), 0), (int) entityID, sharedPreferences.getString(getString(R.string.access_token), ""));
                                prepareGetAllFriends();
                                prepareDeleteNotification();
                                mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
                                mVolleyService.deleteNotification(notifID, sharedPreferences.getString(getString(R.string.access_token), ""));
                                mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
                                mVolleyService.getAllFriends(entityID);
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

    private void prepareEmpty() {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (!(boolean) response.get("error")) {
                        InformationMessage.createToastInformation(GroupActivity.this, getLayoutInflater(), getApplicationContext(), R.drawable.ic_insert_emoticon_blue_24dp,
                                "Added to Friend");
                        btnAdd.setText(getString(R.string.delete_upercase));

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
                                if (Long.parseLong(objres.get("user_friend_id").toString()) == entityID) {
                                    btnAdd.setText(getString(R.string.delete_upercase));
                                    break;
                                }
                            }
                        }
                        if (btnAdd.getText().toString().equals("DELETE")) {
                            btnCommentWall.setVisibility(View.VISIBLE);
                            btnAdd.setOnClickListener(v -> {
                                btnCommentWall.setVisibility(View.INVISIBLE);
                                prepareDeleteFriend();
                                mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
                                mVolleyService.deleteFriend(sharedPreferences.getLong(getString(R.string.prefs_id), 0), (int) entityID, sharedPreferences.getString(getString(R.string.access_token), ""));
                                prepareGetAllFriends();
                                mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
                                mVolleyService.getAllFriends(entityID);
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
    }*/
}
