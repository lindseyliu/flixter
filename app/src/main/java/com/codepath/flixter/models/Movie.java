package com.codepath.flixter.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lindseyl on 1/23/17.
 */

public class Movie {

    private String originalTitle;
    private String overview;
    private Double rate;
    private String releaseDate;
    private String posterPath; //partial url
    private String backdropPath; //partial url


    public Movie(JSONObject jsonObject) {
        try {
            this.posterPath = jsonObject.getString("poster_path");
            this.backdropPath = jsonObject.getString("backdrop_path");
            this.originalTitle = jsonObject.getString("original_title");
            this.overview = jsonObject.getString("overview");
            this.rate = jsonObject.getDouble("vote_average");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //...
    }

    public static ArrayList<Movie> fromJsonArray(JSONArray jsonArray) {
        ArrayList<Movie> results = new ArrayList<>();

        for(int i=0;i<jsonArray.length();i++) {
            try {
                results.add(new Movie(jsonArray.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return results;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPosterPath() {
        return String.format("https://image.tmdb.org/t/p/w342/%s", posterPath);
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getBackdropPath() {
        return String.format("https://image.tmdb.org/t/p/w342/%s", backdropPath);
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public double getRate() {
        return this.rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

}