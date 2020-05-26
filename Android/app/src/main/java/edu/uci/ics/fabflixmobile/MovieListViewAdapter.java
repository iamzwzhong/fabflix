package edu.uci.ics.fabflixmobile;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class MovieListViewAdapter extends ArrayAdapter<Movie> {
    private ArrayList<Movie> movies;

    public MovieListViewAdapter(ArrayList<Movie> movies, Context context) {
        super(context, R.layout.row, movies);
        this.movies = movies;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.row, parent, false);

        Movie movie = movies.get(position);

        TextView titleView = view.findViewById(R.id.title);
        TextView yearView = view.findViewById(R.id.year);
        TextView directorView = view.findViewById(R.id.director);
        TextView genresView = view.findViewById(R.id.genres);
        TextView starsView = view.findViewById(R.id.stars);

        titleView.setText("Title:" + movie.getName());
        yearView.setText("Year:" + movie.getYear() + "");// need to cast the year to a string to set the label
        directorView.setText("Director:" + movie.getDirector());
        genresView.setText("Genres:" + movie.getGenres());
        starsView.setText("Stars:" + movie.getStars());

        return view;
    }
}