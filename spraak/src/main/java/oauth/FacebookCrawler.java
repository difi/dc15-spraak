package oauth;

import facebook4j.Facebook;
import facebook4j.FacebookFactory;
import facebook4j.Post;
import facebook4j.Reading;
import facebook4j.ResponseList;
import java.io.PrintWriter;


//facebookuserID: 104824506525824
//facebookuseID DIFI: 122374377845823


public class FacebookCrawler {

    public static Reading limit = new Reading();
    public static String year = "2015";
    public static String lastYear = "2015";


    public static void main(String[] args) throws Exception {

        Facebook facebook = new FacebookFactory().getInstance();

        PrintWriter writer = new PrintWriter(year + ".txt", "UTF-8");

        ResponseList<Post> feed = facebook.getPosts("122374377845823", limit.limit(250));



        for (int i = 0; i < feed.size(); i++) {


            Post post = feed.get(i);
            year = post.getCreatedTime().toString();


            year = year.substring(year.length()-4, year.length());
            //System.out.println(year);

            if (!lastYear.equals(year)) {
                writer.close();
                writer = new PrintWriter(year+".txt", "UTF-8");
            }
            lastYear = year;
            String message = post.getMessage();
            writer.println(message);

        }

    writer.close();
    }



}
/*
for (int count = 1999; count < 2100; count++) {
        facebook.postStatusMessage("Lets party like it's "+ count );

        }
*/
// Get accesstoken
//        ResponseList<Account> accounts = facebook.getAccounts();
//        Account yourPageAccount = accounts.get(0);  // if index 0 is your page account.
//        String pageAccessToken = yourPageAccount.getAccessToken();
//
//        System.out.println(pageAccessToken);
