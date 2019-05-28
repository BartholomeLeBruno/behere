package com.example.behere;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.behere.actor.Market;
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

        // Retrieve the data from the marker.

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }
}
