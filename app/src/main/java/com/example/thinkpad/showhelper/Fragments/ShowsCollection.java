package com.example.thinkpad.showhelper.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.thinkpad.showhelper.R;
import com.example.thinkpad.showhelper.DB.ShowInfo;


public class ShowsCollection extends ShowsBase {

    public ShowsCollection() {
        setHasOptionsMenu(true);
        setSearchFabVisibility(true);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        String curFilter = sharedPrefs.getString(
                getString(R.string.settings_filter),
                getString(R.string.settings_filterDefault)
        );

        switch (curFilter) {
            case "released":
                setFilterReleased();
                break;

            case "not_released":
                setFilterNotReleased();
                break;

            default:
                Log.e("ShowsCollection", "Filtering error");
                break;
        }

        setSorting();

        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        menu.clear();
        inflater.inflate(R.menu.menu_collection,menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        String curFilter = sharedPrefs.getString(
                getString(R.string.settings_filter),
                getString(R.string.settings_filterDefault)
        );

        String curSorting = sharedPrefs.getString(
                getString(R.string.settings_sort),
                getString(R.string.settings_sortDefault)
        );

        String curSortingDirection = sharedPrefs.getString(
                getString(R.string.settings_sortDirection),
                getString(R.string.settings_sortDirectionDefault)
        );

        switch (curFilter) {
            case "released":
                MenuItem itemReleased = menu.findItem(R.id.menu_show_released);
                itemReleased.setEnabled(false);
                break;

            case "not_released":
                MenuItem itemNotReleased = menu.findItem(R.id.menu_show_notreleased);
                itemNotReleased.setEnabled(false);
                break;

            default:
                Log.e("ShowsCollection", "Filtering error");
                break;
        }

        switch (curSorting) {
            case "release_date":
                MenuItem itemRelease = menu.findItem(R.id.menu_sort_date);
                itemRelease.setEnabled(false);
                break;

            case "average_vote":
                MenuItem itemRating = menu.findItem(R.id.menu_sort_rating);
                itemRating.setEnabled(false);
                break;

            default:
                Log.e("ShowsCollection", "Sorting error");
                break;
        }

        switch (curSortingDirection) {
            case "ASC":
                MenuItem itemAsc = menu.findItem(R.id.menu_sort_asc);
                itemAsc.setEnabled(false);
                break;
            case ("DESC"):
                MenuItem itemDesc = menu.findItem(R.id.menu_sort_desc);
                itemDesc.setEnabled(false);
                break;
            default:
                Log.e("ShowsCollection", "Sorting direction error");
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_show_released:
                saveFilterReleased();
                break;

            case R.id.menu_show_notreleased:
                saveFilterNotReleased();
                break;

            case R.id.menu_sort_date:
                saveSortingBy(ShowInfo.ShowEntry.COLUMN_SHOW_RELEASE_DATE_IN_MILLISECONDS);
                break;

            case R.id.menu_sort_rating:
                saveSortingBy(ShowInfo.ShowEntry.COLUMN_SHOW_AVERAGE_VOTE);
                break;

            case R.id.menu_sort_asc:
                saveSortingDirection("ASC");
                break;

            case R.id.menu_sort_desc:
                saveSortingDirection("DESC");
                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        refreshList();
        return true;
    }


    private void setFilterReleased() {
        String filterOption = ShowInfo.ShowEntry.COLUMN_SHOW_RELEASE_DATE_IN_MILLISECONDS + "<?";

        long currTimeMillis = System.currentTimeMillis();
        String[] filterOptionArgs = {String.valueOf(currTimeMillis)};

        setFilterOption(filterOption);
        setFilterOptionArgs(filterOptionArgs);
    }

    private void setFilterNotReleased() {
        String filterOption = ShowInfo.ShowEntry.COLUMN_SHOW_RELEASE_DATE_IN_MILLISECONDS + ">?";

        long currTimeMillis = System.currentTimeMillis();
        String[] filterOptionArgs = {String.valueOf(currTimeMillis)};

        setFilterOption(filterOption);
        setFilterOptionArgs(filterOptionArgs);
    }

    private void setSorting() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        String sortBy = sharedPrefs.getString(
                getString(R.string.settings_sort),
                getString(R.string.settings_sortDefault)
        );

        String sortDirection = sharedPrefs.getString(
                getString(R.string.settings_sortDirection),
                getString(R.string.settings_sortDirectionDefault)
        );

        String sortingOption = sortBy + " " + sortDirection;
        setSortingOption(sortingOption);
    }


    private void saveFilterReleased() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPrefs.edit();

        editor.putString(getString(R.string.settings_filter), "released");
        editor.apply();
    }

    private void saveFilterNotReleased() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPrefs.edit();

        editor.putString(getString(R.string.settings_filter), "not_released");
        editor.apply();
    }

    private void saveSortingBy(String sortBy) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPrefs.edit();

        editor.putString(getString(R.string.settings_sort), sortBy);
        editor.apply();
    }

    private void saveSortingDirection(String sortDirection) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPrefs.edit();

        editor.putString(getString(R.string.settings_sortDirection), sortDirection);
        editor.apply();
    }
}
