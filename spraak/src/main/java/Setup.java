
import connectors.ElasticConnector;
import crawler.Scrapper;
import oauth.RunnableOauth;
import documentTextExtractor.TextExtractor;
import oauth.TwitterCrawler;
import org.apache.log4j.PropertyConfigurator;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;


public class Setup {




    private final ArrayList<Map> crawlerSettings;
    private final ArrayList<String> fileSettings;
    private final Map oAuthSettings;
    private HashMap<String, Thread> modules;



    public Setup(String filename) {
        JSONParser parser = new JSONParser();

        Object obj = null;
        try {
            obj = parser.parse(new FileReader(filename));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }


        // TODO: Remove hardcode
        JSONObject jsonObject = (JSONObject) obj;
        jsonObject = (JSONObject) jsonObject.get("difi");

        this.crawlerSettings = (ArrayList<Map>) jsonObject.get("crawler");
        this.fileSettings = (ArrayList<String>) jsonObject.get("files");
        this.oAuthSettings = (Map) jsonObject.get("oauth");


        this.modules = new HashMap<String, Thread>();


        initiateThreads();

    }

    public void initiateThreads() {

        ElasticConnector elastic = new ElasticConnector("difi");

        if(!this.crawlerSettings.isEmpty())
            this.modules.put("crawler", new Thread(new Scrapper(this.crawlerSettings, new ElasticConnector("difi"))));

        if(!this.fileSettings.isEmpty())
            this.modules.put("file", new Thread(new TextExtractor(this.fileSettings, new ElasticConnector("difi"))));

        if(!this.oAuthSettings.isEmpty())
            this.modules.put("oauth", new Thread(new RunnableOauth(this.oAuthSettings, new ElasticConnector("difi"))));
    }


    public void setupConnector(){
        // Replace with elastic
    }

    public void start(){
        final List<Thread> threads = new ArrayList<>();

        for(Thread entry: this.modules.values()){
            entry.start();
            threads.add(entry);
        }

        while(true) {
            boolean alive = false;
            for (Thread t: threads){
                if (t.isAlive())
                    alive = true;
            }
            if(!alive)
                break;
        }
        return;
    }

    public static void main(String[] args) {

        String log4jConfPath = "src\\main\\java\\LogfilesDoNotDisturbThem\\log4j.properties";
        PropertyConfigurator.configure(log4jConfPath);

        Setup s = new Setup("setup.json");

        s.start();
    }

}
