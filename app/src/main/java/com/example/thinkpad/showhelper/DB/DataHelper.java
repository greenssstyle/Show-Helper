package com.example.thinkpad.showhelper.DB;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.example.thinkpad.showhelper.Show;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public final class DataHelper {

    private final static String LOG_TAG = "DataHelper";


    private static URL makeURL(String strURL) {
        URL url = null;
        try {
            url = new URL(strURL);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error: Cannot build url", e);
        }
        return url;
    }

    private static String makeHTTPRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error " + urlConnection.getResponseCode());
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream input) throws IOException{
        StringBuilder output = new StringBuilder();
        if (input != null) {
            InputStreamReader inputReader = new InputStreamReader(input, Charset.forName("UTF-8"));
            BufferedReader bufferReader = new BufferedReader(inputReader);
            String line = bufferReader.readLine();
            while (line != null) {
                output.append(line);
                line = bufferReader.readLine();
            }
        }
        return output.toString();
    }


    private static ArrayList<URL> makeURLs(ArrayList<Integer> tmdbIDs) {
        ArrayList<URL> urls = new ArrayList<>();
        for (Integer tmdbID:tmdbIDs) {
            try {
                String tmdbURL = "https://api.themoviedb.org/3/movie/" + tmdbID + "/recommendations";
                Uri url = Uri.parse(tmdbURL);
                Uri.Builder builder = url.buildUpon();
                builder.appendQueryParameter("api_key", "e0ff28973a330d2640142476f896da04");

                URL tmp = new URL(builder.toString());
                urls.add(tmp);
            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, "Error: Cannot build url", e);
            }
        }
        return urls;
    }

    private static ArrayList<String> makeHTTPRequests(ArrayList<URL> urls) throws IOException {
        ArrayList<String> jsonResponses = new ArrayList<>();

        if (urls.isEmpty()) {
            return jsonResponses;
        }

        for (URL url:urls) {
            HttpURLConnection urlConnection = null;

            InputStream input = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                if (urlConnection.getResponseCode() == 200) {
                    input = urlConnection.getInputStream();
                    jsonResponses.add(readFromStream(input));
                } else {
                    Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
                }

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (input != null) {
                    input.close();
                }
            }
        }
        return jsonResponses;
    }


    private static Show extractShow(String jsonShow) {
        if (TextUtils.isEmpty(jsonShow)) {
            return null;
        }

        Show show = null;

        try {
            JSONObject showInfo = new JSONObject(jsonShow);

            int tmdbID = showInfo.getInt("id");
            String title = showInfo.getString("title");
            double vote = showInfo.getDouble("vote_average");
            String release = showInfo.getString("release_date");
            String imageURL = showInfo.getString("backdrop_path");
            String imdbURL = "http://www.imdb.com/title/" + showInfo.getString("imdb_id");
            int count = showInfo.getInt("vote_count");
            String overview = showInfo.getString("overview");

            Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(release);
            long dateInMills = date.getTime();

            show = new Show(tmdbID, title, vote, dateInMills, imageURL, imdbURL, count, overview);

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing json results", e);
        } catch (ParseException e) {
            Log.i("Claudiu", "QuerryUtils stuff happened");
        }

        return show;
    }

    private static List<Show> extractShows(String jsonShows) {
        if (TextUtils.isEmpty(jsonShows)) {
            return null;
        }

        List<Show> shows = new ArrayList<>();

        try {
            JSONObject jsonBase = new JSONObject(jsonShows);
            int numPages = jsonBase.getInt("total_pages");
            JSONArray arrShows = jsonBase.getJSONArray("results");

            for (int i = 0; i < arrShows.length(); i++) {
                JSONObject showInfo = arrShows.getJSONObject(i);

                int tmdbID = showInfo.getInt("id");
                String title = showInfo.getString("title");
                double vote = showInfo.getDouble("vote_average");
                String release = showInfo.getString("release_date");
                String imageURL = showInfo.getString("backdrop_path");

                Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(release);
                long dateInMills = date.getTime();

                Show show = new Show(tmdbID, title, vote, dateInMills, imageURL);

                if (i == 0)
                    show.setTotalPages(numPages);

                shows.add(show);
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing json results", e);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return shows;
    }

    private static List<Show> extractRecommends(ArrayList<String> jsonResponses) {
        List<Show> shows = new ArrayList<>();

        if (jsonResponses.isEmpty()) {
            return null;
        }

        for (String showsJSON:jsonResponses) {
            if (TextUtils.isEmpty(showsJSON)) {
                break;
            }

            try {
                JSONObject jsonBase = new JSONObject(showsJSON);
                JSONArray arrShows = jsonBase.getJSONArray("results");

                for (int i = 0; i < arrShows.length(); i++) {
                    JSONObject showInfo = arrShows.getJSONObject(i);

                    int tmdbID = showInfo.getInt("id");
                    String title = showInfo.getString("title");
                    double vote = showInfo.getDouble("vote_average");
                    String release = showInfo.getString("release_date");
                    String imageURL = showInfo.getString("backdrop_path");

                    Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(release);
                    long dateInMills = date.getTime();

                    Show show = new Show(tmdbID, title, vote, dateInMills, imageURL);

                    boolean inShowsList = false;
                    for (Show savedShow:shows) {
                        if (savedShow.getTMDbID() == show.getTMDbID())
                            inShowsList = true;
                    }

                    if (!inShowsList) {
                        shows.add(show);
                    }
                }

            } catch (JSONException e) {
                Log.e(LOG_TAG, "Problem parsing json results", e);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        Collections.sort(shows, new Comparator<Show>() {
            @Override
            public int compare(Show m1, Show m2) {
                if (m1.getVote() > m2.getVote())
                    return -1;
                else if (m1.getVote() < m2.getVote())
                    return 1;
                else
                    return 0;
            }
        });
        return shows;
    }


    public static Show fetchShow(String strURL) {
        URL url = makeURL(strURL);

        String jsonResponse = null;
        try {
            jsonResponse = makeHTTPRequest(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return extractShow(jsonResponse);
    }

    public static List<Show> fetchShows(String strURL) {
        URL url = makeURL(strURL);

        String jsonResponse = null;
        try {
            jsonResponse = makeHTTPRequest(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return extractShows(jsonResponse);
    }

    public static List<Show> fetchRecommends(ArrayList<Integer> arrTMDbIDs) {
        ArrayList<URL> urls = makeURLs(arrTMDbIDs);

        ArrayList<String> jsonResponses = null;
        try {
            jsonResponses = makeHTTPRequests(urls);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return extractRecommends(jsonResponses);
    }
}
