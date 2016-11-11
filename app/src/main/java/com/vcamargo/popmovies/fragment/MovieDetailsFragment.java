package com.vcamargo.popmovies.fragment;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vcamargo.popmovies.R;
import com.vcamargo.popmovies.adapter.TrailerAdapter;
import com.vcamargo.popmovies.bean.MovieBean;
import com.vcamargo.popmovies.data.MoviesContract;


public class MovieDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener {
    public static final String LOG_TAG = MovieDetailsFragment.class.getSimpleName();
    public static final String[] MOVIES_COLUMNS = {
            MoviesContract.MovieEntry.TABLE_NAME + "." + MoviesContract.MovieEntry._ID,
            MoviesContract.MovieEntry.COLUMN_MOVIE_TITLE_SHORT,
            MoviesContract.MovieEntry.COLUMN_MOVIE_DESCRIPTION,
            MoviesContract.MovieEntry.COLUMN_MOVIE_VOTE_AVG,
            MoviesContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE,
            MoviesContract.MovieEntry.COLUMN_MOVIE_IMG_PATH,
            MoviesContract.MovieEntry.COLUMN_MOVIE_DURATION
    };
    public static final int COL_MOVIE_ID = 0;
    public static final int COL_MOVIE_TITLE_SHORT = 1;
    public static final int COL_MOVIE_DESCRIPTION = 2;
    public static final int COL_MOVIE_VOTE_AVG = 3;
    public static final int COL_MOVIE_RELEASE_DATE = 4;
    public static final int COL_MOVIE_IMG_PATH = 5;
    public static final int COL_MOVIE_DURATION = 6;

    private static final String[] TRAILER_COLUMNS = {
            MoviesContract.VideoEntry.TABLE_NAME + "." + MoviesContract.VideoEntry._ID,
            MoviesContract.VideoEntry.COLUMN_VIDEO_NAME,
            MoviesContract.VideoEntry.COLUMN_VIDEO_URL,
    };
    public static final int COL_VIDEO_ID = 0;
    public static final int COL_VIDEO_NAME = 1;
    public static final int COL_VIDEO_URL = 2;

    private String[] mSelectionArgs = {""};
    public static final String mSelectionClause = MoviesContract.MovieEntry.TABLE_NAME + "." + MoviesContract.MovieEntry._ID + "=?";
    private String mSelectionClause2 = MoviesContract.VideoEntry.TABLE_NAME + "." + MoviesContract.VideoEntry.COLUMN_MOVIE_KEY + "=?";
    private static final int DETAIL_LOADER = 0;
    private static final int TRAILER_LOADER = 1;

    private TextView original_title;
    private TextView movie_synopsis;
    private TextView user_rating;
    private TextView release_date;
    private TextView movie_duration;
    private ImageView img_thumb;
    private TrailerAdapter trailerAdapter;
    String rowID = "";
    public static final String MOVIE_DURATION_SUFFIX = "min";
    public static final String MOVIE_RATING_SUFFIX = "/10";
    public MovieDetailsFragment() {}
    private ProgressDialog loading;
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        getLoaderManager().initLoader(TRAILER_LOADER, null, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);

        Bundle arguments = getArguments();
        if (arguments != null) {
            mSelectionArgs[0] = String.valueOf(arguments.getInt(MoviesContract.MovieEntry._ID));
        }

        original_title = (TextView) rootView.findViewById(R.id.original_title);
        movie_synopsis = (TextView) rootView.findViewById(R.id.movie_synopsis);
        user_rating = (TextView) rootView.findViewById(R.id.user_rating);
        release_date = (TextView) rootView.findViewById(R.id.release_date);
        movie_duration = (TextView) rootView.findViewById(R.id.movie_duration);
        img_thumb = (ImageView) rootView.findViewById(R.id.img_thumb);

        trailerAdapter = new TrailerAdapter(getActivity(),null,0);
        ListView listView = (ListView) rootView.findViewById(R.id.videos_list);
        listView.setAdapter(trailerAdapter);
        listView.setOnItemClickListener(this);
        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader;
        switch (id) {
            case DETAIL_LOADER:
                loader = new CursorLoader(
                        getActivity(),
                        MoviesContract.MovieEntry.CONTENT_URI,
                        MOVIES_COLUMNS,
                        mSelectionClause,
                        mSelectionArgs,
                        null
                );
                break;
            case TRAILER_LOADER:
                loader = new CursorLoader(
                        getActivity(),
                        MoviesContract.VideoEntry.CONTENT_URI,
                        TRAILER_COLUMNS,
                        mSelectionClause2,
                        mSelectionArgs,
                        null
                );
                break;
            default:
                loader = null;
        }

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case DETAIL_LOADER:
                if (data != null && data.moveToFirst() &&
                        !TextUtils.isEmpty(data.getString(COL_MOVIE_RELEASE_DATE))) {
                    if (loading != null) {
                        loading.dismiss();
                    }
                    original_title.setText(data.getString(COL_MOVIE_TITLE_SHORT));
                    movie_synopsis.setText(data.getString(COL_MOVIE_DESCRIPTION));
                    user_rating.setText(data.getString(COL_MOVIE_VOTE_AVG)+MOVIE_RATING_SUFFIX);
                    String date = data.getString(COL_MOVIE_RELEASE_DATE);
                    date = date.substring(0,date.indexOf('-'));
                    release_date.setText(date);
                    movie_duration.setText(data.getString(COL_MOVIE_DURATION)+MOVIE_DURATION_SUFFIX);
                    Picasso
                            .with(getActivity())
                            .load(MovieBean.BASE_URL + data.getString(COL_MOVIE_IMG_PATH))
                            .fit()
                            .into(img_thumb);
                } else {
                    loading = new ProgressDialog(getContext());
                    loading.setIndeterminate(true);
                    loading.show();
                }
                break;
            case TRAILER_LOADER:
                if (data.moveToFirst()) {
                    trailerAdapter.swapCursor(data);
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case TRAILER_LOADER:
                trailerAdapter.swapCursor(null);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Cursor cursor = (Cursor) adapterView.getItemAtPosition(i);
        sendYoutubeIntent(cursor.getString(COL_VIDEO_URL));
    }

    public void sendYoutubeIntent(String id) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            Log.e(LOG_TAG,"Youtube app not found. fallback to web based player");
            startActivity(webIntent);
        }
    }
}
