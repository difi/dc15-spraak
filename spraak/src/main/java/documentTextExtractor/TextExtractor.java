package documentTextExtractor;

import connectors.ElasticConnector;
import org.json.simple.JSONObject;
import java.io.File;
import java.net.URL;
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
            if (path.startsWith("http://")) {
                extractor.setSource(new URL(path));
            }
            else {
                extractor.setSource(path);
            }

            JSONObject json = new JSONObject();
            ArrayList<String> paragraphs = extractor.getParagraphsLongerThan(5);

            db.partOf();

            for (String paragraph : paragraphs) {
                json.put("name", path.substring(path.replaceAll("\\\\","/").lastIndexOf("/") + 1, path.lastIndexOf(".")));
                json.put("filetype",path.substring(path.lastIndexOf(".") + 1, path.length()));
                json.put("type", "file");
                json.put("text",paragraph);
                json.put("words",getNumberOfWords(paragraph));
            }

            db.partOfClose();
            extractor.closeDoc();
            db.write(json);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return;
    }

    int getNumberOfWords(String string) {
        return string.split("[.,:;!?\\s]+").length;
    }
}
