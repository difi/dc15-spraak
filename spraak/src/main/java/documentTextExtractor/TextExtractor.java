package documentTextExtractor;

import com.google.common.collect.Queues;
import connectors.ElasticConnector;
import languageClassifier.AnalyzedText;
import languageClassifier.Classifier;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import utils.Utils;

import java.io.File;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

/**
 * Created by camp-aka on 22.06.2015.
 */
public class TextExtractor implements Runnable {
    private ArrayList<String> settings;
    private DocumentObject single_file;
    public static ElasticConnector db;

    public TextExtractor(String url, ElasticConnector db){
        this.db = db;
        single_file = new DocumentObject(url, db.getOwner());
    }

    @Override
    public void run() {
        if(single_file != null){
            handleFile(single_file);
        }
        if (this.settings != null && !this.settings.isEmpty()) {
            for (String path : this.settings) {
                ArrayList<String> filesToCheck = walk(path);
                for (String filePath : filesToCheck) {
                    handleFile(new DocumentObject(filePath, db.getOwner()));
                }
            }
        }
    }


    /*
    Adds all files under a directory to an ArrayList recursively and returns it.
     */
    public ArrayList<String> walk(String path) {
        File root = new File(path);
        File[] list = root.listFiles();
        ArrayList<String> filePaths = new ArrayList<>();
        if (list == null) {
            return new ArrayList<String>();
        }
        for (File f : list) {
            if (f.isDirectory()) {
                filePaths.addAll(walk(f.getAbsolutePath()));
            }
            else {
                filePaths.add(f.getAbsolutePath());
            }
        }
        return filePaths;
    }

    public static void handleFile(DocumentObject o) {
        DocumentTextExtractor extractor;
        String path = o.source;
        path = path.toLowerCase();
        if (path.endsWith(".pdf")) {
            extractor = new PdfExtractor();
        }
        else if(path.endsWith(".doc")) {
            extractor = new DocExtractor();
        }
        else if (path.endsWith(".docx")){
            extractor = new DocxExtractor();
        }
        else if(path.endsWith(".odt")) {
            extractor = new OdtExtractor();
        }
        else {
            System.err.println("File format " + path.substring(path.lastIndexOf("."),path.length()) + " not supported.");
            System.err.println("in " + path);
            return;
        }

        try {
            if (path.startsWith("http://") || path.startsWith("https://")) {
                URLConnection urlConn = new URL(path).openConnection();
                // Some paths link to corrupted files, or are redirected to html-documents.
                if (urlConn.getContentType() != null && ! urlConn.getContentType().contains("text/html")) {
                    try {
                        extractor.setSource(new URL(path));
                    }catch(javax.net.ssl.SSLHandshakeException e){
                        return;
                    }catch(IndexOutOfBoundsException e){
                        //System.out.println("Index out of bounds");
                        //System.out.println(path);
                        return;
                    }catch(java.io.IOException e) {
                        //System.out.println("IOException");
                        //System.out.println(path);
                        return;
                    }
                }
                else {
                    System.err.println("File " + path + " is not a readable file.");
                    return;
                }
            }
            else {
                try {
                    extractor.setSource(path);
                }catch(java.io.IOException e){
                    return;
                }
            }

            JSONObject json = new JSONObject();
            int docNumberOfWords = Utils.getNumberOfWords(extractor.getAllText());
            ArrayList<String> paragraphs = paragraphs = extractor.getParagraphsLongerThan(300);

            if(docNumberOfWords == 0) {
                return;
            }

            if (paragraphs.size() == 0) {
                paragraphs.add(extractor.getAllText());
            }

            int creationYear = extractor.getCreationYear();
            String type = extractor.isForm() ? "form" : "file";
            String title = extractor.getTitle();
            extractor.closeDoc();

            db.partOfOpen();
            json = new JSONObject();
            json.put("title", (title != null ? title : path.substring(path.replaceAll("\\\\","/").lastIndexOf("/") + 1, path.lastIndexOf("."))));
            json.put("filetype",path.substring(path.lastIndexOf(".") + 1, path.length()));
            json.put("type", type);
            json.put("post_year", creationYear);
            json.put("owner",o.owner);
            json.put("site", o.source);
            String text = "";
            Integer wordCount = 0;
            Float complexity = 0f;
            Float confidence = 0f;
            Map<String,JSONObject> map = new HashMap();
            Float amt = (float) paragraphs.size();
            String langs =  "";
            for (String paragraph : paragraphs) {
                Classifier classifier = new Classifier();
                AnalyzedText analysis = classifier.classify(paragraph);
                wordCount   += (int) analysis.complexity.wordCount;
                complexity  += analysis.complexity.LIX;
                confidence  += analysis.confidence;
                text += paragraph;

                JSONObject obj = new JSONObject();
                if(!map.containsKey(analysis.language)){
                    obj.put("complexity",analysis.complexity.LIX);
                    obj.put("confidence",analysis.confidence);
                    obj.put("count",1);
                    langs+=analysis.language+"\n";
                }else{
                    obj = map.get(analysis.language);
                    obj.put("complexity", ((Float) obj.get("complexity")) + analysis.complexity.LIX);
                    obj.put("confidence", ((Float) obj.get("confidence")) + analysis.confidence);
                    obj.put("count",((int) obj.get("count"))+1);
                }
                map.put(analysis.language,obj);
            }

            confidence /= amt;
            complexity /= amt;
            json.put("lang",null);
            Integer cur = 0;

            for(String key : map.keySet()){
                JSONObject obj = map.get(key);
                obj.put("complexity", Float.parseFloat(obj.get("complexity")+"")/Float.parseFloat(obj.get("count")+""));
                map.put(key,obj);
                if(Integer.parseInt(obj.get("count")+"") > cur) {
                    json.put("lang", key);
                    cur = Integer.parseInt(obj.get("count")+"");
                }
            }
            try{
                json.put("ratio",Float.parseFloat(map.get(json.get("lang")).get("count")+"")/amt);
            }catch(Exception e){
                System.out.println("Could not write the following to db: "+ path);
            }
            json.put("languages",map);
            json.put("confidence",confidence);
            json.put("complexity",complexity);
            json.put("words", wordCount);
            json.put("text",text);




            db.write(json);
            db.partOfClose();

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return;
    }
}
