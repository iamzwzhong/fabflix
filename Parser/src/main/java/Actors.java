
public class Actors {

    private String name;
    private int birthYear;

    public Actors() {
    }

    public Actors(String name, int birthYear) {
        this.name = name;
        this.birthYear = birthYear;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Actor Details - ");
        sb.append("Name:" + getName());
        sb.append(", ");
        sb.append("birthYear:" + getBirthYear());
        sb.append(".");

        return sb.toString();
    }
}
