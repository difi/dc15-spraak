package connectors;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.json.simple.JSONObject;

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


    private ElasticConnector(String type) {
        Settings settings = ImmutableSettings.settingsBuilder()
                .put("client.transport.sniff", true)
                .put("cluster.name", "elasticsearch.difi.no").build();
        Client client = new TransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress("elasticsearch.difi.local", 9300));
        this.type = type;
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
        return msg;
    }

    public void write(JSONObject msg) {
        JSONObject j = new JSONObject();

        // Append UUID if available
        if(this.uuid != null && this.type.equals("file"))
            j.put("uuid", this.uuid);

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

        // if(msg.get("lang") == null)
            // Gjør språk gjennkjenning
        
        System.out.println(j);
        // Just for safety
        /*
        IndexResponse respone = this.client.prepareIndex("spraak", this.type)
                .setSource(j)
                .execute()
                .actionGet();
        */
    }



    public static ElasticConnector getInstance(String type) {
        if (instance == null)
            instance = new ElasticConnector(type);
        return instance;
    }

    public static ElasticConnector getInstance() throws Exception {
        if (instance == null)
            throw new Exception("No instance created!");
        return instance;
    }
}
