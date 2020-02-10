package com.example.thinkpad.showhelper.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.thinkpad.showhelper.Show;
import com.example.thinkpad.showhelper.R;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class ShowAdapter extends ArrayAdapter<Show> {

    public ShowAdapter(Context context, ArrayList<Show> shows) {
        super(context, 0, shows);
    }


    @NonNull
    @Override
    public View getView(int pos, View convert, @NonNull ViewGroup parent) {
        View viewItem = convert;
        if (viewItem == null) {
            viewItem = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        Show currShow = getItem(pos);

        TextView tvTitle = viewItem.findViewById(R.id.item_tv_title);
        tvTitle.setText(currShow.getTitle());

        TextView tvVote = viewItem.findViewById(R.id.item_tv_vote);
        tvVote.setText(formatVote(currShow.getVote()));

        TextView tvDate = viewItem.findViewById(R.id.item_tv_release);
        tvDate.setText(currShow.getDate());

        ImageView imgPoster = viewItem.findViewById(R.id.item_img_thumbnail);
        Picasso.with(getContext())
                .load(currShow.getThumbnailURL())
                .into(imgPoster);

        return viewItem;
    }


    private String formatVote(double vote) {
        DecimalFormat magnitude = new DecimalFormat("0.0");
        return magnitude.format(vote);
    }

    @Override
    public void addAll(Show... items) {
        super.addAll(items);
    }
}
