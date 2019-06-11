package com.esgi.behere.fragment;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SectionsAdapterProfile extends FragmentPagerAdapter {

    public SectionsAdapterProfile(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new EditProfileFragment();
            case 1:
                return new WallProfileFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "EDIT";
            case 1:
                return "WALL";
        }
        return null;
    }
}
