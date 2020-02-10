package com.example.thinkpad.showhelper.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.thinkpad.showhelper.Show;
import com.example.thinkpad.showhelper.R;
import com.example.thinkpad.showhelper.DB.ShowInfo.ShowEntry;
import com.squareup.picasso.Picasso;


public class ShowCursorAdapter extends CursorAdapter {


    public ShowCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0 /* flags */);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvTitle = view.findViewById(R.id.item_tv_title);
        TextView tvVote = view.findViewById(R.id.item_tv_vote);
        TextView tvDate = view.findViewById(R.id.item_tv_release);
        ImageView imgThumbnail = view.findViewById(R.id.item_img_thumbnail);

        int tmdbIDColIndex = cursor.getColumnIndex(ShowEntry.TMDB_ID);
        int titleColIndex = cursor.getColumnIndex(ShowEntry.COLUMN_SHOW_TITLE);
        int voteColIndex = cursor.getColumnIndex(ShowEntry.COLUMN_SHOW_AVERAGE_VOTE);
        int dateColIndex = cursor.getColumnIndex(ShowEntry.COLUMN_SHOW_RELEASE_DATE_IN_MILLISECONDS);
        int thumbnailURLColIndex = cursor.getColumnIndex(ShowEntry.COLUMN_SHOW_THUMBNAIL_URL);

        int tmdbID = cursor.getInt(tmdbIDColIndex);
        String title = cursor.getString(titleColIndex);
        double vote = cursor.getDouble(voteColIndex);
        long dateInMillis = cursor.getLong(dateColIndex);
        String thumbnailURL = cursor.getString(thumbnailURLColIndex);

        Show show = new Show(tmdbID, title, vote, dateInMillis, thumbnailURL);

        tvTitle.setText(show.getTitle());
        tvVote.setText(String.valueOf(show.getVote()));
        tvDate.setText(show.getDate());

        Picasso.with(context).load(thumbnailURL).into(imgThumbnail);
    }
}
