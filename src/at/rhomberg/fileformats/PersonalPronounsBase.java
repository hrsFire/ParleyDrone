package at.rhomberg.fileformats;

//kvtml2 format objects

//for more information
//http://edu.kde.org/kvtml/kvtml2.dtd

public class PersonalPronounsBase { // for singular, dual and plural
	public String firstPerson = "";
	public String secondPerson = "";
	public String thirdPersonMale = ""; // if maleFemaleDifferent is false only thirdPersonMale will be used (without thirdPersonFemale)
	public String thirdPersonFemale = "";
	public String thirdPersonNeutralCommon = ""; // exists only if neutralExists is true
}