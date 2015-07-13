package documentTextExtractor;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.util.PDFTextStripper;
import utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

/**
 * Created by Camp-AKA on 17.06.2015.
 */
public class PdfExtractor implements DocumentTextExtractor {
    private PDDocument pddoc;
    private PDFTextStripper textStripper;

    // Constants
    private static final String SPLIT_STRING = "-0-=s-e-p-a-r-a-t-o-r=-0-";
    private static final int DEFAULT_DROP_THRESHOLD = 3;

    public PdfExtractor() {
        pddoc = null;
        textStripper = null;
    }

    public PdfExtractor(URL url) throws IOException {
        setSource(url);
    }

    public PdfExtractor(String filePath) throws IOException {
        setSource(filePath);
    }

    public String getText(int pageFrom, int pageTo) throws IOException {
        setTextStripperPageBounds(pageFrom, pageTo);
        return textStripper.getText(pddoc).replaceAll(SPLIT_STRING, "");
    }

    public void setSource(URL url) throws IOException {
        pddoc = PDDocument.loadNonSeq(url.openStream(), null);
        textStripper = new PDFTextStripper();
    }

    public void setSource(String filePath) throws IOException {
        PDFParser parser = new PDFParser(new FileInputStream(new File(filePath)));
        parser.parse();
        pddoc = new PDDocument(parser.getDocument());
        textStripper = new PDFTextStripper();
    }

    // Might fail when pdf is large
    public String getAllText() throws IOException {
        return getText(0,pddoc.getNumberOfPages()).trim().replace(SPLIT_STRING, "");
    }

    public int getNumberOfPages() {
        return pddoc.getNumberOfPages();
    }

    private void setTextStripperPageBounds(int startPage, int endPage) {
        textStripper.setStartPage(startPage);
        textStripper.setEndPage(endPage);
    }

    // Returns an ArrayList of strings, text is split on splitString.
    private static ArrayList<String> splitString(String text, String splitString) {
         return new ArrayList<String>(Arrays.asList(text.split(splitString)));
    }

    // Returns an ArrayList with the paragraphs in the text that are longer than minLength if removeShortParagraphs is true.
    // splitString should be unique enough to not appear in the original PDF.
    public ArrayList<String> getParagraphsLongerThan(String splitString, int dropThreshold,
                                               Boolean removeShortParagraphs, int minLength) throws IOException {
        setTextStripperPageBounds(0, pddoc.getNumberOfPages());
        textStripper.setDropThreshold(dropThreshold);
        textStripper.setParagraphEnd(splitString);
        String allText = textStripper.getText(pddoc);
        ArrayList<String> paragraphs = splitString(allText, splitString);
        if (removeShortParagraphs) {
            paragraphs.removeIf(p -> p.trim().length() < minLength);
        }
        return paragraphs;
    }

    public ArrayList<String> getParagraphsLongerThan(int minLength) throws IOException {
        return getParagraphsLongerThan(SPLIT_STRING, DEFAULT_DROP_THRESHOLD, true, minLength);
    }

    // Returns an ArrayList with all paragraphs, regardless of their length.
    // splitString should be unique enough to not appear in the original PDF.
    public ArrayList<String> getAllParagraphs(String splitString, int dropThreshold) throws IOException {
        return getParagraphsLongerThan(splitString, dropThreshold, false, 0);
    }

    public ArrayList<String> getAllParagraphs() throws IOException {
        return getAllParagraphs(SPLIT_STRING, DEFAULT_DROP_THRESHOLD);
    }

    // Returns an ArrayList with all the pages in the document.
    // splitString should be unique enough to not appear in the original PDF.
    public ArrayList<String> getAllPagesText() throws IOException {
        setTextStripperPageBounds(0, pddoc.getNumberOfPages());
        textStripper.setPageEnd(SPLIT_STRING);
        String allText = textStripper.getText(pddoc);
        ArrayList<String> pages = splitString(allText, SPLIT_STRING);
        return pages;
    }

    public Boolean containsInputFields() throws IOException {
        PDAcroForm forms = pddoc.getDocumentCatalog().getAcroForm();
        return forms != null;
    }

    public int getNumberOfWords() throws IOException {
        String allText = textStripper.getText(pddoc);
        return Utils.getNumberOfWords(allText.replaceAll(SPLIT_STRING, ""));
    }

    public void closeDoc() throws IOException {
        pddoc.close();
    }

    public int getCreationYear() throws IOException {
        return pddoc.getDocumentInformation().getCreationDate().get(Calendar.YEAR);
    }
}


