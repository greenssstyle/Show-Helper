package com.example.thinkpad.showhelper.Fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.app.LoaderManager;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.thinkpad.showhelper.Show;
import com.example.thinkpad.showhelper.Loaders.ItemLoader;
import com.example.thinkpad.showhelper.R;
import com.example.thinkpad.showhelper.DB.ShowInfo.ShowEntry;
import com.squareup.picasso.Picasso;


public class DetailsTMDb extends AppCompatActivity implements LoaderManager.LoaderCallbacks{

    private final static String API_KEY = "e0ff28973a330d2640142476f896da04";
    private static final int EXISTING_SHOW_LOADER = 0;
    private static final int EXISTING_SHOW_LOADER_ID = 1;

    private ImageView imgPoster;
    private TextView tvTitle;
    private TextView tvRelease;
    private TextView tvVote;
    private TextView tvCount;
    private TextView tvOverview;
    private Button btnImdbLink;
    private TextView tvEmpty;

    private Show show;
    private String showID;
    private String tmdbID;
    private boolean inCollectionList;
    private boolean isWatched;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        setTitle("Details");

        imgPoster = findViewById(R.id.details_img_poster);
        tvTitle = findViewById(R.id.details_tv_title);
        tvVote = findViewById(R.id.details_tv_vote);
        tvCount = findViewById(R.id.details_tv_count);
        tvRelease = findViewById(R.id.details_tv_release);
        tvOverview = findViewById(R.id.details_tv_overview);
        btnImdbLink = findViewById(R.id.details_btn_imdbURL);
        tvEmpty = findViewById(R.id.details_tv_empty);

        Intent intent = getIntent();
        tmdbID = intent.getExtras().getString("tmdb_id");


        ConnectivityManager connectMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;

