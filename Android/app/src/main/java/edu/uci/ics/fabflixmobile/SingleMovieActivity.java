package edu.uci.ics.fabflixmobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SingleMovieActivity extends Activity {

    private String mData;
    private ArrayList<Star> stars = new ArrayList<>();
    private SingleMovieListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_movie);

        Bundle extras = getIntent().getExtras();
        mData = extras.getString("movieData");

        adapter = new SingleMovieListViewAdapter(stars, this);
        ListView listView = findViewById(R.id.list);
        listView.setAdapter(adapter);
        System.out.println(mData);

        JSONArray jsonarray;
        try {
            jsonarray = new JSONArray(mData);
            String title = "", release = "", director = "", rating = "", genres = "";
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);
                String name = jsonobject.getString("star_name");
                Object year = jsonobject.get("star_dob");
                stars.add(new Star(name, (String) year));
                adapter.notifyDataSetChanged();
                if(i == 0) {
                    title = jsonobject.getString("movie_title");
                    release = jsonobject.getString("movie_year");
                    director = jsonobject.getString("movie_director");
                    rating = (String) jsonobject.get("movie_rating");
                    genres = jsonobject.getString("movie_genre");
                }
            }
            final TextView titleTextView = findViewById(R.id.title);
            titleTextView.setText(title);
            final TextView yearTextView = findViewById(R.id.year);
            yearTextView.setText("Released: " + release);
            final TextView directorTextView = findViewById(R.id.director);
            directorTextView.setText("Director: " + director);
            final TextView ratingTextView = findViewById(R.id.rating);
            ratingTextView.setText("Rating: " + rating);
            final TextView genresTextView = findViewById(R.id.genres);
            genresTextView.setText("Genres: " + genres);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}