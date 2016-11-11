package com.vcamargo.popmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.vcamargo.popmovies.data.MoviesContract.MovieEntry;
import com.vcamargo.popmovies.data.MoviesContract.VideoEntry;
import com.vcamargo.popmovies.data.MoviesContract.ReviewEntry;
/**
 * Created by vinicius.camargo on 07/11/2016.
 */

public class DbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "movies.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY," +
                MovieEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL," +
                MovieEntry.COLUMN_MOVIE_TITLE_SHORT + " TEXT," +
                MovieEntry.COLUMN_MOVIE_DESCRIPTION + " TEXT, " +
                MovieEntry.COLUMN_MOVIE_RELEASE_DATE + " TEXT, " +
                MovieEntry.COLUMN_MOVIE_VOTE_AVG + " REAL, " +
                MovieEntry.COLUMN_MOVIE_IMG_PATH + " TEXT, " +
                MovieEntry.COLUMN_MOVIE_LIST_TYPE + " TEXT, " +
                MovieEntry.COLUMN_MOVIE_IS_FAVORITE + " BOOLEAN, " +
                MovieEntry.COLUMN_MOVIE_DURATION + " TEXT " +
                " );";

        final String SQL_CREATE_VIDEOS_TABLE = "CREATE TABLE " + VideoEntry.TABLE_NAME + " (" +
                VideoEntry._ID + " INTEGER PRIMARY KEY," +
                VideoEntry.COLUMN_VIDEO_ID + " TEXT NOT NULL," +
                VideoEntry.COLUMN_MOVIE_KEY + " INTEGER," +
                VideoEntry.COLUMN_VIDEO_NAME + " TEXT, " +
                VideoEntry.COLUMN_VIDEO_URL + " TEXT, " +

                " FOREIGN KEY (" + VideoEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + ") " +
                " );";

        final String SQL_CREATE_REVIEWS_TABLE = "CREATE TABLE " + ReviewEntry.TABLE_NAME + " (" +
                ReviewEntry._ID + " INTEGER PRIMARY KEY," +
                ReviewEntry.COLUMN_REVIEW_ID + " TEXT NOT NULL," +
                ReviewEntry.COLUMN_MOVIE_KEY + " INTEGER," +
                ReviewEntry.COLUMN_REVIEW_AUTHOR + " TEXT, " +
                ReviewEntry.COLUMN_REVIEW_CONTENT + " TEXT, " +
                ReviewEntry.COLUMN_REVIEW_URL + " TEXT, " +

                " FOREIGN KEY (" + ReviewEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + ") " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_VIDEOS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEWS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ReviewEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + VideoEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
