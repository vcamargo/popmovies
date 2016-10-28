package com.vcamargo.popmovies.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vcamargo.popmovies.R;
import com.vcamargo.popmovies.bean.MovieBean;


public class MovieDetailsFragment extends Fragment {
    MovieBean movieBean = null;
    public MovieDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_movie_details, container, false);

        Intent intent = getActivity().getIntent();
        if (intent != null) {
            movieBean = (MovieBean) intent.getSerializableExtra(MovieBean.INTENT_EXTRA_KEY);
        }
        if (movieBean != null) {
            ((TextView)rootView.findViewById(R.id.original_title)).setText(movieBean.getOriginalTitle());
            ((TextView)rootView.findViewById(R.id.movie_synopsis)).setText(movieBean.getOverview());
            ((TextView)rootView.findViewById(R.id.user_rating)).setText(movieBean.getFormattedVoteAverage());
            ((TextView)rootView.findViewById(R.id.release_date)).setText(movieBean.getFormattedReleaseYear());
            Picasso
                    .with(getActivity())
                    .load(movieBean.getImgPosterId())
                    .fit()
                    .into(((ImageView)rootView.findViewById(R.id.img_thumb)));
        }
        return rootView;
    }

}
