package com.esgi.behere;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.android.volley.error.VolleyError;
import com.esgi.behere.actor.Group;
import com.esgi.behere.adapter.GroupAdapter;
import com.esgi.behere.utils.ApiUsage;
import com.esgi.behere.utils.VolleyCallback;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.List;


public class MyGroupActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private VolleyCallback mResultCallback = null;
    private ListView listMyGroups;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_groups);
        sharedPreferences = getBaseContext().getSharedPreferences(getString(R.string.prefs), MODE_PRIVATE);
        listMyGroups = findViewById(R.id.listMyGroups);
        BottomNavigationView navigationView = findViewById(R.id.footerpub);
        navigationView.setOnNavigationItemSelectedListener(this::onOptionsItemSelected);
        prepareGetAllGroups();
        ApiUsage volleyService = new ApiUsage(mResultCallback, getApplicationContext());
        volleyService.getAllGroups(sharedPreferences.getLong(getString(R.string.prefs_id), 0));


    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Intent next;
        switch (item.getItemId()) {
            case R.id.disconnected:
                next = new Intent(getApplicationContext(), LoginActivity.class);
                next.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                sharedPreferences.edit().clear().apply();
                startActivity(next);
                finish();
                return true;
            case R.id.navigation_myprofile:
                next = new Intent(getApplicationContext(), DefaultProfileActivity.class);
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

    public void openCreateGroup(View view){
        Intent next;
        next = new Intent(getApplicationContext(), CreateGroupeActivity.class);
        startActivity(next);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent next;
        next = new Intent(getApplicationContext(), MapActivity.class);
        startActivity(next);
        finish();
    }


    private void prepareGetAllGroups()
    {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (!(boolean) response.get("error")) {
                        List<Group> groups = new ArrayList<>();
                        JSONParser parser = new JSONParser();
                        JSONArray resGroup = (JSONArray) parser.parse(response.get("group").toString());
                        JSONObject objGroup;
                        long id;
                        String name;
                        for (Object group : resGroup) {
                            Group onegroup = new Group();
                            objGroup = (JSONObject) new JSONTokener(group.toString()).nextValue();
                            name = objGroup.getString("name");
                            id = objGroup.getLong("id");
                            onegroup.setId(id);
                            onegroup.setName(name);
                            groups.add(onegroup);
                        }
                        listMyGroups.setAdapter(new GroupAdapter(getApplicationContext(),groups));
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
