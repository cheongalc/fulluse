package com.fulluse;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class MainPagerAdapter extends FragmentStatePagerAdapter {
    int numberOfTabs;

    public MainPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.numberOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new MainFragment();
            case 1:
                return new EventFragment();
            case 2:
                return new DashboardFragment();
            default:
                return null;
        }
    }


    @Override
    public int getCount() {
        return numberOfTabs;
    }
}
