package com.example.thinkpad.showhelper.Adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.thinkpad.showhelper.R;
import com.example.thinkpad.showhelper.Fragments.ShowsCollection;
import com.example.thinkpad.showhelper.Fragments.ShowsDiscover;
import com.example.thinkpad.showhelper.Fragments.ShowsToWatch;


public class TabsAdapter extends FragmentStatePagerAdapter {

    private Context context;

    
    public TabsAdapter(Context context, FragmentManager fragMgr) {
        super(fragMgr);
        this.context = context;
    }


    @Override
    public int getCount() {
        return 3;
    }


    @Override
    public Fragment getItem(int pos) {
        if (pos == 0) {
            return new ShowsToWatch();
        } else if (pos == 1) {
            return new ShowsCollection();
        } else {
            return new ShowsDiscover();
        }
    }

    public int getItemPosition(Object item) {
        return POSITION_NONE;
    }


    @Override
    public CharSequence getPageTitle(int pos) {
        if (pos == 0) {
            return context.getString(R.string.tab_toWatch);
        } else if (pos == 1) {
            return context.getString(R.string.tab_all);
        } else {
            return context.getString(R.string.tab_discover);
        }
    }
}