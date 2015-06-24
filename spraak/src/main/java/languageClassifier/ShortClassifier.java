package languageClassifier;

import java.util.ArrayList;

public class ShortClassifier {
	//Gå igjennom hvert ord og se om det har en endelse fra regelboka. Gå til neste straks et er funnet.
	//Returnerer antall ord i regelboka funnet i teksten.
	public static float check_endings(ArrayList<String> endinger_bm, String[] text){
	    int i = 0;

	    String word; 
	    float count = 0;
	    for(String check : endinger_bm){
	        i = 0;
	        while(i < text.length && count < endinger_bm.size()){
	            word = text[i];
	            i++;
	            if(!ruleset.exempt.contains(word) && word.endsWith(check) && word.length()>check.length()){
	            	count++;
	                break;
	            }
	        }
	    }
	    return count;
	}

	//Gå igjennom hvert ord ov se om det finnes. Gå til neste straks et er funnet.
	//Returnerer antall ord i regelboka funnet i teksten.
	public static float check_words(ArrayList<String> words, String[] text){
	    int i = 0;
	    String word;
	    int count = 0;
	    for(String check : words){
	        i = 0;
	        while(i < text.length && count < words.size()){
	            word = text[i];
	            i++;
	            if(word.equals(check)){
	                count++;
	                break;
	            }
	        }
	    }
	    return count;
	}
	
	
	//Sjekker endinger og helord, for så å returnere ratioen mellom antall ord ikke gjenkjent og totalt antall ord.
	public static float[] check_text(ArrayList<String> endinger_bm, ArrayList<String> hele_bm, String[] text){
		float[] values = {0.0f,0.0f}; 
		float li = endinger_bm.size() - check_endings(endinger_bm, text);
		float lis = hele_bm.size() - check_words(hele_bm, text);
		if(li == 0)
			values[0] = 0;
		else
			values[0] = ((float) endinger_bm.size() - li)/(float) endinger_bm.size();
		values[1] = ((float) hele_bm.size() - lis)/(float) hele_bm.size();
		return values;
	}
	
	
	static RuleSet ruleset;
	//Klassifiserer en gitt tekst basert på et gitt regelverk
	//Om mer enn 70% av gjenkjente ord er nynorske antar teksten å være nynorsk.
	//Om teksten ikke har gjenkjente ord antas teksten å være bokmål.
	public static String classify(String text, RuleSet ruleset){
		ShortClassifier.ruleset = ruleset;
		String[] text_array = text.toLowerCase().split(" ");

		float[] result = check_text(ruleset.endinger, ruleset.hele, text_array);	
		float[] result_bm = check_text(ruleset.endinger_bm, ruleset.hele_bm, text_array);
		
		float combined = result[0] + result[1];
		float combined_bm = result_bm[0] + result_bm[1];
		

		float percent = combined/(combined+combined_bm);

		if(combined + combined_bm == 0f)
			return "nb";
		else if(percent > 0.7f)
			return "nn";
		else
			return "nb";
	}




}
