package Main;

import java.util.ArrayList;

public class ShortClassifier {

	public static ArrayList<String> deepCopy(String[] t){
		ArrayList<String> x = new ArrayList<String>();
		for(String s: t)
			x.add(s);
		return x;
	}
	
	public static boolean contains(String[] array, String word){
		for(String check : array)
			if(check.equals(word))
				return true;
		return false;
	}
	public static float check_endings(String[] endings, String[] text){
	    int i = 0;
	    ArrayList<String> w = deepCopy(endings);
	    String word; 
	    float count = 0;
	    for(String check : endings){
	        i = 0;
	        while(i < text.length && count < endings.length){
	            word = text[i];
	            i++;
	            if(!contains(ClassifierWords.exempt, word) && word.endsWith(check) && word.length()>check.length()){
	                if(w.contains(check)){
	                    count++;
	                }
	                break;
	            }
	        }
	    }
	    return count;
	}

	public static float check_words(String[] words, String[] text){
	    int i = 0;
	    ArrayList<String> w = deepCopy(words);
	    String word;
	    int count = 0;
	    for(String check : words){
	        i = 0;
	        while(i < text.length && count < words.length){
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
	
	
	public static float[] check_text(String[] endings, String[] words, String[] text){
		float[] values = {0.0f,0.0f}; 
		float li = endings.length - check_endings(endings, text);
		float lis = words.length - check_words(words, text);
//		if(endings == ClassifierWords.endinger_bm)
//			System.out.println("NB");
//		else
//			System.out.println("NN");
//		System.out.println(li + " - " + lis);
		if(li == 0)
			values[0] = 0;
		else
			values[0] = ((float) endings.length - li)/(float) endings.length;
		values[1] = ((float) words.length - lis)/(float) words.length;
		return values;
	}
	
	public static String classify(String text){
		String[] text_array = text.toLowerCase().split(" ");

		float[] result = check_text(ClassifierWords.endinger, ClassifierWords.hele, text_array);	
		float[] result_bm = check_text(ClassifierWords.endinger_bm, ClassifierWords.hele_bm, text_array);
		
		float combined = (result[0]/ClassifierWords.endinger.length) 
				+ ((result[1]*(float)ClassifierWords.hele_bm.length)/(float) ClassifierWords.hele.length);
		float combined_bm = result_bm[0] + ((result_bm[1]*(float)ClassifierWords.hele.length)/(float)ClassifierWords.hele_bm.length);
		
		float percent = combined/(combined+combined_bm);
		
		if(combined + combined_bm == 0f)
			return "nb";
		else if(percent > 0.7f)
			return "nn";
		else
			return "nb";

	}
	
	
	
	
}
