package at.rhomberg.fileformats;

import java.util.ArrayList;
import java.util.TreeMap;

//kvtml2 format objects

//for more information
//http://edu.kde.org/kvtml/kvtml2.dtd

public class LessonContainer {
	public String name = ""; // title of the lesson
	public boolean inPractice = false; // true: if a practice was interrupted
    public ArrayList<LessonContainer> lessonContainerList = new ArrayList<LessonContainer>();
	public ArrayList<Integer> entryList = new ArrayList<Integer>(); // entry ids of an item in a lesson
}
