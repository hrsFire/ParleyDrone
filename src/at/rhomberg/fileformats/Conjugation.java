package at.rhomberg.fileformats;

//kvtml2 format objects

//for more information
//http://edu.kde.org/kvtml/kvtml2.dtd

public class Conjugation {
	public String tense = ""; // tense; user input
	public TranslationSingularPlural singular = new TranslationSingularPlural();
	public TranslationSingularPlural plural = new TranslationSingularPlural();
}
