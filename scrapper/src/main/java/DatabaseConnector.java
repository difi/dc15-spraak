import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 * Created by camp-mli on 18.06.2015.
 */
public class DatabaseConnector {


    private PrintWriter db;
    private static DatabaseConnector instance;

    private DatabaseConnector(String name) throws FileNotFoundException, UnsupportedEncodingException {
        this.db = new PrintWriter(name, "UTF-8");
    }

    public void write(String msg){
        this.db.println(msg);
    }

    public void close(){
        this.db.close();
    }

    public static DatabaseConnector getInstance(String name){
        if(instance == null)
            try {
                instance = new DatabaseConnector(name);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        return instance;
    }

}
