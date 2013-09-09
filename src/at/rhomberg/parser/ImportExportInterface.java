package at.rhomberg.parser;

import at.rhomberg.fileformats.FileFormats;

public interface ImportExportInterface {

	public FileFormats importf( String fileLocation) throws Throwable; // returns null if not supported or
	public String export( FileFormats fileFormats) throws Throwable; // returns null if not supported
}
