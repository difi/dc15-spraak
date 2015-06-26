package oauth;

import connectors.ElasticConnector;
import facebook4j.*;
import facebook4j.conf.ConfigurationBuilder;
import org.json.simple.JSONObject;
import java.util.Map;




//This program requires unique OAuth tokens to run.

public class FacebookCrawler implements Runnable{

    private Reading limit = new Reading();
    private String year;
    private Map settings;
    private ElasticConnector db;

    //dificamp facebookID
    private String ID = "122374377845823";

    public FacebookCrawler(Map settings, ElasticConnector database) {
        this.db = database;
        this.db.setType("oauth");

        this.settings = settings;
    }


    public void getFacebookPost() throws Exception {


        String appId = this.settings.get("app_id").toString();
        String appSecret = this.settings.get("app_secret").toString();
        String accessToken = this.settings.get("access_token").toString();
        String permissions = this.settings.get("permissions").toString();

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true).setOAuthAppId(appId).setOAuthAppSecret(appSecret).
                setOAuthAccessToken(accessToken).setOAuthPermissions(permissions);

        Facebook facebook = new FacebookFactory(cb.build()).getInstance();

        ResponseList<Post> feed = facebook.getPosts(ID, limit.limit(250));

                for (int i = 0; i < feed.size(); i++) {
                    JSONObject facebookPosts = new JSONObject();
                    Post post = feed.get(i);

                    String message = post.getMessage();

                    facebookPosts.put("type", "fb");
                    facebookPosts.put("account", ID);
                    facebookPosts.put("text", message);
                    facebookPosts.put("post_year", year);
                    this.db.write(facebookPosts);

                    year = post.getCreatedTime().toString();
                    year = year.substring(year.length() - 4, year.length());
                }
                //retrieve only post from the last 5 years
        return;

        }

    @Override
    public void run() {
        try {
            this.getFacebookPost();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
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
