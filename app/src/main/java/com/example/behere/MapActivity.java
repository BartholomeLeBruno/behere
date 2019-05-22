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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.behere.actor.Market;
import com.example.behere.utils.ApiUsage;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.GeoApiContext.Builder;
import com.google.maps.android.PolyUtil;
import com.google.maps.model.DirectionsResult;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MapActivity extends AppCompatActivity implements GoogleMap.OnMarkerClickListener,OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int REQUEST_CODE = 1234;
    private static final String PREFS = "PREFS";
    private static final String PREFS_ID = "USER_ID";
    private SharedPreferences sharedPreferences;
    private Dialog match_text_dialog;
    private ArrayList<String> matches_text;
    private DrawerLayout mDrawerLayout;
    private Marker depart;
    private Marker arrivee;
    private Polyline direction;
    private  Marker marker;
    private DirectionsResult result;
    private ArrayList<Market> listMarket = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
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
        listMarket = new ArrayList<>();
        try {
            long id;
             String name;
             Double latitude;
             Double longitutde;
             String description;
             String webSiteLink;
            JSONObject result = ApiUsage.getAllBar();
            if (!(boolean) result.get("error")) {
                JSONParser parser = new JSONParser();
                JSONArray res = (JSONArray) parser.parse(result.get("bar").toString());
                for (Object unres : res) {
                    JSONObject objres = (JSONObject) parser.parse(unres.toString());
                    id = (long) objres.get("id");
                    name = (String)  objres.get("name");
                    description = (String)  objres.get("description");
                    latitude =  (Double)  objres.get("gpsLatitude");
                    longitutde = (Double)  objres.get("gpsLongitude");
                    webSiteLink = (String)  objres.get("webSiteLink");
                    Market market = new Market(id,name,latitude,longitutde, description,webSiteLink);
                    listMarket.add(market);
                }
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

            Toolbar toolbar = findViewById(R.id.toolbar);
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

    private Builder getBuilder() {
        Builder geoApiContext = new Builder();
        return geoApiContext.queryRateLimit(3).apiKey("AIzaSyBuAnhRy95K8XSSehEciHxGTbrlrAtQLj8").connectTimeout(1, TimeUnit.SECONDS)
                .readTimeout(1, TimeUnit.SECONDS).writeTimeout(1, TimeUnit.SECONDS);
    }

    private void addMarkersToMap(DirectionsResult results ) {
        depart = mMap.addMarker(new MarkerOptions().position(
                new LatLng(results.routes[0].legs[0].startLocation.lat,
                        results.routes[0].legs[0].startLocation.lng)).title(results.routes[0].legs[0].startAddress));

        arrivee = mMap.addMarker(new MarkerOptions().position(
                new LatLng(results.routes[0].legs[0].endLocation.lat,
                        results.routes[0].legs[0].endLocation.lng)).title(results.routes[0].legs[0].endAddress).snippet(getEndLocationTitle(results)));

        Log.d("duree:", result.routes[0].legs[0].duration.humanReadable);

        //Search search = dataSource.createSearch(startPoint.getText().toString(), endPoint.getText().toString(), result.routes[0].legs[0].duration.humanReadable);

    }

    private String getEndLocationTitle(DirectionsResult results) {
        return  "Time :"+ results.routes[0].legs[0].duration.humanReadable +
                " Distance :" + results.routes[0].legs[0].distance.humanReadable;
    }

    private void addPolyline(DirectionsResult results) {
        List<LatLng> decodedPath = PolyUtil.decode(results.routes[0].overviewPolyline.getEncodedPath());
        direction =  mMap.addPolyline(new PolylineOptions().addAll(decodedPath));
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
        if(!listMarket.isEmpty())
        {
            for (Market market: listMarket) {
                LatLng latLng = new LatLng(market.getLatitude(), market.getLongitutde());
                marker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(market.getName())
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
            clickCount = clickCount + 1;
            marker.setTag(clickCount);
            Toast.makeText(this,
                    marker.getTitle() +
                            " has been clicked " + clickCount + " times.",
                    Toast.LENGTH_SHORT).show();
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
        }
        return super.onOptionsItemSelected(item);
    }


}