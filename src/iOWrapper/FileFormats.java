package iOWrapper;

import java.util.ArrayList;
import java.util.Date;

// kvtml format objects

// for more information
// http://edu.kde.org/kvtml/kvtml2.dtd

public class FileFormats {
	public Information info = new Information(); // information about the file
	public ArrayList<Identifier> identifierList = new ArrayList<Identifier>();
	public ArrayList<Entry> entryList = new ArrayList<Entry>(); // entries; contains the text which should be translated and the translated text in other languages
	public ArrayList<LessonContainer> lessonContainerList = new ArrayList<LessonContainer>(); // contains lessons
	public ArrayList<WordTypesContainer> wordtypesContainerList = new ArrayList<WordTypesContainer>();
	
	public String objectType = ""; // type of the data e.g. kvtml
}

class Information {
	public String generator = ""; // name of the generator
	public String title = ""; // title of the file
	public String author = ""; // author of the file
	public String contact = ""; // e-mail address of the author or phone number
	public String license = ""; // license of the file
	public String comment = ""; //
	public Date date; // last alteration date e.g. 2012-12-03T22:25:55
	public String category = ""; // e.g. language
}

class ArticleDefinite {
	public String male = "";
	public String female = "";
	public String neutral = "";
}

class ArticleSingular {
	public ArticleDefinite definite = new ArticleDefinite();
	public ArticleDefinite indefinite = new ArticleDefinite();
}

class ArticlePlural {
	public ArticleDefinite definite = new ArticleDefinite();
	public ArticleDefinite indefinite = new ArticleDefinite();
}

class Article {
	public ArticleSingular articleSingular = new ArticleSingular();
	public ArticlePlural articleList = new ArticlePlural();
}

class PersonalPronounsBase { // for singular, dual and plural
	public String firstPerson = "";
	public String secondPerson = "";
	public String thirdPersonMale = ""; // if maleFemaleDifferent is false only thirdPersonMale will be used (without thirdPersonFemale)
	public String thirdPersonFemale = "";
	public String thirdPersonNeutralCommon = ""; // exists only if neutralExists is true
}

class PersonalPronouns {
	public boolean maleFemaleDifferent = false; // default value;
	public boolean neutralExists = false; // default value; only exist if maleFemaleDifferent is true
	public boolean dualExists = false; // default value;
	public PersonalPronounsBase singular = new PersonalPronounsBase();
	public PersonalPronounsBase dual = new PersonalPronounsBase(); // only exists if dualExists is true
	public PersonalPronounsBase plural = new PersonalPronounsBase();
}

class Identifier {
	public int id = -1;
	public String name = ""; // language e.g. English
	public String locale = ""; // short form of the language e.g. en
	public Article article = new Article();
	public PersonalPronouns personalPronouns = new PersonalPronouns();
	public ArrayList<String> tenseList = new ArrayList<String>(); // user input
}

class Entry {
	public int id = -1; // entry id
	public ArrayList<Translation> translationList = new ArrayList<Translation>(2); // contains the text which should be translated and the translated text in other languages
}

class TranslationSingular {
	public String firstPerson = "";
	public String secondPerson = "";
	public String thirdPersonNeutralCommon = "";
}

class TranslationPlural {
	public String firstPerson = "";
	public String secondPerson = "";
	public String thirdPersonNeutralCommon = "";
}

class Conjugation {
	public String tense = ""; // tense; user input
	public TranslationSingular singular = new TranslationSingular();
	public TranslationPlural plural = new TranslationPlural();
}

class DeclensionBase { // for female, male and neutral
	public String nominative = "";
	public String genitive = "";
	public String dative = "";
	public String accusative = "";
	public String ablative = "";
	public String locative = "";
	public String vocative = "";
}

class Declension {
	public ArrayList<DeclensionBase> femaleList = new ArrayList<DeclensionBase>(); // Singular; Dual; Plural
	public ArrayList<DeclensionBase> maleList = new ArrayList<DeclensionBase>();
	public ArrayList<DeclensionBase> neutralList = new ArrayList<DeclensionBase>();
}

class Translation {
	public int id = -1; // id of the translated text
	public String text = ""; // text which should be translated or translated text
	public Grade grade = new Grade(); // shows the progress of learning
	public Conjugation conjugation = new Conjugation();
	public Declension declesion = new Declension();
	public String comment = ""; // information about the text
	public String pronunciation = ""; // with phonetic symbols
	public String example = "";
	public String paraphrase = "";
}

class Grade {
	public int currentGrade = 0; // default value; number of first time successful
	public int count = 0; // default value; number of learning; Grade is only set in the file if count is >= 1
	public int errorCount = 0; // default value; errors during learning
	public Date date; // last learning date e.g. 2012-12-03T22:25:55
}

class LessonContainer {
	public String name = ""; // title of the lesson
	public boolean inpractice = false; // true: if a practice was interrupted
	public ArrayList<Integer> entryList = new ArrayList<Integer>(); // entry id of an item in a lesson
}

class WordTypesContainer {
	public String name = ""; // e.g. Nomen
	public String specialWord = ""; // e.g. noun -> noun/male
	public ArrayList<WordTypesContainerEntry> lessonContainerList = new ArrayList<WordTypesContainerEntry>(); // contains the id of the text which should be translated and the id of the translated text in other languages
}

class WordTypesContainerEntry {
	public int entryId = -1; // id of the text which should be translated
	public int translateId = -1; // id of the translated text
}