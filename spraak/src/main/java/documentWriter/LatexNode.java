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

    public ArrayList<LatexNode> children = new ArrayList();
    private float complexity_nn=0f, complexity_nb=0f, percent_nn=0f, percent_nb=0f;
    private String name =null;
    public LatexNode(float comp_nn, float comp_nb, float percent_nn, float percent_nb){
        this.complexity_nn = comp_nn;
        this.complexity_nb = comp_nb;
        this.percent_nn=percent_nn;
        this.percent_nb=percent_nb;
    }
    public LatexNode(String name, float[] values){
        this(values[0], values[1], values[2], values[3]);
        this.name = name;
    }
    public float[] getValues(){
        return new float[]{complexity_nn, complexity_nb, percent_nn, percent_nb};
    }

    public void addChild(LatexNode node){
        children.add(node);
    }
    public void sumChildren(){
        complexity_nb = 0;
        complexity_nn = 0;
        percent_nb = 0;
        percent_nn = 0;
        for(LatexNode n : children){
            if(n.children.size() > 0)
                n.sumChildren();
            float[] values = n.getValues();
            complexity_nn   +=values[0];
            complexity_nb   +=values[1];
            percent_nn      +=values[2];
            percent_nb      +=values[3];
        }
        complexity_nn/=children.size();
        complexity_nb/=children.size();
        percent_nn/=children.size();
        percent_nb/=children.size();
    }
    public String toString(){
        String string = this.name + ": "+complexity_nn + " - " + complexity_nb + " | " + percent_nn + " - " + percent_nb+"\n";
        return string;
    }

    public void print(){
        System.out.println(this);
        for(LatexNode n : children)
            n.print("\t");
    }
    public void print(String s){
        System.out.println(s + this);
        for(LatexNode n : children)
            n.print(s+"\t");
    }
    public String getName(){
        if(this.name.equals("fb"))
            return "facebook";
        else
            return this.name;
    }
    static Set<String> not_pie_applicable = new HashSet<String>(Arrays.asList());
    static Set<String> spline_applicable = new HashSet<String>(Arrays.asList("Filer","Sosiale Medier"));

    public String getImages(){
        String LatexImages = "";
        if(!not_pie_applicable.contains(this.name)){
            if(ImageGrabber.grabPieChart(this,"Andel bokmål og nynorsk",this.getName().replace(" ","")))
                LatexImages+="\\centerline{\\includegraphics{"+this.getName().replace(" ","")+"pie"+ ".png}}\n";
        }
        if(spline_applicable.contains(this.name)){
            if(ImageGrabber.grabSplineChart(this.children,"Variasjon i Kompleksitet",this.getName().replace(" ","")))
                LatexImages+="\\centerline{\\includegraphics[scale=0.5]{"+this.getName().replace(" ","")+"spline"+ ".png}}\n";
        }
        return LatexImages;
    }
}
