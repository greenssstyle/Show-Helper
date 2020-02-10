package com.example.thinkpad.showhelper.DB;

import com.example.thinkpad.showhelper.DB.ShowInfo.ShowEntry;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class ShowSQL extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "showstracker.db";
    private static final int DATABASE_VERSION = 1;


    ShowSQL(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_SHOWS_TABLE =  "CREATE TABLE " + ShowEntry.TABLE_NAME + " ("
                + ShowEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ShowEntry.TMDB_ID + " INTEGER NOT NULL UNIQUE, "
                + ShowEntry.COLUMN_SHOW_TITLE + " TEXT NOT NULL, "
                + ShowEntry.COLUMN_SHOW_RELEASE_DATE_IN_MILLISECONDS + " INTEGER NOT NULL, "
                + ShowEntry.COLUMN_SHOW_AVERAGE_VOTE + " TEXT NOT NULL, "
                + ShowEntry.COLUMN_SHOW_IMDB_URL + " TEXT NOT NULL,"
                + ShowEntry.COLUMN_SHOW_VOTE_COUNT + " INTEGER NOT NULL,"
                + ShowEntry.COLUMN_SHOW_OVERVIEW + " TEXT NOT NULL,"
                + ShowEntry.COLUMN_SHOW_WATCHED + " INTEGER NOT NULL,"
                + ShowEntry.COLUMN_SHOW_IMAGE_ID + " TEXT NOT NULL,"
                + ShowEntry.COLUMN_SHOW_THUMBNAIL_URL + " TEXT NOT NULL,"
                + ShowEntry.COLUMN_SHOW_IMAGE_URL + " TEXT NOT NULL);";

        db.execSQL(SQL_CREATE_SHOWS_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
}
