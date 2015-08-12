package no.difi.camp.spraak;

import no.difi.camp.spraak.connectors.ElasticConnector;
import no.difi.camp.spraak.crawler.Scrapper;
import no.difi.camp.spraak.oauth.RunnableOauth;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Setup {

    private ArrayList<String> crawlerSettings;
    private ArrayList<String> fileSettings;
    private Map oAuthSettings;
    private ArrayList<Thread> modules;
    private JSONObject targets;

    public Setup(String filename) {
        JSONParser parser = new JSONParser();

        Object obj = null;
        try {
            obj = parser.parse(new FileReader(filename));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }


        // TODO: Remove hardcode
        JSONObject jsonObject = (JSONObject) obj;
        this.targets = (JSONObject) jsonObject.get("targets");



        this.modules = new ArrayList<Thread>();


        initiateThreads();

    }

    public void initiateThreads() {

        Iterator<String> iterator = this.targets.keySet().iterator();

        while(iterator.hasNext()){
            String key = iterator.next();
            JSONObject entry = (JSONObject) this.targets.get(key);
            this.crawlerSettings = (ArrayList<String>) entry.get("crawler");
            this.fileSettings = (ArrayList<String>) entry.get("files");
            this.oAuthSettings = (Map) entry.get("oauth");
            if(this.crawlerSettings != null )//|| !this.crawlerSettings.isEmpty())
                this.modules.add(new Thread(new Scrapper(this.crawlerSettings, new ElasticConnector(key))));

//            if(this.fileSettings != null )//|| !this.fileSettings.isEmpty())
//                this.modules.add(new Thread(new TextExtractor(this.fileSettings, new ElasticConnector("difi"))));
//
            if(this.oAuthSettings != null )//|| !this.oAuthSettings.isEmpty())
                this.modules.add(new Thread(new RunnableOauth(this.oAuthSettings, new ElasticConnector(key))));
        }

    }


    public void setupConnector(){
        // Replace with elastic
    }

    public void start(){
        final List<Thread> threads = new ArrayList<>();

        for(Thread entry: this.modules){
            entry.start();
            threads.add(entry);
        }

        while(true) {
            boolean alive = false;
            for (Thread t: threads){
                if (t.isAlive())
                    alive = true;
            }
            if(!alive) {
                System.out.println("Quitted");
                break;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //System.out.println("Checking threads");
        }
        return;
    }

    public void initiateTrustManager() {
        /*
        Trust everything!
         */
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };

        // Activate the new trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Setup s = new Setup(args[0]);
        s.initiateTrustManager();
        s.start();
    }
}
