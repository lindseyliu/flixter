package com.codepath.flixter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailActivity extends AppCompatActivity {

    private String originalTitle;
    private String overview;
    private Double rate;
    private String releaseDate;
    private String posterPath; //partial url
    private String backdropPath; //partial url

    @BindView(R.id.image)
    ImageView posterIv;
    @BindView(R.id.title)
    TextView titleTv;
    @BindView(R.id.overview)
    TextView overviewTv;
    @BindView(R.id.release_date)
    TextView releaseDateTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        originalTitle = bundle.getString(getResources().getString(R.string.title_key));
        overview = bundle.getString(getResources().getString(R.string.overview_key));
        releaseDate = bundle.getString(getResources().getString(R.string.release_date_key));
        rate = bundle.getDouble(getResources().getString(R.string.rate_key));
        backdropPath = bundle.getString(getResources().getString(R.string.backdrop_key));

        titleTv.setText(originalTitle);
        overviewTv.setText(overview);
        releaseDateTv.setText(releaseDate);
        Picasso.with(this).load(backdropPath)
                .fit().centerInside()
                .placeholder(R.drawable.poster_placeholder).into(posterIv);

    }
}
