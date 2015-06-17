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
        System.out.println("Leol");
        String crawlStorageFolder = "";

        CrawlConfig config1 = new CrawlConfig();

    /*
     * The two crawlers should have different storage folders for their
     * intermediate data
     */
        config1.setCrawlStorageFolder(crawlStorageFolder + "/crawler1");

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

        String[] crawler1Domains = {"http://www.difi.no/"};

        controller1.setCustomData(crawler1Domains);

        controller1.addSeed("http://www.difi.no/");


    /*
     * The first crawler will have 5 concurrent threads and the second
     * crawler will have 7 threads.
     */
        controller1.startNonBlocking(Crawler.class, 5);

        controller1.waitUntilFinish();
        System.out.println("Crawler done, somethingsomething");
    }
}
