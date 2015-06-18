/**
 * Created by camp-mli on 16.06.2015.
 */



import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class Scrapper {
    public static void main(String[] args) {
        String crawlStorageFolder = "";

        CrawlConfig config1 = new CrawlConfig();
        CrawlConfig config2 = new CrawlConfig();

    /*
     * The two crawlers should have different storage folders for their
     * intermediate data
     */
        config1.setCrawlStorageFolder(crawlStorageFolder + "/crawler1");
        config2.setCrawlStorageFolder(crawlStorageFolder + "/crawler2");

        config1.setPolitenessDelay(1000);
        config2.setPolitenessDelay(1000);

        config1.setMaxPagesToFetch(50);
        config2.setMaxPagesToFetch(50);

    /*
     * We will use different PageFetchers for the two crawlers.
     */
        PageFetcher pageFetcher1 = new PageFetcher(config1);
        PageFetcher pageFetcher2 = new PageFetcher(config2);

    /*
     * We will use the same RobotstxtServer for both of the crawlers.
     */
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher1);

        CrawlController controller1 = null;
        CrawlController controller2 = null;

        String[] crawler1Domains = {"http://www.norge.no/nn"};
        String[] crawler2Domains = {"http://www.norge.no/nb"};

        String myDomain = crawler1Domains[0];
        String fname = crawler1Domains[0].split("/",4)[3];

        DatabaseConnector database1 = new DatabaseConnector(fname);
        DatabaseConnector database2 = new DatabaseConnector("lel");
        try {
            controller1 = new CrawlController(config1, pageFetcher1, robotstxtServer);
            controller2 = new CrawlController(config2, pageFetcher2, robotstxtServer);
        } catch (Exception e) {
            e.printStackTrace();
        }


        controller1.setCustomData(crawler1Domains);
        controller2.setCustomData(crawler2Domains);

        controller1.addSeed("http://www.norge.no/nn");

        controller2.addSeed("http://www.norge.no/nb");

    /*
     * The first crawler will have 5 concurrent threads and the second
     * crawler will have 7 threads.
     */
        controller1.startNonBlocking(Crawler.class, 5);
        controller2.startNonBlocking(Crawler.class, 7);

        controller1.waitUntilFinish();

        controller2.waitUntilFinish();
    }
}
