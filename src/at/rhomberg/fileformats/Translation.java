package at.rhomberg.fileformats;

//kvtml2 format objects

//for more information
//http://edu.kde.org/kvtml/kvtml2.dtd

public class Translation {
    // translation id is the TreeMap key; id of the translated text
	public String text = ""; // text which should be translated or translated text
	public Grade grade = new Grade(); // shows the progress of learning
	public Conjugation conjugation = new Conjugation();
	public Declension declension = new Declension();
	public String comment = ""; // information about the text
	public String pronunciation = ""; // with phonetic symbols
	public String example = "";
	public String paraphrase = "";
}
