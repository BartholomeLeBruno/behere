package com.esgi.behere;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.esgi.behere.actor.Publication;
import com.esgi.behere.adapter.CommentMarketAdapter;
import com.esgi.behere.utils.ApiUsage;
import com.esgi.behere.utils.CacheContainer;
import com.esgi.behere.utils.VolleyCallback;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;


public class CommentaryListActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private VolleyCallback mResultCallback = null;
    private ListView listComments;
    private ArrayList<Publication> publications =new ArrayList<>();

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments_market);
        listComments = findViewById(R.id.listComments);
        sharedPreferences = getBaseContext().getSharedPreferences(getString(R.string.prefs), MODE_PRIVATE);
        BottomNavigationView navigationView = findViewById(R.id.footerpub);
        navigationView.setOnNavigationItemSelectedListener(this::onOptionsItemSelected);
        CacheContainer.getInstance().getFriends().clear();
        if(getIntent().getExtras() != null) {
            ApiUsage mVolleyService;
            if (getIntent().getExtras().getString("entityType").equals("Bar")) {
                prepareGetAllCommentsBar();
                mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
                mVolleyService.getAllCommentsBar(getIntent().getExtras().getLong("entityID"));
            }
            if (getIntent().getExtras().getString("entityType").equals("Brewery")) {
                prepareGetAllCommentsBrewery();
                mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
                mVolleyService.getAllCommentsBrewerys(getIntent().getExtras().getLong("entityID"));
            }

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

    @Override
    public void onBackPressed() {
        Intent next;
        next = new Intent(getApplicationContext(), MapActivity.class);
        startActivity(next);
        finish();
    }

    private void prepareGetAllCommentsBar(){
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    if (!(boolean) response.get("error")) {
                        publications = new ArrayList<>();
                        JSONParser parser = new JSONParser();
                        JSONArray resCommentBar = (JSONArray) parser.parse(response.get("commentsBar").toString());
                        if (!resCommentBar.isEmpty()) {
                            for (Object unres : resCommentBar) {
                                JSONObject objres = (JSONObject) new JSONTokener(unres.toString()).nextValue();
                                String resDate = objres.getString("created_at").replace("T"," ").replace(".000Z", " ");
                                Date created_at= formatter.parse(resDate);
                                publications.add(new Publication("", objres.getString("text"),created_at,objres.getLong("user_id"),"bar"));
                            }
                        }
                        Collections.sort(publications);
                        CommentMarketAdapter adapter = new CommentMarketAdapter(Objects.requireNonNull(getApplicationContext()),publications);
                        listComments.setAdapter(adapter);
                    }
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }
            @Override
            public void onError(VolleyError error) {
                TextView tvCommentsTitle = findViewById(R.id.tvCommentsTitle);
                tvCommentsTitle.setText("NO COMMENTS");
            }
        };
    }


    private void prepareGetAllCommentsBrewery(){
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    if (!(boolean) response.get("error")) {
                        publications = new ArrayList<>();
                        JSONParser parser = new JSONParser();
                        JSONArray resCommentBrewery = (JSONArray) parser.parse(response.get("commentsBrewery").toString());
                        if (!resCommentBrewery.isEmpty()) {
                            for (Object unres : resCommentBrewery) {
                                JSONObject objres = (JSONObject) new JSONTokener(unres.toString()).nextValue();
                                String resDate = objres.getString("created_at").replace("T"," ").replace(".000Z", " ");
                                Date created_at= formatter.parse(resDate);
                                publications.add(new Publication("", objres.getString("text"),created_at,objres.getLong("brewery_id"),"brewery"));
                            }
                        }
                        Collections.sort(publications);
                        CommentMarketAdapter adapter = new CommentMarketAdapter(Objects.requireNonNull(getApplicationContext()),publications);
                        listComments.setAdapter(adapter);
                    }
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }
            @Override
            public void onError(VolleyError error) {
                TextView tvCommentsTitle = findViewById(R.id.tvCommentsTitle);
                tvCommentsTitle.setText("NO COMMENTS");
            }
        };
    }
}
