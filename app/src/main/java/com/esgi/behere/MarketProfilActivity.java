package com.esgi.behere;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.esgi.behere.actor.Market;
import com.esgi.behere.utils.ApiUsage;
import com.esgi.behere.utils.InformationMessage;
import com.esgi.behere.utils.VolleyCallback;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Objects;


public class MarketProfilActivity extends AppCompatActivity  implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback {


    private TextView tvNameBar;
    private  GoogleMap mMap;
    private  Marker marker;
    private  TextView contentDesc;
    private VolleyCallback mResultCallback = null;
    private ApiUsage mVolleyService;
    private final String PREFS_ACCESS_TOKEN = "ACCESS_TOKEN";
    private final String PREFS = "PREFS";
    private final String PREFS_LONGITUDE = "LONGITUDE";
    private final String PREFS_LATITUDE = "LATITUDE";

    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_profile);

        tvNameBar = findViewById(R.id.tvNameBar);
        contentDesc = findViewById(R.id.tvDescription);
        sharedPreferences = getBaseContext().getSharedPreferences(PREFS, MODE_PRIVATE);
        Market market =  (Market) Objects.requireNonNull(getIntent().getExtras()).get("market");
        if(market != null) {
            tvNameBar.setText(market.getName());
            contentDesc.setText(market.getDescription());
        }
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        BottomNavigationView navigationView = findViewById(R.id.footerpub);
        navigationView.setOnNavigationItemReselectedListener(this::onOptionsItemSelected);

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
        Market market =  (Market) Objects.requireNonNull(getIntent().getExtras()).get("market");
        assert market != null;
        LatLng latLng = new LatLng(market.getLatitude(), market.getLongitutde());
        marker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(market.getName())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.my_icon_bar)));
        marker.setTag(0);
        mMap.setOnMarkerClickListener(this);
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(latLng, 16.0f);
        mMap.animateCamera(yourLocation);

    }

    /** Called when the user clicks a marker. */
    @Override
    public boolean onMarkerClick(final Marker marker) {
        Integer clickCount = (Integer) marker.getTag();

        // Check if a click count was set, then display the click count.
        if (clickCount != null) {
            Intent destination = new Intent(getApplicationContext(), MapActivity.class);
            sharedPreferences.edit().putString(PREFS_LATITUDE, marker.getPosition().latitude + "").apply();
            sharedPreferences.edit().putString(PREFS_LONGITUDE, marker.getPosition().longitude + "").apply();
            startActivity(destination);
        }
        return false;
    }


    public void onButtonShowPopupWindowClick(View view) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.activity_comment, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        Button btnSendComment = popupView.findViewById(R.id.btnSendComment);
        EditText tvComment =  popupView.findViewById(R.id.tvComment);
        btnSendComment.setOnClickListener((View v) -> {
                prepareAddCommentBar();
                sharedPreferences = getBaseContext().getSharedPreferences(PREFS, MODE_PRIVATE);
                Market market =  (Market) Objects.requireNonNull(getIntent().getExtras()).get("market");
                mVolleyService = new ApiUsage(mResultCallback,getApplicationContext());
                mVolleyService.addCommentsToBar(tvComment.getText().toString(),(int) market.getId(), sharedPreferences.getString(PREFS_ACCESS_TOKEN,""));
                InformationMessage.createToastInformation(MarketProfilActivity.this, getLayoutInflater(), getApplicationContext() ,R.drawable.ic_insert_emoticon_blue_24dp,
                        "We love you my love");
                popupWindow.dismiss();
        });
    }
    private void prepareAddCommentBar(){
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    Log.d("response", response.toString());
                    if ((boolean) response.get("error")) {
                        Toast.makeText(getApplicationContext(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }

            }
            @Override
            public void onError(VolleyError error) { }
        };
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
    }

    @Override
    public void onResume()
    {
        super.onResume();
        prepareAuthentification();
        mVolleyService = new ApiUsage(mResultCallback,getApplicationContext());
        mVolleyService.authentificate(sharedPreferences.getString("USERNAME", ""), sharedPreferences.getString("PASSWORD",""));

    }

    private void prepareAuthentification(){
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (!(boolean) response.get("error")) {
                        JSONObject objres = (JSONObject) new JSONTokener(response.get("user").toString()).nextValue();
                        sharedPreferences.edit().putString("ACESS_TOKEN",objres.getString("token")).apply();
                    }
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            }
            @Override
            public void onError(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Erreur lors de l'authentification", Toast.LENGTH_SHORT).show();
            }
        };
    }
}
