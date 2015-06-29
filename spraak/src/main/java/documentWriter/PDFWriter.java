package documentWriter;

import Server.MainClass;
import org.apache.pdfbox.pdfwriter.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.text.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by camp-lsa on 26.06.2015.
 */
public class PDFWriter {

    public final static String includePath = "C:\\Users\\camp-lsa\\latex";
    public final static String outputPath = "C:\\Users\\camp-lsa\\test";
    public static String report = "";

    final static String header =""+
            "\\documentclass[pdflatex]{article}\n " +
            "\\usepackage[latin1]{inputenc}\n  " +
            "\\usepackage[norsk]{babel}\n"+
            "\\usepackage{changepage}\n"+
            "\\usepackage{graphicx}\n"+
            "\\begin{document}\n";
    final static String footer = " \\end{document}";
    final static String newLine = "\\newline \n";

    public static String getSectionType(int depth, String title){
        String sub = "";
        for(int i = 0; i < depth; i++)
            sub += "sub";
        return "\\"+sub+"section{"+title+"}";
    }
    public static void setMap(Map contents, int depth) {
        report +="\\begin{adjustwidth}{"+depth+"em}{0pt}";
        for (Object s : contents.keySet()){

            if (contents.get(s) instanceof HashMap) {

                report += getSectionType(depth,(String)s)+"\n";

                setMap((HashMap<String, Object>) contents.get(s),depth+1);
            } else {
                String segment = ((String) contents.get(s)).replace("å", "{\\aa}").replace("%", "\\%");
                report += "\\textbf{" + ((String) s).replace("å", "{\\aa}") + "}: " + segment + newLine;
            }
        }
        report +="\\end{adjustwidth}";

    }


    private static long lastWritten = -1;
    public static String getReport() {
         String title = "\\author{Difi}\n \\title{Sammendrag av spr{\\aa}bruk \n i statlige kilder.}" +
//                "\n " +
                "\n\\maketitle\n";
        try {
            long time = System.currentTimeMillis();
            String document = header;
            document += title;
            document += report;
            document += footer;
            /*
            System.out.println(header);
            System.out.println(report);*/
            String command = "cmd.exe /c cd spraak & pdflatex \""+ document + "\" -job-name="+ time + " -disable-installer -quiet -output-directory=LatexFolder -include-directory=resources/LatexIncludes";
            System.out.println("\n\n WRITING \n****************************************");
            Process cmd = Runtime.getRuntime().exec(command);
            BufferedReader br = new BufferedReader(new InputStreamReader(cmd.getInputStream()));
            String line;
            while((line = br.readLine()) != null) {
                System.out.println(line);
            }
            System.out.println("ENDED");
            br.close();
            return "LatexFolder\\"+time+".pdf";
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
