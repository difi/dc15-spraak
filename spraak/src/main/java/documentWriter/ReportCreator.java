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
    //Addresse til lokalt elasticsearch-api.
    final static String basePath = "http://localhost:3002/api/v3/owners/all";

    //returnerer path til skrevet pdf-fil.
    public static String getPDFReport(){
        if(PDFWriter.getTimeSinceWrite() < 60*1000)
            return "LatexFolder"+ PDFWriter.time + "pdf";
        return PDFWriter.getReport(setAll());
    }

    static String regex_socmedia = "twitter|fb";
    static String regex_file = "pdf|odt|doc|docx";

    //Henter info fra elasticsearch og lager et tre av LatexNodes som PDFwriter kan bruke.
    public static LatexNode setAll(){
        LatexNode root = new LatexNode("Sammendrag", new float[]{0,0,0,0});
        JSONObject content = (JSONObject) (getData("")).get("owners");
        System.out.println(content.toJSONString());

        for(Object k : content.keySet()){
            String key = (String) k;

            JSONObject ministry = (JSONObject) content.get(key),
                    temp;

            LatexNode newNode = new LatexNode(key.toString(), new float[]{0,0,0,0}),
                    socMediaNode = new LatexNode("Sosiale Medier", new float[]{0,0,0,0}),
                    filer= new LatexNode("Sosiale Medier", new float[]{0,0,0,0});

            Number n;
            System.out.println(key);
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

                if(term.toString().matches(regex_socmedia))
                    socMediaNode.addChild(child);
                else if(term.toString().matches(regex_file))
                    filer.addChild(child);
                else
                    newNode.addChild(child);
            }

            if(socMediaNode.children.size() > 0)
                newNode.addChild(socMediaNode);
            if(filer.children.size() > 0)
                newNode.addChild(newNode);
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

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF8"));
            String content = br.readLine();
            System.out.println(content);
            JSONParser parser = new JSONParser();

            br.close();
            return (JSONObject) parser.parse(content);
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
