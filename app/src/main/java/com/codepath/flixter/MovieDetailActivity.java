package com.codepath.flixter;

import android.os.Bundle;
import android.util.Log;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
    String MOVIE_DB_API_URL = "https://api.themoviedb.org/3/movie/";
    String TRAILER_URL_SUFFIX = "/trailers?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";
    String FULL_API_URL;
    private OkHttpClient client;

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
        FULL_API_URL = MOVIE_DB_API_URL + Integer.toString(id) + TRAILER_URL_SUFFIX;

        titleTv.setText(originalTitle);
        overviewTv.setText(overview);
        releaseDateTv.setText(getResources().getString(R.string.release_date_prefix) + releaseDate);
        ratingB.setRating(rate/2);

        client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(FULL_API_URL)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                // ... check for failure using `isSuccessful` before proceeding

                // Read data on the worker thread
                final String responseData = response.body().string();
                try {
                    JSONObject json = new JSONObject(responseData);
                    JSONArray trailerJsonResults = json.getJSONArray("youtube");
                    trailer_key = trailerJsonResults.getJSONObject(0).getString("source");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (trailer_key != null) {
                    // Run view-related code back on the main thread
                    MovieDetailActivity.this.runOnUiThread((Runnable) () -> {
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
                    });

                }

            }
        });
    }

    private void fetchTrailerAsync(String url, SyncHttpClient client) {
        client.get(url, new JsonHttpResponseHandler() {
            @Override public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    //try to do json processing in worker thread
                    JSONArray trailerJsonResults = response.getJSONArray("results");
                    trailer_key = trailerJsonResults.getJSONObject(0).getString("key");
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
