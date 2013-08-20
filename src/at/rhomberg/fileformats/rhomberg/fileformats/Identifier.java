package at.rhomberg.fileformats.rhomberg.fileformats;

import java.util.ArrayList;

//kvtml2 format objects

//for more information
//http://edu.kde.org/kvtml/kvtml2.dtd

public class Identifier {
    // identifier id is the TreeMap key
	public String name = ""; // language e.g. English
	public String locale = ""; // short form of the language e.g. en; predetermined
    public String comment = "";
    public int sizeHint = -1;
	public Article article = new Article();
	public PersonalPronouns personalPronouns = new PersonalPronouns();
	public ArrayList<String> tenseList = new ArrayList<String>(); // user input; optional
}
