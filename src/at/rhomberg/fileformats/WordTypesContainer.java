package at.rhomberg.fileformats;

import java.util.TreeMap;

//kvtml2 format objects

//for more information
//http://edu.kde.org/kvtml/kvtml2.dtd

public class WordTypesContainer {
	public String name = ""; // e.g. Nomen
	public String specialWord = ""; // e.g. noun -> noun/male
	public TreeMap<Integer, WordTypesContainerEntry> lessonContainerList = new TreeMap<Integer, WordTypesContainerEntry>(); // contains the ids of the texts which should be translated and the ids of the translated texts in other languages
    // hash table
}

