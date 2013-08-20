package at.rhomberg.fileformats.rhomberg.fileformats;

import java.util.ArrayList;

//kvtml2 format objects

//for more information
//http://edu.kde.org/kvtml/kvtml2.dtd

public class WordTypesContainer {
    public final static String NOUN = "noun";
    public final static String NOUN_MALE = "noun/male";
    public final static String NOUN_FEMALE = "noun/female";
    public final static String NOUN_NEUTRAL = "noun/neutral";
    public final static String VERB = "verb";
    public final static String ADJECTIVE = "adjective";
    public final static String ADVERB = "adverb";


    public String name = ""; // e.g. Nomen
	public String specialWordType = ""; // e.g. noun -> noun/male
    public boolean inPractice = false;
    public ArrayList<WordTypesContainer> wordTypeContainerList = new ArrayList<WordTypesContainer>();
}

