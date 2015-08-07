package documentWriter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Created by camp-lsa on 30.06.2015.
 */
public class ImageGrabber {

    private final static String exportURL = "http://export.highcharts.com?options=";

    //Lager en JSONstreng for et piechart-bilde og kaller grabAndPrint, som henter og skriver bilde.
    public static boolean grabPieChart(LatexNode node, String title, String name){
        try {
            JSONObject obj = getJSONObject("pie", title),
                    nn = new JSONObject(),
                    nb = new JSONObject(),
                    options = new JSONObject(),
                    pie = new JSONObject(),
                    dataLabel = new JSONObject();
            JSONArray series = new JSONArray();

            float num_nn = node.getValues()[2];
            float num_nb = node.getValues()[3];

            dataLabel.put("format", "<b>{point.name}</b>:{point.percentage:.2f}{point.end}");
            pie.put("dataLabels",dataLabel);
            options.put("pie",pie);
            obj.put("plotOptions",options);

            nn.put("name","Nynorsk");
            nn.put("y",num_nn);
            nn.put("end","%");

            nb.put("name", "Bokmål");
            nb.put("y",num_nb);
            nb.put("end","%");

            JSONArray data = new JSONArray();
            data.add(nn);
            data.add(nb);
            JSONObject serie = new JSONObject();
                serie.put("data",data);
                serie.put("name","Brands");
                serie.put("colorByPoint",true);
            series.add(serie);
            obj.put("series",series);

            //Last ned og skriv bilde.
            grabAndPrint(obj.toJSONString(),name+"pie");
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static JSONObject getJSONObject(String type, String title){
        JSONObject job = new JSONObject();
            JSONObject chart = new JSONObject();
            chart.put("type",type);
            chart.put("plotBackgroundColor","white");
            //chart.put("width",300);
        job.put("chart",chart);
            JSONObject _title = new JSONObject();
            _title.put("text", title);
        job.put("title", _title);
        return job;
    }

    //Skriver JSONtekst for et splineChart og kaller grabAndPrint. Returnerer True om bilde kunne hentes/skrives.
    public static boolean grabSplineChart(ArrayList<LatexNode> nodes, String title, String name){
        try {
            JSONObject obj = getJSONObject("areaspline", title),
                    xAxis = new JSONObject(),
                    nn = new JSONObject(),
                    nb = new JSONObject(),
                    options = new JSONObject();

            JSONArray names_list = new JSONArray(),
                    nn_list = new JSONArray(),
                    nb_list = new JSONArray(),
                    series = new JSONArray();
            for(LatexNode node : nodes){
                names_list.add(node.getName());
                nn_list.add(node.getValues()[0]);
                nb_list.add(node.getValues()[1]);
            }

            xAxis.put("categories",names_list);
            obj.put("xAxis",xAxis);

            nn.put("name","Nynorsk");
            nn.put("data",nn_list);
            series.add(nn);

            nb.put("name","Bokmål");
            nb.put("data",nb_list);
            series.add(nb);

            JSONObject areaspline = new JSONObject();
            areaspline.put("fillOpacity",0.1);
            options.put("areaspline",areaspline);
            obj.put("plotOptions",options);
            obj.put("series",series);
            grabAndPrint(obj.toJSONString(),name+"spline");
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }


    //Kontakter export.highcharts.com og laster ned bilde rendret der, for så å skrive ut bilde til LatexFolder.
    public static void grabAndPrint(String text, String name) throws IOException{
        URL url = new URL(exportURL+URLEncoder.encode(text,"utf-8"));
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
        conn.setRequestMethod("POST");

        BufferedImage b = ImageIO.read(conn.getInputStream());

        String file = "spraak\\LatexFolder\\" + name + ".png";
        OutputStream out = Files.newOutputStream(Paths.get(file));
            ImageIO.write(b,"png",out);
        out.close();

    }
}
