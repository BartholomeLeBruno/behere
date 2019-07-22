package com.esgi.behere;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.error.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.ui.NetworkImageView;
import com.esgi.behere.actor.Market;
import com.esgi.behere.tools.StarTools;
import com.esgi.behere.utils.ApiUsage;
import com.esgi.behere.utils.CacheContainer;
import com.esgi.behere.utils.InformationMessage;
import com.esgi.behere.utils.PopupAchievement;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;


public class MarketProfilActivity extends AppCompatActivity implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback {


    private VolleyCallback mResultCallback = null;
    private ApiUsage mVolleyService;
    private SharedPreferences sharedPreferences;
    private LinearLayout linearLayoutStar;
    private Market market;
    private NetworkImageView imageView;
    private ImageView ivNotification;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_profile);

        Button btnSeeComment = findViewById(R.id.btnSeeComment);
        Button btnReservation = findViewById(R.id.btnCreateReservation);
        linearLayoutStar = findViewById(R.id.linearLayoutStar);
        TextView tvNameBar = findViewById(R.id.tvNameBar);
        ivNotification = findViewById(R.id.ivNotification);
        TextView contentDesc = findViewById(R.id.tvDescription);
        sharedPreferences = getBaseContext().getSharedPreferences(getString(R.string.prefs), MODE_PRIVATE);
        Button btnWebsite = findViewById(R.id.btnWebsite);
        Button btnFacebook = findViewById(R.id.btnFacebook);
        imageView = findViewById(R.id.ivMarket);
        market = (Market) Objects.requireNonNull(getIntent().getExtras()).get("market");
        linearLayoutStar.removeAllViews();
        prepareStar();
        mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
        if (market.getType().equals("Bar")) {
            mVolleyService.getNotesBar(market.getId());
            btnReservation.setVisibility(View.VISIBLE);
        }
        if (market.getType().equals("Brewery")) {
            mVolleyService.getNotesBrewery(market.getId());
            btnReservation.setVisibility(View.INVISIBLE);
        }
        btnSeeComment.setOnClickListener(v -> {
            Intent comments = new Intent(getApplicationContext(), CommentaryListActivity.class);
            comments.putExtra("entityID", market.getId());
            comments.putExtra("entityType", market.getType());
            startActivity(comments);
        });
        if (market.getPathPicture() != null)
            imageView.setImageUrl(market.getPathPicture(), new ImageLoader(CacheContainer.getQueue()));
        else
            imageView.setBackground(getDrawable(R.drawable.default_image));

        if (market != null) {
            tvNameBar.setText(market.getName());
            contentDesc.setText(market.getDescription());
            if (contentDesc.getText().toString().isEmpty())
                contentDesc.setVisibility(View.INVISIBLE);
            if (!market.getFacebookLink().equals("null"))
                btnFacebook.setOnClickListener(v -> getIntoTheSite(market.getFacebookLink()));
            else
                btnFacebook.setVisibility(View.GONE);
            if (!market.getWebSiteLink().equals("null")) {
                btnWebsite.setOnClickListener(v -> {
                    if (!market.getWebSiteLink().isEmpty()) {
                        getIntoTheSite(market.getWebSiteLink());
                    }
                });
            } else btnWebsite.setVisibility(View.INVISIBLE);
        }
        prepareGetSubscriptionMarket();
        mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
        mVolleyService.getUser(sharedPreferences.getLong(getString(R.string.prefs_id), 0));
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        BottomNavigationView navigationView = findViewById(R.id.footerpub);
        navigationView.setOnNavigationItemSelectedListener(this::onOptionsItemSelected);
        btnReservation.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ReservationActivity.class);
            intent.putExtra("market", market);
            startActivity(intent);
        });
    }

    private void getIntoTheSite(String webSiteLink) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(webSiteLink));
        startActivity(intent);
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
        Market market = (Market) Objects.requireNonNull(getIntent().getExtras()).get("market");
        assert market != null;
        LatLng latLng = new LatLng(market.getLatitude(), market.getLongitutde());
        Marker marker = googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(market.getName())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.my_icon_bar)));
        marker.setTag(0);
        googleMap.setOnMarkerClickListener(this);
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(latLng, 16.0f);
        googleMap.animateCamera(yourLocation);

    }

    /**
     * Called when the user clicks a marker.
     */
    @Override
    public boolean onMarkerClick(final Marker marker) {
        Integer clickCount = (Integer) marker.getTag();

        // Check if a click count was set, then display the click count.
        if (clickCount != null) {
            Intent destination = new Intent(getApplicationContext(), MapActivity.class);
            sharedPreferences.edit().putString(getString(R.string.latitude), marker.getPosition().latitude + "").apply();
            sharedPreferences.edit().putString(getString(R.string.longitude), marker.getPosition().longitude + "").apply();
            startActivity(destination);
            InformationMessage.createToastInformation(MarketProfilActivity.this, getLayoutInflater(), getApplicationContext(),
                    R.drawable.my_icon_bar, "Follow the line for the beer !");
        }
        return false;
    }


    public void onButtonShowPopupWindowClick(View view) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.activity_comment_star, null);
        AtomicLong note = new AtomicLong();
        note.set(0);
        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);
        prepareStar(popupView, note);
        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window token
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        Button btnSendComment = popupView.findViewById(R.id.btnSendComment);
        EditText tvComment = popupView.findViewById(R.id.tvComment);
        tvComment.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) tvComment.setHint("");
            else tvComment.setHint("Your Comments...");
        });
        sharedPreferences = getBaseContext().getSharedPreferences(getString(R.string.prefs), MODE_PRIVATE);
        btnSendComment.setOnClickListener((View v) -> {
            Market market = (Market) Objects.requireNonNull(getIntent().getExtras()).get("market");
            prepareAddComment();
            if (Objects.requireNonNull(market).getType().equals("Bar")) {
                mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
                mVolleyService.addCommentsToBar(tvComment.getText().toString(), (int) market.getId(), sharedPreferences.getString(getString(R.string.access_token), ""));
                prepareEmpty();
                mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
                mVolleyService.addNoteToBar(note.get(), market.getId(), sharedPreferences.getString(getString(R.string.access_token), ""));
                popupWindow.dismiss();
            } else {
                mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
                mVolleyService.addCommentsToBrewery(tvComment.getText().toString(), (int) market.getId(), sharedPreferences.getString(getString(R.string.access_token), ""));
                prepareEmpty();
                mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
                mVolleyService.addNoteToBrewery(note.get(), market.getId(), sharedPreferences.getString(getString(R.string.access_token), ""));
                popupWindow.dismiss();
            }
        });
    }

    private void prepareStar(View popupView, AtomicLong note) {
        ImageView firstStar = popupView.findViewById(R.id.firstStar);
        ImageView secondStar = popupView.findViewById(R.id.secondStar);
        ImageView thirdStar = popupView.findViewById(R.id.thirdStar);
        ImageView fourthStar = popupView.findViewById(R.id.fourthStar);
        ImageView fifthStar = popupView.findViewById(R.id.fifthStar);
        firstStar.setOnClickListener(first -> {
            firstStar.setImageResource(R.drawable.ic_star_yellow_24dp);
            secondStar.setImageResource(R.drawable.ic_star_border_yellow_24dp);
            thirdStar.setImageResource(R.drawable.ic_star_border_yellow_24dp);
            fourthStar.setImageResource(R.drawable.ic_star_border_yellow_24dp);
            fifthStar.setImageResource(R.drawable.ic_star_border_yellow_24dp);
            note.set(1);
        });
        secondStar.setOnClickListener(second -> {
            firstStar.setImageResource(R.drawable.ic_star_yellow_24dp);
            secondStar.setImageResource(R.drawable.ic_star_yellow_24dp);
            thirdStar.setImageResource(R.drawable.ic_star_border_yellow_24dp);
            fourthStar.setImageResource(R.drawable.ic_star_border_yellow_24dp);
            fifthStar.setImageResource(R.drawable.ic_star_border_yellow_24dp);
            note.set(2);
        });
        thirdStar.setOnClickListener(third -> {
            firstStar.setImageResource(R.drawable.ic_star_yellow_24dp);
            secondStar.setImageResource(R.drawable.ic_star_yellow_24dp);
            thirdStar.setImageResource(R.drawable.ic_star_yellow_24dp);
            fourthStar.setImageResource(R.drawable.ic_star_border_yellow_24dp);
            fifthStar.setImageResource(R.drawable.ic_star_border_yellow_24dp);
            note.set(3);
        });
        fourthStar.setOnClickListener(fourth -> {
            firstStar.setImageResource(R.drawable.ic_star_yellow_24dp);
            secondStar.setImageResource(R.drawable.ic_star_yellow_24dp);
            thirdStar.setImageResource(R.drawable.ic_star_yellow_24dp);
            fourthStar.setImageResource(R.drawable.ic_star_yellow_24dp);
            fifthStar.setImageResource(R.drawable.ic_star_border_yellow_24dp);
            note.set(4);
        });
        fifthStar.setOnClickListener(fifth -> {
            firstStar.setImageResource(R.drawable.ic_star_yellow_24dp);
            secondStar.setImageResource(R.drawable.ic_star_yellow_24dp);
            thirdStar.setImageResource(R.drawable.ic_star_yellow_24dp);
            fourthStar.setImageResource(R.drawable.ic_star_yellow_24dp);
            fifthStar.setImageResource(R.drawable.ic_star_yellow_24dp);
            note.set(5);
        });
    }

    private void prepareAddComment() {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    InformationMessage.createToastInformation(MarketProfilActivity.this, getLayoutInflater(), getApplicationContext(), R.drawable.ic_insert_emoticon_blue_24dp,
                            "We love you my love");
                    Intent n = new Intent(getApplicationContext(),MarketProfilActivity.class);
                    n.putExtra("marker", market);
                    startActivity(n);
                    finish();
                    if ((boolean) response.get("error")) {
                        Toast.makeText(getApplicationContext(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
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
            case R.id.navigation_lexical:
                next = new Intent(getApplicationContext(), LexiconActivity.class);
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

    private void prepareEmpty() {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
            }

            @Override
            public void onError(VolleyError error) {
                if (error.networkResponse.statusCode == 500) {
                    new PopupAchievement().popupAuthentification(getWindow().getDecorView().getRootView());
                }
            }
        };
    }

    private void prepareStar() {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (!(boolean) response.get("error")) {
                        double note = 0;
                        JSONParser parser = new JSONParser();
                        JSONArray resNote;
                        if (market.getType().equals("Bar"))
                            resNote = (JSONArray) parser.parse(response.get("notesBar").toString());
                        else
                            resNote = (JSONArray) parser.parse(response.get("notesBrewery").toString());
                        JSONObject objres;
                        if (!resNote.isEmpty()) {
                            for (Object unres : resNote) {
                                objres = (JSONObject) new JSONTokener(unres.toString()).nextValue();
                                note = note + objres.getDouble("note");
                            }
                            note = note / resNote.size();
                        }
                        StarTools starTools = new StarTools(note, getApplicationContext(), linearLayoutStar);
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

    private void prepareGetSubscriptionMarket() {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (!(boolean) response.get("error")) {
                        JSONParser parser = new JSONParser();
                        boolean verif = false;
                        JSONObject objres = (JSONObject) new JSONTokener(response.get("user").toString()).nextValue();
                        JSONArray barTab = (JSONArray) parser.parse(objres.get("bar").toString());
                        JSONArray breweryTab = (JSONArray) parser.parse(objres.get("brewery").toString());
                        verif = marketBelongToUser(verif, barTab);
                        verif = marketBelongToUser(verif, breweryTab);
                        if (verif) {
                            ivNotification.setImageDrawable(getDrawable(R.drawable.ic_notifications_blue_24dp));
                            ivNotification.setOnClickListener(v -> {
                                prepareUnSuscribe();
                                mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
                                if (market.getType().equals("Bar"))
                                    mVolleyService.unSuscribeBar(sharedPreferences.getLong(getString(R.string.prefs_id), 0), market.getId(), sharedPreferences.getString(getString(R.string.access_token), ""));
                                else
                                    mVolleyService.unSuscribeBrewery(sharedPreferences.getLong(getString(R.string.prefs_id), 0), market.getId(), sharedPreferences.getString(getString(R.string.access_token), ""));
                            });
                        }
                        else{
                            ivNotification.setImageDrawable(getDrawable(R.drawable.ic_notifications_grey_24dp));
                            ivNotification.setOnClickListener(v -> {
                                prepareSuscribe();
                                mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
                                if (market.getType().equals("Bar"))
                                    mVolleyService.suscribeBar(sharedPreferences.getLong(getString(R.string.prefs_id), 0), market.getId(), sharedPreferences.getString(getString(R.string.access_token), ""));
                                else
                                    mVolleyService.suscribeBrewery(sharedPreferences.getLong(getString(R.string.prefs_id), 0), market.getId(), sharedPreferences.getString(getString(R.string.access_token), ""));
                            });
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

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void prepareUnSuscribe() {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                ivNotification.setImageDrawable(getDrawable(R.drawable.ic_notifications_grey_24dp));
                ivNotification.setOnClickListener(v -> {
                    prepareSuscribe();
                    mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
                    if (market.getType().equals("Bar"))
                        mVolleyService.suscribeBar(sharedPreferences.getLong(getString(R.string.prefs_id), 0), market.getId(), sharedPreferences.getString(getString(R.string.access_token), ""));
                    else
                        mVolleyService.suscribeBrewery(sharedPreferences.getLong(getString(R.string.prefs_id), 0), market.getId(), sharedPreferences.getString(getString(R.string.access_token), ""));
                });
            }

            @Override
            public void onError(VolleyError error) {
                if (error.networkResponse.statusCode == 500) {
                    new PopupAchievement().popupAuthentification(getWindow().getDecorView().getRootView());
                }
            }
        };
    }

    private void prepareSuscribe() {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                ivNotification.setImageDrawable(getDrawable(R.drawable.ic_notifications_blue_24dp));
                ivNotification.setOnClickListener(v -> {
                    prepareUnSuscribe();
                    mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
                    if (market.getType().equals("Bar"))
                        mVolleyService.unSuscribeBar(sharedPreferences.getLong(getString(R.string.prefs_id), 0), market.getId(), sharedPreferences.getString(getString(R.string.access_token), ""));
                    else
                        mVolleyService.unSuscribeBrewery(sharedPreferences.getLong(getString(R.string.prefs_id), 0), market.getId(), sharedPreferences.getString(getString(R.string.access_token), ""));
                });
            }

            @Override
            public void onError(VolleyError error) {
                if (error.networkResponse.statusCode == 500) {
                    new PopupAchievement().popupAuthentification(getWindow().getDecorView().getRootView());
                }
            }
        };
    }

    private boolean marketBelongToUser(boolean verif, JSONArray tab) throws JSONException {
        if (!tab.isEmpty()) {
            JSONObject objTab;
            for (Object oneMarket : tab) {
                objTab = (JSONObject) new JSONTokener(oneMarket.toString()).nextValue();
                if (objTab.getLong("id") == market.getId()) {
                    verif = true;
                }
            }
        }
        return verif;
    }

}
