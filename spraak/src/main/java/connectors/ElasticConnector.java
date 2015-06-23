package connectors;

import languageClassifier.AnalyzedText;
import languageClassifier.Classifier;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

/**
 * Created by camp-mli on 19.06.2015.
 */
public class ElasticConnector {

    private static ElasticConnector instance;
    private Client client;
    private String type;
    private String uuid = null;
    private String owner;


    private ElasticConnector(String owner) {
        this.owner = owner;
        this.connect();
    }

    private void connect(){
        Settings settings = ImmutableSettings.settingsBuilder()
                .put("client.transport.sniff", true)
                .put("cluster.name", "elasticsearch.difi.no").build();
        Client client = new TransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress("elasticsearch.difi.local", 9300));
        this.client = client;
    }

    public void partOf(){
        if(this.uuid == null)
            this.uuid = UUID.randomUUID().toString();
    }

    public void partOfClose(){
        this.uuid = null;
    }

    public JSONObject checkCrawl(JSONObject msg){
        if(!msg.containsKey("type"))
            msg.put("type", null);
        if(!msg.containsKey("domain"))
            msg.put("domain", null);
        if(!msg.containsKey("text"))
            msg.put("text", null);
        if(!msg.containsKey("site"))
            msg.put("site", null);
        if(!msg.containsKey("lang"))
            msg.put("lang", null);
        return msg;
    }

    public JSONObject checkFile(JSONObject msg){
        if(!msg.containsKey("type"))
            msg.put("type", null);
        if(!msg.containsKey("name"))
            msg.put("name", null);
        if(!msg.containsKey("text"))
            msg.put("text", null);
        if(!msg.containsKey("lang"))
            msg.put("lang", null);
        if(!msg.containsKey("uuid"))
            msg.put("uuid", null);
        return msg;

    }

    public JSONObject checkOAuth(JSONObject msg){
        if(!msg.containsKey("type"))
            msg.put("type", null);
        if(!msg.containsKey("account"))
            msg.put("account", null);
        if(!msg.containsKey("text"))
            msg.put("text", null);
        if(!msg.containsKey("lang"))
            msg.put("lang", null);
        if(!msg.containsKey("post_year"))
            msg.put("post_year", null);
        return msg;
    }

    public void write(String type, JSONObject msg) {

        // Append UUID if available
        if(this.uuid != null && this.type.equals("file"))
            msg.put("uuid", this.uuid);

        if(this.type.equals("crawl"))
            msg = this.checkCrawl(msg);
        else if(this.type.equals("file"))
            msg = this.checkFile(msg);
        else if(this.type.equals("oauth"))
            msg = this.checkOAuth(msg);



        if(!msg.containsKey("date")) {
            Date d = new Date();
            msg.put("date", d.toString());
        }

        if(msg.get("lang") == null) {
            try {
                AnalyzedText analysis = Classifier.classify((String) msg.get("text"));
                String code = analysis.language;
                Float LIX = analysis.complexity.LIX;
                msg.put("lang", code);
                msg.put("complexity", LIX);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        System.out.println("------------");
        System.out.println(msg.get("text"));
        System.out.println(msg.get("lang") + " - " + msg.get("complexity"));
        System.out.println("------------");

        msg.put("owner", this.owner);


        // Just for safety
        /*
        IndexResponse respone = this.client.prepareIndex("spraak", type)
                .setSource(j)
                .execute()
                .actionGet();
        */
    }


    public static ElasticConnector getInstance(String owner) {
        if (instance == null)
            instance = new ElasticConnector(owner);
        return instance;
    }

    public static ElasticConnector getInstance() {
        if (instance == null)
            return null;
        return instance;
    }
}
