
public class StarsInMovies {

    private String director;
    private String title;
    private String starName;

    public StarsInMovies() {
    }

    public StarsInMovies(String director, String title, String starName) {
        this.director = director;
        this.title = title;
        this.starName = starName;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStarName() {
        return starName;
    }

    public void setStarName(String starName) {
        this.starName = starName;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Movie Details - ");
        sb.append("Director:" + getDirector());
        sb.append(", ");
        sb.append("Title:" + getTitle());
        sb.append(", ");
        sb.append("Star:" + getStarName());
        sb.append(".");

        return sb.toString();
    }
}
