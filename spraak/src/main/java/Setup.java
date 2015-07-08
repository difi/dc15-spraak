
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
import java.sql.Time;
import java.util.*;

import org.apache.log4j.Logger;


public class Setup {




    private ArrayList<String> crawlerSettings;
    private ArrayList<String> fileSettings;
    private Map oAuthSettings;
    private ArrayList<Thread> modules;
    private JSONObject targets;



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
        this.targets = (JSONObject) jsonObject.get("targets");



        this.modules = new ArrayList<Thread>();


        initiateThreads();

    }

    public void initiateThreads() {

        Iterator<String> iterator = this.targets.keySet().iterator();

        while(iterator.hasNext()){
            String key = iterator.next();
            JSONObject entry = (JSONObject) this.targets.get(key);
            this.crawlerSettings = (ArrayList<String>) entry.get("crawler");
            this.fileSettings = (ArrayList<String>) entry.get("files");
            this.oAuthSettings = (Map) entry.get("oauth");
            if(this.crawlerSettings != null )//|| !this.crawlerSettings.isEmpty())
                this.modules.add(new Thread(new Scrapper(this.crawlerSettings, new ElasticConnector(key))));

//            if(this.fileSettings != null )//|| !this.fileSettings.isEmpty())
//                this.modules.add(new Thread(new TextExtractor(this.fileSettings, new ElasticConnector("difi"))));
//
            if(this.oAuthSettings != null )//|| !this.oAuthSettings.isEmpty())
                this.modules.add(new Thread(new RunnableOauth(this.oAuthSettings, new ElasticConnector(key))));
        }

    }


    public void setupConnector(){
        // Replace with elastic
    }

    public void start(){
        final List<Thread> threads = new ArrayList<>();

        for(Thread entry: this.modules){
            entry.start();
            threads.add(entry);
        }

        while(true) {
            boolean alive = false;
            for (Thread t: threads){
                if (t.isAlive())
                    alive = true;
            }
            if(!alive) {
                System.out.println("Quitted");
                break;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //System.out.println("Checking threads");
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
