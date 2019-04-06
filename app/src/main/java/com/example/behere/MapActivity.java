package com.example.behere;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext.Builder;
import com.google.maps.android.PolyUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TransitMode;
import com.google.maps.model.TravelMode;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int REQUEST_CODE = 1234;

    private Dialog match_text_dialog;
    private ArrayList<String> matches_text;
    private EditText startPoint, endPoint;
    private DrawerLayout mDrawerLayout;
    private Marker depart;
    private Marker arrivée;
    private Polyline direction;



    private DirectionsResult result;

    private void loadMapWithPredefinedValues(Bundle extras)
    {
        try {
            String depart = extras.getString("depart");
            String arrivee = extras.getString("arrivee");
            Instant heure = Instant.parse(extras.getString("heure"));
            result = DirectionsApi.newRequest(getBuilder().build()).mode(TravelMode.TRANSIT)
                    .transitMode(TransitMode.TRAIN)
                    .origin(depart)
                    .destination(arrivee).departureTime(heure).await();
            addMarkersToMap(result);
            addPolyline(result);
            LatLng middle = new LatLng((result.routes[0].legs[0].startLocation.lat + result.routes[0].legs[0].endLocation.lat) / 2,
                    (result.routes[0].legs[0].startLocation.lng + result.routes[0].legs[0].endLocation.lng) / 2);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(middle, 17.f));
        }
        catch(IOException  | InterruptedException | ApiException e)
        {
            Log.d("Erreur", "onItemClick: "+e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startPoint = findViewById(R.id.start_EditText);
        endPoint = findViewById(R.id.end_EditText);
        // Drawer navigation
        mDrawerLayout = findViewById(R.id.drawer_layout);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            loadMapWithPredefinedValues(extras);
            //The key argument here must match that used in the other activity
        }
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        //menuItem.setChecked(false);
                        onOptionsItemSelected(menuItem);
                        mDrawerLayout.closeDrawers();
                        return false;
                    }
                }
        );

        Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
       // actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24px);
        startPoint.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!endPoint.getText().toString().equals(""))
                {
                    try {
                        result = DirectionsApi.newRequest(getBuilder().build()).mode(TravelMode.TRANSIT)
                                .transitMode(TransitMode.TRAIN)
                                .origin(startPoint.getText().toString())
                                .destination(endPoint.getText().toString()).departureTime(Instant.now()).await();
                                addMarkersToMap(result);


                    }
                    catch(IOException  | InterruptedException | ApiException e)
                    {
                        Log.d("Erreur", "onItemClick: "+e);
                    }
                }
            }
        });
        endPoint.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!startPoint.getText().toString().equals(""))
                {
                    try {
                        result = DirectionsApi.newRequest(getBuilder().build()).mode(TravelMode.TRANSIT)
                                .transitMode(TransitMode.TRAIN)
                                .origin(startPoint.getText().toString())
                                .destination(endPoint.getText().toString()).departureTime(Instant.now()).await();
                        addMarkersToMap(result);
                        addPolyline(result);
                        LatLng middle = new LatLng((result.routes[0].legs[0].startLocation.lat + result.routes[0].legs[0].endLocation.lat)/2,
                                (result.routes[0].legs[0].startLocation.lng + result.routes[0].legs[0].endLocation.lng)/2);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(middle, 17.f));
                    }
                    catch(IOException  | InterruptedException | ApiException e)
                    {
                        Log.d("Erreur", "onItemClick: "+e);
                    }
                }
            }
        });
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

        arrivée = mMap.addMarker(new MarkerOptions().position(
                new LatLng(results.routes[0].legs[0].endLocation.lat,
                        results.routes[0].legs[0].endLocation.lng)).title(results.routes[0].legs[0].endAddress).snippet(getEndLocationTitle(results)));

        Log.d("startPoint:", startPoint.getText().toString());
        Log.d("endPoint:", endPoint.getText().toString());
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



/*    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.nav_advancedResearch:
                intent = new Intent(MainActivity.this, AdvancedResearchActivity.class);
                MainActivity.this.startActivity(intent);
                return true;
            case R.id.nav_history:
                intent = new Intent(MainActivity.this, HistoryActivity.class);
                MainActivity.this.startActivity(intent);
                return true;
            case R.id.nav_details:
                // TODO: Ajouter activité détails
        }
        return super.onOptionsItemSelected(item);
    }
    */

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
        //mMap.getUiSettings().setZoomControlsEnabled(true);
        //mMap.setMyLocationEnabled(true);
        // Add a marker in Sydney and move the camera
        LatLng Paris = new LatLng(48.8534,  2.3488);
       // mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.zoomTo(15.0f));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Paris, 15.0f));
    }

}