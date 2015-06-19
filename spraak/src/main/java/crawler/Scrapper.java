package crawler;
/**
 * Created by camp-mli on 16.06.2015.
 */



import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

import java.util.ArrayList;
import java.util.Map;

public class Scrapper implements Runnable {

    private ArrayList<Map> settings;

    public Scrapper(ArrayList<Map> settings) {
        this.settings = settings;
    }

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


            System.out.println("started");
            controller.startNonBlocking(Crawler.class, threads);

            controller.waitUntilFinish();
            System.out.println("started");

        }

    }
}
