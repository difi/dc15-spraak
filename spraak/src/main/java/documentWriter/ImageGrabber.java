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

    private  static String exportURL = "http://export.highcharts.com?options=";
    private static String text ="{'chart':{'plotBackgroundColor':null,'plotBorderWidth':null,'plotShadow':false,'type':'pie'},'title':{'text':'Nynorsk- og bokmålsandelen til Difi:'},'plotOptions':{'pie':{'dataLabels':{'enabled':true,'format':'<b>{point.name}</b>:{point.percentage:.1f}{point.end}','style':{'color':'black'}}}},'series':[{'name':'Brands','colorByPoint':true,'data':[{'name':'Nynorsk','y':99,'end':'S'},{'name':'B','y':1,'end':'S'}]}]}";

    public static LinkedHashMap<String, Float> complexityValuesNN;
    public static LinkedHashMap<String, Float> complexityValuesNB;
    public static LinkedHashMap<String, Float> percentagesNN;
    public static LinkedHashMap<String, Float> percentagesNB;

    public static void resetValues(){
        complexityValuesNB = new LinkedHashMap();
        complexityValuesNN = new LinkedHashMap();
        percentagesNN= new LinkedHashMap();
        percentagesNB= new LinkedHashMap();
    }

    public static boolean grabPieChart(String name){
        if(name.equals("Sammendrag")){
            LinkedHashMap<String,Float> map = new LinkedHashMap();
            map.put("Nynorsk",ImageGrabber.percentagesNN.get("Sammendrag"));
            map.put("Bokmål",ImageGrabber.percentagesNB.get("Sammendrag"));
            return grabPieChart(map, "Prosentandel nynorsk og bokmål", "piechartSammendrag");
        }
        return false;
    }

    public static boolean grabPieChart(Map<String, Float> values, String title, String name){
        try {
            text = "{'chart':{'height':300,'plotBackgroundColor':'white','plotBorderWidth':null,'plotShadow':false,'type':'pie'},title:{'text':'"+ title +"'},'plotOptions':{'pie':{'dataLabels':{'enabled':true,'format':'<b>{point.name}</b>:{point.percentage:.1f}{point.end}','style':{'color':'black'}}}},'series':[{'name':'Brands','colorByPoint':true,'data':[";
            int i = 0;
            for(String key : values.keySet()){
                System.out.println(key +": " + values.get(key));
                text += "{'name':'" +key +"','y':"+values.get(key)+",'end':'%'}";
                if(i++ < values.size()-1)
                    text+=",";
            }
            text += "]}]}";
            System.out.println(text);
            System.out.println("Calling grabandprint");
            grabAndPrint(text,name);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static boolean grabSplineChart(String name){
        ArrayList<float[]> values = new ArrayList();
        String[] names = null;
        if(name.equals("Sammendrag")) {

            names = new String[complexityValuesNN.size()-5];
            int i = 0;
            for(String s: complexityValuesNN.keySet()) {
                if(!s.equals("Sammendrag") && !s.matches("pdf|odt|doc|docx")) {
                    names[i++] = s;
                    values.add(new float[]{complexityValuesNN.get(s), complexityValuesNB.get(s)});
                }
            }
            return grabSplineChart(names, values, "Variasjon i kompleksitet over medier", "splineChartSammendrag");
        }
        else if(name.equals("Filer")){
            names = new String[4];
            int i = 0;
            for(String s: complexityValuesNN.keySet()) {
                if(s.matches("odt|doc|docx|pdf")) {
                    names[i++] = s;
                    values.add(new float[]{complexityValuesNN.get(s), complexityValuesNB.get(s)});
                }
            }
            return grabSplineChart(names, values, "Variasjon i kompleksitet blant filtyper", "splineChartFiler");
        }
        return false;
    }

    public static boolean grabSplineChart(String[] names, ArrayList<float[]> values, String title, String name){
        try {
            text = "{'chart':{'type':'areaspline'},'title':{'text':'" + title + "'},'legend':{'layout':'vertical','align':'left','verticalAlign':'top','x':570,'y':60,'floating':true,'borderWidth':1,'backgroundColor':'white'},'xAxis':{'categories':[";
            String complexity_values_nn = "";
            String complexity_values_nb = "";
            for(int i = 0; i < names.length;i++){
                text += "'"+names[i]+"'";
                complexity_values_nn+=values.get(i)[0];
                complexity_values_nb+=values.get(i)[1];
                if(i < names.length-1) {
                    text += "," ;
                    complexity_values_nb+=",";
                    complexity_values_nn+=",";

                }
            }

            text +="]},'yAxis':{'title':{'text':'LIX-score'}},'plotOptions':{'areaspline':{'fillOpacity':0.1}},'series':[";
            text += "{'name':'Nynorsk','data':["+complexity_values_nn+"]},";
            text += "{'name':'Bokmål','data':["+complexity_values_nb+"]}";
            text += "]}";
            System.out.println(text);
            grabAndPrint(text,name);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }


    public static void grabAndPrint(String text, String name) throws IOException{
        URL url = new URL(exportURL+URLEncoder.encode(text,"utf-8"));

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
        conn.setRequestMethod("POST");
        BufferedImage b = ImageIO.read(conn.getInputStream());
        ImageIO.write(b, "png", new File("spraak\\LatexFolder\\" + name + ".png"));
        ImageIO.write(b, "png", new File("C:\\users\\camp-lsa\\" + name + ".png"));
    }
}
