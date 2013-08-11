package at.rhomberg.fileformats;

import java.util.Date;
import java.util.TreeMap;

// kvtml2 format objects

// for more information
// http://edu.kde.org/kvtml/kvtml2.dtd

public class FileFormats {
	public Information info = new Information(); // information about the file
	public TreeMap<Integer, Identifier> identifierList = new TreeMap<Integer, Identifier>();
	public TreeMap<Integer, Entry> entryList = new TreeMap<Integer, Entry>(); // entries; contains the text which should be translated and the translated text in other languages
	public TreeMap<Integer, LessonContainer> lessonContainerList = new TreeMap<Integer, LessonContainer>(); // contains lessons
	public TreeMap<Integer, WordTypesContainer> wordTypesContainerList = new TreeMap<Integer, WordTypesContainer>();
	
	// not supported by kvtml2; only for the program
	public String objectType = ""; // type of the data e.g. kvtml2
	public boolean changed = false; // is true if no changes in a file are needed
	public boolean fileExists = false; // is true if a file is created on a data medium
}

/*
not supported


identifiertype
sizehint

leitnerboxes

deactivated


<!ELEMENT translation    (text?, comment?, pronunciation?, falsefriend?, antonym?, synonym?, example?, paraphrase?, comparison?, conjugation*, grade?, image?, multiplechoice?, sound?) >
falsefriend
falsefriend fromid
antonym
synonym

comparison
absolute
comparative
superlative

multiplechoice
choice

image
sound

*/