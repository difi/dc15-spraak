package crawler; /**
 * Created by camp-mli on 16.06.2015.
 */


import java.util.Set;
import java.util.regex.Pattern;

import connectors.FileConnector;
import org.apache.commons.lang3.StringUtils;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;


public class Crawler extends WebCrawler {

    private static final Pattern FILTERS = Pattern.compile(
            ".*(\\.(css|js|bmp|gif|jpe?g" + "|png|tiff?|mid|mp2|mp3|mp4" + "|wav|avi|mov|mpeg|ram|m4v|pdf" +
                    "|rm|smil|wmv|swf|wma|zip|rar|gz))$");

    private String prev = null;
    private String[] myCrawlDomains;
    private String myDomain;
    private FileConnector db;

    @Override
    public void onStart() {

        myCrawlDomains = (String[]) myController.getCustomData();

        myDomain = myCrawlDomains[0];
        FileConnector db = FileConnector.getInstance("Something");
        System.out.println("Lel");
    }

    @Override
    public boolean shouldVisit(Page page, WebURL url) {
        String href = url.getURL().toLowerCase();
        if (FILTERS.matcher(href).matches()) {
            return false;
        }

        for (String crawlDomain : myCrawlDomains) {
            if (href.startsWith(crawlDomain)) {
                return true;
            }
        }

        return false;
    }


    public String clean(String s){

        // Remove multiple spaces and tabs
        s = s.replace("\n","").replace("\r","");
        s = s.trim().replaceAll(" +"," ");
        s = s.trim().replaceAll("\t+", " ");

        if(this.prev == null) {
            this.prev = s;
            return s;
        }


        // Clean away difference in string
        // if we find a difference, keep the string
        // for the next page, or replace.

        // TODO: Actually make sure this works in the long run
        String diff = StringUtils.difference(this.prev, s);
        if(!diff.equals("")){
            s = diff;
        }else{
            this.prev = s;
        }
        return s;
    }

    @Override
    public void visit(Page page) {
        if (page.getParseData() instanceof HtmlParseData) {
            System.out.println(this.myDomain);
            System.out.println(page.getWebURL().getURL());
            System.out.println("=======");
            // Review
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            String text = htmlParseData.getText();
            String html = htmlParseData.getHtml();
            Set<WebURL> links = htmlParseData.getOutgoingUrls();

            Document doc = Jsoup.parse(html);

            // Construct a string based on textnodes
            String out = "";
            for(Element el: doc.select("body").select("*")){
                if(!el.textNodes().isEmpty()){
                    for(TextNode node : el.textNodes()){
                        if(!node.isBlank())
                            out = out + " " + node.getWholeText();
                    }
                }
            }
            out = this.clean(out);
            this.db.write(out);
        }
    }
}
