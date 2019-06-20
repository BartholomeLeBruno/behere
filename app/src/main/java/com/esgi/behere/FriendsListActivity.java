package com.esgi.behere;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;


public class FriendsListActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private static final String PREFS = "PREFS";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_friends);

        //listview.setAdapter(new FriendAdpater(this, new String[] { "data1",
          //      "data2","data1","data1","data1","data1","data1","data1","data1","data1","data1","data1","data1","data1","data1","data1","data1","data1","data1","data1","data1","data1","data1","data1","data1","data1","datalast" }));
        sharedPreferences = getBaseContext().getSharedPreferences(PREFS, MODE_PRIVATE);
        BottomNavigationView navigationView = findViewById(R.id.footerpub);
        navigationView.getMenu().setGroupCheckable(4,true,false);
        navigationView.setOnNavigationItemReselectedListener(this::onOptionsItemSelected);

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

    /*private void prepareGetAllFriends(){
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    ListView listview = findViewById(R.id.listFriends);
                    ResultSearch resultSearch;
                    if (!(boolean) response.get("error")) {
                        JSONParser parser = new JSONParser();
                        JSONArray resUsers = (JSONArray) parser.parse(response.get("message").toString());
                        if (!resUsers.isEmpty()) {
                            for (Object unres : resUsers) {
                                JSONObject objres = (JSONObject) new JSONTokener(unres.toString()).nextValue();
                                resultSearch = new ResultSearch();
                                resultSearch.setName(objres.getString("name") + " " +objres.getString("surname"));
                                resultSearch.setType("User");
                                resultSearch.setId(Long.parseLong(objres.getString("id")));
                                if(resultSearch.getId() != sharedPreferences.getLong(PREFS_ID,0))
                                    resultSearches.add(resultSearch);
                            }
                        }
                        SearchAdapter adapter = new SearchAdapter(getApplicationContext(),resultSearches);
                        listview.setAdapter(adapter);
                    }
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }
            @Override
            public void onError(VolleyError error) {
                Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(loginActivity);

            }
        };
    }*/
}
