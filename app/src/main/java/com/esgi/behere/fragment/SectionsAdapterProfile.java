package com.esgi.behere.fragment;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

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
            case 2:
                return new NotificationFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "EDIT";
            case 1:
                return "WALL";
            case 2:
                return "NOTIFICATION";
        }
        return null;
    }
}
