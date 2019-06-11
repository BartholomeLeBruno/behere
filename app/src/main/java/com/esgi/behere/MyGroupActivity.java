package com.esgi.behere;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;


public class MyGroupActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private static final String PREFS = "PREFS";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_groups);
        sharedPreferences = getBaseContext().getSharedPreferences(PREFS, MODE_PRIVATE);
        BottomNavigationView navigationView = findViewById(R.id.footer);
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
                next = new Intent(MyGroupActivity.this, LoginActivity.class);
                sharedPreferences.edit().clear().apply();
                startActivity(next);
                return true;
            case R.id.navigation_myprofile:
                next = new Intent(MyGroupActivity.this, DefaultProfileActivity.class);
                startActivity(next);
                return true;
            case R.id.navigation_home:
                next = new Intent(MyGroupActivity.this, MapActivity.class);
                startActivity(next);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
