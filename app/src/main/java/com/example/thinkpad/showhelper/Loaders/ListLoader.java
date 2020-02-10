package com.example.thinkpad.showhelper.Loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.thinkpad.showhelper.Show;
import com.example.thinkpad.showhelper.DB.DataHelper;

import java.util.List;


public class ListLoader extends AsyncTaskLoader<List<Show>> {

    private String url;


    public ListLoader(Context context, String url) {
        super(context);
        this.url = url;
    }


    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Show> loadInBackground() {
        if (url == null) {
            return null;
        }

        return DataHelper.fetchShows(url);
    }
}
