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
	public ArrayList<translation> translationList = new ArrayList<translation>(2);
}

class translation {
	public int id = -1;
	public String text = "";
	public grade grade = new grade();
	
	//
	//
	// add new columns
	// drop down and edit text
	
	/*
	<translation id="0">
        <text>worthwhile</text>
        <conjugation>
          <tense>k</tense>
          <singular>
            <firstperson>
              <text>k</text>
            </firstperson>
            <secondperson>
              <text>k</text>
            </secondperson>
            <thirdpersonneutralcommon>
              <text>k</text>
            </thirdpersonneutralcommon>
          </singular>
          <plural>
            <firstperson>
              <text>k</text>
            </firstperson>
            <secondperson>
              <text>k</text>
            </secondperson>
            <thirdpersonneutralcommon>
              <text>k</text>
            </thirdpersonneutralcommon>
          </plural>
        </conjugation>
      </translation>
	 */
}

class grade {
	public int currentgrade = 1; // default values
	public int count = 1; // default values
	public int errorcount = 0; // default values
	public Date date;
}

class lessons {
	ArrayList<container> container = new ArrayList<container>(); // <entry id="197"/> in container section
}

class container {
	public String name = "";
	public boolean inpractice = false;
	ArrayList<Integer> entry = new ArrayList<Integer>();
}

class wordtypes {
	// not implemented yet
	
	/*
	<wordtypes>
    <container>
      <name>Nomen</name>
      <specialwordtype>noun</specialwordtype>
      <container>
        <name>männlich</name>
        <specialwordtype>noun/male</specialwordtype>
      </container>
      <container>
        <name>weiblich</name>
        <specialwordtype>noun/female</specialwordtype>
        <entry id="17">
          <translation id="0"/>
        </entry>
      </container>
      <container>
        <name>sächlich</name>
        <specialwordtype>noun/neutral</specialwordtype>
      </container>
    </container>
    <container>
      <name>Verb</name>
      <specialwordtype>verb</specialwordtype>
      <entry id="41">
        <translation id="0"/>
      </entry>
    </container>
    <container>
      <name>Adjektiv</name>
      <specialwordtype>adjective</specialwordtype>
    </container>
    <container>
      <name>Adverb</name>
      <specialwordtype>adverb</specialwordtype>
    </container>
  </wordtypes>
	 */
}