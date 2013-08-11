package at.rhomberg.fileformats;

import java.util.Date;

//kvtml2 format objects

//for more information
//http://edu.kde.org/kvtml/kvtml2.dtd

public class Grade {
	public int currentGrade = 0; // default value; number of first time successful
	public int count = 0; // default value; number of learning; Grade is only set in the file if count is >= 1
	public int errorCount = 0; // default value; errors during learning
	public Date date = new Date(); // last learning date e.g. 2012-12-03T22:25:55
}
