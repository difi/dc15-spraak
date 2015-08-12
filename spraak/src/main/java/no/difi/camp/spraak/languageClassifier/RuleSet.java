package no.difi.camp.spraak.languageClassifier;

import java.util.ArrayList;

public class RuleSet {
	//hele ord nynorsk
	public  ArrayList<String> hele = new ArrayList<String>(),
	//endinger nynorsk
	endinger = new ArrayList<String>(),
	//hele ord bokmål
	hele_bm = new ArrayList<String>(),
	//endinger bokmål
	endinger_bm = new ArrayList<String>(),
	//ignorerte ord(endinger)
	exempt = new ArrayList<String>(),

	//Utenlandske ord.
	foreign = new ArrayList<>();
}
