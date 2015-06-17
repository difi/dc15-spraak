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

    PdfExtractor() {
        pddoc = null;
        textStripper = null;
    }

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
        textStripper.setStartPage(pageFrom);
        textStripper.setEndPage(pageTo);
        return  textStripper.getText(pddoc);
    }

    // Might fail when pdf is large
    public String getAllText() throws IOException {
        return getText(0,pddoc.getNumberOfPages());
    }

    public int numberOfPages() {
        return pddoc.getNumberOfPages();
    }
}


