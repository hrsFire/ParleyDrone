package at.rhomberg.parleydrone;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class IOWrapper extends FileFormats{
		
	public IOWrapper()
	{
		
	}
	
	public information getInfo() {
		return info;
	}
	public String getObjectType() {
		return objectType;
	}
	
	public ArrayList<identifier> getIdentifierList() {
		return identifierList;
	}
	public ArrayList<entry> getEntryList() {
		return entryList;
	}	

	// read and parse file from different locations
	
	public boolean getFileFromInternalStorage( String fileName, String fileLocation) {
		
		return true;
	}
	
	public boolean getFileFromSDCard( String fileName, String fileLocation) {
		
		return true;
	}
	
	public boolean getFileFromNetwork( String fileName, String fileLocation) { // data type
		// ftp, http
		return true;
	}
	
	// other functions
	
	private String getStringFromFile( String fileLocationAndName) {
		
		File file = new File( fileLocationAndName);
		FileReader fr = null;
		try {
			fr = new FileReader( file);
		} catch (FileNotFoundException e) {
			return "-1";
		}
		
		String output = "";
		if( fr != null) {
			BufferedReader br = new BufferedReader( fr);
			
			try {
				while( (output += br.readLine()) != null) {
					output += "\n";
				}
				br.close();
				fr.close();
			} catch (IOException e) {
				try {
					br.close();
					fr.close();
				} catch (IOException e1) {
					;
				}
				return "-1";
			}
		}
		
		return output;
	}
	
	private boolean checkString() {
		
		return true;
	}
	
	// kvtml functions
	
	private boolean checkKvtml() {
		/* header
		<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE kvtml PUBLIC "kvtml2.dtd" "http://edu.kde.org/kvtml/kvtml2.dtd">
<kvtml version="2.0">
</kvtml>
		 */
		
		return true;
	}
	
	private boolean parseAndSetKvtmlObjects( String kvtmlString) {
		// <translation id="1"/>
		// id -> to null should be  "" -> a new row
		
		
		return true;
	}
	
	// save functions
	
	public boolean saveFileToSDCard( FileFormats fileFormats) {
		
		return true;
	}
	
	public boolean saveFileToInternalStorrage( FileFormats fileformats) {
		
		return true;
	}
}
