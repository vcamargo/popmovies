package com.vcamargo.popmovies.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.vcamargo.popmovies.R;

/**
 * Created by vinicius.camargo on 11/11/2016.
 */

public class Utils {
    public static String getListType(Context mContext) {
        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(mContext);
        return sharedPrefs.getString(
                mContext.getString(R.string.pref_sort_key),
                mContext.getString(R.string.pref_sort_mostpopular));
    }
}
