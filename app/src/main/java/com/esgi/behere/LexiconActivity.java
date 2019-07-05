package com.esgi.behere;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.GridView;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.esgi.behere.actor.Beer;
import com.esgi.behere.actor.BeerType;
import com.esgi.behere.adapter.SupplementTypeOfBeerAdapter;
import com.esgi.behere.utils.ApiUsage;
import com.esgi.behere.utils.PopupAchievement;
import com.esgi.behere.utils.VolleyCallback;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;

public class LexiconActivity extends AppCompatActivity {

    private TabLayout tabTypeOfBeer;
    private VolleyCallback mResultCallback = null;
    private ApiUsage mVolleyService;
    //private TabItem dotItem;
    private ArrayList<BeerType> beerTypes;
    public static ListView listView_result;
    private GridView gridViewBeers;
    private  ArrayList<Beer> beers;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lexicon);
        tabTypeOfBeer = findViewById(R.id.tabTypeOfBeer);
        listView_result = findViewById(R.id.listView_result);
        gridViewBeers = findViewById(R.id.gridViewBeers);
        prepareTypeOfBeer();
        mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
        mVolleyService.getAllTypeOfBeer();
        tabTypeOfBeer.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText().toString().equals("...")) {
                    listView_result.setAdapter(new SupplementTypeOfBeerAdapter(getApplicationContext(), beerTypes));
                    listView_result.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void prepareTypeOfBeer() {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (!(boolean) response.get("error")) {
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

                                } else {
                                    if (!done) {
                                        newTab.setText("...");
                                        tabTypeOfBeer.addTab(newTab);
                                    }
                                    done = true;
                                    //tabTypeOfBeer.addView(new Spinner(getApplicationContext()));
                                    beerTypes.add(new BeerType(objres.getString("name")));
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
}
