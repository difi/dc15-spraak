package documentWriter;


import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;

/**
 * Created by camp-lsa on 29.06.2015.
 */
public class ReportCreator {
    final static String basePath = "http://localhost:3002/api/v3/owners/all";
    public static void main(String... args){

        LatexNode l = setAll();
        l.print();
        PDFWriter.createReport(l,0);

        try {
            String path  = PDFWriter.getReport();
            System.out.println("cmd /c start \"\" /max C:\\Users\\camp-lsa\\IdeaProjects\\spraak\\" + path);
            Runtime.getRuntime().exec("cmd /c start \"\" /max C:\\Users\\camp-lsa\\IdeaProjects\\dc15-spraak\\spraak\\" + path);

        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public static String getPDFReport(){
        if(PDFWriter.getTimeSinceWrite() < 60*1000)
            return "LatexFolder"+ PDFWriter.time + "pdf";
        //PDFWriter.createReport(setAll(), 0);
        return PDFWriter.getReport();
    }

    static String regex_socmedia = "twitter|fb";
    static String regex_file = "pdf|odt|doc|docx";
    //Henter info fra elasticsearch og lager et map tilpasset pdfwriter.
    public static LatexNode setAll(){
        ImageGrabber.resetValues();
        LatexNode root = new LatexNode("Sammendrag", new float[]{0,0,0,0});
        JSONObject content = (JSONObject) (getData("")).get("owners");

        for(Object k : content.keySet()){
            String key = (String) k;
            JSONObject ministry = (JSONObject) content.get(k);
            LatexNode newNode = new LatexNode(key.toString(), new float[]{0,0,0,0}),
                socMediaNode = new LatexNode("Sosiale Medier", new float[]{0,0,0,0}),
                fileNode = new LatexNode("Filer", new float[]{0,0,0,0});

            for(Object x : ((JSONObject) ministry.get("topterms")).keySet()){
                JSONObject obj = (JSONObject) ((JSONObject) ministry.get("topterms")).get(x);
                Number n;

                JSONObject temp = ((JSONObject) obj.get("complexity_nn"));
                n = ((Number)temp.get("avg"));
                float a = n== null ? 0f : n.floatValue();


                temp = ((JSONObject) obj.get("complexity_nb"));
                n = ((Number)temp.get("avg"));
                float b = n== null ? 0f : n.floatValue();

                temp = ((JSONObject) obj.get("lang_terms"));
                if(temp.get("nn") == null)
                    n = 0;
                else
                    n = ((Number)((JSONObject) temp.get("nn")).get("doc_count"));
                float c = n== null ? 0f : n.floatValue();

                if(temp.get("nb") == null)
                    n = 0;
                else
                    n = ((Number) ((JSONObject) temp.get("nb")).get("doc_count"));
                float d = n== null ? 0f : n.floatValue();

                float perNN = 100 * c/(c+d);
                float perNB = 100-perNN;

                float[] values = {a,b,perNN,perNB};
                LatexNode child = new LatexNode(x.toString(), values);

                if(x.toString().matches(regex_file))
                    fileNode.addChild(child);
                else if(x.toString().matches(regex_socmedia))
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

    //TAr inn JSONobjects og gjør dem om til hashmaps, samt fyller maps i ImageGrabber for å bruke til å eksportere bilder.
    public static float[] prepareData(JSONObject obj){

        LinkedHashMap<String, String> map = new LinkedHashMap();

        float nn = ((Number) ((JSONObject) (obj.get("complexity_nn"))).get("doc_count")).floatValue();
        float nb = ((Number) ((JSONObject) (obj.get("complexity_nb"))).get("doc_count")).floatValue();
        float prosNynorsk = (float) (100 * nn / (nn + nb));

        Number nn_comp = ((Number) ((JSONObject) ((JSONObject) (obj.get("complexity_nn"))).get("complexity")).get("avg"));
        if(nn_comp==null){
            nn_comp = 0;
        }

        Number nb_comp = ((Number) ((JSONObject) ((JSONObject) (obj.get("complexity_nb"))).get("complexity")).get("avg"));
        if(nb_comp==null){
            nb_comp = 0;
        }

        //Legg til info funnet til ImageGrabber sine maps.
        return new float[]{nn_comp.floatValue(), nb_comp.floatValue(), nn,nb};
    }
}
