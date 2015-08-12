package no.difi.camp.spraak.languageClassifier;

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

    @Override
    public String toString(){
        return language + ": " + complexity + " - " + confidence;
    }
}
