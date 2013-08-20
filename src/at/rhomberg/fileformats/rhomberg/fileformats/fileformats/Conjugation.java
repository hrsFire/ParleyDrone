package at.rhomberg.fileformats.rhomberg.fileformats.fileformats;

//kvtml2 format objects

//for more information
//http://edu.kde.org/kvtml/kvtml2.dtd

public class Conjugation {
	public String tense = ""; // tense; user input
	public TranslationSingularDualPlural singular = new TranslationSingularDualPlural();
    public TranslationSingularDualPlural dual = new TranslationSingularDualPlural();
	public TranslationSingularDualPlural plural = new TranslationSingularDualPlural();
}
