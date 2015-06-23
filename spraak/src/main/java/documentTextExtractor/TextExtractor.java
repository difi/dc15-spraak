package documentTextExtractor;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by camp-aka on 22.06.2015.
 */
public class TextExtractor implements Runnable {
    private ArrayList<String> settings;

    public TextExtractor(ArrayList<String> settings) {
        this.settings = settings;
    }

    @Override
    public void run() {
        if (this.settings.isEmpty()) {
            System.err.println("No text extractor settings found.");
            return;
        }

        for (String path : this.settings) {
            ArrayList<String> filesToCheck = walk(path);
            for (String filePath : filesToCheck) {
                handleFile(filePath);
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
        else if (path.endsWith(".docx")){
            extractor = new WordDocExtractor();
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

            System.out.println("Reading " + path);

            // TODO get text and put it somewhere here
            extractor.closeDoc();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
