package documentWriter;


import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by camp-lsa on 29.06.2015.
 */
public class ReportCreator {
    final static String basePath = "http://localhost:3002/api/v3/owners/all";
    public static void main(String... args){
        LatexNode l = setAll();
        PDFWriter.createReport(l,0);
        try {
            String path  = PDFWriter.getReport();
            System.out.println("cmd /c start \"\" /max C:\\Users\\camp-lsa\\IdeaProjects\\spraak\\" + path);
            Runtime.getRuntime().exec("cmd /c start \"\" /max C:\\Users\\camp-lsa\\IdeaProjects\\dc15-spraak\\spraak\\" + path);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //returnerer path til skrevet pdf-fil.
    public static String getPDFReport(){
        if(PDFWriter.getTimeSinceWrite() < 60*1000)
            return "LatexFolder"+ PDFWriter.time + "pdf";
        return PDFWriter.getReport();
    }

    static String regex_socmedia = "twitter|fb";
    static String regex_file = "pdf|odt|doc|docx";

    //Henter info fra elasticsearch og lager et tre av LatexNodes som PDFwriter kan bruke.
    public static LatexNode setAll(){
        LatexNode root = new LatexNode("Sammendrag", new float[]{0,0,0,0});
        JSONObject content = (JSONObject) (getData("")).get("owners");

        for(Object k : content.keySet()){
            String key = (String) k;


            JSONObject
                    ministry = (JSONObject) content.get(key),
                    temp;
            LatexNode
                    newNode = new LatexNode(key.toString(), new float[]{0,0,0,0}),
                    socMediaNode = new LatexNode("Sosiale Medier", new float[]{0,0,0,0}),
                    fileNode = new LatexNode("Filer", new float[]{0,0,0,0});
            Number n;

            for(Object term : ((JSONObject) ministry.get("topterms")).keySet()){
                JSONObject obj = (JSONObject) ((JSONObject) ministry.get("topterms")).get(term);

                temp = ((JSONObject) obj.get("complexity_nn"));
                n = ((Number)temp.get("avg"));
                float a = n== null ? 0f : n.floatValue();

                temp = ((JSONObject) obj.get("complexity_nb"));
                n = ((Number)temp.get("avg"));
                float b = n== null ? 0f : n.floatValue();

                temp = ((JSONObject) obj.get("lang_terms"));

                n = temp.get("nn") == null ? 0 : ((Number) ((JSONObject) temp.get("nn")).get("doc_count"));
                float c = n == null ? 0f : n.floatValue();

                n = temp.get("nb") == null ? 0 : ((Number) ((JSONObject) temp.get("nb")).get("doc_count"));
                float d = n == null ? 0f : n.floatValue();


                float percentNN = 100 * c/(c+d);
                float percentNB = 100-percentNN;

                float[] values = {a,b,percentNN,percentNB};
                LatexNode child = new LatexNode(term.toString(), values);

                if(term.toString().matches(regex_file))
                    fileNode.addChild(child);
                else if(term.toString().matches(regex_socmedia))
                    socMediaNode.addChild(child);
                else
                    newNode.addChild(child);
            }
            newNode.addChild(socMediaNode);
            newNode.addChild(fileNode);
            root.addChild(newNode);
        }
        root.sumChildren();
        return root;
    }

    //Henter JSONdata fra elasticsearch.
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

}
