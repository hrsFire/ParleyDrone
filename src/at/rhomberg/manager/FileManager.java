package at.rhomberg.manager;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileManager {

    public String getStringFromFile( String fileLocation, String fileName) {
        File file = new File( fileLocation  + "/" + fileName);
        FileReader fr;
        try {
            fr = new FileReader( file);
        } catch (FileNotFoundException e) {
            return null;
        }

        StringBuilder output = new StringBuilder();
        Log.d("ParleyDrone information", "get the full text");
        if( fr != null) {
            BufferedReader br = new BufferedReader( fr);

            try {
                String cache;

                while( (cache = br.readLine()) != null) {
                    output.append( cache + "\n");
                }
                br.close();
                fr.close();
            } catch (IOException e) {
                try {
                    fr.close();
                } catch (IOException e1) {
                    return null;
                }
                return null;
            }
        }
        Log.d("ParleyDrone information", "finished");

        return output.toString();
    }

    public boolean saveFile( String string, String fileLocation, String fileName) {
        File file = new File( fileLocation + "/" + fileName);
        FileWriter fw;

        try {
            fw = new FileWriter( file);
        } catch( IOException e) {
            return false;
        }

        if( fw != null) {
            try {
                fw.write( string);
                fw.close();
            } catch( IOException e) {
                try {
                    fw.close();
                } catch( IOException e1) {
                    return false;
                }
                return false;
            }
        }

        return true;
    }

    public boolean createFolder( String folderLocation, String folderName) {
        if( new File( folderLocation).exists()) {
            File f = new File( folderLocation + "/" + folderName);

            if( f.mkdirs()) {
                return true;
            }
        }

        return false;
    }

    public boolean isFileValid(String folderLocation, String folderName) {
        File file = new File( folderLocation + "/" + folderName);
        if( file.canRead()) {
            return true;
        }

        return false;
    }
}
