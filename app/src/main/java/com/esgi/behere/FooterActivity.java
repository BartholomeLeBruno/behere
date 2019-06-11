package com.esgi.behere;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.widget.TextView;

public class FooterActivity extends AppCompatActivity {
    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_myprofile:
                    mTextMessage.setText("My Profile");
                    return true;
                case R.id.navigation_home:
                    mTextMessage.setText("HOME");
                    return true;
                case R.id.disconnected:
                    mTextMessage.setText("Disconnect");
                    return true;
                case R.id.navigation_mygroups:
                    mTextMessage.setText("My Groups");
                    return true;
                case R.id.navigation_lexical:
                    mTextMessage.setText("Lexical");
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
        navView.getMenu().getItem(0).setCheckable(false);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
