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

public class SingleMovieListViewAdapter extends ArrayAdapter<Star> {
    private ArrayList<Star> stars;

    public SingleMovieListViewAdapter(ArrayList<Star> stars, Context context) {
        super(context, R.layout.row_movie, stars);
        this.stars = stars;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.row_movie, parent, false);

        Star star = stars.get(position);

        TextView titleView = view.findViewById(R.id.name);
        TextView yearView = view.findViewById(R.id.year);


        titleView.setText("Star:" + star.getName());
        yearView.setText("Year:" + star.getYear() + "");// need to cast the year to a string to set the label

        return view;
    }
}