package com.vcamargo.popmovies.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by vinicius.camargo on 25/10/2016.
 */

public class MovieBean implements Parcelable {
    public static final String BASE_URL = "http://image.tmdb.org/t/p/w185/";
    public static final String INTENT_EXTRA_KEY = "MovieBean";
    private static final String AVERAGE_VOTE_SUFFIX = "/10";
    private String imgPosterId; //poster_path
    private String imgThumbId; //backdrop_path
    private String originalTitle; //original_title
    private String overview; //overview
    private String voteAverage; //vote_average
    private String releaseDate; //release_date


    public static final Creator<MovieBean> CREATOR = new Creator<MovieBean>() {
        @Override
        public MovieBean createFromParcel(Parcel in) {
            return new MovieBean(in);
        }

        @Override
        public MovieBean[] newArray(int size) {
            return new MovieBean[size];
        }
    };

    public MovieBean(String imgPosterId, String imgThumbId, String originalTitle, String overview, String voteAverage, String releaseDate) {
        this.imgPosterId = imgPosterId;
        this.imgThumbId = imgThumbId;
        this.originalTitle = originalTitle;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
    }

    public MovieBean() {
        this.imgPosterId = "";
        this.imgThumbId = "";
        this.originalTitle = "";
        this.overview = "";
        this.voteAverage = "";
        this.releaseDate = "";
    }

    public String getFormattedReleaseYear() {
        return getReleaseDate().substring(0,getReleaseDate().indexOf('-'));
    }

    public String getFormattedVoteAverage() {
        return getVoteAverage() + AVERAGE_VOTE_SUFFIX;
    }

    public String getImgPosterId() {
        return imgPosterId;
    }

    public String getFullURLgetImgPosterId() {
        return BASE_URL + imgPosterId;
    }

    public void setImgPosterId(String imgPosterId) {
        this.imgPosterId = imgPosterId;
    }

    public String getImgThumbId() {
        return BASE_URL + imgThumbId;
    }

    public void setImgThumbId(String imgThumbId) {
        this.imgThumbId = imgThumbId;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(getImgPosterId());
        parcel.writeString(getImgThumbId());
        parcel.writeString(getOriginalTitle());
        parcel.writeString(getOverview());
        parcel.writeString(getVoteAverage());
        parcel.writeString(getReleaseDate());
    }

    private MovieBean(Parcel in) {
        setImgPosterId(in.readString());
        setImgThumbId(in.readString());
        setOriginalTitle(in.readString());
        setOverview(in.readString());
        setVoteAverage(in.readString());
        setReleaseDate(in.readString());
    }
}
