package com.example.thinkpad.showhelper.Loaders;

import android.content.Context;
import android.support.annotation.Nullable;
import android.content.AsyncTaskLoader;

import com.example.thinkpad.showhelper.Show;
import com.example.thinkpad.showhelper.DB.DataHelper;


public class ItemLoader extends AsyncTaskLoader<Show> {

    private String url;


    public ItemLoader(Context context, String url) {
        super(context);
        this.url = url;
    }


    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public Show loadInBackground() {
        if (url == null) {
            return null;
        }

        return DataHelper.fetchShow(url);
    }
}
