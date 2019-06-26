package com.esgi.behere;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.android.volley.VolleyError;
import com.esgi.behere.actor.Bar;
import com.esgi.behere.actor.Market;
import com.esgi.behere.actor.ResultSearch;
import com.esgi.behere.utils.ApiUsage;
import com.esgi.behere.utils.CacheContainer;
import com.esgi.behere.utils.VolleyCallback;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.Polyline;
import com.google.maps.GeoApiContext.Builder;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.maps.DirectionsApi;
import com.google.maps.android.PolyUtil;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.esgi.behere.adapter.SearchAdapter;

import org.json.JSONTokener;
import org.json.simple.JSONArray;
import org.json.JSONObject;

import org.json.simple.parser.JSONParser;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.esgi.behere.utils.CacheContainer.initializeQueue;

public class MapActivity extends AppCompatActivity implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback, com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    private static final String PREFS = "PREFS";
    private static final String PREFS_ID = "USER_ID";
    private final String PREFS_LONGITUDE = "LONGITUDE";
    private final String PREFS_LATITUDE = "LATITUDE";
    private SharedPreferences sharedPreferences;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private Boolean mLocationPermissionsGranted = false;
    FusedLocationProviderClient mFusedLocationProviderClient;
    private LatLng latLng;
    FloatingActionButton btnRecenter;
    private Marker marker, home;
    Polyline polyline;
    final String TAG = "MapActivity";
    private VolleyCallback mResultCallback = null;
    private ApiUsage mVolleyService;
    private ListView listView;
    private List<ResultSearch> resultSearches = new ArrayList<>();
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;



    MaterialSearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initializeQueue();
        // Drawer navigation
        btnRecenter = findViewById(R.id.btnCenter);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        sharedPreferences = getBaseContext().getSharedPreferences(PREFS, MODE_PRIVATE);
        BottomNavigationView navigationView = findViewById(R.id.footerpub);
        navigationView.setOnNavigationItemSelectedListener(this::onOptionsItemSelected);
        prepareGetAllBar();
        mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
        mVolleyService.getAllBar();
        prepareGetAllBrewery();
        mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
        mVolleyService.getAllBrewery();
        getLocationPermission();
        Toolbar toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);
        searchView = findViewById(R.id.search_view);
        listView = findViewById(R.id.listView_result);
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                prepareGetAllEntities();
                mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
                mVolleyService.getAllEntities();
                listView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSearchViewClosed() {
                listView.setVisibility(View.INVISIBLE);

            }
        });
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                sharedPreferences = getBaseContext().getSharedPreferences(PREFS, MODE_PRIVATE);
                List<ResultSearch> lsFound = new ArrayList<>();
                if (newText != null && !newText.trim().equals("")) {
                    for (ResultSearch item : resultSearches) {
                        if (item.getName().contains(newText))
                            lsFound.add(item);
                    }
                    SearchAdapter adapter = new SearchAdapter(MapActivity.this, lsFound);
                    listView.setAdapter(adapter);
                }
                return true;
            }
        });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if(home != null) {
                    home.remove();
                }
                if(polyline != null) {
                    polyline.remove();
                }
                home = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude()))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_person_current_location)));
                home.setTag(0);
                btnRecenter.setOnClickListener((View v) -> mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude()), DEFAULT_ZOOM)));
                if (!Objects.equals(sharedPreferences.getString(PREFS_LATITUDE, ""), "")) {
                    double latitude = Double.parseDouble(Objects.requireNonNull(sharedPreferences.getString(PREFS_LATITUDE, "")));
                    double longitude = Double.parseDouble(Objects.requireNonNull(sharedPreferences.getString(PREFS_LONGITUDE, "")));
                    if (latitude != 0 && longitude != 0) {
                        addPolyline(home.getPosition(), new LatLng(latitude, longitude));
                    }
                }
            }
        };
        FloatingActionButton btnDelete = findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(v -> {
            if(polyline != null) polyline.remove();
            sharedPreferences.edit().remove(PREFS_LONGITUDE).apply();
            sharedPreferences.edit().remove(PREFS_LATITUDE).apply();
            RelativeLayout relativeLayout = findViewById(R.id.rlTimeResult);
            relativeLayout.setVisibility(View.INVISIBLE);
        });
        startTrackingLocation();
    }

    private void startTrackingLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},1
                    );
        } else {
            mFusedLocationClient.requestLocationUpdates
                    (getLocationRequest(),
                            mLocationCallback,
                            null /* Looper */);
        }
    }

    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
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
        if (!CacheContainer.getInstance().getMarketHashMap().isEmpty()) {
            for (Map.Entry<String, Market> market : CacheContainer.getInstance().getMarketHashMap().entrySet()) {
                LatLng latLng = new LatLng(market.getValue().getLatitude(), market.getValue().getLongitutde());
                marker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(market.getKey())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.my_icon_bar)));
                marker.setTag(0);
            }
        }
        mMap.setOnMarkerClickListener(this);
        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    private void addPolyline(LatLng home, LatLng toMarket) {
        try {
            DirectionsResult result = DirectionsApi.newRequest(getBuilder().build()).mode(TravelMode.WALKING)
                    .origin(new com.google.maps.model.LatLng(home.latitude, home.longitude))
                    .destination(new com.google.maps.model.LatLng(toMarket.latitude, toMarket.longitude)).departureTime(Instant.now()).await();
            List<LatLng> decodedPath = PolyUtil.decode(result.routes[0].overviewPolyline.getEncodedPath());
            String durationLegs =  result.routes[0].legs[0].duration.humanReadable;
            RelativeLayout rlTime = findViewById(R.id.rlTimeResult);
            rlTime.setVisibility(View.VISIBLE);
            TextView tvLegsTime = findViewById(R.id.tvLegsTime);
            tvLegsTime.setText(durationLegs);
            // decodedPath.add(home);
            // decodedPath.add(toMarket);
            polyline = mMap.addPolyline(new PolylineOptions().addAll(decodedPath));
            polyline.remove();
            polyline = mMap.addPolyline(new PolylineOptions().addAll(decodedPath));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMap.clear();
        CacheContainer.getInstance().getMarketHashMap().clear();


    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(getApplicationContext(), location.toString(), Toast.LENGTH_SHORT).show();
    }


    /** Called when the user clicks a marker. */
    @Override
    public boolean onMarkerClick(final Marker marker) {

        if(marker.equals(home)) { return  true; }
        // Retrieve the data from the marker.
        Integer clickCount = (Integer) marker.getTag();

        // Check if a click count was set, then display the click count.
        if (clickCount != null) {
            Market m = CacheContainer.getInstance().getMarketHashMap().get(marker.getTitle());
            Intent nextStep = new Intent(MapActivity.this, MarketProfilActivity.class);
            nextStep.putExtra("market", m);
            startActivity(nextStep);
        }

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
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
        }
        return false;
    }


    private void prepareGetAllBar() {
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
                        Log.d("voila", response.toString());
                        JSONArray res = (JSONArray) parser.parse(response.get("bar").toString());
                        if (!res.isEmpty()) {
                            for (Object unres : res) {
                                objres = (JSONObject) new JSONTokener(unres.toString()).nextValue();
                                id = Long.parseLong(objres.get("id").toString());
                                name = objres.get("name").toString();
                                description = objres.get("description").toString();
                                latitude = Double.parseDouble(objres.get("gpsLatitude").toString());
                                longitutde = Double.parseDouble(objres.get("gpsLongitude").toString());
                                webSiteLink = objres.get("webSiteLink").toString();
                                CacheContainer.getInstance().getMarketHashMap().put(name, new Bar(id, name, latitude, longitutde, description, webSiteLink, "Bar"));
                                marker = mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(latitude, longitutde))
                                        .title(name)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.my_icon_bar)));
                                marker.setTag(0);
                            }
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

    private void prepareGetAllBrewery() {
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
                        Log.d("voila", response.toString());
                        JSONArray res = (JSONArray) parser.parse(response.get("brewery").toString());
                        if (!res.isEmpty()) {
                            for (Object unres : res) {
                                objres = (JSONObject) new JSONTokener(unres.toString()).nextValue();
                                id = Long.parseLong(objres.get("id").toString());
                                name = objres.get("name").toString();
                                description = objres.get("description").toString();
                                latitude = Double.parseDouble(objres.get("gpsLatitude").toString());
                                longitutde = Double.parseDouble(objres.get("gpsLongitude").toString());
                                webSiteLink = objres.get("webSiteLink").toString();
                                CacheContainer.getInstance().getMarketHashMap().put(name, new Bar(id, name, latitude, longitutde, description, webSiteLink, "Brewery"));
                                marker = mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(latitude, longitutde))
                                        .title(name)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.beergarden)));
                                marker.setTag(0);
                            }
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

    private void getDeviceLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionsGranted) {
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener((@NonNull Task task) -> {
                            if (task.isSuccessful()) {
                                Location homeLocation = (Location) task.getResult();
                                assert homeLocation != null;
                                latLng = new LatLng(homeLocation.getLatitude(), homeLocation.getLongitude());
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
                            } else {
                                Toast.makeText(MapActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                            }
                        }
                );
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
            Intent next = new Intent(getApplicationContext(), LoginActivity.class);
            sharedPreferences.edit().clear().apply();
            next.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(next);
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionsGranted = false;
        if (grantResults.length > 0) {
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionsGranted = false;
                    return;
                }
            }
            mLocationPermissionsGranted = true;
            initMap();
        }
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) mapFragment.getMapAsync(MapActivity.this);
    }

    private void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onBackPressed() {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);

    }

    private Builder getBuilder() {
        Builder geoApiContext = new Builder();
        return geoApiContext.queryRateLimit(3).apiKey("AIzaSyBuAnhRy95K8XSSehEciHxGTbrlrAtQLj8").connectTimeout(1, TimeUnit.SECONDS)
                .readTimeout(1, TimeUnit.SECONDS).writeTimeout(1, TimeUnit.SECONDS);
    }

    private void prepareGetAllEntities(){
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    ResultSearch resultSearch;
                    if (!(boolean) response.get("error")) {
                        JSONParser parser = new JSONParser();
                        JSONArray resUsers = (JSONArray) parser.parse(response.get("users").toString());
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
                        JSONArray resBars = (JSONArray) parser.parse(response.get("bars").toString());
                        if (!resBars.isEmpty()) {
                            for (Object unres : resBars) {
                                JSONObject objres = (JSONObject) new JSONTokener(unres.toString()).nextValue();
                                resultSearch = new ResultSearch();
                                resultSearch.setName(objres.getString("name"));
                                resultSearch.setType("Bar");
                                resultSearch.setId(Long.parseLong(objres.getString("id")));
                                resultSearches.add(resultSearch);
                            }
                        }
                        JSONArray resbrewerys = (JSONArray) parser.parse(response.get("brewerys").toString());
                        if (!resbrewerys.isEmpty()) {
                            for (Object unres : resbrewerys) {
                                JSONObject objres = (JSONObject) new JSONTokener(unres.toString()).nextValue();
                                resultSearch = new ResultSearch();
                                resultSearch.setName(objres.getString("name"));
                                resultSearch.setType("Brewery");
                                resultSearch.setId(Long.parseLong(objres.getString("id")));
                                resultSearches.add(resultSearch);
                            }
                        }
                        JSONArray resGroups = (JSONArray) parser.parse(response.get("groups").toString());
                        if (!resGroups.isEmpty()) {
                            for (Object unres : resGroups) {
                                JSONObject objres = (JSONObject) new JSONTokener(unres.toString()).nextValue();
                                resultSearch = new ResultSearch();
                                resultSearch.setName(objres.getString("name"));
                                resultSearch.setType("Group");
                                resultSearch.setId(Long.parseLong(objres.getString("id")));
                                resultSearches.add(resultSearch);
                            }
                        }
                        SearchAdapter adapter = new SearchAdapter(getApplicationContext(),resultSearches);
                        listView.setAdapter(adapter);
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
    }
}