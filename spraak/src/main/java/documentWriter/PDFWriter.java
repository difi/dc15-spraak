package documentWriter;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by camp-lsa on 26.06.2015.
 */
public class PDFWriter {

    public final static String includePath = "C:\\Users\\camp-lsa\\latex";
    public final static String outputPath = "C:\\Users\\camp-lsa\\test";
    public static String report = "";

    //Header til latex-koden. Pakker osv.
    final static String header =""+
            "\\documentclass[pdflatex]{article}\n " +
            "\\usepackage[latin1]{inputenc}\n  " +
            "\\usepackage[norsk]{babel}\n"+
            "\\usepackage{changepage}\n"+
            "\\usepackage{graphicx}\n"+
            "\\begin{document}\n";
    //Footer til latex-koden.
    final static String footer = " \\end{document}";
    final static String newLine = "\\newline \n";


    //returnerer \section, \subsection etc avhengig av dybde.
    public static String getSectionType(int depth, String title){
        String sub = "";
        for(int i = 0; i < depth; i++)
            sub += "sub";

        return "\\"+sub+"section{"+title+"}";
    }

    public static DecimalFormat f = new DecimalFormat("##.##");
    public static String bolden(String s) {
        return "\\textbf{"+s+"}";
    }
    //itererer over map hentet fra elasticsearch og legger det til rapporten som latexkode.
    public static void createReport(LatexNode node, int depth) {
        report +="\\begin{adjustwidth}{"+depth+"em}{0pt}";
        report += getSectionType(depth,node.getName())+"\n";

        report += bolden("Antall bokm{\\aa}l: ") + f.format(node.getValues()[3]) + newLine;
        report += bolden("Antall nynorsk: ")+ f.format(node.getValues()[2]) + newLine;
        report += bolden("Kompleksitet Bokm{\\aa}l: ") + f.format(node.getValues()[1]) + newLine;
        report += bolden("Kompleksitet Nynorsk: ")+ f.format(node.getValues()[0]) + newLine;
        report += node.getImages() + "\n";

        if(node.children.size() > 0)
            for(LatexNode n : node.children)
                createReport(n,depth+1);

        report +="\\end{adjustwidth}";
    }


    public static long getTimeSinceWrite(){
        return System.currentTimeMillis()-time;
    }

    public static long time = 0;
    //Printer en rapport og returnerer lokaliseringen til strengen.
    public static String getReport() {
         String title = "\\author{Difi}\n \\title{Sammendrag av spr{\\aa}bruk \n i statlige kilder.}" +
                "\n\\maketitle\n";
        try {
            time = System.currentTimeMillis();
            String document = header;
            document += title;
            document += report;
            document += footer;
            System.out.println(report);
            String command = "cmd.exe /c cd spraak & pdflatex \""+ document + "\" -job-name="+ time + " -disable-installer -quiet -output-directory=LatexFolder -include-directory=resources/LatexIncludes";

            Process cmd = Runtime.getRuntime().exec(command);
            BufferedReader br = new BufferedReader(new InputStreamReader(cmd.getInputStream()));

            String line;
            while((line = br.readLine()) != null) {
                System.out.println(line);
            }
            br.close();

            return "LatexFolder\\"+time+".pdf";
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
