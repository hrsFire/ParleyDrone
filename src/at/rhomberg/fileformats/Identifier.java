package at.rhomberg.fileformats;

import java.util.TreeMap;

//kvtml2 format objects

//for more information
//http://edu.kde.org/kvtml/kvtml2.dtd

public class Identifier {
    // identifier id is the TreeMap key
	public String name = ""; // language e.g. English
	public String locale = ""; // short form of the language e.g. en; predetermined
	public Article article = new Article();
	public PersonalPronouns personalPronouns = new PersonalPronouns();
	public TreeMap<Integer, String> tenseList = new TreeMap<Integer, String>(); // user input; optional
}
