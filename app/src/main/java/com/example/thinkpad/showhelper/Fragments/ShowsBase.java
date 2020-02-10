package com.example.thinkpad.showhelper.Fragments;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.thinkpad.showhelper.Adapters.ShowCursorAdapter;
import com.example.thinkpad.showhelper.R;
import com.example.thinkpad.showhelper.DB.ShowInfo.ShowEntry;


public class ShowsBase extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private View viewBase;
    private TextView tvEmpty;

    private static int SHOW_LOADER = 0;

    private String filterOption = null;
    private String[] filterOptionArgs = null;
    private String sortingOption = null;

    private boolean isSearchFabVisible = false;

    ShowCursorAdapter cursorAdapter;


    public ShowsBase() {}


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        viewBase = inflater.inflate(R.layout.list, container, false);
        ListView lvShows = viewBase.findViewById(R.id.list_lst_list);
        tvEmpty = viewBase.findViewById(R.id.list_tv_empty);
        final SwipeRefreshLayout swipeRefresher = viewBase.findViewById(R.id.list_swiperefresh);
        FloatingActionButton searchFab = viewBase.findViewById(R.id.list_fab_search);

        cursorAdapter = new ShowCursorAdapter(getContext(), null);

        lvShows.setEmptyView(tvEmpty);
        lvShows.setAdapter(cursorAdapter);
        lvShows.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), Details.class);

                Uri showURL = ContentUris.withAppendedId(ShowEntry.CONTENT_URI, id);
                intent.setData(showURL);

                startActivity(intent);
            }
        });

        swipeRefresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
                swipeRefresher.setRefreshing(false);
            }
        });

        if (isSearchFabVisible) {
            searchFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), Search.class);
                    startActivity(intent);
                }
            });
        } else {
            searchFab.setVisibility(View.GONE);
        }

        getLoaderManager().initLoader(SHOW_LOADER, null, this);
        return viewBase;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] selectedCols = {
                ShowEntry._ID,
                ShowEntry.TMDB_ID,
                ShowEntry.COLUMN_SHOW_TITLE,
                ShowEntry.COLUMN_SHOW_AVERAGE_VOTE,
                ShowEntry.COLUMN_SHOW_RELEASE_DATE_IN_MILLISECONDS,
                ShowEntry.COLUMN_SHOW_THUMBNAIL_URL
        };

        return new CursorLoader(getContext(), ShowEntry.CONTENT_URI, selectedCols, filterOption, filterOptionArgs, sortingOption);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        View barLoading = viewBase.findViewById(R.id.list_probar_loading);

        barLoading.setVisibility(View.GONE);
        tvEmpty.setText(R.string.txt_noContents);
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }


    public void refreshList() {
        if (getActivity() != null) {
            ((MainActivity)getActivity()).dataChanged();
        }
    }


    public void setFilterOption(String filterOption) {
        this.filterOption = filterOption;
    }

    public void setFilterOptionArgs(String[] filterOptionArgs) {
        this.filterOptionArgs = filterOptionArgs;
    }


    public void setSortingOption(String sortingOption) {
        this.sortingOption = sortingOption;
    }


    public void setSearchFabVisibility(boolean visible) {
        isSearchFabVisible = visible;
    }
}
