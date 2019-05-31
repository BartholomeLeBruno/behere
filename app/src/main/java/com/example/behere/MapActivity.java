package com.example.behere;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.example.behere.actor.Bar;
import com.example.behere.actor.Market;
import com.example.behere.utils.ApiUsage;
import com.example.behere.utils.VolleyCallback;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONTokener;
import org.json.simple.JSONArray;
import org.json.JSONObject;

import org.json.simple.parser.JSONParser;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.behere.utils.CacheContainer.initializeQueue;

public class MapActivity extends AppCompatActivity implements GoogleMap.OnMarkerClickListener,OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int REQUEST_CODE = 1234;
    private static final String PREFS = "PREFS";
    private static final String PREFS_ID = "USER_ID";
    private SharedPreferences sharedPreferences;
    Dialog match_text_dialog;
    ArrayList<String> matches_text;
    private DrawerLayout mDrawerLayout;
    Marker marker;
    private HashMap<String,Market> mapMarket = new HashMap<>();
    private String TAG = "MapActivity";
    VolleyCallback mResultCallback = null;
    ApiUsage mVolleyService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initializeQueue();
        // Drawer navigation
        mDrawerLayout = findViewById(R.id.drawer_layout);
        sharedPreferences = getBaseContext().getSharedPreferences(PREFS, MODE_PRIVATE);
        BottomNavigationView navigationView = findViewById(R.id.footer);
        navigationView.setOnNavigationItemReselectedListener(
                new BottomNavigationView.OnNavigationItemReselectedListener() {
                 @Override
                 public void onNavigationItemReselected(@NonNull MenuItem menuItem) {
                     onOptionsItemSelected(menuItem);
                     mDrawerLayout.closeDrawers();
                 }
             }
        );
        prepareGetAllBar();
        mVolleyService = new ApiUsage(mResultCallback,getApplicationContext());
        mVolleyService.getAllBar();
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //setSupportActionBar(toolbar);
       // actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24px);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            match_text_dialog = new Dialog(MapActivity.this);
            match_text_dialog.setContentView(R.layout.dialog_matches_frag);
            match_text_dialog.setTitle("Select Matching Text");
            ListView textlist = match_text_dialog.findViewById(R.id.list);
            matches_text = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            ArrayAdapter<String> adapter =    new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, matches_text);
            textlist.setAdapter(adapter);
            match_text_dialog.show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(!mapMarket.isEmpty())
        {
            for (Map.Entry<String,Market> market: mapMarket.entrySet()) {
                LatLng latLng = new LatLng(market.getValue().getLatitude(), market.getValue().getLongitutde());
                marker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(market.getKey())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.my_icon_bar)));
                marker.setTag(0);
            }
        }
        mMap.setOnMarkerClickListener(this);
    }


    /** Called when the user clicks a marker. */
    @Override
    public boolean onMarkerClick(final Marker marker) {

        // Retrieve the data from the marker.
        Integer clickCount = (Integer) marker.getTag();

        // Check if a click count was set, then display the click count.
        if (clickCount != null) {
            if(mapMarket.containsKey(marker.getTitle())) {
                Market m = mapMarket.get(marker.getTitle());
                Intent nextStep = new Intent(MapActivity.this, MarketProfilActivity.class);
                nextStep.putExtra("market", m);
                startActivity(nextStep);
            }
        }

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.disconnected:
                Intent intent = new Intent(MapActivity.this, LoginActivity.class);
                sharedPreferences.edit().remove(PREFS_ID).apply();
                startActivity(intent);
                return true;
            case R.id.navigation_home:
                Intent intentHome = new Intent(MapActivity.this, MapActivity.class);
                startActivity(intentHome);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    void prepareGetAllBar() {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    long id;
                    String name;
                    double latitude;
                    double longitutde;
                    String description;
                    String webSiteLink;
                    JSONObject objres;
                    if (!(boolean) response.get("error")) {
                        JSONParser parser = new JSONParser();
                        JSONArray res = (JSONArray) parser.parse(response.get("bar").toString());
                        for (Object unres : res) {
                            objres = (JSONObject) new JSONTokener(unres.toString()).nextValue();
                            id =  Long.parseLong(objres.get("id").toString());
                            name =  objres.get("name").toString();
                            description =  objres.get("description").toString();
                            latitude =  Double.parseDouble(objres.get("gpsLatitude").toString());
                            longitutde = Double.parseDouble(objres.get("gpsLongitude").toString());
                            webSiteLink =  objres.get("webSiteLink").toString();
                            mapMarket.put(name,  new Bar(id, name, latitude, longitutde, description, webSiteLink));
                            marker = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(latitude,longitutde))
                                    .title(name)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.my_icon_bar)));
                            marker.setTag(0);
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
}