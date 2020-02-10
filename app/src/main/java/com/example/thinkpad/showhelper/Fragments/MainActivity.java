package com.example.thinkpad.showhelper.Fragments;

import android.support.v4.view.ViewPager;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.thinkpad.showhelper.Adapters.TabsAdapter;
import com.example.thinkpad.showhelper.R;


public class MainActivity extends AppCompatActivity {

    TabsAdapter tabAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager pager = findViewById(R.id.main_viewpager);
        TabLayout tabs = findViewById(R.id.main_tabs);

        tabAdapter = new TabsAdapter(this, getSupportFragmentManager());
        pager.setAdapter(tabAdapter);

        tabs.setupWithViewPager(pager);
        tabs.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        tabs.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorPrimaryDark));
    }


    public void dataChanged() {
        tabAdapter.notifyDataSetChanged();
    }
}
