package com.example.thinkpad.showhelper.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.thinkpad.showhelper.R;
import com.example.thinkpad.showhelper.Adapters.ShowAdapter;
import com.example.thinkpad.showhelper.Show;
import com.example.thinkpad.showhelper.Loaders.ListLoader;

import java.util.ArrayList;
import java.util.List;


public class Search extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Show>> {

    private final static String API_KEY = "e0ff28973a330d2640142476f896da04";
    private static int SHOWS_LOADER_ID = 0;

    private ListView lvShows;
    private TextView tvEmpty;
    private SearchView svSearch;
    private Parcelable appStatus;

    private String query;
    private ShowAdapter adapter;
    private ArrayList<Show> arrShows = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setTitle(R.string.txt_search);

        svSearch = findViewById(R.id.search_sv_searchbar);

        svSearch.setIconifiedByDefault(false);
        svSearch.setSubmitButtonEnabled(true);
        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                loadSearchResults(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                return false;
            }
        });
    }


    @Override
    public void onPause() {
        super.onPause();
        if (adapter != null) {
            appStatus = lvShows.onSaveInstanceState();
        }
    }


    private void loadSearchResults(String query) {
        this.query = query;

        View barLoading = findViewById(R.id.search_probar_loading);
        lvShows = findViewById(R.id.search_lst_list);
        tvEmpty = findViewById(R.id.search_tv_empty);

        adapter = new ShowAdapter(this, arrShows);

        barLoading.setVisibility(View.VISIBLE);

        lvShows.setEmptyView(tvEmpty);
        lvShows.setAdapter(adapter);
        lvShows.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(Search.this, DetailsTMDb.class);
                Show show = arrShows.get(position);
                intent.putExtra("tmdb_id", String.valueOf(show.getTMDbID()));
                startActivity(intent);
            }
        });


        ConnectivityManager connectMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;

        if (connectMgr != null) {
            networkInfo = connectMgr.getActiveNetworkInfo();
        }

        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderMgr = getSupportLoaderManager();
            loaderMgr.initLoader(SHOWS_LOADER_ID++, null, this);
        } else {
            barLoading.setVisibility(View.GONE);

            tvEmpty.setText(R.string.txt_noConnection);
        }
    }


    @Override
    public Loader<List<Show>> onCreateLoader(int id, Bundle args) {
        svSearch.clearFocus();

        String tmdbURL = "https://api.themoviedb.org/3/search/movie";
        Uri url = Uri.parse(tmdbURL);
        Uri.Builder builder = url.buildUpon();

        builder.appendQueryParameter("api_key", API_KEY);
        builder.appendQueryParameter("query", query);

        return new ListLoader(this, builder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Show>> loader, List<Show> shows) {
        View barLoading = findViewById(R.id.search_probar_loading);
        barLoading.setVisibility(View.GONE);

        tvEmpty.setText(R.string.txt_noContents);
        adapter.clear();

        if (shows != null && !shows.isEmpty()) {
            adapter.addAll(shows);
        }

        if(appStatus != null) {
            lvShows.onRestoreInstanceState(appStatus);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Show>> loader) {
        adapter.clear();
    }
}
