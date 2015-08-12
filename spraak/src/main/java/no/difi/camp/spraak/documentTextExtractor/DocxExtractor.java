package no.difi.camp.spraak.documentTextExtractor;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import no.difi.camp.spraak.utils.Utils;

/**
 * Created by camp-aka on 18.06.2015.
 */
public class DocxExtractor implements DocumentTextExtractor {

    XWPFWordExtractor extractor;
    XWPFDocument doc;

    public DocxExtractor() {
        extractor = null;
        doc = null;
    }

    public DocxExtractor(URL url) throws IOException {
        setSource(url);
    }

    public DocxExtractor(String filePath) throws IOException {
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
        return extractor.getText().trim();
    }

    public ArrayList<String> getAllParagraphs() throws IOException {
        List<XWPFParagraph> paragraphs = doc.getParagraphs();
        ArrayList<String> paragraphText = new ArrayList<String>();
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
        return Utils.getNumberOfWords(getAllText());
    }

    public void closeDoc() throws IOException {
        // Do nothing. AutoCloseable.
    }

    public int getCreationYear() throws IOException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
        return Integer.parseInt(dateFormat.format(doc.getProperties().getCoreProperties().getCreated()));
    }

    public String getTitle() throws IOException {
        return doc.getProperties().getCoreProperties().getTitle();
    }

    /*
   Everything except PDF without input fields is considered to be a form.
    */
    public boolean isForm() throws IOException {
        return true;
    }
}
