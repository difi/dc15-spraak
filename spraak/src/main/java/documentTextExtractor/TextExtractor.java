package documentTextExtractor;

import connectors.ElasticConnector;
import org.json.simple.JSONObject;
import utils.Utils;

import java.io.File;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by camp-aka on 22.06.2015.
 */
public class TextExtractor implements Runnable {
    private ArrayList<String> settings;
    private String single_file;
    private ElasticConnector db;

    public TextExtractor(ArrayList<String> settings, ElasticConnector database) {
        this.settings = settings;
        this.db = database;
        this.db.setType("file");
    }

    public TextExtractor(String url, ElasticConnector db){
        single_file = url;
        this.db = db;
    }
    @Override
    public void run() {
        if(single_file != null){
            handleFile(single_file);
        }
        else if (this.settings != null && !this.settings.isEmpty()) {
            for (String path : this.settings) {
                ArrayList<String> filesToCheck = walk(path);
                for (String filePath : filesToCheck) {
                    handleFile(filePath);
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

    public void handleFile(String path) {
        DocumentTextExtractor extractor;
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
            ArrayList<String> paragraphs = extractor.getParagraphsLongerThan(300);
            int creationYear = extractor.getCreationYear();
            String type = extractor.isForm() ? "form" : "file";
            int docNumberOfWords = Utils.getNumberOfWords(extractor.getAllText());
            extractor.closeDoc();

            db.partOfOpen();

            for (String paragraph : paragraphs) {
                json = new JSONObject();
                json.put("name", path.substring(path.replaceAll("\\\\","/").lastIndexOf("/") + 1, path.lastIndexOf(".")));
                json.put("filetype",path.substring(path.lastIndexOf(".") + 1, path.length()));
                json.put("type", type);
                json.put("text",paragraph);
                json.put("words", docNumberOfWords);
                json.put("postYear", creationYear);
                db.write(json);
            }

            db.partOfClose();

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return;
    }
}
