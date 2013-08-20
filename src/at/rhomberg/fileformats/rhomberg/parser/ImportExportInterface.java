package at.rhomberg.fileformats.rhomberg.parser;

import at.rhomberg.fileformats.rhomberg.fileformats.FileFormats;

public interface ImportExportInterface {

	public FileFormats importf(String textFile) throws Throwable; // returns null if not supported or
	public String export(FileFormats fileFormats) throws Throwable; // returns null if not supported
}
