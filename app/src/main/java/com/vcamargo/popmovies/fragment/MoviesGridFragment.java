package com.vcamargo.popmovies.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.vcamargo.popmovies.MovieDetailsActivity;
import com.vcamargo.popmovies.R;
import com.vcamargo.popmovies.adapter.MoviesAdapter;
import com.vcamargo.popmovies.asynctask.GetMoviesTask;
import com.vcamargo.popmovies.data.MoviesContract;
import com.vcamargo.popmovies.data.MoviesProvider;
import com.vcamargo.popmovies.sync.MoviesSyncAdapter;
import com.vcamargo.popmovies.utils.Utils;

import static com.vcamargo.popmovies.asynctask.GetMoviesTask.EXTRA_MOVIE_ID;
import static com.vcamargo.popmovies.asynctask.GetMoviesTask.EXTRA_MOVIE_ROW_ID;

public class MoviesGridFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {
    private MoviesAdapter movieAdapter;

    private static final String SELECTED_KEY = "selected_position";
    private static final int MOVIES_LOADER = 0;
    private static final String mSelectionClause = MoviesContract.MovieEntry.COLUMN_MOVIE_LIST_TYPE + "=?";
    private static final String[] mSelectionValues = {""};
    //projection columns
    private static final String[] MOVIES_COLUMNS = {
            MoviesContract.MovieEntry.TABLE_NAME + "." + MoviesContract.MovieEntry._ID,
            MoviesContract.MovieEntry.COLUMN_MOVIE_IMG_PATH,
            MoviesContract.MovieEntry.COLUMN_MOVIE_ID
    };
    public static final int COL_ROW_ID = 0;
    public static final int COL_MOVIE_IMG_PATH = 1;
    public static final int COL_MOVIE_ID = 2;

    private ProgressDialog loading;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(MOVIES_LOADER, null, this);
    }

    public MoviesGridFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!mSelectionValues[0].equals(Utils.getListType(getContext()))) {
            //preference was changed
            resetLoader();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_grid, container, false);

        movieAdapter = new MoviesAdapter(getActivity(), null, 0);

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
            MoviesSyncAdapter.syncImmediately(getContext());
        } else {
            if (loading != null) {
                loading.dismiss();
            }
            Snackbar.make(getView(), R.string.snack_no_network_error, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        mSelectionValues[0] = Utils.getListType(getContext());
        return new CursorLoader(getActivity(),
                MoviesContract.MovieEntry.CONTENT_URI,
                MOVIES_COLUMNS,
                mSelectionClause,
                mSelectionValues,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            movieAdapter.swapCursor(data);
            if (loading != null) {
                loading.dismiss();
            }
        } else {
            loading = new ProgressDialog(getContext());
            loading.setIndeterminate(true);
            loading.show();

            updateMovies();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        movieAdapter.swapCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Cursor cursor = (Cursor) adapterView.getItemAtPosition(i);
        if (cursor != null) {
            String rowID = String.valueOf(cursor.getInt(COL_ROW_ID));
            if (MoviesProvider.shouldCallAPI(getContext(), rowID)) {
                Bundle extras = new Bundle();
                extras.putString(EXTRA_MOVIE_ID, cursor.getString(COL_MOVIE_ID));
                extras.putString(EXTRA_MOVIE_ROW_ID, rowID);
                GetMoviesTask task = new GetMoviesTask(getContext(), extras);
                task.execute();
            }
            Intent intent = new Intent(getActivity(), MovieDetailsActivity.class)
                    .putExtra(MoviesContract.MovieEntry._ID, cursor.getInt(COL_ROW_ID));
            startActivity(intent);
        }
    }

    public void resetLoader() {
        getLoaderManager().restartLoader(MOVIES_LOADER, null, this);
    }
}
