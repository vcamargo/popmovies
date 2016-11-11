package com.vcamargo.popmovies.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vcamargo.popmovies.R;
import com.vcamargo.popmovies.fragment.MovieDetailsFragment;

/**
 * Created by vinicius.camargo on 10/11/2016.
 */

public class TrailerAdapter extends CursorAdapter {
    public TrailerAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = LayoutInflater.from(mContext)
                .inflate(R.layout.trailer_list_item, parent, false);
        TrailerAdapter.ViewHolder vh = new TrailerAdapter.ViewHolder(v);
        v.setTag(vh);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TrailerAdapter.ViewHolder viewHolder = (TrailerAdapter.ViewHolder) view.getTag();
        String trailerName = cursor.getString(MovieDetailsFragment.COL_VIDEO_NAME);

    }

    public static class ViewHolder  {
        public TextView trailerName;
        public ViewHolder(View itemView) {
            trailerName = (TextView) itemView.findViewById(R.id.trailer_name);
        }

    }
}
