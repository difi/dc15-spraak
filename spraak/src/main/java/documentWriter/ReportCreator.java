package documentWriter;


import org.json.simple.parser.JSONParser;
import org.json.simple.JSONArray;
import org.apache.http.HttpConnection;
import org.apache.http.HttpConnectionMetrics;
import org.json.simple.JSONObject;
import sun.awt.image.ImageWatched;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by camp-lsa on 29.06.2015.
 */
public class ReportCreator {
    public static void getReport(){

    }
    final static String basePath = "http://localhost:3002/api/v1/all";
    public static void main(String... args){
        LinkedHashMap<String, LinkedHashMap> m = setAll();
        PDFWriter.setMap(m,0);
        //File f = new File();
        try {
            String path  = PDFWriter.getReport();
            System.out.println("cmd /c start \"\" /max C:\\Users\\camp-lsa\\IdeaProjects\\spraak\\" + path);
            Runtime.getRuntime().exec("cmd /c start \"\" /max C:\\Users\\camp-lsa\\IdeaProjects\\dc15-spraak\\spraak\\" + path);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static LinkedHashMap setAll(){
        ImageGrabber.resetValues();
        LinkedHashMap<String, LinkedHashMap> map = new LinkedHashMap<>();
        JSONObject content = getData("");
        JSONArray buckets = (JSONArray) ((JSONObject) content.get("toptags")).get("buckets");

        map.put("Sammendrag", prepareDataMap("Sammendrag", (JSONObject) content.get("all")));
        map.put("Hjemmesider", prepareDataMap("Hjemmesider", (JSONObject) buckets.get(0)));
        LinkedHashMap<String, LinkedHashMap> submap = new LinkedHashMap<>();
        submap.put("Twitter",prepareDataMap("Twitter", (JSONObject) buckets.get(1)));
        System.out.println(buckets.get(2));
        submap.put("Facebook",prepareDataMap("Facebook", (JSONObject) buckets.get(2)));
        map.put("Sosiale Medier",submap);

        submap = new LinkedHashMap<>();
        submap.put("docx",prepareDataMap("docx", (JSONObject) buckets.get(3)));
        submap.put("pdf",prepareDataMap("pdf", (JSONObject) buckets.get(4)));
        submap.put("doc",prepareDataMap("doc", (JSONObject) buckets.get(5)));
        submap.put("odt",prepareDataMap("odt", (JSONObject) buckets.get(6)));
        map.put("Filer",submap);



        return map;
    }

    public static JSONObject getData(String path){
        try {
            URL url = new URL(basePath+path);
            HttpURLConnection conn = (HttpURLConnection) (url).openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String content = br.readLine();

            JSONParser parser = new JSONParser();

            br.close();
            return (JSONObject) parser.parse(content);
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static LinkedHashMap<String, String> prepareDataMap(String type, JSONObject obj){

        LinkedHashMap<String, String> map = new LinkedHashMap();

        int nn = ((Number) ((JSONObject) (obj.get("complexity_nn"))).get("doc_count")).intValue();
        int nb = ((Number) ((JSONObject) (obj.get("complexity_nb"))).get("doc_count")).intValue();

        float prosNynorsk = (float) (100 * nn / (nn + nb));
        map.put("Mengde Nynorsk", "" + prosNynorsk +"%");
        map.put("Mengde Bokmål", "" + (100 - prosNynorsk)+"%");

        Number nn_comp = ((Number) ((JSONObject) ((JSONObject) (obj.get("complexity_nn"))).get("complexity")).get("avg"));
        if(nn_comp==null){
            map.put("Kompleksitet Nynorsk","0 LIX");
            nn_comp = 0;
        }else{
            map.put("Kompleksitet Nynorsk",nn_comp.intValue() +" LIX");
        }

        Number nb_comp = ((Number) ((JSONObject) ((JSONObject) (obj.get("complexity_nb"))).get("complexity")).get("avg"));
        if(nb_comp==null){
            map.put("Kompleksitet Bokmål","0 LIX");
            nb_comp = 0;
        }else{
            map.put("Kompleksitet Bokmål",nb_comp.intValue() +" LIX");
        }


            if(type.matches("docx|doc|pdf|odt")){
                ImageGrabber.percentagesNN.put(type, prosNynorsk);
                ImageGrabber.percentagesNB.put(type, 100f - prosNynorsk);
                ImageGrabber.complexityValuesNN.put(type, nn_comp.floatValue());
                ImageGrabber.complexityValuesNB.put(type, nb_comp.floatValue());
                type = "Filer";
                if(!ImageGrabber.percentagesNN.containsKey(type)){
                    ImageGrabber.percentagesNN.put(type, prosNynorsk);
                    ImageGrabber.percentagesNB.put(type, 100f - prosNynorsk);
                    ImageGrabber.complexityValuesNN.put(type, nn_comp.floatValue());
                    ImageGrabber.complexityValuesNB.put(type, nb_comp.floatValue());
                }else
                {
                    ImageGrabber.percentagesNN.put(type, ImageGrabber.percentagesNN.get(type)/4+prosNynorsk);
                    ImageGrabber.percentagesNB.put(type, ImageGrabber.percentagesNB.get(type)/4+ 100f - prosNynorsk);
                    ImageGrabber.complexityValuesNN.put(type, ImageGrabber.complexityValuesNN.get(type)/4+nn_comp.floatValue());
                    ImageGrabber.complexityValuesNB.put(type, ImageGrabber.complexityValuesNB.get(type)/4+nb_comp.floatValue());
                }
            }
            else {
                ImageGrabber.percentagesNN.put(type, prosNynorsk);
                ImageGrabber.percentagesNB.put(type, 100f - prosNynorsk);
                ImageGrabber.complexityValuesNN.put(type, nn_comp.floatValue());
                ImageGrabber.complexityValuesNB.put(type, nb_comp.floatValue());
            }

        return map;

    }
}
