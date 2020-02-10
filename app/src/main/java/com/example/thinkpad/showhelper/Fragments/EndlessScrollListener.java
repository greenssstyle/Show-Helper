package com.example.thinkpad.showhelper.Fragments;

import android.widget.AbsListView;


public abstract class EndlessScrollListener implements AbsListView.OnScrollListener {

    private int visibleThreshold;
    private int currPage;
    private int indexStartPage;
    private int prevCountTotal = 0;
    private boolean isLoading = true;


    public EndlessScrollListener(int visibleThreshold, int startPage) {
        this.visibleThreshold = visibleThreshold;
        this.indexStartPage = startPage;
        this.currPage = startPage;
    }


    @Override
    public void onScroll(AbsListView view, int visibleFirst, int countVisible, int numTotal) {
        if (numTotal < prevCountTotal) {
            this.currPage = this.indexStartPage;
            this.prevCountTotal = numTotal;
            if (numTotal == 0) { this.isLoading = true; }
        }

        if (isLoading && (numTotal > prevCountTotal)) {
            isLoading = false;
            prevCountTotal = numTotal;
            currPage++;
        }

        if (!isLoading && ((visibleFirst + countVisible + visibleThreshold) >= numTotal) ) {
            isLoading = onLoadMore(currPage + 1, numTotal);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) { }


    public abstract boolean onLoadMore(int page, int countTotal);
}