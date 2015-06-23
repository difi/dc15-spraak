package oauth;
//Uses the twitter4j framework: http://twitter4j.org/en/index.html

import connectors.ElasticConnector;
import twitter4j.*;
import java.util.List;
import java.util.Map;
import twitter4j.conf.*;
import org.json.simple.JSONObject;

//This program requires unique OAuth tokens to run.

public class TwitterCrawler implements Runnable {

    public static int pageNumber = 1;
    public static String year;
    private Map settings;
    private ElasticConnector db = ElasticConnector.getInstance("oauth");

    public TwitterCrawler(Map settings) {
        this.settings = settings;
    }

    public void getTwitterPost() throws Exception {

        //get tokens from setup.json.
        String consumerKey = this.settings.get("consumer_key").toString();
        String consumerSecret = this.settings.get("consumer_secret").toString();
        String accessToken = this.settings.get("access_token").toString();
        String accessTokenSecret = this.settings.get("access_token_secret").toString();
        //System.out.println(consumerKey+"\n" + consumerSecret +"\n"+ accessToken+"\n" + accessTokenSecret);

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true).setOAuthConsumerKey(consumerKey).setOAuthConsumerSecret(consumerSecret)
                .setOAuthAccessToken(accessToken).setOAuthAccessTokenSecret(accessTokenSecret);

        //Initialize Twitterfactory
        Twitter twitter = new TwitterFactory(cb.build()).getInstance();

        JSONObject twitterPosts = new JSONObject();
        List<Status> statuses;
        String user = "Nettkvalitet";

        while (true) {
            try {

                Paging page = new Paging(pageNumber++, 100);
                statuses = twitter.getUserTimeline(user, page);

                //iterate and write tweets to files
                for (Status status : statuses) {
                    String tweet = status.getText();

                    year = status.getCreatedAt().toString();
                    year = year.substring(year.length() - 4, year.length());

                    if (!tweet.startsWith("RT")) {
                        twitterPosts.put("type", "twitter");
                        twitterPosts.put("account", user);
                        twitterPosts.put("text", status.getText());
                        twitterPosts.put("tweet_year", year);
                        this.db.write(twitterPosts);
                    }
                }

                //Breaks the chain after specified year
                if (year.equals("2009"))
                    break;
            }

            //catches exception if twitter is down
            catch (TwitterException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        try {
            this.getTwitterPost();
        } catch (Exception e) {
            e.printStackTrace();
        }
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


