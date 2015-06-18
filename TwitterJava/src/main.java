
import twitter4j.*;


import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import twitter4j.conf.*;



//This program requires an OAuth property file for access to the twitter API.
public class main {

    private static int pageNumber = 1;
    private static String user = "Nettkvalitet";
    public static List<Status> statuses = new ArrayList<>();



    public static void main(String[] args) throws Exception {

        //Initialize some stuff
        PrintWriter writer = new PrintWriter("DIFI_TWITTER.TXT", "UTF-8");
        Twitter twitter = new TwitterFactory().getInstance();



        while(true) {
            try {

                Paging page = new Paging(pageNumber++, 100);
                statuses = twitter.getUserTimeline(user, page);

                //iterate and write tweets to a file
                for (Status status : statuses) {
                    String tweet = status.getText();
                    //System.out.println(tweet);

                    //Retweet check
                    if (tweet.startsWith("RT")) {
                        writer.println("Detta var vist ein retweet");
                    }
                    else{

                        //Prints every tweet that is not a RT to a file
                        writer.println(status.getUser().getName() + " : " + status.getText());
                    }
                }
                //add date and year checker.

                //Breaks the chain after x iterations
                if (pageNumber == 10)
                        break;
            }
            catch(TwitterException e) {
                e.printStackTrace();
            }


        }

        writer.close();


    }


}




//Old code used for interacting with the API


/*    private static int counter = 0;
    private static int pageNumber = 1;

    public static void main(String[] args) throws Exception {

        // Post one tweet to your own feed
        Twitter twitter = TwitterFactory.getSingleton();
        Status status = twitter.updateStatus("Heia gruppe 1");
        System.out.println("Successfully updated the status to [" + status.getText() + "]");*//**//*


        //post the last 20 tweets on any given feed.
        Twitter twitter = TwitterFactory.getSingleton();


        List<Status> statuses = twitter.getUserTimeline("Nettkvalitet");

       // System.out.println("Showing home timeline.");

        PrintWriter writer = new PrintWriter("DIFI_TWITTER.TXT", "UTF-8");

        for (Status status : statuses) {

            counter += 1;
            System.out.println("fjasorama" + counter);

            writer.println(status.getUser().getName() + " : " + status.getText());
        }
        writer.close();
    }
}*/


