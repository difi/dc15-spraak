
import connectors.FileConnector;
import crawler.Scrapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by camp-mli on 18.06.2015.
 */
public class Setup {

    private final ArrayList<Map> crawlerSettings;
    private final ArrayList<String> fileSettings;
    private final Map oAuthSettings;

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


    }

    public void setupCrawler(){
        System.out.println("Started crawler");
        Scrapper.start(this.crawlerSettings);
    }

    public void setupFileCrawler(){

    }

    public void setupOAuthCrawler(){

    }

    public void setupConnector(){

        // Replace with elastic
        FileConnector fconn = FileConnector.getInstance("Something");
    }

    public void setup(){
        this.setupConnector();
        this.setupCrawler();
    }

    public void start(){

    }

    public static void main(String[] args) {
        Setup s = new Setup("setup.json");
        s.setup();
        s.start();
    }

}
