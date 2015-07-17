package crawler; /**
 * Created by camp-mli on 16.06.2015.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Pattern;

import documentTextExtractor.PdfExtractor;
import documentTextExtractor.TextExtractor;
import utils.Utils;

import connectors.FileConnector;
import connectors.ElasticConnector;
import org.apache.commons.lang3.StringUtils;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;


public class Crawler extends WebCrawler {

    private static final Pattern
            FILTERS = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g" + "|png|tiff?|mid|mp2|mp3|mp4" + "|wav|avi|mov|mpeg|ram|m4v" +
                    "|rm|smil|wmv|swf|wma|zip|rar|gz))$")
            ,ACCEPTFILTERS = Pattern.compile(".*(\\.(pdf|docx|doc|odf))$");

    private String prev = null;
    private HashMap<String, Object> settings;
    private String[] myCrawlDomains;
    private ElasticConnector db;
    private String domain;
    private static ArrayList<String> forms = new ArrayList();
    @Override
    public void onStart() {

        this.settings = (HashMap<String, Object>) myController.getCustomData();

        this.db = (ElasticConnector) this.settings.get("db");

        this.db.setType("crawl");

        myCrawlDomains = (String[]) this.settings.get("domains");

        this.domain = myCrawlDomains[0];
    }

    @Override
    public boolean shouldVisit(Page page, WebURL url) {

        String href = url.getURL().toLowerCase().substring(url.getURL().indexOf("://"));
        if (FILTERS.matcher(href).matches()) {
            return false;
        }
        //TODO: Legg til funksjonalitet som laster ned fil
        else if(ACCEPTFILTERS.matcher(href).matches()){
            this.db.setType("file");
            TextExtractor t = new TextExtractor(url.getURL(), this.db);
            try{
                t.run();
            }catch(OutOfMemoryError e){
                System.out.println("Out of memory");
                System.out.println(url.getURL());
            }catch(Exception e){
                System.out.println("Failed to grab pdf...");
            }
            this.db.setType("crawler");
            return false;
        }
        for (String crawlDomain : myCrawlDomains) {
            if (href.startsWith(crawlDomain.substring(crawlDomain.indexOf("://")))) {
                return true;
            }
        }

        return false;
    }


    public String clean(String s){

        // Remove multiple spaces and tabs
        s = Utils.clean(s);

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

    public boolean exists(String word){
        for(String s : forms){
            if(s.equals(word))
                return true;
        }
        return false;
    }

    @Override
    public void visit(Page page) {
        if (page.getParseData() instanceof HtmlParseData) {
            //System.out.println(this.myDomain);
            String str = "";
            for(int i = 0; i < page.getWebURL().getDepth(); i++)
                str+="\t";
            JSONObject j = new JSONObject();
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

            //finds forms.

            Elements forms = doc.select("body").select("form");
            for(Element el : forms){
                String name = el.attr("action");
                for(Element b : el.getElementsByTag("input")) {
                    if(b.hasAttr("name"))
                        name += b.attr("name");
                }
                if(!exists(name)){
                    j.put("domain", this.domain);
                    j.put("site", page.getWebURL().getURL());
                    j.put("type", "form");
                    j.put("text", clean(el.text()));
                    j.put("words", Utils.getNumberOfWords(out));
                    this.forms.add(name);
                    this.db.write(j);
                }
            }
            out = this.clean(out);

            // TODO: Fix
            j.put("type", "web");
            j.put("domain", this.domain);
            j.put("site", page.getWebURL().getURL());
            j.put("text", out);
            j.put("words", Utils.getNumberOfWords(out));
            this.db.write(j);
            return;
        }
        return;
    }
}
