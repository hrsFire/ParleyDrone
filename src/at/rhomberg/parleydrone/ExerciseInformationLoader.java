package at.rhomberg.parleydrone;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import at.rhomberg.fileformats.Entry;
import at.rhomberg.fileformats.FileFormats;
import at.rhomberg.iowrapper.IOWrapper;

public class ExerciseInformationLoader {

    private static IOWrapper iow = new IOWrapper();
    private static ArrayList<PracticeStructure> entryIdsForExercise = new ArrayList<PracticeStructure>();
    private static int sizeOfEntriesForExercise;
    private static int currentEntryIdForExercise, currentEntryIdForFileFormats;
    private static String fileLocation;
    private static String fileName;
    private static int toTranslateId;
    private static int translatedTextId;

    public void loadExerciseInformation( Activity activity) {
        // get necessary information
        Intent intent = activity.getIntent();
        fileLocation = intent.getStringExtra( "fileLocation");
        fileName = intent.getStringExtra( "fileName");


        Log.d("ParleyDrone information", "start parsing");

        FileFormats fileFormats = null;

        if( iow.getFileFromInternalExternalStorage( fileName, fileLocation)) {
            Log.d("ParleyDrone information", "start parsing");
            fileFormats = iow.getFileFormats();
            Log.d("ParleyDrone information", "finished parsing");

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences( activity);

            JSONArray jsonArray = null;
            try {
                if( sharedPreferences.getString("lastOpenedCollections", "") != "")
                    jsonArray = new JSONArray( sharedPreferences.getString("lastOpenedCollections", ""));
                else
                    jsonArray = new JSONArray();
            } catch (JSONException e) {
            }

            try {
                if (jsonArray != null) {
                    if( (jsonArray.length() > 0) && (jsonArray.length() <= 5)) {
                        String[] lastOpenedCollections = new String[5];

                        lastOpenedCollections[0] = fileLocation + "/" + fileName;

                        for( int i = 1; i < jsonArray.length(); i++) {
                            lastOpenedCollections[i] = jsonArray.getString(i);

                            if( lastOpenedCollections[i].equals( lastOpenedCollections[0]))
                                throw new Exception(); // end the block to prevent a entry in the collection
                        }

                        for( int i = 0, n = 1; i < lastOpenedCollections.length - 1; i++) {
                            if( lastOpenedCollections[i] != null) {
                                jsonArray.put(n, lastOpenedCollections[i]);
                                n++;
                            }
                        }
                    }
                    else
                        jsonArray.put( 1, fileLocation + "/" + fileName);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("lastOpenedCollections", jsonArray.toString());
                    editor.commit();
                }

            } catch (JSONException e) {
            } catch (Exception e) {
            }
        }
        else
            activity.finish();

        if( fileFormats != null) {
            Iterator<Integer> entryListKeySetIterator = fileFormats.entryList.keySet().iterator();
            int i;
            for( ; entryListKeySetIterator.hasNext(); ) {
                i = entryListKeySetIterator.next();

                if( fileFormats.entryList.get( i).translationList.size() > 1) {
                    entryIdsForExercise.add( new PracticeStructure( i));
                }
            }

            sizeOfEntriesForExercise = entryIdsForExercise.size();

            if( sizeOfEntriesForExercise >= 1)
                loadNextEntry();
            else
                activity.finish();
        }
        else
            activity.finish();
    }

    public boolean loadNextEntry() {
        boolean isSearching = true;
        int breakCount = 0;
        int random;

        while( isSearching) {
            random = (int) Math.round( Math.random() * (entryIdsForExercise.size() -1));

            if( entryIdsForExercise.get( random).isFinished == false) {
                isSearching = false;

                currentEntryIdForExercise = random;
                currentEntryIdForFileFormats = entryIdsForExercise.get(currentEntryIdForExercise).entryId;

                return true;
            }
            else {
                ArrayList<Integer> values = new ArrayList<Integer>();
                boolean isNotAvailable = true;

                for( int i = 0; i < values.size(); i++) {
                    if( values.get(i).intValue() == random) {
                        isNotAvailable = false;
                    }
                }

                if( isNotAvailable) {
                    values.add( random);
                    breakCount++;
                }
            }

            if( breakCount == entryIdsForExercise.size()) {
                isSearching = false;

                return false;
            }
        }
        //nextTextButton.setVisibility( View.INVISIBLE);
        return false;
    }

    public FileFormats getFileFormats() {
        return iow.getFileFormats();
    }

    public ArrayList<PracticeStructure> getEntryIdsForExercise() {
        return entryIdsForExercise;
    }

    public int getSizeOfEntriesForExercise() {
        return sizeOfEntriesForExercise;
    }

    public int getCurrentEntryIdForExercise() {
        return currentEntryIdForExercise;
    }

    public int getCurrentEntryIdForFileFormats() {
        return currentEntryIdForFileFormats;
    }

    public int getToTranslateId() {
        return toTranslateId;
    }

    public int getTranslatedTextId() {
        return translatedTextId;
    }

    public void setToTranslateId( int id) {
        this.toTranslateId = id;
    }

    public void setTranslatedTextId( int id) {
        this.translatedTextId = id;
    }

    public void destroyClass() {
        iow = new IOWrapper();
        entryIdsForExercise = new ArrayList<PracticeStructure>();
        sizeOfEntriesForExercise = -1;
        currentEntryIdForExercise = -1;
        currentEntryIdForFileFormats = -1;
        fileLocation = null;
        fileName = null;
        toTranslateId = -1;
        translatedTextId = -1;
    }
}
