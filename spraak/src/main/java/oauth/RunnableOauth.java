package oauth;

import com.drew.metadata.Face;
import connectors.ElasticConnector;

import java.util.ArrayList;
import java.util.Map;

public class RunnableOauth implements Runnable {

    private Map settings;
    private ElasticConnector database;

    public RunnableOauth(Map settings, ElasticConnector database){
        this.database = database;
        this.settings = settings;
    }


    public void run() {
        ArrayList<Thread> threads = new ArrayList<Thread>();
        if(this.settings.containsKey("twitter")) {
            Thread tw = new Thread(new TwitterCrawler((Map)this.settings.get("twitter"), this.database));
            tw.start();
            threads.add(tw);
        }
        if(this.settings.containsKey("fb")) {
            Thread fb = new Thread(new FacebookCrawler((Map)this.settings.get("fb"), this.database));
            fb.start();
            threads.add(fb);
        }


        while(true){
            boolean alive = false;
            for(Thread e: threads){
                if(e.isAlive())
                    alive = true;
            }
            if(!alive) {
                System.out.println("All dead");
                break;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return;
    }
}
