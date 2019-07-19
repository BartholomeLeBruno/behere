package com.esgi.behere;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class FooterActivity extends AppCompatActivity {
    private TextView mTextMessage;

    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_myprofile:
                    mTextMessage.setText(getString(R.string.myProfile));
                    return true;
                case R.id.navigation_home:
                    mTextMessage.setText(getString(R.string.home));
                    return true;
                case R.id.disconnected:
                    mTextMessage.setText(getString(R.string.disconnect));
                    return true;
                case R.id.navigation_mygroups:
                    mTextMessage.setText(getString(R.string.mygroups));
                    return true;
                case R.id.navigation_lexical:
                    mTextMessage.setText(getString(R.string.lexical));
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_footer);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        mTextMessage = findViewById(R.id.message);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
