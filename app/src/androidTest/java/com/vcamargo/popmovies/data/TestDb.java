package com.vcamargo.popmovies.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

/**
 * Created by vinicius.camargo on 07/11/2016.
 */
public class TestDb extends AndroidTestCase {
    public static final String LOG_TAG = TestDb.class.getSimpleName();

    void deleteTheDatabase() {
        mContext.deleteDatabase(DbHelper.DATABASE_NAME);
    }

    public void setUp() {
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable {

        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(MoviesContract.MovieEntry.TABLE_NAME);
        tableNameHashSet.add(MoviesContract.VideoEntry.TABLE_NAME);
        tableNameHashSet.add(MoviesContract.ReviewEntry.TABLE_NAME);

        mContext.deleteDatabase(DbHelper.DATABASE_NAME);
        SQLiteDatabase db = new DbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        do {
            tableNameHashSet.remove(c.getString(0));
        } while (c.moveToNext());

        assertTrue("Error: Your database was created without movie, video or review entry tables",
                tableNameHashSet.isEmpty());

        c = db.rawQuery("PRAGMA table_info(" + MoviesContract.MovieEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        final HashSet<String> movieColumnHashSet = new HashSet<String>();
        movieColumnHashSet.add(MoviesContract.MovieEntry.COLUMN_MOVIE_ID);
        movieColumnHashSet.add(MoviesContract.MovieEntry.COLUMN_MOVIE_TITLE_SHORT);
        movieColumnHashSet.add(MoviesContract.MovieEntry.COLUMN_MOVIE_DESCRIPTION);
        movieColumnHashSet.add(MoviesContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE);
        movieColumnHashSet.add(MoviesContract.MovieEntry.COLUMN_MOVIE_VOTE_AVG);
        movieColumnHashSet.add(MoviesContract.MovieEntry.COLUMN_MOVIE_IMG_PATH);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            movieColumnHashSet.remove(columnName);
        } while (c.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required entry columns",
                movieColumnHashSet.isEmpty());
        db.close();
    }

    public void testMoviesTable() {
        insertMovie();
    }

    public long insertMovie() {
        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtil.createDummyMoviesValues();

        long movieRowId;
        movieRowId = db.insert(MoviesContract.MovieEntry.TABLE_NAME, null, testValues);

        assertTrue(movieRowId != -1);
        Cursor cursor = db.query(
                MoviesContract.MovieEntry.TABLE_NAME,
                null, 
                null, 
                null, 
                null, 
                null, 
                null 
        );

        assertTrue( "Error: No Records returned from location query", cursor.moveToFirst() );

        TestUtil.validateCurrentRecord("Error: Location Query Validation Failed",
                cursor, testValues);

        assertFalse( "Error: More than one record returned from location query",
                cursor.moveToNext() );

        cursor.close();
        db.close();
        return movieRowId;
    }
}