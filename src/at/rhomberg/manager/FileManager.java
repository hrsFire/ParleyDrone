package at.rhomberg.manager;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileManager {

    public String getStringFromFile( String fileLocationAndName) {
        File file = new File( fileLocationAndName);
        FileReader fr;
        try {
            fr = new FileReader( file);
        } catch (FileNotFoundException e) {
            return null;
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
                    fr.close();
                } catch (IOException e1) {
                    return null;
                }
                return null;
            }
        }

        return output;
    }

    public boolean saveFile( String string, String fileName, String fileLocation) {
        File file = new File( fileLocation);
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

    public boolean createFolder( String fileLocation) {
        String fileDirectory;
        String state = Environment.getExternalStorageState();

        File f = new File( Environment.getExternalStorageDirectory() + fileLocation);

        if( Environment.MEDIA_MOUNTED.equals(state)) {
            if( !f.exists()) {
                if( f.mkdirs()) {
                    return true;
                }
            }
        }

        return false;
    }
}
