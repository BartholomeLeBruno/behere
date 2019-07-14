package com.esgi.behere;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ListView;

import com.android.volley.error.VolleyError;
import com.esgi.behere.actor.Beer;
import com.esgi.behere.actor.BeerType;
import com.esgi.behere.adapter.BeerAdapter;
import com.esgi.behere.adapter.SupplementTypeOfBeerAdapter;
import com.esgi.behere.utils.ApiUsage;
import com.esgi.behere.utils.PopupAchievement;
import com.esgi.behere.utils.VolleyCallback;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.HashMap;

public class LexiconActivity extends AppCompatActivity {

    private TabLayout tabTypeOfBeer;
    private VolleyCallback mResultCallback = null;
    private ApiUsage mVolleyService;
    private ArrayList<BeerType> beerTypes;
    public static ListView listView_result;
    public static GridView gridViewBeers;
    private ArrayList<Beer> beers;
    private SharedPreferences sharedPreferences;

    private HashMap<String, Long> header;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lexicon);
        tabTypeOfBeer = findViewById(R.id.tabTypeOfBeer);
        listView_result = findViewById(R.id.listView_result);
        gridViewBeers = findViewById(R.id.gridViewBeers);
        sharedPreferences = getBaseContext().getSharedPreferences(getString(R.string.prefs), MODE_PRIVATE);
        BottomNavigationView navigationView = findViewById(R.id.footerpub);
        navigationView.setOnNavigationItemSelectedListener(this::onOptionsItemSelected);
        prepareTypeOfBeer();
        mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
        mVolleyService.getAllTypeOfBeer();
        tabTypeOfBeer.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText().toString().equals("...")) {
                    listView_result.setAdapter(new SupplementTypeOfBeerAdapter(getApplicationContext(), beerTypes));
                    listView_result.setVisibility(View.VISIBLE);
                } else {
                    listView_result.setVisibility(View.INVISIBLE);
                    beers = new ArrayList<>();
                    gridViewBeers.setAdapter(new BeerAdapter(getApplicationContext(),beers));
                    prepareGetBeer();
                    mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
                    mVolleyService.getAllBeerWithTypeOfBeer(header.getOrDefault(tab.getText().toString(), (long) 1));
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (!tab.getText().toString().equals("...")) {
                    listView_result.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (tab.getText().toString().equals("...")) {
                    listView_result.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    private void prepareTypeOfBeer() {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (!(boolean) response.get("error")) {
                        header = new HashMap<>();
                        beerTypes = new ArrayList<>();
                        JSONParser parser = new JSONParser();
                        JSONArray resType = (JSONArray) parser.parse(response.get("typeOfBeer").toString());
                        JSONObject objres;
                        TabLayout.Tab newTab;
                        boolean done = false;
                        if (!resType.isEmpty()) {
                            for (int i = 0; i < resType.size(); i++) {
                                objres = (JSONObject) new JSONTokener(resType.get(i).toString()).nextValue();
                                newTab = tabTypeOfBeer.newTab();

                                if (i < 4) {
                                    newTab.setText(objres.getString("name"));
                                    tabTypeOfBeer.addTab(newTab);
                                    header.put(objres.getString("name"), objres.getLong("id"));

                                } else {
                                    if (!done) {
                                        newTab.setText("...");
                                        tabTypeOfBeer.addTab(newTab);
                                    }
                                    done = true;
                                    BeerType beerType = new BeerType(objres.getString("name"));
                                    beerType.setId((int)objres.getLong("id"));
                                    beerTypes.add(beerType);
                                }

                            }
                        }
                    }
                } catch (Exception e) {
                    startActivity(getIntent());
                    finish();
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

    private void prepareGetBeer() {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (!(boolean) response.get("error")) {
                        beers = new ArrayList<>();
                        JSONParser parser = new JSONParser();
                        JSONArray resBeer = (JSONArray) parser.parse(response.get("beer").toString());
                        JSONObject objres;
                        Beer oneBeer;
                        if (!resBeer.isEmpty()) {
                            for (Object beer : resBeer) {
                                oneBeer = new Beer();
                                objres = (JSONObject) new JSONTokener(beer.toString()).nextValue();
                                oneBeer.setId(objres.getLong("id"));
                                oneBeer.setColor(objres.getString("color"));
                                oneBeer.setDescription(objres.getString("description"));
                                oneBeer.setName(objres.getString("name"));
                                oneBeer.setOrigin(objres.getString("origin"));
                                beers.add(oneBeer);
                            }
                            gridViewBeers.setAdapter( new BeerAdapter(getApplicationContext(),beers));
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
            case R.id.navigation_myprofile:
                next = new Intent(getApplicationContext(), DefaultProfileActivity.class);
                startActivity(next);
                return true;
            case R.id.navigation_mygroups:
                next = new Intent(getApplicationContext(), MyGroupActivity.class);
                startActivity(next);
                return true;
            case R.id.navigation_home:
                next = new Intent(getApplicationContext(), MapActivity.class);
                next.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(next);
                finish();
                return true;
        }
        return false;
    }
}
