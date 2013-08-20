package at.rhomberg.fileformats.rhomberg.fileformats;

import java.util.ArrayList;

//kvtml2 format objects

//for more information
//http://edu.kde.org/kvtml/kvtml2.dtd

public class Container {
    public final static String NOUN = "noun";
    public final static String NOUN_MALE = "noun/male";
    public final static String NOUN_FEMALE = "noun/female";
    public final static String NOUN_NEUTRAL = "noun/neutral";
    public final static String VERB = "verb";
    public final static String ADJECTIVE = "adjective";
    public final static String ADVERB = "adverb";

    public String name = ""; // e.g. Nomen, title of the lesson
	public String specialWordType = ""; // e.g. noun,  noun/male
    public boolean inPractice = false; // true: if a practice has been interrupted
    public ArrayList<Container> container = new ArrayList<Container>();
	public ArrayList<Integer> entryList = new ArrayList<Integer>(); // e.g. entry ids of an item in a lesson
	public String image = "";
}