package documentWriter;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by camp-lsa on 01.07.2015.
 */
public class LatexNode {

    static Set<String> not_pie_applicable = new HashSet<String>(Arrays.asList());
    static Set<String> spline_applicable = new HashSet<String>(Arrays.asList("Sosiale Medier"));
    public LatexNode parent;
    public ArrayList<LatexNode> children = new ArrayList();
    private float
            complexity_nn=0f,
            complexity_nb=0f,
            percent_nn=0f,
            percent_nb=0f;

    private String name = null;

    public LatexNode(float comp_nn, float comp_nb, float percent_nn, float percent_nb){
        this.complexity_nn  = comp_nn;
        this.complexity_nb  = comp_nb;
        this.percent_nn     = percent_nn;
        this.percent_nb     = percent_nb;
    }

    public LatexNode(String name, float[] values){
        this(values[0], values[1], values[2], values[3]);
        this.name = name;
    }

    public float[] getValues(){
        return new float[]{complexity_nn, complexity_nb, percent_nn, percent_nb};
    }

    public void addChild(LatexNode node){
        children.add(node); node.parent = this;
    }

    public String toString(){
        return complexity_nn + " - " + complexity_nn + " | " + percent_nn + " - " + percent_nb;
    }

    //Rekursiv funksjon som summerer verdier for alle barn-noder. Summen av denne nodens barn blir denne nodens verdier.
    public void sumChildren(){
        complexity_nb   = 0;
        complexity_nn   = 0;
        percent_nb      = 0;
        percent_nn      = 0;
        for(LatexNode n : children){
            if(n.children.size() > 0)
                n.sumChildren();
            float[] values = n.getValues();
            complexity_nn   +=values[0];
            complexity_nb   +=values[1];
            percent_nn      +=values[2];
            percent_nb      +=values[3];
        }
        complexity_nn   /=children.size();
        complexity_nb   /=children.size();
        percent_nn      /=children.size();
        percent_nb      /=children.size();
    }

    //returnerer navnet til noden.
    public String getName(){
        if(this.name.equals("fb"))
            return "facebook";
        else if(this.name.equals("web"))
            return "Nettsider";
        else if(this.name.equals("file"))
            return "Filer";
        else
            return this.name;
    }

    //Henter navn for bilde. Erstatter æ,ø og å med unicode.
    public String getImageName(){
        String name = "";
        LatexNode n = this;
        while(n!= null){
            name = n.getName()+name;
            n = n.parent;
        }
        return name.replaceAll("\u00E5|\u00E6|\u00F8","");
    }

    //Kaller chart-funksjoner hos ImageGrabber returnerer latex for bildene.
    public String getImages(){
        String LatexImages = "";
        if(!not_pie_applicable.contains(this.name))
            if(ImageGrabber.grabPieChart(this,"Andel bokmål og nynorsk",this.getImageName()))
                LatexImages+="\\centerline{\\includegraphics[scale=0.5]{"+this.getImageName()+"pie"+ "}}";

        if(spline_applicable.contains(this.name)){
            if(!LatexImages.equals(""))
                LatexImages+="\n";
            if(ImageGrabber.grabSplineChart(this.children, "Variasjon i Kompleksitet", this.getImageName()))
                LatexImages+="{\\centerline{\\includegraphics{LatexFolder/"+this.getImageName()+"spline" + ".png}}}";
        }

        return LatexImages;
    }
}
