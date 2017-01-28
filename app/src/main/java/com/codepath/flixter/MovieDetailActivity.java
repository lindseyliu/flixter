package com.codepath.flixter;

import android.os.Bundle;
import android.util.Log;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class MovieDetailActivity extends YouTubeBaseActivity {

    private String originalTitle;
    private String overview;
    private float rate;
    private String releaseDate;
    private String posterPath; //partial url
    private String backdropPath; //partial url
    private String YOUTUBE_API_KEY = "AIzaSyBTlQO_oUbBTCHVuDoSoiFPF1RfTmReqig";
    private int id;
    private String trailer_key;
    String MOVIE_DB_API_URL =
            "https://api.themoviedb.org/3/movie/now_playing?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";
    AsyncHttpClient client = new AsyncHttpClient();

    @BindView(R.id.trailer)
    YouTubePlayerView trailerV;
    @BindView(R.id.title)
    TextView titleTv;
    @BindView(R.id.overview)
    TextView overviewTv;
    @BindView(R.id.release_date)
    TextView releaseDateTv;
    @BindView(R.id.rating)
    RatingBar ratingB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        originalTitle = bundle.getString(String.valueOf(R.string.title_key));
        overview = bundle.getString(String.valueOf(R.string.overview_key));
        releaseDate = bundle.getString(String.valueOf(R.string.release_date_key));
        rate = bundle.getFloat(String.valueOf(R.string.rate_key));
        backdropPath = bundle.getString(String.valueOf(R.string.backdrop_key));
        id = bundle.getInt("id");

        titleTv.setText(originalTitle);
        overviewTv.setText(overview);
        releaseDateTv.setText(getResources().getString(R.string.release_date_prefix) + releaseDate);
        ratingB.setRating(rate/2);

        fetchTrailerAsync(MOVIE_DB_API_URL, client);

        trailerV.initialize(YOUTUBE_API_KEY,
                new YouTubePlayer.OnInitializedListener() {
                    @Override public void onInitializationSuccess(YouTubePlayer.Provider provider,
                            YouTubePlayer youTubePlayer, boolean b) {
                        youTubePlayer.cueVideo(trailer_key);
                    }

                    @Override public void onInitializationFailure(YouTubePlayer.Provider provider,
                            YouTubeInitializationResult youTubeInitializationResult) {

                    }
                });
    }

    private void fetchTrailerAsync(String url, AsyncHttpClient client) {
        client.get(url, new JsonHttpResponseHandler() {
            @Override public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    //try to do json processing in worker thread
                    JSONArray trailerJsonResults = response.getJSONArray("results");
                    trailer_key = trailerJsonResults.getJSONObject(0).getString("key");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                    JSONObject errorResponse) {
                Log.d("DEBUG", "Fetch feed error: " + errorResponse.toString());
            }
        });
    }
}
