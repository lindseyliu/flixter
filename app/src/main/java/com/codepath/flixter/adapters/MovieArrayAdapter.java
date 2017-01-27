package com.codepath.flixter.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.flixter.MovieDetailActivity;
import com.codepath.flixter.R;
import com.codepath.flixter.models.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lindseyl on 1/23/17.
 */

public class MovieArrayAdapter extends ArrayAdapter<Movie> {
    private Boolean isPortrait;

    private static final int TYPE_HIGH_RATE = 0;
    private static final int TYPE_LOW_RATE = 1;

    public MovieArrayAdapter(Context context, List<Movie> movies) {
        super(context, 0, movies);
    }

    @Override
    public int getItemViewType (int position) {
        return getItem(position).getRate() > 5 ? TYPE_HIGH_RATE : TYPE_LOW_RATE;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Movie movie = getItem(position);
        int type = getItemViewType(position);
        switch (type) {
            case TYPE_HIGH_RATE:
                MovieBackDropViewHolder backDropVH;
                if (convertView == null) {
                    convertView = getInflatedLayoutForType(type);
                    backDropVH = new MovieBackDropViewHolder(convertView);
                    convertView.setTag(R.id.view_holder, backDropVH);
                } else {
                    backDropVH = (MovieBackDropViewHolder) convertView.getTag(R.id.view_holder);
                }

                backDropVH.image.setTag(R.id.position, position);
                Picasso.with(getContext()).load(movie.getBackdropPath())
                        .fit().centerInside()
                        .placeholder(R.drawable.poster_placeholder).into(backDropVH.image);
                break;
            case TYPE_LOW_RATE:
                // view lookup cache
                MovieViewHolder movieVH;
                // Check if an existing view is being reused, otherwise inflate the view
                if (convertView == null) {
                    convertView = getInflatedLayoutForType(type);
                    movieVH = new MovieViewHolder(convertView);
                    convertView.setTag(R.id.view_holder, movieVH);
                } else {
                    movieVH = (MovieViewHolder) convertView.getTag(R.id.view_holder);
                }
                // Populate the data into the template view using the data object
                movieVH.title.setText(movie.getOriginalTitle());
                movieVH.overview.setText(movie.getOverview());
                movieVH.image.setTag(R.id.position, position);
                if (isPortrait) {
                    Picasso.with(getContext()).load(movie.getPosterPath()).resize(300, 0)
                            .onlyScaleDown()
                            .placeholder(R.drawable.poster_placeholder).into(movieVH.image);
                } else {
                    Picasso.with(getContext()).load(movie.getBackdropPath()).resize(500, 0)
                            .onlyScaleDown()
                            .placeholder(R.drawable.poster_placeholder).into(movieVH.image);
                }
                break;
            default:
                break;
        }
        return convertView;
    }

    private View getInflatedLayoutForType(int type) {
        switch (type) {
            case TYPE_HIGH_RATE:
                return LayoutInflater
                        .from(getContext()).inflate(R.layout.item_movie_high_rate, null);
            case TYPE_LOW_RATE:
                return LayoutInflater
                        .from(getContext()).inflate(R.layout.item_movie, null);
            default:
                return null;
        }
    }

    class MovieViewHolder {
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.overview)
        TextView overview;
        @BindView(R.id.image)
        ImageView image;

        public MovieViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        @OnClick(R.id.image)
        void goToDetail(View view) {
            Intent i = new Intent(getContext(), MovieDetailActivity.class);
            Movie movieItem = getItem((Integer)view.getTag(R.id.position));
            Bundle bundle = new Bundle();
            bundle.putString("original_title", movieItem.getOriginalTitle());
            bundle.putString("overview", movieItem.getOverview());
            bundle.putString("release_date", movieItem.getReleaseDate());
            bundle.putDouble("rate", movieItem.getRate());
            bundle.putString("backdrop_path", movieItem.getBackdropPath());
            i.putExtras(bundle);

            getContext().startActivity(i);
        }
    }

    class MovieBackDropViewHolder {
        @BindView(R.id.image)
        ImageView image;

        public MovieBackDropViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        @OnClick(R.id.image)
        void goToDetail(View view) {
            Intent i = new Intent(getContext(), MovieDetailActivity.class);
            Movie movieItem = getItem((Integer)view.getTag(R.id.position));
            Bundle bundle = new Bundle();
            bundle.putString("original_title", movieItem.getOriginalTitle());
            bundle.putString("overview", movieItem.getOverview());
            bundle.putString("release_date", movieItem.getReleaseDate());
            bundle.putDouble("rate", movieItem.getRate());
            bundle.putString("backdrop_path", movieItem.getBackdropPath());
            i.putExtras(bundle);

            getContext().startActivity(i);
        }
    }



    public Boolean getPortrait() {
        return isPortrait;
    }

    public void setPortrait(Boolean portrait) {
        isPortrait = portrait;
    }
}
