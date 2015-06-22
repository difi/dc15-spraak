package documentTextExtractor;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

/**
 * Created by camp-aka on 18.06.2015.
 */
public class WordDocExtractor implements DocumentTextExtractor {

    XWPFWordExtractor extractor;
    XWPFDocument doc;

    WordDocExtractor() {
        extractor = null;
        doc = null;
    }

    WordDocExtractor(URL url) throws IOException {
        setSource(url);
    }

    WordDocExtractor(String filePath) throws IOException {
        setSource(filePath);
    }
    public void setSource(URL url) throws IOException {
        doc = new XWPFDocument(url.openStream());
        extractor = new XWPFWordExtractor(doc);
    }

    public void setSource(String filePath) throws IOException {
        doc = new XWPFDocument(new FileInputStream(filePath));
        extractor = new XWPFWordExtractor(doc);
    }

    // Might not always work as expected because page numbers in metadata might be outdated,
    // As they are updated when the document is edited in MS Word.
    public int getNumberOfPages() {
        return doc.getProperties().getExtendedProperties().getUnderlyingProperties().getPages();
    }

    public String getAllText() throws IOException {
        return extractor.getText();
    }

    public ArrayList<String> getAllParagraphs() throws IOException {
        List<XWPFParagraph> paragraphs = doc.getParagraphs();
        ArrayList<String> paragraphText = new ArrayList<>();
        for (XWPFParagraph p : paragraphs) {
            paragraphText.add(p.getText());
        }
        return paragraphText;
    }

    public ArrayList<String> getParagraphsLongerThan(int minLength) throws IOException {
        ArrayList<String> paragraphs = getAllParagraphs();
        paragraphs.removeIf(p -> p.trim().length() < minLength);
        return paragraphs;
    }

    public int getNumberOfWords() throws IOException {
        return getAllText().split("[\\s]+").length;
    }

    public void closeDoc() throws IOException {
        // Do nothing. AutoCloseable.
    }

}
