import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Camp-AKA on 17.06.2015.
 */
public class PdfExtractor {
    private PDDocument pddoc;
    private PDFTextStripper textStripper;

    PdfExtractor(URL url) throws IOException {
        pddoc = PDDocument.load(url);
        textStripper = new PDFTextStripper();
    }

    PdfExtractor(String filePath) throws IOException {
        PDFParser parser = new PDFParser(new FileInputStream(new File(filePath)));
        parser.parse();
        pddoc = new PDDocument(parser.getDocument());
        textStripper = new PDFTextStripper();
    }

    public String getText(int pageFrom, int pageTo) throws IOException {
        setTextStripperPageBounds(pageFrom, pageTo);
        return textStripper.getText(pddoc);
    }

    // Might fail when pdf is large
    public String getAllText() throws IOException {
        return getText(0,pddoc.getNumberOfPages());
    }

    public int numberOfPages() {
        return pddoc.getNumberOfPages();
    }

    private void setTextStripperPageBounds(int startPage, int endPage) {
        textStripper.setStartPage(startPage);
        textStripper.setEndPage(endPage);
    }

    // Returns an ArrayList of strings, text is split on splitString.
    public static ArrayList<String> splitString(String text, String splitString) {
         return new ArrayList<>(Arrays.asList(text.split(splitString)));
    }

    // Returns an ArrayList with the paragraphs in the text that are longer than minLength if removeShortParagraphs is true.
    // splitString should be unique enough to not appear in the original PDF.
    public ArrayList<String> getLongParagraphs(String splitString, int dropThreshold,
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

    // Returns an ArrayList with all paragraphs, regardless of their length.
    // splitString should be unique enough to not appear in the original PDF.
    public ArrayList<String> getAllParagraphs(String splitString, int dropThreshold) throws IOException {
        return getLongParagraphs(splitString, dropThreshold, false, 0);
    }

    // Returns an ArrayList with all the pages in the document.
    // splitString should be unique enough to not appear in the original PDF.
    public ArrayList<String> getAllPages(String splitString) throws IOException {
        setTextStripperPageBounds(0, pddoc.getNumberOfPages());
        textStripper.setPageEnd(splitString);
        String allText = textStripper.getText(pddoc);
        ArrayList<String> pages = splitString(allText, splitString);
        return pages;
    }

}


