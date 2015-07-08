package documentWriter;


import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by camp-lsa on 26.06.2015.
 */
public class PDFWriter {
    public static DecimalFormat f = new DecimalFormat("##.##");
    public static String report = "";

    //Header til latex-koden. Pakker osv.
    final static String header =
            "\\documentclass[pdflatex]{article}\n " +
            "\\usepackage[latin1]{inputenc}\n  " +
            "\\usepackage[norsk]{babel}\n"+
            "\\usepackage{changepage}\n"+
            "\\usepackage{graphicx}\n"+
            "\\graphicspath{{/LatexFolder//}}\n"+
            "\\begin{document}\n";

    //Footer til latex-koden.
    final static String footer = " \\end{document}";
    final static String newLine = " \\newline \n";


    //returnerer \section, \subsection etc avhengig av dybde.
    public static String getSectionType(int depth, String title){
        String sub = "";
        for(int i = 0; i < depth; i++)
            sub += "sub";
        return "\\"+sub+"section{"+title+"}";
    }


    //Gjør tekst bold i latex.
    public static String bolden(String s) {
        return "\\textbf{"+s+"}";
    }


    //itererer over map hentet fra elasticsearch og legger det til rapporten som latexkode.
    public static void createReport(LatexNode node, int depth) {
        //report += newLine+node.getImages(); //"\\includegraphics{Sammendraglanekassenpie}";
        pw.println(getSectionType(depth,node.getName()));
        pw.println(bolden("Antall bokm{\\aa}l: ") + f.format(node.getValues()[3])+"\\newline") ;
        pw.println(bolden("Antall nynorsk: ")+ f.format(node.getValues()[2])+"\\newline");
        pw.println(bolden("Kompleksitet Bokm{\\aa}l: ") + f.format(node.getValues()[1]) +"\\newline");
        pw.println(bolden("Kompleksitet Nynorsk: ")+ f.format(node.getValues()[0])+"\\newline");
        pw.println(node.getImages());

        if(node.children.size() > 0)
            for(LatexNode n : node.children)
                createReport(n,depth+1);
    }

    public static long getTimeSinceWrite(){
        return System.currentTimeMillis()-time;
    }

    private static PrintWriter pw;
    public static long time = 0;
    //Printer en rapport og returnerer lokaliseringen til strengen.
    public static String getReport(LatexNode l) {
         String title = "\\author{Difi}\n \\title{Sammendrag av spr{\\aa}bruk \n i statlige kilder.}" +
                "\n\\maketitle\n";
        try {
            pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(new File("C:\\Users\\camp-lsa\\IdeaProjects\\dc15-spraak\\spraak\\temp.tex"))));
                pw.println(header);
                pw.println(title);
                createReport(l,0);
                pw.println(footer);
            pw.close();

            time = System.currentTimeMillis();

            String command = "cmd.exe /c cd spraak & pdflatex temp.tex -job-name="+ time + " -quiet -output-directory=LatexFolder -include-directory=resources/LatexIncludes";

            System.out.println(command);
            Process cmd = Runtime.getRuntime().exec(command);
            BufferedReader br = new BufferedReader(new InputStreamReader(cmd.getInputStream()));
            System.out.println("Got here....");

            String line;
            while((line = br.readLine()) != null) {
                System.out.println(line);
            }
            br.close();
            cmd.waitFor();

            return "LatexFolder\\"+time+".pdf";
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
