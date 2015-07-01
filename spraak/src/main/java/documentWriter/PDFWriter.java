package documentWriter;


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
    //Henter bilde og returnerer latexkode for bilde dersom bildet kunne hentes.
    public static String getImageForName(String s){
        if(((String) s).contains("Sammendrag")){
            if(ImageGrabber.grabPieChart("Sammendrag") && ImageGrabber.grabSplineChart("Sammendrag"))
                return "\n\\includegraphics[scale=0.7]{LatexFolder/piechartSammendrag.png}\n\\includegraphics[scale=0.7]{LatexFolder/splineChartSammendrag.png}" + newLine;
        }
        if(s.contains("Filer")){
            if(ImageGrabber.grabSplineChart("Filer"))
                return "\n\\includegraphics[scale=0.7]{splineChartFiler.png}" + newLine;
        }
        return "";
    }

    //itererer over map hentet fra elasticsearch og legger det til rapporten som latexkode.
    public static void setMap(Map contents, int depth) {
        report +="\\begin{adjustwidth}{"+depth+"em}{0pt}";
        for (Object s : contents.keySet()){

            if (contents.get(s) instanceof HashMap) {
                report += getSectionType(depth,(String)s)+"\n";
                setMap((HashMap<String, Object>) contents.get(s),depth+1);
                report += getImageForName((String) s);
            } else {
                String segment = ((String) contents.get(s)).replace("å", "{\\aa}").replace("%", "\\%");
                report += "\\textbf{" + ((String) s).replace("å", "{\\aa}") + "}: " + segment + newLine;
            }
        }
        report +="\\end{adjustwidth}";
    }



    //Printer en rapport og returnerer lokaliseringen til strengen.
    public static String getReport() {
         String title = "\\author{Difi}\n \\title{Sammendrag av spr{\\aa}bruk \n i statlige kilder.}" +
                "\n\\maketitle\n";
        try {
            long time = System.currentTimeMillis();
            String document = header;
            document += title;
            document += report;
            document += footer;
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
