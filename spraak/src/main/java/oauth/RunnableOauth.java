package oauth;

import java.util.Map;

/**
 * Created by camp-mth on 22.06.2015.
 */
public class RunnableOauth implements Runnable{

    private Map settings;

    public RunnableOauth(Map settings){
        this.settings = settings;
    }




    @Override
    public void run() {

        if(!this.settings.isEmpty()) {

            Thread t = new Thread(new TwitterCrawler((Map)this.settings.get("twitter")));
            t.start();

        }
    }
}
