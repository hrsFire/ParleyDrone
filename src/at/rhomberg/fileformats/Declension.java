package at.rhomberg.fileformats;

// kvtml2 format objects

// for more information
// http://edu.kde.org/kvtml/kvtml2.dtd

public class Declension {
	public DeclensionFemaleMaleNeutralList femaleList = new DeclensionFemaleMaleNeutralList();  // Singular; Dual; Plural
	public DeclensionFemaleMaleNeutralList maleList = new DeclensionFemaleMaleNeutralList();    // Singular; Dual; Plural
	public DeclensionFemaleMaleNeutralList neutralList = new DeclensionFemaleMaleNeutralList(); // Singular; Dual; Plural
}
