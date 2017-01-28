package com.codepath.flixter;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.codepath.flixter.adapters.MovieArrayAdapter;
import com.codepath.flixter.models.Movie;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class MovieListActivity extends AppCompatActivity {

    @BindView(R.id.lvMovies)
    ListView lvItem;
    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;

    ArrayList<Movie> movies;
    MovieArrayAdapter movieAdapter;

    String MOVIE_DB_API_URL =
            "https://api.themoviedb.org/3/movie/now_playing?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";
    AsyncHttpClient client = new AsyncHttpClient();

    private static final String LIST_STATE = "listState";
    private Parcelable mListState = null;

    // Write list state to bundle
    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        mListState = lvItem.onSaveInstanceState();
        state.putParcelable(LIST_STATE, mListState);
    }

    // Restore list state from bundle
    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        mListState = state.getParcelable(LIST_STATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchFeedAsync(MOVIE_DB_API_URL, client); // make sure data has been reloaded into adapter first
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        ButterKnife.bind(this);

        movies = new ArrayList<>();
        movieAdapter = new MovieArrayAdapter(this, movies);
        lvItem.setAdapter(movieAdapter);

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            movieAdapter.setPortrait(true);
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            movieAdapter.setPortrait(false);
        }

        fetchFeedAsync(MOVIE_DB_API_URL, client);

        swipeContainer.setColorSchemeResources(R.color.color_accent);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {
                fetchFeedAsync(MOVIE_DB_API_URL, client);
                swipeContainer.setRefreshing(false);
            }
        });
    }

    @Override public void onConfigurationChanged(Configuration newConfig) {
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            movieAdapter.setPortrait(true);
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            movieAdapter.setPortrait(false);
        }
        fetchFeedAsync(MOVIE_DB_API_URL, client);
    }

    private void fetchFeedAsync(String url, AsyncHttpClient client) {
        client.get(url, new JsonHttpResponseHandler() {
            @Override public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    //try to do json processing in worker thread
                    movieAdapter.clear();
                    JSONArray movieJsonResults = response.getJSONArray("results");
                    movies.addAll(Movie.fromJsonArray(movieJsonResults));
                    movieAdapter.notifyDataSetChanged();
                    if (mListState != null) {
                        lvItem.onRestoreInstanceState(mListState);
                        mListState = null;
                    }
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
