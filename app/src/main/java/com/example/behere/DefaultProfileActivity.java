package com.example.behere;

import android.os.Bundle;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import com.example.behere.fragment.SectionsAdapterProfile;


public class DefaultProfileActivity extends AppCompatActivity {

     TabLayout tabLayout;
     TabItem edit;
     TabItem wall;
     SectionsAdapterProfile mSectionsPagerAdapter;
     ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_profile);

        edit = findViewById(R.id.tabEdit);
        wall = findViewById(R.id.tabWall);

        mSectionsPagerAdapter = new SectionsAdapterProfile(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager =  findViewById(R.id.activity_main_viewpager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout =  findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(mViewPager);

    }



}
