package languageClassifier;

import java.util.ArrayList;

public class RuleSet {
	//hele ord nynorsk
	public  ArrayList<String> hele = new ArrayList<String>(),
	//endinger nynorsk
	endinger = new ArrayList<String>(),
	//hele ord bokm�l
	hele_bm = new ArrayList<String>(),
	//endinger bokm�l
	endinger_bm = new ArrayList<String>(),
	//ignorerte ord(endinger)
	exempt = new ArrayList<String>();
}
