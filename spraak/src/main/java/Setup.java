
import connectors.FileConnector;
import crawler.Scrapper;
import documentTextExtractor.TextExtractor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by camp-mli on 18.06.2015.
 */
public class Setup {

    private final ArrayList<Map> crawlerSettings;
    private final ArrayList<String> fileSettings;
    private final Map oAuthSettings;
    private HashMap<String, Thread> modules;

    public Setup(String filename){
        JSONParser parser = new JSONParser();

        Object obj = null;
        try {
            obj = parser.parse(new FileReader(filename));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = (JSONObject) obj;

        this.crawlerSettings = (ArrayList<Map>)jsonObject.get("crawler");
        this.fileSettings = (ArrayList<String>)jsonObject.get("files");
        this.oAuthSettings = (Map)jsonObject.get("oauth");
        
        
        this.modules = new HashMap<String, Thread>();
        if(!this.crawlerSettings.isEmpty())
            this.modules.put("crawler", new Thread(new Scrapper(this.crawlerSettings)));

        // Wait for the other modules
        /*if(!this.fileSettings.isEmpty())
            this.modules.put("file", new Scrapper(this.crawlerSettings));
        if(!this.oAuthSettings.isEmpty())
            this.modules.put("oauth", new Scrapper(this.crawlerSettings));
        */

    }

    public void setupConnector(){

        // Replace with elastic
        FileConnector fconn = FileConnector.getInstance("Something");
    }

    public void start(){
        // Not used yet
        this.setupConnector();
        for(Thread entry: this.modules.values()){
            entry.start();
        }
    }

    public void stop(){
        for(Thread entry: this.modules.values()){
            // Implement this
            entry.interrupt();
        }
    }

    public static void main(String[] args) {
        Setup s = new Setup("spraak/setup.json");
        //s.setup();
        s.start();
    }

}
