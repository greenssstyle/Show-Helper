package com.example.thinkpad.showhelper.Fragments;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.thinkpad.showhelper.Show;
import com.example.thinkpad.showhelper.R;
import com.example.thinkpad.showhelper.DB.ShowInfo.ShowEntry;
import com.squareup.picasso.Picasso;


public class Details extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_SHOW_LOADER = 0;
    private Uri showURL;

    private ImageView imgPoster;
    private TextView tvTitle;
    private TextView tvVote;
    private TextView tvCount;
    private TextView tvRelease;
    private TextView tvOverview;
    private Button btnImdbLink;
    private TextView tvEmpty;

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
        showURL = intent.getData();

        inCollectionList = true;
        getLoaderManager().initLoader(EXISTING_SHOW_LOADER, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] selectedCols = {
                ShowEntry._ID,
                ShowEntry.TMDB_ID,
                ShowEntry.COLUMN_SHOW_TITLE,
                ShowEntry.COLUMN_SHOW_AVERAGE_VOTE,
                ShowEntry.COLUMN_SHOW_VOTE_COUNT,
                ShowEntry.COLUMN_SHOW_RELEASE_DATE_IN_MILLISECONDS,
                ShowEntry.COLUMN_SHOW_OVERVIEW,
                ShowEntry.COLUMN_SHOW_IMDB_URL,
                ShowEntry.COLUMN_SHOW_IMAGE_ID,
                ShowEntry.COLUMN_SHOW_WATCHED
        };

        return new CursorLoader(this, showURL, selectedCols,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) { return; }

        View barLoading = findViewById(R.id.details_probar_loading);
        barLoading.setVisibility(View.GONE);

        if (cursor.moveToFirst()) {
            int showIDColIndex = cursor.getColumnIndex(ShowEntry._ID);
            int tmdbIDColIndex = cursor.getColumnIndex(ShowEntry.TMDB_ID);
            int titleColIndex = cursor.getColumnIndex(ShowEntry.COLUMN_SHOW_TITLE);
            int voteColIndex = cursor.getColumnIndex(ShowEntry.COLUMN_SHOW_AVERAGE_VOTE);
            int countColIndex = cursor.getColumnIndex(ShowEntry.COLUMN_SHOW_VOTE_COUNT);
            int releaseColIndex = cursor.getColumnIndex(ShowEntry.COLUMN_SHOW_RELEASE_DATE_IN_MILLISECONDS);
            int overviewColIndex = cursor.getColumnIndex(ShowEntry.COLUMN_SHOW_OVERVIEW);
            int imdbURLColIndex = cursor.getColumnIndex(ShowEntry.COLUMN_SHOW_IMDB_URL);
            int watchedColIndex = cursor.getColumnIndex(ShowEntry.COLUMN_SHOW_WATCHED);
            int imageIDColIndex = cursor.getColumnIndex(ShowEntry.COLUMN_SHOW_IMAGE_ID);

            final String showID = cursor.getString(showIDColIndex);
            final int tmdbID = cursor.getInt(tmdbIDColIndex);
            final String title = cursor.getString(titleColIndex);
            final double vote = cursor.getDouble(voteColIndex);
            final int count = cursor.getInt(countColIndex);
            final long release = cursor.getLong(releaseColIndex);
            final String overview = cursor.getString(overviewColIndex);
            final String imdbURL = cursor.getString(imdbURLColIndex);
            final String imageID = cursor.getString(imageIDColIndex);
            isWatched = cursor.getInt(watchedColIndex) == 1;

            final Show show = new Show(tmdbID, title, vote, release, imageID, imdbURL, count, overview);
            show.setWatched(isWatched);
            String cinemaReleaseDate = "Released at: " + show.getDate();

            Picasso.with(this).load(show.getImageURL()).into(imgPoster);
            tvTitle.setText(show.getTitle());
            tvVote.setText(String.valueOf(show.getVote()));
            tvCount.setText(String.valueOf(show.getCount()));
            tvRelease.setText(cinemaReleaseDate);
            tvOverview.setText(show.getOverview());
            btnImdbLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(show.getImdbURL()));
                    startActivity(intent);
                }
            });

            if (title != null && !title.isEmpty()) {
                tvEmpty.setVisibility(View.GONE);
            } else {
                tvEmpty.setText(R.string.txt_noData);
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
    public void onLoaderReset(Loader<Cursor> loader) { }


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
