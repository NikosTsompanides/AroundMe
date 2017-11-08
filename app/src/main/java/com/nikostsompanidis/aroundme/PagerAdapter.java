package com.nikostsompanidis.aroundme;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Nikos Tsompanidis on 6/11/2017.
 */



public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    Bundle highlightsBundle,tipsBundle,photosBundle;

    public PagerAdapter(FragmentManager fm, int NumOfTabs,Bundle highlightsBundle,Bundle photosBundle,Bundle tipsBundle) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.highlightsBundle=highlightsBundle;
        this.tipsBundle=tipsBundle;
        this.photosBundle=photosBundle;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                HighlightsFragment tab1 = new HighlightsFragment();
                tab1.setArguments(highlightsBundle);
                return tab1;
            case 1:
                PhotosFragment tab2 = new PhotosFragment();
                tab2.setArguments(photosBundle);
                return tab2;
            case 2:
                TipsFragment tab3 = new TipsFragment();
                tab3.setArguments(tipsBundle);
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}