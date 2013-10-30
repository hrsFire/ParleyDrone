package at.rhomberg.iowrapper;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import at.rhomberg.fileformats.Entry;
import at.rhomberg.fileformats.FileFormats;
import at.rhomberg.fileformats.Identifier;
import at.rhomberg.fileformats.Information;
import at.rhomberg.manager.FileManager;
import at.rhomberg.parleydrone.ParleyDrone;
import at.rhomberg.parser.KVTML2Parser;

public class IOWrapper extends FileFormats{

    private FileManager fileManager = new FileManager();
    private ArrayList<FileFormats> fileFormatsList = new ArrayList<FileFormats>(1);

	public IOWrapper()
	{	
	}

    public FileFormats getFileFormats(int id) {
        if( (id >= 0) && (id < fileFormatsList.size()))
            return fileFormatsList.get(id);
        return null;
    }
	public Information getInformation(int id) {
        if( (id >= 0) && (id < fileFormatsList.size()))
            return fileFormatsList.get(id).information;
        return null;
	}
	
	public String getObjectType(int id) {
        if( (id >= 0) && (id < fileFormatsList.size()))
		    return fileFormatsList.get(id).objectType;
        return null;
	}
	
	public TreeMap<Integer, Identifier> getIdentifierList(int id) {
        if( (id >= 0) && (id < fileFormatsList.size()))
		    return fileFormatsList.get(id).identifierList;
        return null;
	}
	public TreeMap<Integer, Entry> getEntryList(int id) {
        if( (id >= 0) && (id < fileFormatsList.size()))
		    return fileFormatsList.get(id).entryList;
        return null;
	}	


	// read and parse file from different locations
    // the return value is the id for the ArrayList
	public int getFileFromInternalExternalStorage( String fileName, String fileLocation) {
        String string;
        String type;

        Log.d("ParleyDrone information", "start getStringFromFile");
        string = fileManager.getStringFromFile(fileLocation, fileName);
        Log.d("ParleyDrone information", "finished parsing");

        if( (string != null) && (string != "")) {
            Log.d("ParleyDrone information", "start getFileType");
            type = getFileType( string, fileName);
            Log.d("ParleyDrone information", "finished getFileType");

            if( type == KVTML2) {
                KVTML2Parser parser = new KVTML2Parser();
                FileFormats ff;

                try {
                    Log.d("ParleyDrone information", "start parsing kvtml2");
                    ff = parser.importf( fileLocation + "/" + fileName);
                    Log.d("ParleyDrone information", "finished parsing kvtml2");
                    if( ff != null) {
                        ff.objectType = KVTML2;
                        if( fileFormatsList.add( ff)) {
                            return fileFormatsList.size() - 1;
                        }
                    }
                } catch (ParserConfigurationException e) {
                    Log.d( "ParleyDrone errors", "ParserConfigurationException: " + e.getStackTrace());
                    return -1;
                } catch (SAXException e) {
                    Log.d( "ParleyDrone errors", "SAXException: " + e.getStackTrace());
                    return -1;
                } catch (IOException e) {
                    Log.d( "ParleyDrone errors", "IOException: " + e.getStackTrace());
                    return -1;
                } catch (Exception e) {
                    Log.d( "ParleyDrone errors", "Exception: " + e.getStackTrace());
                    return -1;
                }  catch (Throwable e) {
                    Log.d( "ParleyDrone errors", "Throwable: " + e.getStackTrace());
                    return -1;
                }
            }
            Log.d("ParleyDrone information", "couldn't load the file");
        }
		return -1;
	}
	
	public int getFileFromNetwork( String fileName, String fileLocation) {  // data type
		// ftp, http
		return -1;
	}


    // get the file type of the document

