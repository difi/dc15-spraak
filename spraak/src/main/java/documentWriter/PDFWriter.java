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

    public final static String outputPath = "C:\\Users\\camp-lsa\\latex";
    public static String report = "";

    final static String header =""+
            "\\documentclass[pdflatex]{article}\n " +
            "\\usepackage[latin1]{inputenc}\n  " +
            "\\usepackage{changepage}\n"+
            "\\usepackage{graphicx}\n"+
            "\\begin{document}\n";
    final static String footer = " \\end{document}";
    final static String newLine = "\\newline \n";

    public static void main(String... args){
        Map<String, Map> m = new LinkedHashMap<String, Map>();
        Map<String, Map> m1 = new LinkedHashMap<String, Map>();
        Map<String, String> n = new LinkedHashMap<String, String>();
        n.put("Mengde Nynorsk","20%");
        n.put("Mengde Bokmål","80%");
        n.put("Kompleksitet Nynorsk","37 LIX");
        n.put("Kompleksitet Bokmål","38 LIX");
        m1.put("Norge.no", n);
        m.put("Side", m1);

        m1 = new LinkedHashMap<String, Map>();
        n = new LinkedHashMap<String, String>();
        n.put("Mengde Nynorsk","40%");
        n.put("Mengde Bokmål","60%");
        n.put("Kompleksitet Nynorsk","15 LIX");
        n.put("Kompleksitet Bokmål","14 LIX");
        m1.put("twitter.no/difi", n);
        m.put("Sosiale Medier", m1);
        setMap(m,0);
        getReport();
    }
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
        try {
            long time = System.currentTimeMillis();
            String document = header;
            document += report;
            document += footer;
            System.out.println(header);
            System.out.println(report);
            String command = "cmd.exe /c cd C:\\Users\\camp-lsa\\test & pdflatex \""+ document + "\" -job-name="+ time + " -include-directory="+outputPath;  //+" -quiet -disable-installer"
            System.out.println("\n\n WRITING \n****************************************");
            Process cmd = Runtime.getRuntime().exec(command);
            BufferedReader br = new BufferedReader(new InputStreamReader(cmd.getInputStream()));
            String line = "";
            while((line = br.readLine()) != null)
                System.out.println(line);
            br.close();
            return outputPath+"\\"+time+".pdf";
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
