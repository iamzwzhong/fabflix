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

public class ListViewActivity extends Activity {

    private EditText search;
    private Button findButton;
    private Button prevButton;
    private Button nextButton;
    private String url;
    private ArrayList<Movie> movies;
    private ArrayList<Movie> display_movies;
    private MovieListViewAdapter adapter;
    private int Page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);
        search = findViewById(R.id.search);
        findButton = findViewById(R.id.find);
        prevButton = findViewById(R.id.prev);
        nextButton = findViewById(R.id.next);
        //this should be retrieved from the database and the backend server
        movies = new ArrayList<>();
        display_movies = new ArrayList<>();

        adapter = new MovieListViewAdapter(display_movies, this);

        ListView listView = findViewById(R.id.list);
        listView.setAdapter(adapter);

        //url = "https://10.0.2.2:8443/cs122b_spring20_project_war/";

        url = "https://3.15.185.148:8443/cs122b-spring20-project/";

        findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Page = 0;
                find();
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Page != 0) {
                    Page -= 1;
                }
                display();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Page += 1;
                display();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = movies.get(position);
                String message = String.format("Clicked on position: %d, name: %s, %d", position, movie.getName(), movie.getYear());
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                toMovie(movie.getId());
            }
        });
    }

    public void display() {
        int count = 0;
        for (int i = Page * 20; i < (Page * 20) + 20; i++) {
            if(movies.size() > i) {
                if(display_movies.size()  <= count) {
                    display_movies.add(movies.get(i));
                }
                else {
                    display_movies.set(count, movies.get(i));
                }
                adapter.notifyDataSetChanged();
            }
            else {
                if(display_movies.size() > count) {
                    display_movies.remove(count);
                }
                adapter.notifyDataSetChanged();
            }
            count += 1;
        }
    }

    public void toMovie(String Id) {

        // Use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        //request type is GET
        final StringRequest findRequest = new StringRequest(Request.Method.GET, url + "api/single-movie?id=" + Id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Intent moviePage = new Intent(ListViewActivity.this, SingleMovieActivity.class);
                moviePage.putExtra("movieData", response);
                //without starting the activity/page, nothing would happen
                startActivity(moviePage);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("toMovie.error", error.toString());
                    }
                });

        // !important: queue.add is where the login request is actually sent
        queue.add(findRequest);

    }

    public void find() {

        // Use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        //request type is GET
        String param = search.getText().toString();
        final StringRequest findRequest = new StringRequest(Request.Method.GET, url + "api/mainsearch?main=" + param, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray jsonarray;
                try {
                    jsonarray = new JSONArray(response);
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject jsonobject = jsonarray.getJSONObject(i);
                        String id = jsonobject.getString("movies_id");
                        String title = jsonobject.getString("movies_title");
                        int year = jsonobject.getInt("movies_year");
                        String director = jsonobject.getString("movies_director");
                        String genres = jsonobject.getString("movies_genres");
                        String stars = jsonobject.getString("movies_actors");
                        movies.add(new Movie(id, title, (short) year, director, genres, stars));
                        if(i < 20) {
                            display_movies.add(movies.get(i));
                            adapter.notifyDataSetChanged();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("find.error", error.toString());
                    }
                });

        // !important: queue.add is where the login request is actually sent
        queue.add(findRequest);

    }
}