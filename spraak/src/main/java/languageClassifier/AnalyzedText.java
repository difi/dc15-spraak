package languageClassifier;

import javax.xml.soap.Text;

/**
 * Created by camp-lsa on 22.06.2015.
 */
public class AnalyzedText {
    public TextComplexity complexity;
    public String language;
    public float confidence;
    public AnalyzedText(String lng, TextComplexity Score, float confidence){
        language = lng;
        complexity = Score;
        this.confidence = confidence;
    }
}
