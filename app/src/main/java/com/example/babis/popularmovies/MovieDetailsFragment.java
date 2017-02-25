package com.example.babis.popularmovies;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieDetailsFragment extends Fragment {


    TextView ratings, overviews, titles, dates;
    ImageView posters;

    public MovieDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);
        ratings = (TextView) rootView.findViewById(R.id.rating_text_view);
        overviews = (TextView) rootView.findViewById(R.id.overview_text_view);
        dates = (TextView) rootView.findViewById(R.id.release_dates_text_view);
        titles = (TextView) rootView.findViewById(R.id.title_text_view);
        posters = (ImageView) rootView.findViewById(R.id.poster_image_view);

        Intent intent = getActivity().getIntent();

        if (intent != null) {
            if (intent.hasExtra("title")) {
                titles.setText(intent.getStringExtra("title"));
            }
            if (intent.hasExtra("date")) {
                dates.setText(intent.getStringExtra("date"));
            }
            if (intent.hasExtra("rating")) {
                ratings.setText(intent.getStringExtra("rating"));
            }
            if (intent.hasExtra("overview")) {
                overviews.setText(intent.getStringExtra("overview"));
            }
            if (intent.hasExtra("poster")) {
                Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w500/" + intent.getStringExtra("poster")).placeholder(R.drawable.unavaliable).into(posters);
            }
        }


        return rootView;
    }
}