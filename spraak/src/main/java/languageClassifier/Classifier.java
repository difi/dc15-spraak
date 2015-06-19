package languageClassifier;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.carrotsearch.labs.langid.DetectedLanguage;
import com.carrotsearch.labs.langid.LangIdV3;

public class Classifier {
	
		
	private static LangIdV3 langid;
	
	
	//Sett modell til � kun lete etter nynorsk og bokm�l.
	private static void init() throws IOException{
		Set<String> set = new HashSet<String>(Arrays.asList(new String[] {"nb"}));
		float[] ptc, pc;
		short[] dsa;
		int[][] dsaOutput;
		
		langid = new LangIdV3();
		loadConfig();
		rule_set = dictionaries.get("default");
	}
	
	//Laster default config "config.ini"
	public static void loadConfig() throws IOException{
		loadConfig("resources/config.ini","default");
	}
	
	//map som inneholder alle regelsett som er lagret.
	static Map<String, RuleSet> dictionaries = new HashMap();
	
	//Laster inn en config-fil.
	public static void loadConfig(String path,String name) throws IOException{
		if(dictionaries.containsKey(name)){
			return;
		}
		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
			String line = "";
			RuleSet ruleset = new RuleSet();
			String current = "";
			while((line = br.readLine()) != null){
				line = line.replace(" ", "");
				if(line.startsWith("//"))
					continue;
				else if(line.startsWith("_")){
					current = line.substring(1);
					System.out.println(current);
					continue;
				}
				else{
					if(line.isEmpty()){
						
					}
					else if(current.equals("hele")){
						ruleset.hele.add(line);
					}else if(current.equals("endinger")){
						ruleset.endinger.add(line);
					}else if(current.equals("hele_bm")){
						ruleset.hele_bm.add(line);
					}else if(current.equals("endinger_bm")){
						ruleset.endinger_bm.add(line);
					}else if(current.equals("exempt")){
						ruleset.exempt.add(line);
					}
				}
			}
			br.close();
			System.out.println(ruleset.hele.size());
			System.out.println(ruleset.endinger.size());
			System.out.println(ruleset.hele_bm.size());
			System.out.println(ruleset.endinger_bm.size());
			System.out.println(ruleset.exempt.size());
			dictionaries.put(name, ruleset);
			
		}
		catch(IOException e){
			throw(e);
		}
	}
	//hent mulige regelsett.
	public Set<String> getRuleSets(){
		return dictionaries.keySet();
	}
	
	//Skift regler for nynorsk. Returnerer False om det ikke ble gjort.
	public boolean setRuleSet(String name){
		if(dictionaries.containsKey(name))
		{
			rule_set = dictionaries.get(name);
			return true;
		}
		return false;
	}
	static RuleSet rule_set;
	//klassifiserer tekst, bruker 'hjemmesnekra' static-class "ShortClassifier" om teksten er under 300 tegn lang.
	public static String classify(String str) throws IOException{
		if(langid == null){
			init();
		}
		int length = str.length();
		if(length < 300){
			return ShortClassifier.classify(str,rule_set);
		}
		else{
			DetectedLanguage result = langid.classify(str, true);
			return result.getLangCode();
		}
	}
	

}