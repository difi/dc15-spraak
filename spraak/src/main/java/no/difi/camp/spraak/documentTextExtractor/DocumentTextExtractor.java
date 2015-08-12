package no.difi.camp.spraak.documentTextExtractor;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by camp-aka on 18.06.2015.
 */
public interface DocumentTextExtractor {
    public void setSource(URL url) throws IOException;
    public void setSource(String filePath) throws IOException;
    public int getNumberOfPages() throws IOException;
    public String getAllText() throws IOException;
    public ArrayList<String> getAllParagraphs() throws IOException;
    public ArrayList<String> getParagraphsLongerThan(int minLength) throws IOException;
    public int getNumberOfWords() throws IOException;
    public void closeDoc() throws IOException;
    public int getCreationYear() throws IOException;
    public String getTitle() throws IOException;
    // Returns true for everything except PDF without input fields
    public boolean isForm() throws IOException;
}
