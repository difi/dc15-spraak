package no.difi.camp.spraak.languageClassifier;

/**
 * Created by camp-lsa on 22.06.2015.
 */
public class TextComplexity {
    public float wordCount = 0f, stops = 0f, longWords = 0f;

    public float LIX;

    public TextComplexity(){
        this.wordCount  = -1;
        this.longWords  = -1;
        this.stops      = -1;
        this.LIX        = -1;
    }

    public TextComplexity(String text) {
        int curWord = 0;
        for (Character c : text.toCharArray()) {
            if (c == ' ' || ("" + c).matches((".*[.,?!:]"))) {
                if (curWord > 6)
                    longWords++;
                if (curWord > 0)
                    wordCount++;
                curWord = 0;
                if(("" + c).matches((".*[.,?!:]")))
                    stops++;
            } else {
                if(c.isUpperCase(c))
                    stops++;
                curWord++;
            }
        }
        if (curWord > 6)
            longWords++;
        if (curWord > 0)
            wordCount++;

        if (stops != 0)
            this.LIX = ((wordCount) / (stops)) + ((longWords * 100) / wordCount);
        else if (stops != 0 && wordCount == 0)
            this.LIX = 0f;
        else if (stops == 0 && wordCount != 0)
            this.LIX = ((longWords) * 100) / wordCount;
    }

}
