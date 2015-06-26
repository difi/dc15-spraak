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

public class Scrapper implements Runnable {

    private ArrayList<Map> settings;
    private ElasticConnector database;

    public Scrapper(ArrayList<Map> settings, ElasticConnector database ) {
        this.database = database;
        this.settings = settings;
    }


    public static void main(String[] args) {

        // Test a given domain


        /*
         * crawlStorageFolder is a folder where intermediate crawl data is
         * stored.
         */

        CrawlConfig config1 = new CrawlConfig();

        /*
         * The two crawlers should have different storage folders for their
         * intermediate data
         */
        config1.setCrawlStorageFolder("./crawler");

        config1.setPolitenessDelay(1000);

        config1.setMaxPagesToFetch(50);

        /*
         * We will use different PageFetchers for the two crawlers.
         */
        PageFetcher pageFetcher1 = new PageFetcher(config1);

        /*
         * We will use the same RobotstxtServer for both of the crawlers.
         */
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher1);

        CrawlController controller1 = null;
        try {
            controller1 = new CrawlController(config1, pageFetcher1, robotstxtServer);
        } catch (Exception e) {
            e.printStackTrace();
        }


        // DOmain here
        String domain = "";

        String[] crawler1Domains = {domain};

        HashMap<String, Object> settings = new HashMap<>();
        settings.put("domains", crawler1Domains);
        settings.put("db", ElasticConnector.class);

        controller1.setCustomData(settings);

        controller1.addSeed(domain);

        /*
         * The first crawler will have 5 concurrent threads and the second
         * crawler will have 7 threads.
         */
        controller1.startNonBlocking(Crawler.class, 1);

        controller1.waitUntilFinish();
}


    @Override
    public void run () {

        if (this.settings.isEmpty())
            return;

        // For handing an ID to the crawler
        for (Map entry : this.settings) {


            // Setup used variables

            String domain;
            String[] crawlerDomains;
            String fname;

            if (entry.containsKey("domain")) {
                domain = (String) entry.get("domain");
                //crawlerDomains = new String[]{domain};
                //fname = domain.split("/",4)[3];
                fname = "norge";
                crawlerDomains = new String[]{domain};
            } else {
                System.out.println("Needs a domain!");
                return;
            }

            int threads = 5;

            if (entry.containsKey("threads")) {
                Number n = (Number) entry.get("threads");
                threads = n.intValue();
            }


            // Setup config
            CrawlConfig config = new CrawlConfig();

            String crawlStorageFolder = "";

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

            HashMap<String, Object> settings = new HashMap<>();
            settings.put("domains", crawlerDomains);
            settings.put("db", this.database);
            controller.setCustomData(crawlerDomains);

            controller.addSeed(domain);

            if (entry.containsKey("delay")) {
                Number n = (Number) entry.get("delay");
                config.setPolitenessDelay(n.intValue());
            }
            if (entry.containsKey("pages")) {
                Number n = (Number) entry.get("pages ");
                config.setMaxPagesToFetch(n.intValue());
            }

            System.out.println("started");
            controller.setCustomData(crawlerDomains);

            controller.addSeed(domain);


            controller.startNonBlocking(Crawler.class, threads);

            controller.waitUntilFinish();
        }

    }
}
