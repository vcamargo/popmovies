package com.vcamargo.popmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by vinicius.camargo on 04/11/2016.
 */

public class MoviesContract {
    public static final String CONTENT_AUTHORITY = "com.vcamargo.popmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIE = "movies";
    public static final String PATH_VIDEO = "videos";
    public static final String PATH_REVIEW = "reviews";

    public static final class MovieEntry implements BaseColumns {
        public static final String TABLE_NAME = "movie";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_MOVIE_TITLE_SHORT = "title_short";
        public static final String COLUMN_MOVIE_DESCRIPTION = "description";
        public static final String COLUMN_MOVIE_RELEASE_DATE = "release_date";
        public static final String COLUMN_MOVIE_VOTE_AVG = "vote_avg";
        public static final String COLUMN_MOVIE_IMG_PATH = "img_path";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class VideoEntry implements BaseColumns {
        public static final String TABLE_NAME = "video";
        public static final String COLUMN_MOVIE_KEY = "movie_id";
        public static final String COLUMN_VIDEO_ID = "video_id";
        public static final String COLUMN_VIDEO_NAME = "video_name";
        public static final String COLUMN_VIDEO_URL = "video_url";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_VIDEO).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VIDEO;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VIDEO;

        public static Uri buildVideoUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class ReviewEntry implements BaseColumns {
        public static final String TABLE_NAME = "review";
        public static final String COLUMN_MOVIE_KEY = "movie_id";
        public static final String COLUMN_REVIEW_ID = "review_id";
        public static final String COLUMN_REVIEW_AUTHOR = "review_author";
        public static final String COLUMN_REVIEW_CONTENT = "review_content";
        public static final String COLUMN_REVIEW_URL = "review_url";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;

        public static Uri buildReviewUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

/**
 *                TABLES DEFINITION
 *
 * ===MOVIES===           ===VIDEOS===      ===REVIEWS===
 * ============           ============      =============
 * MOVIE_ID ------------->MOVIE_KEY -------->MOVIE_KEY
 * MOVIE_TITLE_SHORT      VIDEO_ID          REVIEW_ID
 * MOVIE_DESCRIPTION      VIDEO_NAME        REVIEW_AUTHOR
 * MOVIE_RELEASE_DATE     VIDEO_URL         REVIEW_CONTENT
 * MOVIE_VOTE_AVG                           REVIEW_URL
 * MOVIE_IMG_PATH
 *
 * **/
}
