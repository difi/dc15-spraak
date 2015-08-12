package no.difi.camp.spraak;

import no.difi.camp.spraak.documentTextExtractor.*;
import org.junit.Test;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Created by camp-aka on 25.06.2015.
 */

public class ExtractorTests {

    @Test
    public void emptyFilesShouldReturnEmptyThings() throws IOException {
        DocumentTextExtractor extractor = null;

        String[] emptyFiles = { "emptyDocx.docx", "emptyDoc.doc", "emptyPdf.pdf", "emptyOdt.odt"};

        for (String file : emptyFiles) {
            if (file.endsWith(".pdf")) {
                extractor = new PdfExtractor();
            }
            else if (file.endsWith(".docx")){
                extractor = new DocxExtractor();
            }
            else if (file.endsWith(".doc")){
                extractor = new DocExtractor();
            }
            else if (file.endsWith(".odt")){
                extractor = new OdtExtractor();
            }

            extractor.setSource("src/test/resources/" + file);

            assertTrue("Empty file must return empty ArrayList with paragraphs longer than 1", extractor.getParagraphsLongerThan(1).isEmpty());
            assertTrue("Empty file must return empty string of all text. Fails for " + file, extractor.getAllText().isEmpty());
            extractor.closeDoc();
        }
    }

    @Test
    public void filesWithContentShouldReturnContent() throws IOException {
        DocumentTextExtractor extractor = null;

        String[] contentFiles = {"content.docx","content.doc", "content.odt", "content.pdf"};

        for (String file : contentFiles) {
            if (file.endsWith(".pdf")) {
                extractor = new PdfExtractor();
            }
            else if (file.endsWith(".docx")){
                extractor = new DocxExtractor();
            }
            else if (file.endsWith(".doc")){
                extractor = new DocExtractor();
            }
            else if (file.endsWith(".odt")){
                extractor = new OdtExtractor();
            }

            extractor.setSource("src/test/resources/" + file);

            assertTrue("Number of words must be 22", extractor.getNumberOfWords() == 22);
            assertTrue("Number of pages must be 1", extractor.getNumberOfPages() == 1);
            extractor.closeDoc();
        }
    }


}
