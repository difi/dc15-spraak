package twitter;
//Requires twitter4j framework: http://twitter4j.org/en/index.html

import twitter4j.*;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import twitter4j.conf.*;

//This program requires an OAuth property file for access to the twitter API.


public class TwitterScrapper {



    public static int pageNumber = 1;

    //Set variables to the current year. Then magic happens
    public static String year = "2015";
    public static String lastYear = "2015";

    public static void main(String[] args) throws Exception {



        //Initialize some stuff
        Twitter twitter = new TwitterFactory().getInstance();
        PrintWriter writer = new PrintWriter(year +".txt", "UTF-8");
        List<Status> statuses;
        String user = "Nettkvalitet";

        while (true) {
            try {

                Paging page = new Paging(pageNumber++, 100);
                statuses = twitter.getUserTimeline(user, page);

                //iterate and write tweets to files
                for (Status status : statuses) {
                    String tweet = status.getText();

                    //Creates a string with the year the tweet was posted
                    year = status.getCreatedAt().toString();
                    year = year.substring(year.length()-4, year.length());


                    //Create new file if the year has changed
                    if (!lastYear.equals(year)) {
                        writer.close();
                        writer = new PrintWriter(year +".txt", "UTF-8");
                    }

                    lastYear = year;

                    //check if retweet
                    if (tweet.startsWith("RT")) {
                        writer.println("Ditta var vist ein retweet");

                    } else {
                        //Prints every tweet that is not a RT to a file
                        writer.println(status.getUser().getName() + " : " + status.getText() +" | Ble postet: " + status.getCreatedAt());
                    }
                }

                //Breaks the chain after x iterations
                if (pageNumber == 10)
                    break;
            }

            //catches exception if twitter is down
            catch (TwitterException e) {
                e.printStackTrace();
            }
        }
        writer.close();
    }
}



//Old code used for interacting with the API

/*    private static int counter = 0;
    private static int pageNumber = 1;

    public static void TwitterScrapper(String[] args) throws Exception {

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


