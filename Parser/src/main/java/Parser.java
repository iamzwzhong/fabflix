import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

public class Parser{

    public Parser() throws IOException{
    }

    public static void main(String[] args) throws IOException, SQLException {
        try {
            File f = new File("report.txt");
            if (f.createNewFile()) {
                System.out.println("File created successfully");
            }
             else {
                 System.out.println("File already exists");
                 PrintWriter writer = new PrintWriter(f);
                 writer.print("");
                 writer.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        MovieParser m = new MovieParser();
        m.runParser();
        System.out.println("movie parsed");
        ActorParser a = new ActorParser();
        a.runParser();
        System.out.println("actor parsed");
        CastParser c = new CastParser();
        c.runParser();
        System.out.println("cast parsed");
    }

}
