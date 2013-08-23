package at.rhomberg.iowrapper;

import android.os.Environment;

import java.util.ArrayList;
import java.util.TreeMap;

import at.rhomberg.fileformats.Entry;
import at.rhomberg.fileformats.FileFormats;
import at.rhomberg.fileformats.Identifier;
import at.rhomberg.fileformats.Information;
import at.rhomberg.manager.FileManager;
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
            return fileFormatsList.get(id).info;
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

        string = fileManager.getStringFromFile(fileLocation + "/" + fileName);
        if( (string != null) || (string != "")) {
            type = getFileType( string);
            if( type == KVTML2) {
                KVTML2Parser parser = new KVTML2Parser();
                FileFormats ff;

                try {
                    ff = parser.importf( string);
                    if( ff != null) {
                        ff.objectType = KVTML2;
                        if( fileFormatsList.add( ff))
                            return fileFormatsList.size() - 1;
                    }
                } catch (Throwable throwable) {
                    return -1;
                }
            }
        }
		return -1;
	}
	
	public int getFileFromNetwork( String fileName, String fileLocation) {  // data type
		// ftp, http
		return -1;
	}


    // get the file type of the document

    private String getFileType( String string) {
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

        return null;
    }


    // xml functions

    private boolean checkXML(String string, String typeString) {
        int typeAt = 0;
        boolean quotationOpen = false;
        boolean angleBracketOpen = false;

        // remove multiple space characters
        String text = removeMultipleSpaceCharacters( string);
        String type = removeMultipleSpaceCharacters( typeString);

        if( text.length() >= type.length()) {
            for( int i = 0; (i < text.length()) || (typeAt < type.length()); i++) {
                // before or after a angle bracket are new lines or space characters
                if( ((text.charAt(i) == ' ') || (text.charAt(i) == '\n')) && !quotationOpen) {
                    typeAt++;
                }
                else if( (text.charAt(i) == '<') && (type.charAt(typeAt) == '<')) {
                    angleBracketOpen = true;
                    typeAt++;
                }
                else if( (text.charAt(i) == '>') && (type.charAt(typeAt) == '>') && angleBracketOpen) {
                    angleBracketOpen = false;
                    typeAt++;
                }
                else if( (text.charAt(i) == type.charAt(typeAt)))
                {
                    if( text.charAt(i) == '"') {
                        quotationOpen = !quotationOpen;
                        typeAt++;
                    }
                    else if( (text.charAt(i) == ' ') && quotationOpen) {
                        typeAt++;
                    }
                }
                else {
                    typeAt = 0;
                }
            }
            if( typeAt == (type.length()-1)) {
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
        String fullFileLocation;
        String export;



        if( fileManager.createFolder( fileLocation)){
            if( fileFormats.objectType == KVTML2) {
                KVTML2Parser parser = new KVTML2Parser();

                try {
                    export = parser.export( fileFormats);
                } catch (Throwable throwable) {
                    return false;
                }
                if( export != null) {
                    fileManager.saveFile( export, fileName, fileLocation);
                    return true;
                }
            }
        }

		return false;
	}
}
