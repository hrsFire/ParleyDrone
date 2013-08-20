package at.rhomberg.fileformats.rhomberg.fileformats;

import java.util.TreeMap;

//kvtml2 format objects

//for more information
//http://edu.kde.org/kvtml/kvtml2.dtd

public class Entry {
	// entry id is the TreeMap key
    public boolean deactivated = false;
    public int sizeHint = -1;
	public TreeMap<Integer, Translation> translationList = new TreeMap<Integer, Translation>(); // contains the text which should be translated and the translated text in other languages
}