    private String getFileType( String string, String fileName) {
        String type;
        /*
		header
		the attributes must be in this order
		encoding is optional
		version is mandatory
		xml doctype is not required -> fallback to file extension

		<?xml version="1.0" encoding="UTF-8"?>
			<!DOCTYPE kvtml PUBLIC "kvtml2.dtd" "http://edu.kde.org/kvtml/kvtml2.dtd">
			<kvtml version="2.0">
			</kvtml>
		*/

        type = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE kvtml PUBLIC \"kvtml2.dtd\" \"http://edu.kde.org/kvtml/kvtml2.dtd\">";
        if( checkXML( string, type))
            return KVTML2;
        else {
            String[] name = fileName.split( Pattern.quote("."));

            if( name.length > 1) {
                return name[name.length-1].toString();
            }
        }
        return null;
    }


    // xml functions

    private boolean checkXML(String string, String typeString) {
        int typeAt = 0;
        int pos = 0;
        String stringCache = "";
        boolean quotationOpen = false;
        boolean angleBracketOpen = false;
        String verification = "";

        // remove multiple space characters
        Log.d("ParleyDrone information", "start removeMultipleSpaceCharacters");
        pos = string.indexOf(string.indexOf(">") + 1);
        if( pos > 0)
            stringCache = string.substring( 0, pos);
        String text = removeMultipleSpaceCharacters( stringCache);
        Log.d("ParleyDrone information", "finished removeMultipleSpaceCharacters");
        Log.d("ParleyDrone information", "start removeMultipleSpaceCharacters");
        String type = removeMultipleSpaceCharacters( typeString);
        Log.d("ParleyDrone information", "finished removeMultipleSpaceCharacters");

        Log.d("ParleyDrone information", "start to check for errors in the xml file");
        if( string.length() >= type.length()) {// text
            for( int i = 0; (i < string.length()) && (typeAt < type.length()); i++) {
                // before or after a angle bracket are new lines or space characters

                if( (string.charAt(i) == type.charAt(typeAt))) {
                    if( string.charAt(i) == '<') {
                        angleBracketOpen = true;
                        typeAt++;
                        verification += string.charAt(i);
                    }
                    else if( (string.charAt(i) == '>') && angleBracketOpen) {
                        angleBracketOpen = false;
                        typeAt++;
                        verification += string.charAt(i);
                    }
                    else if( string.charAt(i) == '"') {
                        quotationOpen = !quotationOpen;
                        typeAt++;
                        verification += string.charAt(i);
                    }
                    else if( (string.charAt(i) == ' ') && quotationOpen) {
                        typeAt++;
                        verification += string.charAt(i);
                    }
                    else {
                        typeAt++;
                        verification += string.charAt(i);
                    }
                }
                else if( (string.charAt(i) == ' ') || (string.charAt(i) == '\n')) {
                }
                else {
                    typeAt = 0;
                    verification = "";
                }
            }
            Log.d("ParleyDrone information", "finished checkXML");
            if( type.equals(verification)) {
                return true;
            }
        }
        return false;
    }

    private String removeMultipleSpaceCharacters( String string) {
        String text = "";
        boolean quotationOpen = false;
        boolean spaceCharacterBefore = false;

        for( int i = 0; i < string.length(); i++) {
            if( string.charAt(i) == '"')
                quotationOpen = !quotationOpen;
            else if( (string.charAt(i) == ' ') && !quotationOpen) {
                if( spaceCharacterBefore)
                    continue;
                else
                    spaceCharacterBefore = true;
            }
            else
                spaceCharacterBefore = false;

            text += string.charAt(i);
        }

        return text;
    }


    // save functions

	public boolean saveFile( FileFormats fileFormats, String fileName, String fileLocation) {
        String export;

        if( fileManager.createFolder( fileLocation, fileName)){
            if( fileFormats.objectType == KVTML2) {
                KVTML2Parser parser = new KVTML2Parser();

                try {
                    export = parser.export( fileFormats);
                } catch (Throwable throwable) {
                    return false;
                }
                if( export != null) {
                    fileManager.saveFile( export, fileLocation, fileName);
                    return true;
                }
            }
        }

		return false;
	}
}
