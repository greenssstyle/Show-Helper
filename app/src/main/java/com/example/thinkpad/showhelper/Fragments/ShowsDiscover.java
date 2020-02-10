package com.example.thinkpad.showhelper.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.thinkpad.showhelper.R;
import com.example.thinkpad.showhelper.Adapters.ShowAdapter;
import com.example.thinkpad.showhelper.Show;
import com.example.thinkpad.showhelper.Loaders.ListLoader;
import com.example.thinkpad.showhelper.Loaders.RecommendsLoader;
import com.example.thinkpad.showhelper.Loaders.TMDbLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ShowsDiscover extends Fragment implements LoaderManager.LoaderCallbacks<List<Show>> {

    private final static String API_KEY = "e0ff28973a330d2640142476f896da04";
    private final static String TOP_RATED_URL = "https://api.themoviedb.org/3/movie/top_rated";
    private final static String POPULAR_URL = "https://api.themoviedb.org/3/discover/movie";
    private final static String RECOMMENDED_OPTION = "recommended_option";
    private int DATABASE_LOADER_ID = 0;
    private int HTTP_LOADER_ID = 1;

    private int currPage = 1;
    private int totalPages;
    private boolean isRecommended = false;

    private View viewBase;
    private TextView tvEmpty;
    private ListView lvShows;
    private Parcelable appStatus;

    private String tmdbURL;
    private ArrayList<Integer> tmdbIDs;

    private ShowAdapter adapter;
    private static ArrayList<Show> arrShows = new ArrayList<>();


    public ShowsDiscover() {
        setHasOptionsMenu(true);
    }

    private void initTmdbURL() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        tmdbURL = sharedPrefs.getString(
                getString(R.string.settings_tmdbURL),
                getString(R.string.settings_tmdbURLDefault)
        );

        if (Objects.equals(tmdbURL, "recommended_option")) {
            isRecommended = true;
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        initTmdbURL();

        viewBase = inflater.inflate(R.layout.list, container, false);
        lvShows = viewBase.findViewById(R.id.list_lst_list);
        tvEmpty = viewBase.findViewById(R.id.list_tv_empty);
        final SwipeRefreshLayout swipeRefresher = viewBase.findViewById(R.id.list_swiperefresh);
        FloatingActionButton searchFab = viewBase.findViewById(R.id.list_fab_search);

        adapter = new ShowAdapter(getActivity(), arrShows);

        lvShows.setEmptyView(tvEmpty);
        lvShows.setAdapter(adapter);
        lvShows.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DetailsTMDb.class);

                Show show = arrShows.get(position);
                intent.putExtra("tmdb_id", String.valueOf(show.getTMDbID()));

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

        searchFab.setVisibility(View.GONE);


        ConnectivityManager connectMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;

        if (connectMgr != null) {
            networkInfo = connectMgr.getActiveNetworkInfo();
        }

        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderMgr = getLoaderManager();
            if (isRecommended) {
                new TMDbLoader(this);
            } else {
                loaderMgr.initLoader(HTTP_LOADER_ID, null, this);
            }
        } else {
            View barLoading = viewBase.findViewById(R.id.list_probar_loading);
            barLoading.setVisibility(View.GONE);
            tvEmpty.setText(R.string.txt_noConnection);
        }

        if (!isRecommended) {
            lvShows.setOnScrollListener(new EndlessScrollListener(5, 1) {
                @Override
                public boolean onLoadMore(int page, int countTotal) {
                    if (page <= totalPages) {
                        View barLoading = viewBase.findViewById(R.id.list_probar_loading);

                        barLoading.setVisibility(View.VISIBLE);

                        ConnectivityManager connectMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo networkInfo = null;
                        if (connectMgr != null) {
                            networkInfo = connectMgr.getActiveNetworkInfo();
                        }

                        if (networkInfo != null && networkInfo.isConnected()) {
                            LoaderManager loaderMgr = getLoaderManager();

                            loaderMgr.initLoader(++HTTP_LOADER_ID, null, ShowsDiscover.this);
                            currPage++;

                            return true;
                        } else {
                            barLoading = viewBase.findViewById(R.id.list_probar_loading);

                            barLoading.setVisibility(View.GONE);
                            tvEmpty.setText(R.string.txt_noConnection);

                            return false;
                        }
                    } else {
                        return false;
                    }
                }
            });
        }

        return viewBase;
    }


    @Override
    public void onPause() {
        super.onPause();
        appStatus = lvShows.onSaveInstanceState();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(appStatus != null) {
            lvShows.onRestoreInstanceState(appStatus);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public Loader<List<Show>> onCreateLoader(int id, Bundle args) {
        if (isRecommended) {
            return new RecommendsLoader(getActivity(), tmdbIDs);
        } else {
            String page = String.valueOf(currPage);

            Uri url = Uri.parse(tmdbURL);
            Uri.Builder builder = url.buildUpon();

            builder.appendQueryParameter("api_key", API_KEY);
            builder.appendQueryParameter("page", page);

            return new ListLoader(getActivity(), builder.toString());
        }
    }

    @Override
    public void onLoadFinished(Loader<List<Show>> loader, List<Show> shows) {
        View barLoading = viewBase.findViewById(R.id.list_probar_loading);
        barLoading.setVisibility(View.GONE);

        if (!isRecommended) {
            if (currPage == 1) {
                totalPages = shows.get(0).getTotalPages();
            }
        }

        if (HTTP_LOADER_ID == loader.getId() && (loader.getId()-1) * 20 == adapter.getCount()) {
            tvEmpty.setText(R.string.txt_noRecommends);

            if (shows != null && !shows.isEmpty()) {
                adapter.addAll(shows);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Show>> loader) {
        adapter.clear();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        menu.clear();
        inflater.inflate(R.menu.menu_discover,menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        switch (tmdbURL) {
            case POPULAR_URL:
                MenuItem itemPopular = menu.findItem(R.id.menu_show_popular);
                itemPopular.setEnabled(false);
                break;

            case TOP_RATED_URL:
                MenuItem itemTopRated = menu.findItem(R.id.menu_show_toprated);
                itemTopRated.setEnabled(false);
                break;

            case RECOMMENDED_OPTION:
                MenuItem itemRecommend = menu.findItem(R.id.menu_show_recommended);
                itemRecommend.setEnabled(false);
                break;


            default:
                Log.e("ShowsCollection", "Filtering error");
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_show_popular:
                saveTMDbURL(POPULAR_URL);
                break;

            case R.id.menu_show_toprated:
                saveTMDbURL(TOP_RATED_URL);
                break;

            case R.id.menu_show_recommended:
                saveTMDbURL(RECOMMENDED_OPTION);
                break;


            default:
                return super.onOptionsItemSelected(item);
        }

        refreshList();
        return true;
    }


    private void saveTMDbURL(String url) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPrefs.edit();

        editor.putString(getString(R.string.settings_tmdbURL), url);
        editor.apply();
    }


    public void startRecommendsLoader(ArrayList<Integer> tmdbIDs) {
        this.tmdbIDs = tmdbIDs;
        getLoaderManager().initLoader(HTTP_LOADER_ID, null, this);
    }


    private void refreshList() {
        if (getActivity() != null) {
            ((MainActivity)getActivity()).dataChanged();
        }
    }
}



