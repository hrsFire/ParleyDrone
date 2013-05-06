package at.rhomberg.parleydrone;

import java.util.ArrayList;
import java.util.Date;

// kvtml format objects
public class FileFormats {
	public information info = new information();
	public ArrayList<identifier> identifierList = new ArrayList<identifier>();
	public ArrayList<entry> entryList = new ArrayList<entry>();
	public lessons lessons = new lessons();
	
	// type of the data
	public String objectType = "";
}

class information {
	public String converter = "";
	public String title = "";
	public String author = "";
	public String contact = "";
	public String license = "";
	public String comment = "";
	public Date date;
	public String category = "";
}

class identifier {
	public int id = -1;
	public String name = "";
	public String locale = "";
}

class entry {
	public int id = -1;
	public translation translationList[] = new translation[2];
}

class translation {
	public int id = -1;
	public String text = "";
	public grade grade = new grade();
}

class grade {
	public int currentgrade = 1; // default values
	public int count = 1; // default values
	public int errorcount = 0; // default values
	public Date date;
}

class lessons {
	ArrayList<container> container = new ArrayList<container>();
}

class container {
	public String name = "";
	public boolean inpractice = false;
	ArrayList<Integer> entry = new ArrayList<Integer>();
}

class wordtypes {
	// not implemented yet
}