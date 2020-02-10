package com.example.thinkpad.showhelper.Loaders;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.example.thinkpad.showhelper.DB.ShowInfo;
import com.example.thinkpad.showhelper.Fragments.ShowsDiscover;

import java.util.ArrayList;


public class TMDbLoader implements LoaderManager.LoaderCallbacks<Cursor> {

    private ShowsDiscover fragDiscover;

    
    public TMDbLoader(ShowsDiscover fragDiscover) {
        this.fragDiscover = fragDiscover;
        fragDiscover.getLoaderManager().initLoader(0, null, this);
    }

    
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] selectedCols = {
                ShowInfo.ShowEntry._ID,
                ShowInfo.ShowEntry.TMDB_ID
        };

        if (fragDiscover.getContext() != null) {
            return new CursorLoader(fragDiscover.getContext(), ShowInfo.ShowEntry.CONTENT_URI, selectedCols,null,null,null);
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        ArrayList<Integer> tmdbIDs = new ArrayList<>();
        
        while (cursor.moveToNext()) {
            int tmdbIDColIndex = cursor.getColumnIndex(ShowInfo.ShowEntry.TMDB_ID);
            int tmdbID = cursor.getInt(tmdbIDColIndex);
            tmdbIDs.add(tmdbID);
        }
        
        fragDiscover.startRecommendsLoader(tmdbIDs);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }
}
