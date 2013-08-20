package at.rhomberg.fileformats.rhomberg.fileformats;

//kvtml2 format objects

//for more information
//http://edu.kde.org/kvtml/kvtml2.dtd

public class Translation {
    // translation id is the TreeMap key; id of the translated text
	public String text = ""; // text which should be translated or translated text
	public Grade grade = new Grade();   // shows the progress of learning
	public Conjugation conjugation = new Conjugation();
	public Declension declension = new Declension();
	public String comment = ""; // information about the text
	public String pronunciation = "";   // with phonetic symbols
	public String example = "";
	public String paraphrase = "";
	//
	//public String falseFriend = ""; with attr id, id is unique
	public String antonym = "";
	public String synonym = "";
	public Comparison comparison = new Comparison();
	public MultipleChoice multipleChoice = new MultipleChoice();
	public String image = "";   // path to the sound file, relative paths are recommend
	public String sound = "";   // path to the sound file, relative paths are recommend
}