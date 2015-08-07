package connectors;

import languageClassifier.AnalyzedText;
import languageClassifier.Classifier;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.joda.time.DateTime;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.json.simple.JSONObject;
import utils.Utils;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;

/**
 * Created by camp-mli on 19.06.2015.
 */
public class ElasticConnector {

    //Bare noe lars la til for å se hvor mye som er gjort.
    private Client client;
    private String uuid = null;
    private String type;
    private String owner;
    static Logger logger = Logger.getLogger(ElasticConnector.class);
    private Classifier classifier;
    public ElasticConnector(String owner){
        try {
            this.classifier = new Classifier();
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        this.owner = owner;
        this.connect();
    }

    private void connect(){
        Settings settings = ImmutableSettings.settingsBuilder()
                .put("cluster.name", "elasticsearch.difi.no").build();

        Client client = new TransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress("elasticsearch.difi.local", 9300));

        this.client = client;
    }

    public void partOfOpen(){
        if(this.uuid == null)
            this.uuid = UUID.randomUUID().toString();
    }

    public void partOfClose(){
        this.uuid = null;
    }

    public JSONObject check(JSONObject msg){
        if(!msg.containsKey("type"))
            msg.put("type", null);
        if(!msg.containsKey("text"))
            msg.put("text", null);
        if(!msg.containsKey("lang"))
            msg.put("lang", null);
        if(!msg.containsKey("words"))
            msg.put("words", 0);
        if(!msg.containsKey("post_year"))
            msg.put("post_year", DateTime.now().year().get());
        return msg;
    }

    public JSONObject checkCrawl(JSONObject msg){
        if(!msg.containsKey("domain"))
            msg.put("domain", null);
        if(!msg.containsKey("site"))
            msg.put("site", null);
        if(!msg.containsKey("uuid"))
            msg.put("uuid", null);
        return msg;
    }

    public JSONObject checkFile(JSONObject msg){
        if(!msg.containsKey("name"))
            msg.put("name", null);
        if(!msg.containsKey("uuid"))
            msg.put("uuid", null);
        return msg;

    }

    public JSONObject checkOAuth(JSONObject msg){
        if(!msg.containsKey("account"))
            msg.put("account", null);
        return msg;
    }

    public void write(JSONObject msg) {

        if(true)
            return;
        // Append UUID if available
        if(this.uuid != null)
            msg.put("uuid", this.uuid);

        msg = this.check(msg);

        //bør byttes ut.

        if(this.type.equals("crawl")) {
            msg = this.checkCrawl(msg);
        }
        else if(this.type.equals("file")) {
            msg = this.checkFile(msg);
        }
        else if(this.type.equals("oauth")) {
            msg = this.checkOAuth(msg);
            System.out.println(msg);

        }



        if(!msg.containsKey("date")) {
            Date d = new Date();
            msg.put("date", d.toString());
        }

        if(msg.get("lang") == null) {
            try {
                AnalyzedText analysis = classifier.classify((String) msg.get("text"));
                String code = analysis.language;
                Float LIX = analysis.complexity.LIX;
                Float confidence = analysis.confidence;
                msg.put("lang", code);
                msg.put("complexity", LIX);
                msg.put("confidence", confidence);
                if(!msg.containsKey("words")) {
                    msg.put("words", analysis.complexity.wordCount);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }catch(java.nio.BufferUnderflowException e){
                System.out.println("Buffer underflow");
                System.out.println(msg);
            }catch(java.nio.BufferOverflowException e){
                System.out.println("Buffer overflow");
                System.out.println(msg);
            }
        }else if(msg.get("lang").equals("nn") || msg.get("lang").equals("nb")){
            return;
        }

        logger.info(msg.get("text"));
        logger.info(msg.get("lang") + " - " + msg.get("complexity"));

        msg.put("text", Utils.clean((String) msg.get("text")));
        msg.put("owner", this.owner);

        // Just for safety
        // Retry the insert if it does not work
        int i = 0;
        while(i != 5) {
            try {
//                IndexResponse respone = this.client.prepareIndex("spraak", this.type)
//                        .setSource(msg)
//                        .execute()
//                        .actionGet();
                System.out.println(msg);
                break;
            }catch(Exception e){
                i += 1;
                try {
                    System.out.println("Retry thread");
                    Thread.sleep(5000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                    return;
                }
            }
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
