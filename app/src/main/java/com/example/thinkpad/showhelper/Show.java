package com.example.thinkpad.showhelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class Show {

    private int tmdbID;

    private String title;
    private double vote;
    private long dateInMillis;
    private String imageID;
    private String count;
    private String overview;
    private boolean watched;

    private String imdbURL;
    private String thumbnailURL;
    private String imageURL;

    private String thumbnailSize = "w300";
    private String imageSize = "w780";

    private int totalPages;


    public Show(int tmdbID, String title, double vote, long dateInMillis, String imageID) {
        this.tmdbID = tmdbID;

        this.title = title;
        this.imageID = imageID;
        this.vote = vote;
        this.dateInMillis = dateInMillis;
        this.watched = false;

        this.thumbnailURL = "http://image.tmdb.org/t/p/" + thumbnailSize + imageID;
        this.imageURL = "http://image.tmdb.org/t/p/" + imageSize + imageID;
    }

    public Show(int tmdbID, String title, double vote, long dateInMillis, String imageID, String imdbURL, int count, String overview) {
        this.tmdbID = tmdbID;

        this.title = title;
        this.imageID = imageID;
        this.vote = vote;
        this.count = count + " votes";
        this.dateInMillis = dateInMillis;
        this.overview = overview;
        this.watched = false;

        this.imdbURL = imdbURL;
        this.thumbnailURL = "http://image.tmdb.org/t/p/" + thumbnailSize + imageID;
        this.imageURL = "http://image.tmdb.org/t/p/" + imageSize + imageID;
    }


    public int getTMDbID(){
        return tmdbID;
    }

    public String getTitle() {
        return title;
    }

    public String getImageID() {
        return imageID;
    }

    public double getVote() {
        return vote;
    }

    public String getCount() {
        return count;
    }

    public String getDate() {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateInMillis);
        return formatter.format(calendar.getTime());
    }

    public long getDateInMillis() {
        return dateInMillis;
    }

    public String getOverview() {
        return overview;
    }


    public int getWatchedIntValue() {
        if (watched) {
            return 1;
        }
        return 0;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public String getImdbURL() {
        return imdbURL;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }


    public void setWatched(boolean watched) {
        this.watched = watched;
    }

    public void setTotalPages(int num) {
        this.totalPages = num;
    }
}