        if (connectMgr != null) {
            networkInfo = connectMgr.getActiveNetworkInfo();
        }

        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderMgr = getLoaderManager();
            loaderMgr.initLoader(EXISTING_SHOW_LOADER, null, this);
        } else {
            View barLoading = findViewById(R.id.details_probar_loading);
            barLoading.setVisibility(View.GONE);
            tvEmpty.setText(R.string.txt_noConnection);
        }

        getLoaderManager().initLoader(EXISTING_SHOW_LOADER, null, this);
    }


    @Override
    public Loader onCreateLoader(int id, Bundle bundle) {
        if (id == EXISTING_SHOW_LOADER) {
            String tmdbURL = "https://api.themoviedb.org/3/movie/" + tmdbID;

            Uri url = Uri.parse(tmdbURL);
            Uri.Builder builder = url.buildUpon();

            builder.appendQueryParameter("api_key", API_KEY);

            return new ItemLoader(this, builder.toString());
        } else if (id == EXISTING_SHOW_LOADER_ID){
            String[] selectedCols = {
                    ShowEntry._ID,
                    ShowEntry.COLUMN_SHOW_WATCHED
            };

            String filterOption = ShowEntry.TMDB_ID + "=?";
            String[] filterOptionArgs = {String.valueOf(show.getTMDbID())};

            return new CursorLoader(this, ShowEntry.CONTENT_URI,selectedCols,filterOption,filterOptionArgs,null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        if (loader.getId() == EXISTING_SHOW_LOADER) {
            this.show = (Show)data;

            if (show == null) { return; }

            getLoaderManager().initLoader(EXISTING_SHOW_LOADER_ID, null, this);

            View barLoading = findViewById(R.id.details_probar_loading);
            barLoading.setVisibility(View.GONE);

            String imageURL = show.getImageURL();
            String title = show.getTitle();
            String vote = String.valueOf(show.getVote());
            String count = show.getCount();
            String release = show.getDate();
            String overview = show.getOverview();
            final String imdbURL = show.getImdbURL();
            String cinemaReleaseDate = "Released at: " + release;

            Picasso.with(this).load(imageURL).into(imgPoster);
            tvTitle.setText(title);
            tvVote.setText(vote);
            tvCount.setText(count);
            tvRelease.setText(cinemaReleaseDate);
            tvOverview.setText(overview);
            btnImdbLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(imdbURL));
                    startActivity(intent);
                }
            });

            if (title != null && !title.isEmpty()) {
                tvEmpty.setVisibility(View.GONE);
            } else {
                tvEmpty.setText(R.string.txt_noData);
            }
        } else {
            Cursor cursor = (Cursor)data;
            if (cursor != null && cursor.getCount() >= 1 && cursor.moveToFirst()) {
                inCollectionList = true;

                int showIDColIndex = cursor.getColumnIndex(ShowEntry._ID);
                int watchedColIndex = cursor.getColumnIndex(ShowEntry.COLUMN_SHOW_WATCHED);
                showID = cursor.getString(showIDColIndex);
                isWatched = cursor.getInt(watchedColIndex) == 1;

                show.setWatched(isWatched);
            } else {
                inCollectionList = false;
                isWatched = false;
            }

            final ToggleButton btnAddRemove = findViewById(R.id.details_btn_addremove);
            btnAddRemove.setChecked(inCollectionList);
            btnAddRemove.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked) {
                        addShow(show);
                        inCollectionList = true;
                    } else {
                        removeShow(showID);
                        inCollectionList = false;
                    }
                }
            });

            final ToggleButton btnWatched = findViewById(R.id.details_btn_watch);
            btnWatched.setChecked(isWatched);
            btnWatched.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked) {
                        if (inCollectionList) {
                            setShowWatched(show, showID, 1);
                        } else {
                            show.setWatched(true);
                            btnAddRemove.toggle();
                        }
                    } else {
                        setShowWatched(show, showID, 0);
                    }
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader loader) { }


    private void addShow(Show show) {
        ContentValues values = new ContentValues();

        values.put(ShowEntry.TMDB_ID, show.getTMDbID());
        values.put(ShowEntry.COLUMN_SHOW_TITLE, show.getTitle());
        values.put(ShowEntry.COLUMN_SHOW_AVERAGE_VOTE, show.getVote());
        values.put(ShowEntry.COLUMN_SHOW_VOTE_COUNT, show.getCount());
        values.put(ShowEntry.COLUMN_SHOW_RELEASE_DATE_IN_MILLISECONDS, show.getDateInMillis());
        values.put(ShowEntry.COLUMN_SHOW_OVERVIEW, show.getOverview());
        values.put(ShowEntry.COLUMN_SHOW_IMDB_URL, show.getImdbURL());
        values.put(ShowEntry.COLUMN_SHOW_IMAGE_ID, show.getImageID());
        values.put(ShowEntry.COLUMN_SHOW_THUMBNAIL_URL, show.getThumbnailURL());
        values.put(ShowEntry.COLUMN_SHOW_IMAGE_URL, show.getImageURL());
        values.put(ShowEntry.COLUMN_SHOW_WATCHED, show.getWatchedIntValue());

        getContentResolver().insert(ShowEntry.CONTENT_URI, values);
    }

    private void removeShow(String showID) {
        getContentResolver().delete(Uri.withAppendedPath(ShowEntry.CONTENT_URI, showID), null, null);
    }


    private void setShowWatched(Show show, String showID, int isWatchedAsInt){
        ContentValues values = new ContentValues();

        values.put(ShowEntry.TMDB_ID, show.getTMDbID());
        values.put(ShowEntry.COLUMN_SHOW_TITLE, show.getTitle());
        values.put(ShowEntry.COLUMN_SHOW_AVERAGE_VOTE, show.getVote());
        values.put(ShowEntry.COLUMN_SHOW_VOTE_COUNT, show.getCount());
        values.put(ShowEntry.COLUMN_SHOW_RELEASE_DATE_IN_MILLISECONDS, show.getDateInMillis());
        values.put(ShowEntry.COLUMN_SHOW_OVERVIEW, show.getOverview());
        values.put(ShowEntry.COLUMN_SHOW_IMDB_URL, show.getImdbURL());
        values.put(ShowEntry.COLUMN_SHOW_IMAGE_ID, show.getImageID());
        values.put(ShowEntry.COLUMN_SHOW_THUMBNAIL_URL, show.getThumbnailURL());
        values.put(ShowEntry.COLUMN_SHOW_IMAGE_URL, show.getImageURL());
        values.put(ShowEntry.COLUMN_SHOW_WATCHED, isWatchedAsInt);

        getContentResolver().update(Uri.withAppendedPath(ShowEntry.CONTENT_URI, showID), values, null, null);
    }
}
