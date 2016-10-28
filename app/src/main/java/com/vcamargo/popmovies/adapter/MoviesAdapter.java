package com.vcamargo.popmovies.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.vcamargo.popmovies.R;
import com.vcamargo.popmovies.bean.MovieBean;

import java.util.List;

/**
 * Created by vinicius.camargo on 26/10/2016.
 */

public class MoviesAdapter extends ArrayAdapter<MovieBean> {
    private Activity mContext;
    public MoviesAdapter(Activity mContext, List<MovieBean> movieBeanList) {
        super(mContext, 0, movieBeanList);

        this.mContext = mContext;
    }
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MovieBean movieBean = getItem(position);
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_movie, parent, false);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.grid_movie_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Picasso
                .with(mContext)
                .load(movieBean.getFullURLgetImgPosterId())
                .into(holder.imageView);
        return convertView;
    }

    static class ViewHolder {
        ImageView imageView;
    }
}


