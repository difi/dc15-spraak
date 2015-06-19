package Main;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

import com.carrotsearch.labs.langid.DetectedLanguage;


/**
 * Created by camp-lsa on 17.06.2015.
 */
public class Navn {
    public Navn(){
    	long time = System.currentTimeMillis();
    	try{
    		String FILE = "C:\\Users\\camp-lsa\\Documents\\DIFI_TWITTER.txt";
	    	
    		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(FILE), "utf-8"));
    		String line;
    		float nn = 0, nb = 0, actual_nb = 0, actual_nn = 0; 
    		
	    	while((line = br.readLine()) != null){
	    		if(line.length() < 2)
	    			continue;
	    		String actualLanguage = line.substring(0, 2);
	    		if(actualLanguage.equals("NN"))
	    			actual_nn++;
	    		else if(actualLanguage.equals("NB"))
	    			actual_nb++;
	    		else{
	    			System.out.println(actualLanguage);
	    			continue;
	    		}
	    		
	    		String toAnalyze = line.substring(2);
	    		String result = Classifier.classify(toAnalyze);
	    		if(result.equals("nn") )
	    			nn++;
	    		else
	    			nb++;
	    		}
	    	br.close();
	    	
	    	float avg = nn/actual_nn;
	    	float avg_nb = nb/actual_nb;
	    	System.out.println("Classified nn: " + nn);
	    	System.out.println("nn in file: " + actual_nn);
	    	System.out.println("Classified nb:" + nb);
	    	System.out.println("nb in file: " + actual_nb);
	    	System.out.println(((int) (avg*100))+ "%");
	    	System.out.println(((int) (avg_nb*100))+ "%");
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    	}
    	long time1 = System.currentTimeMillis();
    	System.out.println(time1-time + "ms");
    }
    public static void main(String... args) {
        new Navn();
    }
  
}


 