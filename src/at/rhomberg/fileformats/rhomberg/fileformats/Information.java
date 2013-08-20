package at.rhomberg.fileformats.rhomberg.fileformats;

import java.util.Date;

//kvtml2 format objects

//for more information
//http://edu.kde.org/kvtml/kvtml2.dtd

public class Information {
	public String generator = ""; // name of the generator
	public String title = ""; // title of the file
	public String author = ""; // author of the file
	public String contact = ""; // e-mail address of the author or phone number
	public String license = ""; // license of the file
	public String comment = ""; //
	public Date date = new Date(); // last alteration date e.g. 2012-12-03T22:25:55
	public String category = ""; // e.g. language
}
