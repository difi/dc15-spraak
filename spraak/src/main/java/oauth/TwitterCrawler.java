package oauth;
//Uses the twitter4j framework: http://twitter4j.org/en/index.html

import connectors.ElasticConnector;
import java.util.List;
import java.util.Map;

import twitter4j.*;
import twitter4j.conf.*;
import org.json.simple.JSONObject;
import org.apache.log4j.Logger;

//This program requires unique OAuth tokens to run.

public class TwitterCrawler implements Runnable {

    static Logger logger = Logger.getLogger(TwitterCrawler.class);
    private int pageNumber = 1;
    private String year;
    private Map settings;
    private ElasticConnector db;
    public boolean done = false;




    public TwitterCrawler(Map settings, ElasticConnector db) {
        this.db = db;
        this.db.setType("oauth");
        this.settings = settings;
    }

    public void getTwitterPost() throws Exception {




        //get tokens from setup.json.
        String consumerKey = this.settings.get("consumer_key").toString();
        String consumerSecret = this.settings.get("consumer_secret").toString();
        String accessToken = this.settings.get("access_token").toString();
        String accessTokenSecret = this.settings.get("access_token_secret").toString();

        //implements the oauth tokens
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true).setOAuthConsumerKey(consumerKey).setOAuthConsumerSecret(consumerSecret)
                .setOAuthAccessToken(accessToken).setOAuthAccessTokenSecret(accessTokenSecret);

        //Initialize Twitterfactory
        Twitter twitter = new TwitterFactory(cb.build()).getInstance();

        List<Status> statuses;
        String user = "Nettkvalitet";

        while (true) {
            try {
                Paging page = new Paging(pageNumber++, 100);
                statuses = twitter.getUserTimeline(user, page);

                //iterate and write tweets to jsonObjects
                for (Status status : statuses) {
                    JSONObject twitterPosts = new JSONObject();
                    String tweet = status.getText();

                    year = status.getCreatedAt().toString();
                    year = year.substring(year.length() - 4, year.length());

                    if (!tweet.startsWith("RT")) {
                        twitterPosts.put("type", "twitter");
                        twitterPosts.put("account", user);
                        twitterPosts.put("text", status.getText());
                        twitterPosts.put("post_year", year);
                        this.db.write(twitterPosts);
                    }



                }


                //retrieve only post from the last 5 years
                if (year.equals("2009"))
                    break;
            }

            //catches exception if twitter is down
            catch (TwitterException e) {
                e.printStackTrace();
            }
            break;
        }
        return;
    }


    @Override
    public void run() {
        try {
            this.getTwitterPost();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }
}










/*                    //Create new file if the year has changed
                    if (!lastYear.equals(year)) {
                        writer.close();
                        writer = new PrintWriter(year + ".txt", "UTF-8");
                    }

                    lastYear = year; */

















//Old code used for interacting with the API could be explored further.

/*    private static int counter = 0;
    private static int pageNumber = 1;

    public static void main (String[] args) throws Exception {

        // Post one tweet to your own feed
        Twitter twitter = TwitterFactory.getSingleton();
        Status status = twitter.updateStatus("Heia gruppe 1");
        System.out.println("Successfully updated the status to [" + status.getText() + "]");


        //post the last 20 tweets on any given feed.
        Twitter twitter = TwitterFactory.getSingleton();


        List<Status> statuses = twitter.getUserTimeline("Nettkvalitet");

       System.out.println("Showing home timeline.");

        PrintWriter writer = new PrintWriter("DIFI_TWITTER.TXT", "UTF-8");

        for (Status status : statuses) {

            counter += 1;
            System.out.println("fjasorama" + counter);

            writer.println(status.getUser().getName() + " : " + status.getText());
        }
        writer.close();
    }
}*/


