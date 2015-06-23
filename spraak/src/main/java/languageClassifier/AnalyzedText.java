package languageClassifier;

import javax.xml.soap.Text;

/**
 * Created by camp-lsa on 22.06.2015.
 */
public class AnalyzedText {
    public TextComplexity complexity;
    public String language;
    public AnalyzedText(String lng, TextComplexity Score){
        language = lng;
        complexity = Score;
    }
}
