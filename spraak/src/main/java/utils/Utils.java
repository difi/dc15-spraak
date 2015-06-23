package utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by camp-mli on 23.06.2015.
 */
public class Utils {

    public static String clean(String s){

        // Remove multiple spaces and tabs
        s = s.replace("\n","").replace("\r","");
        s = s.trim().replaceAll(" +"," ");
        s = s.trim().replaceAll("\t+", " ");
        s = s.toLowerCase();

        // Remove https and url's
        String urlPattern = "((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern p = Pattern.compile(urlPattern,Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(s);
        int i = 0;
        while (m.find()) {
            s = s.replaceAll(m.group(i),"").trim();
            i++;
        }
        // Remove hashtag
        s = s.replaceAll("#[A-Za-z]+","");

        //Remove twitter @
        s = s.replaceAll("@[A-Za-z]+","");

        return s;
    }
}
