package com.esgi.behere;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.esgi.behere.fragment.SectionsAdapterProfile;


public class DefaultProfileActivity extends AppCompatActivity {

    TabLayout tabLayout;
    TabItem edit;
    TabItem wall;
    SectionsAdapterProfile mSectionsPagerAdapter;
    ViewPager mViewPager;
    private SharedPreferences sharedPreferences;
    private static final String PREFS = "PREFS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_profile);

        edit = findViewById(R.id.tabInfo);
        wall = findViewById(R.id.tabWall);
        sharedPreferences = getBaseContext().getSharedPreferences(PREFS, MODE_PRIVATE);

        mSectionsPagerAdapter = new SectionsAdapterProfile(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager =  findViewById(R.id.activity_main_viewpager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout =  findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(mViewPager);
        BottomNavigationView navigationView = findViewById(R.id.footer);
        navigationView.getMenu().getItem(1).setChecked(true);
        navigationView.setOnNavigationItemReselectedListener(
                new BottomNavigationView.OnNavigationItemReselectedListener() {
                    @Override
                    public void onNavigationItemReselected(@NonNull MenuItem menuItem) {
                        onOptionsItemSelected(menuItem);
                    }
                }
        );

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
            case R.id.navigation_mygroups:
                next = new Intent(getApplicationContext(), MyGroupActivity.class);
                next.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(next);
                finish();
                return true;
            case R.id.navigation_home:
                next = new Intent(getApplicationContext(), MapActivity.class);
                next.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(next);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent next;
        next = new Intent(getApplicationContext(), MapActivity.class);
        startActivity(next);
        finish();
    }


}
