package com.example.behere;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.behere.actor.Market;
import com.example.behere.utils.ApiUsage;
import com.example.behere.utils.VolleyCallback;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MarketProfilActivity extends AppCompatActivity  implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback {


     TextView tvNameBar;
     GoogleMap mMap;
     Marker marker;
     TextView contentDesc;
    private VolleyCallback mResultCallback = null;
    private ApiUsage mVolleyService;
    private static final String PREFS_ACCESS_TOKEN = "ACCESS_TOKEN";
    private static final String PREFS = "PREFS";
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_profile);

        tvNameBar = findViewById(R.id.tvNameBar);
        contentDesc = findViewById(R.id.tvDescription);

        Market market =  (Market) getIntent().getExtras().get("market");

        tvNameBar.setText(market.getName());
        contentDesc.setText(market.getDescription());
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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
        Market market =  (Market) getIntent().getExtras().get("market");
        LatLng latLng = new LatLng(market.getLatitude(), market.getLongitutde());
        marker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(market.getName())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.my_icon_bar)));
        marker.setTag(0);
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(latLng, 16.0f);
        mMap.animateCamera(yourLocation);

    }

    /** Called when the user clicks a marker. */
    @Override
    public boolean onMarkerClick(final Marker marker) {
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
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        Button btnSendComment = popupView.findViewById(R.id.btnSendComment);
        EditText tvComment =  popupView.findViewById(R.id.tvComment);
        btnSendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences = getBaseContext().getSharedPreferences(PREFS, MODE_PRIVATE);
                Market market =  (Market) getIntent().getExtras().get("market");
                mVolleyService = new ApiUsage(mResultCallback,getApplicationContext());
                mVolleyService.addCommentsToBar(tvComment.getText().toString(),(int) market.getId(), sharedPreferences.getString(PREFS_ACCESS_TOKEN,""));
                popupWindow.dismiss();
            }
        });
    }
}
