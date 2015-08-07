package crawler;
/**
 * Created by camp-mli on 16.06.2015.
 */



import connectors.ElasticConnector;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Scrapper implements Runnable {

    private ArrayList<String> settings;
    private ElasticConnector database;

    public Scrapper(ArrayList<String> settings, ElasticConnector database ) {
        this.database = database;
        this.settings = settings;
    }

    @Override
    public void run () {

        if (this.settings.isEmpty())
            return;

        // For handing an ID to the crawler
        for (String entry : this.settings) {


            // Setup used variables

            String domain;
            String[] crawlerDomains;
            String fname;

            domain = (String) entry;
            //crawlerDomains = new String[]{domain};
            //fname = domain.split("/",4)[3];

            fname = UUID.randomUUID().toString();
            crawlerDomains = new String[]{domain};

            int threads = 1;

            // Setup config
            CrawlConfig config = new CrawlConfig();

            String crawlStorageFolder = "./cache";

            config.setCrawlStorageFolder(crawlStorageFolder + "/" + fname);

            PageFetcher pageFetcher = new PageFetcher(config);
            RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
            RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);

            CrawlController controller;
            try {
                controller = new CrawlController(config, pageFetcher, robotstxtServer);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            HashMap<String, Object> crawlerSettings = new HashMap<String, Object>();
            crawlerSettings.put("domains", crawlerDomains);
            crawlerSettings.put("db", this.database);
            controller.setCustomData(crawlerSettings);


            System.out.println("started");

            controller.addSeed(domain);


            controller.startNonBlocking(Crawler.class, threads);

            controller.waitUntilFinish();
        }
        return;

    }
}
