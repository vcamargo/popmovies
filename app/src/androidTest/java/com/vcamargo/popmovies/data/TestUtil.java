package com.vcamargo.popmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

/**
 * Created by vinicius.camargo on 07/11/2016.
 */

public class TestUtil extends AndroidTestCase {

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    static ContentValues createDummyMoviesValues() {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_ID, 429);
        testValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_TITLE_SHORT, "The Good, the Bad and the Ugly");
        testValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_DESCRIPTION, "While the Civil War rages between the Union" +
                " and the Confederacy, three men – a quiet loner, a ruthless hit man and a Mexican bandit – comb" +
                " the American Southwest in search of a strongbox containing $200,000 in stolen gold.");
        testValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE, "1966-12-23");
        testValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_VOTE_AVG, 7.82);
        testValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_IMG_PATH, "/xGC2fY5KFmtuXnsuQwYQKFOLZFy.jpg");

        return testValues;
    }

    static ContentValues createVideoValues(long locationRowId) {
        ContentValues weatherValues = new ContentValues();
        weatherValues.put(MoviesContract.VideoEntry.COLUMN_MOVIE_KEY, locationRowId);
        weatherValues.put(MoviesContract.VideoEntry.COLUMN_VIDEO_ID, 12345);
        weatherValues.put(MoviesContract.VideoEntry.COLUMN_VIDEO_NAME, "Trailer 1");
        weatherValues.put(MoviesContract.VideoEntry.COLUMN_VIDEO_URL, "AxWlpeI9KRk");

        return weatherValues;
    }

    static ContentValues createReviewValues(long locationRowId) {
        ContentValues weatherValues = new ContentValues();
        weatherValues.put(MoviesContract.ReviewEntry.COLUMN_MOVIE_KEY, locationRowId);
        weatherValues.put(MoviesContract.ReviewEntry.COLUMN_REVIEW_ID, 12345);
        weatherValues.put(MoviesContract.ReviewEntry.COLUMN_REVIEW_AUTHOR, "John Chard");
        weatherValues.put(MoviesContract.ReviewEntry.COLUMN_REVIEW_CONTENT, "I'm looking for the owner of that horse." +
                " He's tall, blonde, he smokes a cigar, and he's a pig!\\r\\n\\r\\nIt's debatable of course, since there" +
                " are legions of fans of the first two films in Sergio Leone's Dollars Trology, but with each film there not" +
                " only came a longer running time, but also a rise in quality - debatable of course!\\r\\n\\r\\nHere for the third" +
                " and final part of the trilogy, Leone adds Eli Wallach to the established pairing of Lee Van Cleef and Clint Eastwood, " +
                "and brings all his tools of the trade to the party. Plot is slight, the three principals are on a collision course to find some " +
                "buried gold, with each man having varying degrees of scuzziness, so how will it pan out?\\r\\n\\r\\nSuch is the genius of the narrative, " +
                "it's a fascinating journey to undertake. The characterisations are ripe and considered, the various traits and peccadilloes beautifully enhanced," +
                " and with Leone being Leone, there's no shortage of cruelty and humour. He also brings his style, the close ups, long shots and some outstanding framing " +
                "of characters in various situations.\\r\\n\\r\\nThe story encompasses The Civil War, which pitches our leads into \\\"The Battle of Branston Bridge\\\", where" +
                " here we get to see just how great Leone was at constructing full on battle sequences. It's exciting, thrilling and literally dynamite, whilst Aldo Giuffrè as " +
                "Captain Clinton turns in some memorable support.\\r\\n\\r\\nThe Euro locations pass muster as the Wild West, superbly photographed by Tonino Delli Colli, and then of " +
                "course there is Ennio Morricone's musical compositions. It's a score that has become as iconic as Eastwood's Man With No Name, a part of pop culture for ever more." +
                " It mocks the characters at times, energises them at others, whilst always us the audience are aurally gripped.\\r\\n\\r\\nThere's obviously some daft coincidences, " +
                "this is after all pasta world, and the near three hour run time could be construed as indulgent. But here's the thing, those who love The Good, The Bad and the Ugly " +
                "could quite easily stand for another hour of Leone's classic. I mean, more barbed dialogue, brutal violence and fun! Great, surely!\\r\\n\\r\\nFrom the sublime arcade game" +
                " like opening credit sequences, to the legendary cemetery stand-off at the finale, this is a Western deserving of the high standing it is held. 9/10");
        weatherValues.put(MoviesContract.ReviewEntry.COLUMN_REVIEW_URL, "https://www.themoviedb.org/review/572f36d39251413c27000d3f");

        return weatherValues;
    }

    static long insertDummyMoviesValue(Context context) {
        // insert our test records into the database
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtil.createDummyMoviesValues();

        long locationRowId;
        locationRowId = db.insert(MoviesContract.MovieEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert dummy values", locationRowId != -1);

        return locationRowId;
    }


}
