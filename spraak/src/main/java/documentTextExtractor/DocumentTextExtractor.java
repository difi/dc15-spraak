package documentTextExtractor;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by camp-aka on 18.06.2015.
 */
public interface DocumentTextExtractor {
    public void setSource(URL url) throws IOException;
    public void setSource(String filePath) throws IOException;
    public int getNumberOfPages();
    public String getAllText() throws IOException;
    public ArrayList<String> getAllParagraphs() throws IOException;
    public ArrayList<String> getParagraphsLongerThan(int minLength) throws IOException;
}
