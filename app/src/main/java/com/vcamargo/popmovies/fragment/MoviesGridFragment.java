package com.vcamargo.popmovies.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.vcamargo.popmovies.bean.MovieBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MoviesGridFragment extends Fragment implements AdapterView.OnItemClickListener{
    private MoviesAdapter movieAdapter;
    private int currentPage = 1;

    public MoviesGridFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(getActivity(), MovieDetailsActivity.class)
                .putExtra(MovieBean.INTENT_EXTRA_KEY, movieAdapter.getItem(i));
        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_movie_grid, container, false);
        movieAdapter = new MoviesAdapter(getActivity(), new ArrayList<MovieBean>());

        GridView gridview = (GridView) rootView.findViewById(R.id.movies_grid);
        gridview.setAdapter(movieAdapter);
        gridview.setOnItemClickListener(this);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
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
        }
    }

    public class GetMoviesTask extends AsyncTask<String, Void, MovieBean[]> {
        private final String LOG_TAG = GetMoviesTask.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected MovieBean[] doInBackground(String... strings) {
            if (strings.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String moviesJsonStr = null;

            String language = "en-US";

            try {
            final String MOVIES_BASE_URL =
                    "https://api.themoviedb.org/3/movie/" + strings[0] + "?";
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
                return getMoviesDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(MovieBean[] movieBeen) {
            if (movieBeen != null) {
                movieAdapter.clear();
                movieAdapter.addAll(movieBeen);
                movieAdapter.notifyDataSetChanged();
            }
        }

        private MovieBean[] getMoviesDataFromJson(String moviesJsonStr) throws JSONException {
            final String MOVIES_LIST = "results";
            final String IMG_POSTER_PATH = "poster_path";
            final String IMG_THUMB_ID = "backdrop_path";
            final String ORIGINAL_TITLE = "original_title";
            final String OVERVIEW = "overview";
            final String VOTE_AVERAGE = "vote_average";
            final String RELEASE_DATE = "release_date";

            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray(MOVIES_LIST);

            MovieBean[] moviesArrayList = new MovieBean[20];
            for(int i = 0; i < moviesArray.length(); i++) {
                JSONObject moviesDetails = moviesArray.getJSONObject(i);
                MovieBean movieBean = new MovieBean(moviesDetails.getString(IMG_POSTER_PATH),
                        moviesDetails.getString(IMG_THUMB_ID),moviesDetails.getString(ORIGINAL_TITLE),
                        moviesDetails.getString(OVERVIEW),moviesDetails.getString(VOTE_AVERAGE),
                        moviesDetails.getString(RELEASE_DATE));

                moviesArrayList[i] = movieBean;
            }
            return moviesArrayList;
        }
    }
}
