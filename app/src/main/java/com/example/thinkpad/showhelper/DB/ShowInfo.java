package com.example.thinkpad.showhelper.DB;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


public class ShowInfo {

    private ShowInfo() {}


    public static final String CONTENT_AUTHORITY = "com.example.thinkpad.showhelper";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_SHOWS = "movies";


    public static final class ShowEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_SHOWS);

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SHOWS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SHOWS;

        public static final String TABLE_NAME = "movies";

        public static final String _ID = BaseColumns._ID;
        public static final String TMDB_ID = "tmdb_id";

        public static final String COLUMN_SHOW_TITLE = "title";
        public static final String COLUMN_SHOW_IMAGE_ID = "image_id";
        public static final String COLUMN_SHOW_RELEASE_DATE_IN_MILLISECONDS = "release_date";
        public static final String COLUMN_SHOW_AVERAGE_VOTE = "average_vote";
        public static final String COLUMN_SHOW_VOTE_COUNT = "votes_count";
        public static final String COLUMN_SHOW_OVERVIEW = "overview";

        public static final String COLUMN_SHOW_WATCHED = "watched";

        public static final String COLUMN_SHOW_IMDB_URL = "imdb_url";
        public static final String COLUMN_SHOW_THUMBNAIL_URL = "thumbnail_url";
        public static final String COLUMN_SHOW_IMAGE_URL = "image_url";
    }
}
