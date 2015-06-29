package documentWriter;


import org.json.simple.parser.JSONParser;
import org.json.simple.JSONArray;
import org.apache.http.HttpConnection;
import org.apache.http.HttpConnectionMetrics;
import org.json.simple.JSONObject;
import sun.awt.image.ImageWatched;

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
    final static String basePath = "http://10.243.202.58:3002/api/v1/all";
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
        LinkedHashMap<String, LinkedHashMap> map = new LinkedHashMap<>();
        JSONObject content = getData("");
        JSONArray buckets = (JSONArray) ((JSONObject) content.get("toptags")).get("buckets");

        map.put("Sammendrag", prepareDataMap("", (JSONObject) content.get("all")));
        map.put("Hjemmesider", prepareDataMap("", (JSONObject) buckets.get(0)));
        LinkedHashMap<String, LinkedHashMap> submap = new LinkedHashMap<>();
            submap.put("Twitter",prepareDataMap("", (JSONObject) buckets.get(1)));
            submap.put("Facebook",prepareDataMap("", (JSONObject) buckets.get(2)));
        map.put("Sosiale Medier",submap);

        submap = new LinkedHashMap<>();
            submap.put("docx",prepareDataMap("", (JSONObject) buckets.get(3)));
            submap.put("pdf",prepareDataMap("", (JSONObject) buckets.get(4)));
            submap.put("doc",prepareDataMap("", (JSONObject) buckets.get(5)));
            submap.put("odt",prepareDataMap("", (JSONObject) buckets.get(6)));
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
        System.out.println(nn +  " - " + nb);
        //System.out.println(obj);
            int prosNynorsk = (int) (100 * nn / (nn + nb));
            map.put("Mengde Nynorsk", "" + prosNynorsk +"%");
            map.put("Mengde Bokmål", "" + (100 - prosNynorsk)+"%");

            Number nn_comp = ((Number) ((JSONObject) ((JSONObject) (obj.get("complexity_nn"))).get("complexity")).get("avg"));
            if(nn_comp==null){
                map.put("Kompleksitet Nynorsk","0 LIX");
            }else{
                map.put("Kompleksitet Nynorsk",nn_comp.intValue() +" LIX");
            }
            Number nb_comp = ((Number) ((JSONObject) ((JSONObject) (obj.get("complexity_nb"))).get("complexity")).get("avg"));
            if(nb_comp==null){
                map.put("Kompleksitet Bokmål","0 LIX");
            }else{
                map.put("Kompleksitet Bokmål",nb_comp.intValue() +" LIX");
            }
        return map;

    }
}
