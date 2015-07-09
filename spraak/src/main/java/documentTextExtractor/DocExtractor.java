package documentTextExtractor;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import utils.Utils;

import javax.print.Doc;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by camp-aka on 23.06.2015.
 */
public class DocExtractor implements DocumentTextExtractor {
    HWPFDocument doc;
    WordExtractor extractor;

    public DocExtractor() {
        doc = null;
        extractor = null;
    }

    public DocExtractor(URL url) throws IOException {
        setSource(url);
    }

    public DocExtractor(String filePath) throws IOException {
        setSource(filePath);
    }

    @Override
    public void setSource(URL url) throws IOException {
        doc = new HWPFDocument(url.openStream());
        extractor = new WordExtractor(doc);
    }

    @Override
    public void setSource(String filePath) throws IOException {
        doc = new HWPFDocument(new FileInputStream(filePath));
        extractor = new WordExtractor(doc);
    }

    @Override
    public int getNumberOfPages() {
        return doc.getSummaryInformation().getPageCount();
    }

    @Override
    public String getAllText() throws IOException {
        return extractor.getText().trim();
    }

    @Override
    public ArrayList<String> getAllParagraphs() throws IOException {
        return new ArrayList<String>(Arrays.asList(extractor.getParagraphText()));
    }

    @Override
    public ArrayList<String> getParagraphsLongerThan(int minLength) throws IOException {
        ArrayList<String> allParagraphs = getAllParagraphs();
        allParagraphs.removeIf(p -> p.trim().length() < minLength);
        return allParagraphs;
    }

    @Override
    public int getNumberOfWords() throws IOException {
        return Utils.getNumberOfWords(getAllText());
    }

    @Override
    public void closeDoc() throws IOException {
        extractor.close();
    }
}
