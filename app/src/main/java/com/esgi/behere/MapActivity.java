package com.esgi.behere;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.error.VolleyError;
import com.esgi.behere.actor.Bar;
import com.esgi.behere.actor.Market;
import com.esgi.behere.actor.ResultSearch;
import com.esgi.behere.adapter.SearchAdapter;
import com.esgi.behere.utils.ApiUsage;
import com.esgi.behere.utils.CacheContainer;
import com.esgi.behere.utils.PopupAchievement;
import com.esgi.behere.utils.VolleyCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext.Builder;
import com.google.maps.android.PolyUtil;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.simple.JSONArray;
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
    private SharedPreferences sharedPreferences;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private Boolean mLocationPermissionsGranted = false;
    private LatLng latLng;
    private FloatingActionButton btnRecenter;
    private Marker marker, home;
    private Polyline polyline;
    private VolleyCallback mResultCallback = null;
    private ApiUsage mVolleyService;
    private ListView listView;
    private List<ResultSearch> resultSearches = CacheContainer.getInstance().getResultSearches();
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private MaterialSearchView searchView;
    private TabLayout tabLayout;
    private String selectedTab = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeQueue();
        btnRecenter = findViewById(R.id.btnCenter);
        tabLayout = findViewById(R.id.tabLayout);
        tabLayoutHandlerOnTabSelected();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        sharedPreferences = getBaseContext().getSharedPreferences(getString(R.string.prefs), MODE_PRIVATE);
        Log.d("user_id", sharedPreferences.getLong(getString(R.string.prefs_id), 0) +"");

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
        searchViewHandlerOnSearch();
        if (searchViewHandlerOnQuery()) return;
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (home != null) {
                    home.remove();
                }
                if (polyline != null) {
                    polyline.remove();
                }
                home = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude()))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_person_current_location)));
                home.setTag(0);
                btnRecenter.setOnClickListener((View v) -> mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude()), DEFAULT_ZOOM)));
                if (!Objects.equals(sharedPreferences.getString(getString(R.string.latitude), ""), "")) {
                    double latitude = Double.parseDouble(Objects.requireNonNull(sharedPreferences.getString(getString(R.string.latitude), "")));
                    double longitude = Double.parseDouble(Objects.requireNonNull(sharedPreferences.getString(getString(R.string.longitude), "")));
                    if (latitude != 0 && longitude != 0) {
                        addPolyline(home.getPosition(), new LatLng(latitude, longitude));
                    }
                }
            }
        };
        FloatingActionButton btnDelete = findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(v -> {
            if (polyline != null) polyline.remove();
            sharedPreferences.edit().remove(getString(R.string.longitude)).apply();
            sharedPreferences.edit().remove(getString(R.string.latitude)).apply();
            RelativeLayout relativeLayout = findViewById(R.id.rlTimeResult);
            relativeLayout.setVisibility(View.INVISIBLE);
        });
        startTrackingLocation();
        updateNotification();
    }

    private void tabLayoutHandlerOnTabSelected() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectedTab = Objects.requireNonNull(tab.getText()).toString();
                List<ResultSearch> lsFound = new ArrayList<>();
                if (!"All".equals(selectedTab)) {
                    for (ResultSearch item : resultSearches) {
                        if(!"Market".equals(selectedTab)) {
                            if (item.getType().equals(selectedTab))
                                lsFound.add(item);
                        }
                        else {
                            if (item.getType().equals("Bar") || item.getType().equals("Brewery"))
                                lsFound.add(item);
                        }
                    }

                    SearchAdapter adapter = new SearchAdapter(MapActivity.this, lsFound);
                    listView.setAdapter(adapter);
                } else {
                    SearchAdapter adapter = new SearchAdapter(MapActivity.this, resultSearches);
                    listView.setAdapter(adapter);
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

    private boolean searchViewHandlerOnQuery() {
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<ResultSearch> lsFound = new ArrayList<>();
                if (newText != null && !newText.trim().equals("")) {
                    if (!"All".equals(selectedTab)) {
                        for (ResultSearch item : resultSearches) {
                            if (item.getName().contains(newText) && item.getType().equals(selectedTab))
                                lsFound.add(item);
                        }

                        SearchAdapter adapter = new SearchAdapter(MapActivity.this, lsFound);
                        listView.setAdapter(adapter);
                    } else {
                        SearchAdapter adapter = new SearchAdapter(MapActivity.this, resultSearches);
                        listView.setAdapter(adapter);
                    }
                }
                return true;
            }
        });
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;
    }

    private void searchViewHandlerOnSearch() {
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                prepareGetAllEntities();
                mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
                mVolleyService.getAllEntities();
                List<ResultSearch> lsFound = new ArrayList<>();
                if (!"All".equals(selectedTab)) {
                    for (ResultSearch item : resultSearches) {
                        if (item.getType().equals(selectedTab))
                            lsFound.add(item);
                    }
                    SearchAdapter adapter = new SearchAdapter(MapActivity.this, lsFound);
                    listView.setAdapter(adapter);
                } else {
                    SearchAdapter adapter = new SearchAdapter(MapActivity.this, resultSearches);
                    listView.setAdapter(adapter);
                }
                listView.setVisibility(View.VISIBLE);
                tabLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSearchViewClosed() {
                listView.setVisibility(View.INVISIBLE);
                tabLayout.setVisibility(View.INVISIBLE);

            }
        });
    }

    private void updateNotification() {
        Handler handler = new Handler();
        int delay = 7000;

        handler.postDelayed(new Runnable() {
            public void run() {
                prepareGetNotification();
                mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
                mVolleyService.getNotification(sharedPreferences.getLong(getString(R.string.prefs_id), 0), sharedPreferences.getString(getString(R.string.access_token), ""));
                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    private void prepareGetNotification() {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (!(boolean) response.get("error")) {
                        long notifnumber = 0;
                        JSONParser parser = new JSONParser();
                        JSONArray resNotification = (JSONArray) parser.parse(response.getJSONArray("notification").toString());
                        if (!resNotification.isEmpty()) {
                            notifnumber = resNotification.size();
                        }
                        if (sharedPreferences.contains("NOTIFICATIONSIZE")) {
                            if (sharedPreferences.getLong("NOTIFICATIONSIZE", 0) != notifnumber) {
                                Toast.makeText(getApplicationContext(), "Vous avez reçu une nouvelle notification", Toast.LENGTH_LONG).show();
                                sharedPreferences.edit().putLong("NOTIFICATIONSIZE", notifnumber).apply();
                            }
                        } else {
                            sharedPreferences.edit().putLong("NOTIFICATIONSIZE", notifnumber).apply();
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


    private void startTrackingLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, 1
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
            String durationLegs = result.routes[0].legs[0].duration.humanReadable;
            RelativeLayout rlTime = findViewById(R.id.rlTimeResult);
            rlTime.setVisibility(View.VISIBLE);
            TextView tvLegsTime = findViewById(R.id.tvLegsTime);
            tvLegsTime.setText(durationLegs);
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


    /**
     * Called when the user clicks a marker.
     */
    @Override
    public boolean onMarkerClick(final Marker marker) {

        if (marker.equals(home)) {
            return true;
        }
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
            case R.id.navigation_lexical:
                next = new Intent(getApplicationContext(), LexiconActivity.class);
                startActivity(next);
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
                    String facebookLink;
                    JSONObject objres;
                    String pathPicture;
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
                                facebookLink = objres.get("facebokLink").toString();
                                pathPicture = objres.get("pathPicture").toString();
                                CacheContainer.getInstance().getMarketHashMap().put(name, new Bar(id, name, latitude, longitutde, description, webSiteLink, facebookLink, "Bar",pathPicture));
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
                    String facebookLink;
                    JSONObject objres;
                    String pathPicture;
                    if (!(boolean) response.get("error")) {
                        JSONParser parser = new JSONParser();
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
                                facebookLink = objres.get("facebokLink").toString();
                                pathPicture = objres.get("pathPicture").toString();
                                CacheContainer.getInstance().getMarketHashMap().put(name, new Bar(id, name, latitude, longitutde, description, webSiteLink, facebookLink, "Brewery",pathPicture));
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
        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionsGranted) {
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener((@NonNull Task task) -> {
                            if (task.isSuccessful()) {
                                Location homeLocation = (Location) task.getResult();
                                if (homeLocation != null) {
                                    latLng = new LatLng(homeLocation.getLatitude(), homeLocation.getLongitude());
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
                                }
                            } else {
                                Toast.makeText(MapActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                            }
                        }
                );
            }
        } catch (SecurityException e) {
            String TAG = "MapActivity";
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

    private void prepareGetAllEntities() {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    resultSearches.clear();
                    ResultSearch resultSearch;
                    JSONObject objres;
                    if (!(boolean) response.get("error")) {
                        JSONParser parser = new JSONParser();
                        JSONArray resUsers = (JSONArray) parser.parse(response.get("users").toString());
                        if (!resUsers.isEmpty()) {
                            for (Object unres : resUsers) {
                                objres = (JSONObject) new JSONTokener(unres.toString()).nextValue();
                                resultSearch = new ResultSearch();
                                resultSearch.setName(objres.getString("name") + " " + objres.getString("surname"));
                                resultSearch.setType("User");
                                if (JSONObject.NULL != objres.getString("pathPicture"))
                                    resultSearch.setPath(objres.get("pathPicture").toString());
                                resultSearch.setPath(objres.get("pathPicture").toString());
                                resultSearch.setId(Long.parseLong(objres.getString("id")));
                                if (resultSearch.getId() != sharedPreferences.getLong(getString(R.string.prefs_id), 0))
                                    resultSearches.add(resultSearch);
                            }
                        }
                        JSONArray resBars = (JSONArray) parser.parse(response.get("bars").toString());
                        loadEntity(resBars, "Bar");
                        JSONArray resbrewerys = (JSONArray) parser.parse(response.get("brewerys").toString());
                        loadEntity(resbrewerys, "Brewery");
                        JSONArray resGroups = (JSONArray) parser.parse(response.get("groups").toString());
                        loadEntity(resGroups, "Group");
                    }
                } catch (Exception e) {
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

    private void loadEntity(JSONArray resEntity, String entity) throws JSONException {
        if (!resEntity.isEmpty()) {
            for (Object unres : resEntity) {
                fllResultSearches(unres, entity);
            }
        }
    }

    private void fllResultSearches(Object unres, String entity) throws JSONException {
        JSONObject objres;
        ResultSearch resultSearch;
        objres = (JSONObject) new JSONTokener(unres.toString()).nextValue();
        resultSearch = new ResultSearch();
        resultSearch.setName(objres.getString("name"));
        resultSearch.setType(entity);
        resultSearch.setId(Long.parseLong(objres.getString("id")));
        resultSearch.setPath(objres.get("pathPicture").toString());
        if (JSONObject.NULL != objres.getString("pathPicture"))
            resultSearch.setPath(objres.get("pathPicture").toString());
        resultSearches.add(resultSearch);
    }


}