package com.example.thinkpad.showhelper.DB;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.thinkpad.showhelper.DB.ShowInfo.ShowEntry;


public class ShowProvider extends ContentProvider {

    private static final int SHOWS = 100;
    private static final int SHOW_ID = 101;
    private static final UriMatcher urlMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    public static final String LOG_TAG = ShowProvider.class.getSimpleName();

    static {
        urlMatcher.addURI(ShowInfo.CONTENT_AUTHORITY, ShowInfo.PATH_SHOWS, SHOWS);
        urlMatcher.addURI(ShowInfo.CONTENT_AUTHORITY, ShowInfo.PATH_SHOWS + "/#", SHOW_ID);
    }

    private ShowSQL showSQL;


    @Override
    public boolean onCreate() {
        showSQL = new ShowSQL(getContext());
        return true;
    }


    @Override
    public Cursor query(@NonNull Uri uri, String[] selectedCols, String filterOption,
                        String[] filterOptionArgs, String sortingOption) {
        SQLiteDatabase database = showSQL.getReadableDatabase();
        Cursor cursor;

        int matcher = urlMatcher.match(uri);
        switch (matcher) {
            case SHOWS:
                cursor = database.query(
                        ShowEntry.TABLE_NAME,
                        selectedCols, filterOption,
                        filterOptionArgs,
                        null,
                        null,
                        sortingOption);
                break;

            case SHOW_ID:
                filterOption = ShowEntry._ID + "=?";
                filterOptionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                cursor = database.query(
                        ShowEntry.TABLE_NAME,
                        selectedCols,
                        filterOption,
                        filterOptionArgs,
                        null,
                        null,
                        sortingOption);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }


    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        final int match = urlMatcher.match(uri);

        switch (match) {
            case SHOWS:
                return addShow(uri, contentValues);

            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }


    private Uri addShow(Uri uri, ContentValues values) {
        String tmdbID = values.getAsString(ShowEntry.TMDB_ID);
        if (tmdbID == null) {
            throw new IllegalArgumentException("Requires an valid tmdb id");
        }

        String title = values.getAsString(ShowEntry.COLUMN_SHOW_TITLE);
        if (title == null) {
            throw new IllegalArgumentException("Requires an valid title");
        }

        String imageID = values.getAsString(ShowEntry.COLUMN_SHOW_IMAGE_ID);
        if (imageID == null) {
            throw new IllegalArgumentException("Requires an valid image id");
        }

        String release = values.getAsString(ShowEntry.COLUMN_SHOW_RELEASE_DATE_IN_MILLISECONDS);
        if (release == null) {
            throw new IllegalArgumentException("Requires an valid release date");
        }

        String vote = values.getAsString(ShowEntry.COLUMN_SHOW_AVERAGE_VOTE);
        if (vote == null) {
            throw new IllegalArgumentException("Requires an valid average vote");
        }

        String count = values.getAsString(ShowEntry.COLUMN_SHOW_VOTE_COUNT);
        if (count == null) {
            throw new IllegalArgumentException("Requires an valid vote count");
        }

        String overview = values.getAsString(ShowEntry.COLUMN_SHOW_OVERVIEW);
        if (overview == null) {
            throw new IllegalArgumentException("Requires an valid overview");
        }

        String imdbURL = values.getAsString(ShowEntry.COLUMN_SHOW_IMDB_URL);
        if (imdbURL == null) {
            throw new IllegalArgumentException("Requires an valid imdb url");
        }

        String watched = values.getAsString(ShowEntry.COLUMN_SHOW_WATCHED);
        if (watched == null) {
            throw new IllegalArgumentException("Requires an valid watched value");
        }

        String imageURL = values.getAsString(ShowEntry.COLUMN_SHOW_IMAGE_URL);
        if (imageURL == null) {
            throw new IllegalArgumentException("Requires an valid image url");
        }

        String thumbnailURL = values.getAsString(ShowEntry.COLUMN_SHOW_THUMBNAIL_URL);
        if (thumbnailURL == null) {
            throw new IllegalArgumentException("Requires an valid thumbnail url");
        }


        SQLiteDatabase database = showSQL.getWritableDatabase();

        long id = database.insert(ShowEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }


    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues,
                      String filterOption, String[] filterOptionArgs) {
        final int match = urlMatcher.match(uri);

        switch (match) {
            case SHOWS:
                return updateShow(uri, contentValues, filterOption, filterOptionArgs);

            case SHOW_ID:
                filterOption = ShowEntry._ID + "=?";
                filterOptionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateShow(uri, contentValues, filterOption, filterOptionArgs);

            default:
                throw new IllegalArgumentException("Update cannot be done for " + uri);
        }
    }

    private int updateShow(Uri uri, ContentValues values, String filterOption, String[] filterOptionArgs) {
        String tmdbID = values.getAsString(ShowEntry.TMDB_ID);

        if (tmdbID == null) {
            throw new IllegalArgumentException("Requires an valid tmdb id");
        }

        String title = values.getAsString(ShowEntry.COLUMN_SHOW_TITLE);
        if (title == null) {
            throw new IllegalArgumentException("Requires an valid title");
        }

        String imageID = values.getAsString(ShowEntry.COLUMN_SHOW_IMAGE_ID);
        if (imageID == null) {
            throw new IllegalArgumentException("Show requires an image id");
        }

        String release = values.getAsString(ShowEntry.COLUMN_SHOW_RELEASE_DATE_IN_MILLISECONDS);
        if (release == null) {
            throw new IllegalArgumentException("Requires an valid release date");
        }

        String vote = values.getAsString(ShowEntry.COLUMN_SHOW_AVERAGE_VOTE);
        if (vote == null) {
            throw new IllegalArgumentException("Requires an valid average vote");
        }

        String count = values.getAsString(ShowEntry.COLUMN_SHOW_VOTE_COUNT);
        if (count == null) {
            throw new IllegalArgumentException("Requires an valid vote count");
        }

        String overview = values.getAsString(ShowEntry.COLUMN_SHOW_OVERVIEW);
        if (overview == null) {
            throw new IllegalArgumentException("Requires an valid overview");
        }

        String imdbURL = values.getAsString(ShowEntry.COLUMN_SHOW_IMDB_URL);
        if (imdbURL == null) {
            throw new IllegalArgumentException("Requires an valid imdb url");
        }

        String watched = values.getAsString(ShowEntry.COLUMN_SHOW_WATCHED);
        if (watched == null) {
            throw new IllegalArgumentException("Show requires a watched value");
        }

        String imageURL = values.getAsString(ShowEntry.COLUMN_SHOW_IMAGE_URL);
        if (imageURL == null) {
            throw new IllegalArgumentException("Show requires an image url");
        }

        String thumbnailURL = values.getAsString(ShowEntry.COLUMN_SHOW_THUMBNAIL_URL);
        if (thumbnailURL == null) {
            throw new IllegalArgumentException("Show requires a thumbnail url");
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase db = showSQL.getWritableDatabase();

        int updatedRows = db.update(ShowEntry.TABLE_NAME, values, filterOption, filterOptionArgs);

        if (updatedRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return updatedRows;
    }


    @Override
    public int delete(@NonNull Uri uri, String filterOption, String[] filterOptionArgs) {
        SQLiteDatabase db = showSQL.getWritableDatabase();

        int deletedRows;

        final int match = urlMatcher.match(uri);

        switch (match) {
            case SHOWS:
                deletedRows = db.delete(ShowEntry.TABLE_NAME, filterOption, filterOptionArgs);
                break;

            case SHOW_ID:
                filterOption = ShowEntry._ID + "=?";
                filterOptionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                deletedRows = db.delete(ShowEntry.TABLE_NAME, filterOption, filterOptionArgs);
                break;

            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (deletedRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return deletedRows;
    }


    @Override
    public String getType(@NonNull Uri uri) {
        final int match = urlMatcher.match(uri);

        switch (match) {
            case SHOWS:
                return ShowEntry.CONTENT_LIST_TYPE;

            case SHOW_ID:
                return ShowEntry.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalStateException("Unknown URI " + uri + " with matcher " + match);
        }
    }
}
