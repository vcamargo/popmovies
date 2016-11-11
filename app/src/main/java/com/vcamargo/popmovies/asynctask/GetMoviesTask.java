package com.vcamargo.popmovies.asynctask;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.vcamargo.popmovies.BuildConfig;
import com.vcamargo.popmovies.data.MoviesContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by vinicius.camargo on 10/11/2016.
 */

public class GetMoviesTask extends AsyncTask<Void, Void, Void> {
    private final String LOG_TAG = GetMoviesTask.class.getSimpleName();
    private Context mContext;
    private String movieRowId = "-1";
    private String movieId="";
    public static final String EXTRA_MOVIE_ID = "movie_id";
    public static final String EXTRA_MOVIE_ROW_ID = "movie_row_id";
    public GetMoviesTask(Context mContext, Bundle extras){
        this.mContext = mContext;
        movieId = extras.getString(EXTRA_MOVIE_ID);
        movieRowId = extras.getString(EXTRA_MOVIE_ROW_ID);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... args) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String moviesJsonStr = null;
        String appendToResponse = "videos,reviews";

        try {
            final String MOVIES_BASE_URL =
                    "https://api.themoviedb.org/3/movie/" + movieId + "?";
            final String API_KEY_PARAM = "api_key";
            final String APPEND_TO_RESPONSE = "append_to_response";

            Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                        .appendQueryParameter(APPEND_TO_RESPONSE, appendToResponse)
                        .build();

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("content-type", "application/json");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            moviesJsonStr = buffer.toString();
        } catch (IOException ex) {
            Log.e(LOG_TAG, "Error ", ex);
            return null;
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        try {
            getMoviesDetailsFromJson(moviesJsonStr);
            return null;
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    private void getMoviesDetailsFromJson(String moviesDetailsJsonStr) throws JSONException {
        final String MOVIE_DURATION = "runtime";
        final String VIDEOS = "videos";
        final String JSON_RESULTS = "results";
        final String VIDEO_NAME = "name";
        final String VIDEO_URL = "key";
        final String REVIEWS = "reviews";
        final String REVIEW_AUTHOR = "author";
        final String REVIEW_CONTENT = "content";
        final String OVERVIEW = "overview";
        final String VOTE_AVERAGE = "vote_average";
        final String RELEASE_DATE = "release_date";
        final String ORIGINAL_TITLE = "original_title";

        JSONObject moviesDetailsJson = new JSONObject(moviesDetailsJsonStr);
        String runtime = moviesDetailsJson.getString(MOVIE_DURATION);
        String overview = moviesDetailsJson.getString(OVERVIEW);
        String voteAvg = moviesDetailsJson.getString(VOTE_AVERAGE);
        String releaseDate = moviesDetailsJson.getString(RELEASE_DATE);
        String originalTitle = moviesDetailsJson.getString(ORIGINAL_TITLE);

        ContentValues movieValues = new ContentValues();
        movieValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_DURATION, runtime);
        movieValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_DESCRIPTION, overview);
        movieValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_VOTE_AVG, voteAvg);
        movieValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE, releaseDate);
        movieValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_TITLE_SHORT, originalTitle);
        mContext.getContentResolver().update(MoviesContract.MovieEntry.CONTENT_URI, movieValues,
                MoviesContract.MovieEntry.TABLE_NAME +"."+MoviesContract.MovieEntry._ID+"=?", new String[]{movieRowId});


        JSONObject videosJson = moviesDetailsJson.getJSONObject(VIDEOS);
        JSONArray videosArray = videosJson.getJSONArray(JSON_RESULTS);
        Vector<ContentValues> cVVectorVideos = new Vector<>(videosArray.length());

        for(int i = 0; i < videosArray.length(); i++) {
            String movieKey;
            String movieName;
            String videoID;

            JSONObject moviesDetails = videosArray.getJSONObject(i);
            movieKey = moviesDetails.getString(VIDEO_URL);
            movieName = moviesDetails.getString(VIDEO_NAME);
            videoID = moviesDetails.getString("id");

            movieValues = new ContentValues();
            movieValues.put(MoviesContract.VideoEntry.COLUMN_VIDEO_URL, movieKey);
            movieValues.put(MoviesContract.VideoEntry.COLUMN_VIDEO_NAME, movieName);
            movieValues.put(MoviesContract.VideoEntry.COLUMN_VIDEO_ID, videoID);
            movieValues.put(MoviesContract.VideoEntry.COLUMN_MOVIE_KEY, movieRowId);
            cVVectorVideos.add(movieValues);
        }

        JSONObject reviewsJson = moviesDetailsJson.getJSONObject(REVIEWS);
        JSONArray reviewsArray = reviewsJson.getJSONArray(JSON_RESULTS);
        Vector<ContentValues> cVVectorReviews = new Vector<>(reviewsArray.length());

        for(int i = 0; i < reviewsArray.length(); i++) {
            String reviewAuthor;
            String reviewContent;
            String reviewID;
            String reviewURL;

            JSONObject reviewDetails = reviewsArray.getJSONObject(i);
            reviewAuthor = reviewDetails.getString(REVIEW_AUTHOR);
            reviewContent = reviewDetails.getString(REVIEW_CONTENT);
            reviewID = reviewDetails.getString("id");
            reviewURL = reviewDetails.getString("url");

            movieValues = new ContentValues();
            movieValues.put(MoviesContract.ReviewEntry.COLUMN_REVIEW_AUTHOR, reviewAuthor);
            movieValues.put(MoviesContract.ReviewEntry.COLUMN_REVIEW_CONTENT, "blablabla");
            movieValues.put(MoviesContract.ReviewEntry.COLUMN_REVIEW_ID, reviewID);
            movieValues.put(MoviesContract.VideoEntry.COLUMN_MOVIE_KEY, movieRowId);
            movieValues.put(MoviesContract.ReviewEntry.COLUMN_REVIEW_URL, reviewURL);
            cVVectorReviews.add(movieValues);
        }
        // add to database
        if (cVVectorVideos.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cVVectorVideos.size()];
            cVVectorVideos.toArray(cvArray);
            mContext.getContentResolver().bulkInsert(MoviesContract.VideoEntry.CONTENT_URI, cvArray);
            Log.d(LOG_TAG, "Sync Complete. " + cVVectorVideos.size() + " Videos Inserted");
        }

        if (cVVectorReviews.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cVVectorReviews.size()];
            cVVectorReviews.toArray(cvArray);
            mContext.getContentResolver().bulkInsert(MoviesContract.ReviewEntry.CONTENT_URI, cvArray);
            Log.d(LOG_TAG, "Sync Complete. " + cVVectorReviews.size() + " Reviews Inserted");
        }
    }
}
