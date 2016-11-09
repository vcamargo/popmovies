package com.vcamargo.popmovies.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.vcamargo.popmovies.BuildConfig;
import com.vcamargo.popmovies.MovieDetailsActivity;
import com.vcamargo.popmovies.R;
import com.vcamargo.popmovies.adapter.MoviesAdapter;
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

public class MoviesGridFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {
    private MoviesAdapter movieAdapter;
    private int currentPage = 1;
    private static final String SELECTED_KEY = "selected_position";
    private static final int MOVIES_LOADER = 0;

    //projection columns
    private static final String[] MOVIES_COLUMNS = {
            MoviesContract.MovieEntry.TABLE_NAME + "." + MoviesContract.MovieEntry._ID,
            MoviesContract.MovieEntry.COLUMN_MOVIE_IMG_PATH
    };
    public static final int COL_MOVIE_ID = 0;
    public static final int COL_MOVIE_IMG_PATH = 1;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(MOVIES_LOADER, null, this);
    }

    public MoviesGridFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateMovies();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_movie_grid, container, false);

        movieAdapter = new MoviesAdapter(getActivity(),null,0);

        GridView gridview = (GridView) rootView.findViewById(R.id.movies_grid);
        gridview.setAdapter(movieAdapter);
        gridview.setOnItemClickListener(this);
        return rootView;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void updateMovies() {
        if (isNetworkAvailable()) {
            GetMoviesTask asynTask = new GetMoviesTask();
            SharedPreferences sharedPrefs =
                    PreferenceManager.getDefaultSharedPreferences(getActivity());
            String queryType = sharedPrefs.getString(
                    getString(R.string.pref_sort_key),
                    getString(R.string.pref_sort_mostpopular));
            asynTask.execute(queryType);
            getLoaderManager().restartLoader(MOVIES_LOADER, null, this);
        } else {
            Snackbar.make(getView(),R.string.snack_no_network_error,Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
       return new CursorLoader(getActivity(),
                MoviesContract.MovieEntry.CONTENT_URI,
                MOVIES_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        movieAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        movieAdapter.swapCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Cursor cursor = (Cursor) adapterView.getItemAtPosition(i);
        if (cursor != null) {
            Intent intent = new Intent(getActivity(), MovieDetailsActivity.class)
                    .putExtra(MoviesContract.MovieEntry._ID, cursor.getInt(COL_MOVIE_ID));
            startActivity(intent);
        }
    }

    public class GetMoviesTask extends AsyncTask<String, Void, Void> {
        private final String LOG_TAG = GetMoviesTask.class.getSimpleName();
        private String mListType;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... strings) {
            if (strings.length == 0) {
                return null;
            }
            mListType = strings[0];
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String moviesJsonStr = null;

            String language = "en-US";

            try {
            final String MOVIES_BASE_URL =
                    "https://api.themoviedb.org/3/movie/" + mListType + "?";
            final String API_KEY_PARAM = "api_key";
            final String LANGUAGE_PARAM = "language";
            final String PAGE_PARAM = "page";

            Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                    .appendQueryParameter(LANGUAGE_PARAM, language)
                    .appendQueryParameter(PAGE_PARAM, String.valueOf(currentPage))
                    .appendQueryParameter(API_KEY_PARAM, BuildConfig.MOVIE_DB_API_KEY)
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
                getMoviesDataFromJson(moviesJsonStr);
                return null;
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        private void getMoviesDataFromJson(String moviesJsonStr) throws JSONException {
            final String MOVIES_LIST = "results";
            final String IMG_POSTER_PATH = "poster_path";
            final String ORIGINAL_TITLE = "original_title";
            final String OVERVIEW = "overview";
            final String VOTE_AVERAGE = "vote_average";
            final String RELEASE_DATE = "release_date";
            final String MOVIE_ID = "id";

            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray(MOVIES_LIST);
            Vector<ContentValues> cVVector = new Vector<>(moviesArray.length());

            for(int i = 0; i < moviesArray.length(); i++) {
                String movieId;
                String movieTitleShort;
                String movieDescription;
                String movieReleaseDate;
                String movieVoteAvg;
                String movieImgPath;

                JSONObject moviesDetails = moviesArray.getJSONObject(i);
                movieId = moviesDetails.getString(MOVIE_ID);
                movieTitleShort = moviesDetails.getString(ORIGINAL_TITLE);
                movieDescription = moviesDetails.getString(OVERVIEW);
                movieReleaseDate = moviesDetails.getString(RELEASE_DATE);
                movieVoteAvg = moviesDetails.getString(VOTE_AVERAGE);
                movieImgPath = moviesDetails.getString(IMG_POSTER_PATH);

                ContentValues movieValues = new ContentValues();
                movieValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_ID, movieId);
                movieValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_TITLE_SHORT, movieTitleShort);
                movieValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_DESCRIPTION, movieDescription);
                movieValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE, movieReleaseDate);
                movieValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_VOTE_AVG, movieVoteAvg);
                movieValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_IMG_PATH, movieImgPath);
                movieValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_LIST_TYPE, mListType);
                movieValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_IS_FAVORITE, false);

                cVVector.add(movieValues);
            }

            // add to database
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                getContext().getContentResolver().delete(MoviesContract.MovieEntry.CONTENT_URI, null, null);
                getContext().getContentResolver().bulkInsert(MoviesContract.MovieEntry.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "Sync Complete. " + cVVector.size() + " Inserted");
        }
    }
}
