package edu.uci.ics.fabflixmobile;

public class Movie {
    private String id;
    private String name;
    private short year;
    private String director;
    private String genres;
    private String stars;

    public Movie(String id, String name, short year, String director, String genres, String stars) {
        this.id = id;
        this.name = name;
        this.year = year;
        this.director = director;
        this.genres = genres;
        this.stars = stars;
    }

    public String getId() { return id; }

    public String getName() { return name; }

    public short getYear() {
        return year;
    }

    public String getDirector() { return director; }

    public String getGenres() { return genres; }

    public String getStars() { return stars; }
}