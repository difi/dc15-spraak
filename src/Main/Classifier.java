package Main;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.carrotsearch.labs.langid.DetectedLanguage;
import com.carrotsearch.labs.langid.LangIdV3;
import com.carrotsearch.labs.langid.Model;

public class Classifier {
	
		
	private static LangIdV3 langid;
	
	
	//Sett modell til å kun lete etter nynorsk og bokmål.
	private static void init(){
		Set<String> set = new HashSet<String>(Arrays.asList(new String[] {"nb"}));
		float[] ptc, pc;
		short[] dsa;
		int[][] dsaOutput;
		
		langid = new LangIdV3();
		loadConfig();
	}
	
	public static boolean loadConfig(){
		return loadConfig("resources/config.ini");
	}
	public static boolean loadConfig(String path){
		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
			String line = "";
			while((line = br.readLine()) != null){
				String lang = line.split("=")[0];
				String[] words = line.split("=")[1].split(",");
				langid.addLangList(lang, words);
			}
			br.close();
			
		}
		catch(Exception e){
			return false;
		}

		
		return true;
	}
	
	public static String classify(String str){
		if(langid == null){
			init();
		}
		int length = str.length();
		if(length < 300){
			return ShortClassifier.classify(str);
		}
		else{
			DetectedLanguage result = langid.classify(str, true);
			return result.getLangCode();
		}
	}
	

}
