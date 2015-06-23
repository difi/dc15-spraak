package oauth;

import java.util.Map;

public class RunnableOauth implements Runnable{

    private Map settings;

    public RunnableOauth(Map settings){
        this.settings = settings;
    }

    @Override
    public void run() {

/*        if(this.settings.containsKey("twitter")) {
            Thread tw = new Thread(new TwitterCrawler((Map)this.settings.get("twitter")));
            tw.start();
        }*/
        if(this.settings.containsKey("fb")) {
            Thread fb = new Thread(new FacebookCrawler((Map)this.settings.get("fb")));
            fb.start();
        }
    }
}
