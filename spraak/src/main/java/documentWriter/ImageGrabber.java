package documentWriter;

import org.json.simple.parser.JSONParser;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by camp-lsa on 30.06.2015.
 */
public class ImageGrabber {

    private final static String exportURL = "http://export.highcharts.com?options=";

    private static String text = "";

    //Holder info om verdier hentet.
    public static LinkedHashMap<String, Float> complexityValuesNN;
    public static LinkedHashMap<String, Float> complexityValuesNB;
    public static LinkedHashMap<String, Float> percentagesNN;
    public static LinkedHashMap<String, Float> percentagesNB;

    //regex
    private static final String filtyper = "pdf|odt|doc|docx";

    //Tømmer alle hashmaps
    public static void resetValues(){
        complexityValuesNB = new LinkedHashMap();
        complexityValuesNN = new LinkedHashMap();
        percentagesNN = new LinkedHashMap();
        percentagesNB = new LinkedHashMap();
    }


    //Lager en JSONstreng for et piechart-bilde og kaller grabAndPrint, som henter og skriver bilde.
    public static boolean grabPieChart(LatexNode node, String title, String name){
        try {
            float num_nn = 0;
            float num_nb = 0;

            title = "Andel Nynorsk og Bokmål";
            num_nn = node.getValues()[2];
            num_nb = node.getValues()[3];
            text = "{'chart':{'height':300,'plotBackgroundColor':'white','plotBorderWidth':null,'plotShadow':false,'type':'pie'},title:{'text':'"+ title +"'},'plotOptions':{'pie':{'dataLabels':{'enabled':true,'format':'<b>{point.name}</b>:{point.percentage:.2f}{point.end}','style':{'color':'black'}}}},'series':[{'name':'Brands','colorByPoint':true,'data':[";
            text += "{'name':'" +"Nynorsk" +"','y':"+num_nn+",'end':'%'}";
            text +=",";
            text += "{'name':'" +"Bokmål" +"','y':"+num_nb+",'end':'%'}";
            text += "]}]}";
            //Last ned og skriv bilde.
            grabAndPrint(text,name+"pie");
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
    //Skriver JSONtekst for et splineChart og kaller grabAndPrint. Returnerer True om bilde kunne hentes/skrives.
    //TODO: gjør om til noe som jobber med JSONObjects, i stedet?
    public static boolean grabSplineChart(ArrayList<LatexNode> nodes, String title, String name){
        try {
            text = "{'chart':{'type':'areaspline'},'title':{'text':'" + title + "'},'legend':{'layout':'vertical','align':'left','verticalAlign':'top','x':570,'y':60,'floating':true,'borderWidth':1,'backgroundColor':'white'},'xAxis':{'categories':[";
            String complexity_values_nn = "";
            String complexity_values_nb = "";
            for(int i = 0; i < nodes.size();i++){
                LatexNode node = nodes.get(i);
                text +="'"+node.getName()+"'";
                complexity_values_nn +=node.getValues()[0];
                complexity_values_nb +=node.getValues()[1];
                if(i < nodes.size()-1) {
                    text += "," ;
                    complexity_values_nb+=",";
                    complexity_values_nn+=",";
                }
            }
            text +="]},'yAxis':{'title':{'text':'LIX-score'}},'plotOptions':{'areaspline':{'fillOpacity':0.1}},'series':[";
            text += "{'name':'Nynorsk','data':["+complexity_values_nn+"]},";
            text += "{'name':'Bokmål','data':["+complexity_values_nb+"]}";
            text += "]}";

            grabAndPrint(text,name+"spline");
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }


    //Kontakter export.highcharts.com og laster ned bilde rendret der, for så å skrive ut bilde til LatexFolder.
    public static void grabAndPrint(String text, String name) throws IOException{

        URL url = new URL(exportURL+URLEncoder.encode(text,"utf-8"));
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
        conn.setRequestMethod("POST");

        BufferedImage b = ImageIO.read(conn.getInputStream());
        System.out.println(name);
        System.out.println(b.getWidth());
        ImageIO.write(b, "png", new File("LatexFolder\\" + name.replace(" ","") + ".png"));
    }
}
