package com.vcamargo.popmovies.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.vcamargo.popmovies.R;
import com.vcamargo.popmovies.bean.MovieBean;
import com.vcamargo.popmovies.fragment.MoviesGridFragment;

/**
 * Created by vinicius.camargo on 26/10/2016.
 */

public class MoviesAdapter extends CursorAdapter {

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = LayoutInflater.from(mContext)
                .inflate(R.layout.grid_item_movie, parent, false);
        MoviesAdapter.ViewHolder vh = new MoviesAdapter.ViewHolder(v);
        v.setTag(vh);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        String imageUrl = cursor.getString(MoviesGridFragment.COL_MOVIE_IMG_PATH);
        imageUrl = MovieBean.BASE_URL + imageUrl;

        Picasso
                .with(mContext)
                .load(imageUrl)
                .into(viewHolder.imageView);
    }

    public static class ViewHolder  {
        public ImageView imageView;
        public ViewHolder(View itemView) {
            imageView = (ImageView) itemView.findViewById(R.id.grid_movie_image);
        }

    }

    public MoviesAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }
}


