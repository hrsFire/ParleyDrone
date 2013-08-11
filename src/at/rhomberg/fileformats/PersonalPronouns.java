package at.rhomberg.fileformats;

//kvtml2 format objects

//for more information
//http://edu.kde.org/kvtml/kvtml2.dtd

public class PersonalPronouns {
	public boolean maleFemaleDifferent = false; // default value
	public boolean neutralExists = false; // default value; only exist if maleFemaleDifferent is true
	public boolean dualExists = false; // default value;
	public PersonalPronounsBase singular = new PersonalPronounsBase();
	public PersonalPronounsBase dual = new PersonalPronounsBase(); // only exists if dualExists is true
	public PersonalPronounsBase plural = new PersonalPronounsBase();
}
