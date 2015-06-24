package oauth;

import connectors.ElasticConnector;

import java.util.Map;

public class RunnableOauth implements Runnable{

    private Map settings;
    private ElasticConnector database;

    public RunnableOauth(Map settings, ElasticConnector database){
        this.database = database;
        this.settings = settings;
    }

    @Override
    public void run() {

        if(this.settings.containsKey("twitter")) {
            Thread tw = new Thread(new TwitterCrawler((Map)this.settings.get("twitter"), this.database));
            tw.start();
        }
        if(this.settings.containsKey("fb")) {
            Thread fb = new Thread(new FacebookCrawler((Map)this.settings.get("fb"), this.database));
            fb.start();
        }
    }
}
