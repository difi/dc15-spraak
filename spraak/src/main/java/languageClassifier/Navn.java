package languageClassifier;

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
			int lowComp = 0, highComp = 0;
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
	    			continue;
	    		}
	    		
	    		String toAnalyze = line.substring(2);
	    		AnalyzedText result = Classifier.classify(toAnalyze);
	    		if(result.language.equals("nn") )
	    			nn++;
	    		else
	    			nb++;
				if(result.complexity.LIX < 30){
					lowComp++;
					}
				else{
					highComp++;
				}
	    		}
	    	br.close();
	    	
	    	float avg = 100*nn/actual_nn;
	    	float avg_nb = 100*nb/actual_nb;
			System.out.println("AVG nn: " + avg +"%");
			System.out.println("AVG nb: " + avg_nb +"%");
			System.out.println("Complex: " + highComp);
			System.out.println("Not Complex: " + lowComp);
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


 