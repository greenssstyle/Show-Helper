package com.example.thinkpad.showhelper.Loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.thinkpad.showhelper.Show;
import com.example.thinkpad.showhelper.DB.DataHelper;

import java.util.ArrayList;
import java.util.List;


public class RecommendsLoader extends AsyncTaskLoader<List<Show>> {

    private ArrayList<Integer> tmdbIDs;


    public RecommendsLoader(Context context, ArrayList<Integer> tmdbIDs) {
        super(context);
        this.tmdbIDs = tmdbIDs;
    }


    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Show> loadInBackground() {
        if (tmdbIDs == null) {
            return null;
        }

        return DataHelper.fetchRecommends(tmdbIDs);
    }
}
