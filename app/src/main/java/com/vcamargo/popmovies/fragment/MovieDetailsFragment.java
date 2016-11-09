package com.vcamargo.popmovies.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vcamargo.popmovies.R;
import com.vcamargo.popmovies.bean.MovieBean;
import com.vcamargo.popmovies.data.MoviesContract;


public class MovieDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    //projection columns
    private static final String[] MOVIES_COLUMNS = {
            MoviesContract.MovieEntry.TABLE_NAME + "." + MoviesContract.MovieEntry._ID,
            MoviesContract.MovieEntry.COLUMN_MOVIE_TITLE_SHORT,
            MoviesContract.MovieEntry.COLUMN_MOVIE_DESCRIPTION,
            MoviesContract.MovieEntry.COLUMN_MOVIE_VOTE_AVG,
            MoviesContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE,
            MoviesContract.MovieEntry.COLUMN_MOVIE_IMG_PATH
    };
    public static final int COL_MOVIE_ID = 0;
    public static final int COL_MOVIE_TITLE_SHORT = 1;
    public static final int COL_MOVIE_DESCRIPTION = 2;
    public static final int COL_MOVIE_VOTE_AVG = 3;
    public static final int COL_MOVIE_RELEASE_DATE = 4;
    public static final int COL_MOVIE_IMG_PATH = 5;

    private String[] mSelectionArgs = {""};
    private String mSelectionClause = MoviesContract.MovieEntry.TABLE_NAME + "." + MoviesContract.MovieEntry._ID + "=?";
    private static final int DETAIL_LOADER = 0;

    private TextView original_title;
    private TextView movie_synopsis;
    private TextView user_rating;
    private TextView release_date;
    private ImageView img_thumb;

    public MovieDetailsFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
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
        img_thumb = (ImageView) rootView.findViewById(R.id.img_thumb);

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                MoviesContract.MovieEntry.CONTENT_URI,
                MOVIES_COLUMNS,
                mSelectionClause,
                mSelectionArgs,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            original_title.setText(data.getString(COL_MOVIE_TITLE_SHORT));
            movie_synopsis.setText(data.getString(COL_MOVIE_DESCRIPTION));
            user_rating.setText(data.getString(COL_MOVIE_VOTE_AVG));
            String date = data.getString(COL_MOVIE_RELEASE_DATE);
            date = date.substring(0,date.indexOf('-'));
            release_date.setText(date);
            Picasso
                    .with(getActivity())
                    .load(MovieBean.BASE_URL + data.getString(COL_MOVIE_IMG_PATH))
                    .fit()
                    .into(img_thumb);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}
}
