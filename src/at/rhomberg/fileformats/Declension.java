package at.rhomberg.fileformats;

import java.util.TreeMap;

//kvtml2 format objects

//for more information
//http://edu.kde.org/kvtml/kvtml2.dtd

public class Declension {
	public TreeMap<Integer, DeclensionBase> femaleList = new TreeMap<Integer, DeclensionBase>();  // Singular; Dual; Plural
	public TreeMap<Integer, DeclensionBase> maleList = new TreeMap<Integer, DeclensionBase>();    // Singular; Dual; Plural
	public TreeMap<Integer, DeclensionBase> neutralList = new TreeMap<Integer, DeclensionBase>(); // Singular; Dual; Plural
}
