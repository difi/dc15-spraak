package documentTextExtractor;

import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.text.Paragraph;

import javax.swing.text.html.HTMLDocument;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * Created by camp-aka on 23.06.2015.
 */
public class OdtExtractor implements DocumentTextExtractor {
    TextDocument doc;
    String textContent;

    public OdtExtractor() {
        doc = null;
        textContent = null;
    }

    public OdtExtractor(String filePath) throws IOException {
        setSource(filePath);
        textContent = null;
    }

    public OdtExtractor(URL url) throws IOException {
        setSource(url);
        textContent = null;
    }
    @Override
    public void setSource(URL url) throws IOException {
        try {
            doc = TextDocument.loadDocument(url.openStream());
        }
        catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public void setSource(String filePath) throws IOException {
        try {
            doc = TextDocument.loadDocument(filePath);
        }
        catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public int getNumberOfPages() throws IOException {
        try {
            // Average number of words per page is 250. It will have to do for now.
            return (int) Math.ceil(getNumberOfWords() / 250.00);
        }
        catch (Exception e) {
            throw new IOException(e.getMessage());
        }

    }

    @Override
    public String getAllText() throws IOException {
        try {
            if (textContent == null) {
                return doc.getContentRoot().getTextContent();
            }
            return textContent.trim();
        }
        catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public ArrayList<String> getAllParagraphs() throws IOException {
        Iterator<Paragraph> p = doc.getParagraphIterator();
        ArrayList<String> paragraphs = new ArrayList<>();
        while(p.hasNext()) {
            String par = p.next().getTextContent();
            if (!par.isEmpty()) {
                paragraphs.add(par);
            }
        }
        return paragraphs;
    }

    @Override
    public ArrayList<String> getParagraphsLongerThan(int minLength) throws IOException {
        ArrayList<String> paragraphs = getAllParagraphs();
        paragraphs.removeIf(p -> p.length() < minLength);
        return paragraphs;
    }

    @Override
    public int getNumberOfWords() throws IOException {
        return  getAllText().split("[.,:;!?\\s]+").length;
    }

    @Override
    public void closeDoc() throws IOException {
        doc.close();
    }
}
