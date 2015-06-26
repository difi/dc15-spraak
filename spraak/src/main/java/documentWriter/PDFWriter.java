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
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by camp-lsa on 26.06.2015.
 */
public class PDFWriter {
    public PDFWriter(){
        PDDocument doc = new PDDocument();
        PDFont font = PDType1Font.TIMES_ROMAN;
        try{
            PDPage page = new PDPage();
            doc.addPage(page);

            PDRectangle box = page.findMediaBox();
            float margin = 72;
            float width = box.getWidth()-margin-margin;
            float startX = box.getLowerLeftX() + margin;
            float startY = box.getUpperRightY()-margin;
            PDPageContentStream contents = new PDPageContentStream(doc,page);
            contents.beginText();
                contents.setFont(font,20);
                contents.moveTextPositionByAmount(startX,startY);
                contents.drawString("Yoyoyoyo");
                contents.endText();
            contents.close();
            doc.save("C:\\Users\\camp-lsa\\Documents\\"+ System.currentTimeMillis() +".pdf");
            doc.close();

        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    static String header = "\\documentclass[pdflatex]{article}\n " +
            "\\usepackage[latin1]{inputenc}  " +
            //"\\usepackage[utf8]{inputenc} " +
            "\\usepackage{graphicx}" +
            "\\begin{document}";
    static String footer = " \\end{document}";
    static String newLine = "\\newline \n";
    private static long lastWritten = -1;
    public static String getReport(HashMap<String,String> content){
        //TODO: bestem syntaks for content og skriv ut det i stedet.

        long time = System.currentTimeMillis();
        if(lastWritten != -1 && time-lastWritten < 20000) {
            System.out.println("Time since last write is below 20s");
            return "C:\\users\\camp-lsa\\test\\" + lastWritten + ".pdf";
        }
        try {
            for(String key : content.keySet()){

            }

            String document = header;
            document += "\\textbf{Statistikk for bruk av nynorsk og bokm{\\aa}l p{\\aa} statlige sider:} " + newLine;
            document += "Tekster p{\\aa} nynorsk:  " + MainClass.nn + newLine;
            document += "Tekster p{\\aa} Bokm{\\aa}l: " + MainClass.nb + newLine;
            document += newLine;
            document += "Kompleksitet Nynorsk: " + ((int) (MainClass.total_nn_complexity /(MainClass.nn == 0 ? 1 : MainClass.nn))) +newLine;
            document += "Kompleksitet Bokm{\\aa}l: " + ((int) (MainClass.total_nb_complexity / (MainClass.nb == 0 ? 1 : MainClass.nb))) +newLine;
            document += "\\begin{figure}[b!p]\\centering\\includegraphics[scale=0.2]{bilde}\\end{figure}";
            document += footer;
            document.replaceAll("å","{\\aa}");
            String command = "cmd.exe /c cd C:\\Users\\camp-lsa\\test & pdflatex \""+ document + "\" -job-name="+time + " -include-directory=C:\\Users\\camp-lsa\\latex";
            System.out.println(command.replaceAll("\n",""));
            System.out.println();


            Process cmd = Runtime.getRuntime().exec(command);
            BufferedReader br = new BufferedReader(new InputStreamReader(cmd.getInputStream()));
            String line ="";
            while((line = br.readLine()) != null){
                System.out.println(line);
            }
            br.close();
            lastWritten =time;
            return "C:\\users\\camp-lsa\\test\\"+time+".pdf";
            /*
            Scanner c = new Scanner(cmd.getInputStream());
            cmd.getOutputStream().write("cd C:\\Users\\camp-lsa\\test \r\n".getBytes());
            cmd.getOutputStream().flush();
            cmd.getOutputStream().write("dir > o.txt \r\n".getBytes());
            cmd.getOutputStream().flush();*/
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public static void main(String... args){

        System.out.println("Writing pdf...");
        getReport(null);

    }
}
