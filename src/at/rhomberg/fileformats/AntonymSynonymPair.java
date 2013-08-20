package at.rhomberg.fileformats;

//kvtml2 format objects

//for more information
//http://edu.kde.org/kvtml/kvtml2.dtd

public class AntonymSynonymPair {
	public Integer[] first = new Integer[2];	// 1: entry id, 2: translation id
	public Integer[] second = new Integer[2];	// 1: entry id, 2: translation id
}