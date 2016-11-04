package com.vcamargo.popmovies.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.vcamargo.popmovies.R;
import com.vcamargo.popmovies.bean.MovieBean;

import java.util.List;

/**
 * Created by vinicius.camargo on 26/10/2016.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {
    private Activity mContext;
    private List<MovieBean> movieBeanList;
    private MovieBean movieBean;

    @Override
    public MoviesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext)
                .inflate(R.layout.grid_item_movie, parent, false);
        MoviesAdapter.ViewHolder vh = new MoviesAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        movieBean = getItem(position);
        Picasso
                .with(mContext)
                .load(movieBean.getFullURLgetImgPosterId())
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return movieBeanList.size();
    }

    public MovieBean getItem(int position) {
        return movieBeanList.get(position);
    }

    public void addAll(List<MovieBean> items){
        movieBeanList.addAll(items);
    }

    public void clear(){
        movieBeanList.clear();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public ImageView imageView;
        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            imageView = (ImageView) itemView.findViewById(R.id.grid_movie_image);
        }

        @Override
        public void onClick(View view) {

        }
    }

    public MoviesAdapter(Activity mContext, List<MovieBean> movieBeanList) {
        this.mContext = mContext;
        this.movieBeanList = movieBeanList;
    }
}


