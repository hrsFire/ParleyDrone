package at.rhomberg.parleydrone;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;

import at.rhomberg.fileformats.FileFormats;
import at.rhomberg.iowrapper.IOWrapper;

public class CollectionSummaryActivity extends Activity {

    private TextView toTranslateTextView, translatedTextTextView;
    private Button showResultButton, nextTextButton, rightButton, wrongButton;
    private int currentEntryIdForExercise, currentEntryIdForFileFormats;
    private int sizeOfEntriesForExercise;
    private ArrayList<PracticeStructure> entryIdsForExercise = new ArrayList<PracticeStructure>();
    private int toTranslateId, translatedTextId;
    private FileFormats fileFormats;
    private int right = 0;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*LinearLayout layout = new LinearLayout(this);

CheckBox checkBox = new CheckBox(this);
checkBox.setText("text");

layout.addView( checkBox);
layout.setOrientation( LinearLayout.VERTICAL);
LinearLayout layout1 = new LinearLayout(this);
CheckBox checkBox1 = new CheckBox(this);
checkBox1.setText( "bla");
layout1.getChildAt(layout1.getChildCount()).setId();
layout1.addView( checkBox1);
layout.addView( layout1);
layout1.setPadding( 28 * (int) getResources().getDisplayMetrics().density, 0, 0, 0);
setContentView(layout);*/

        /*FragmentManager fragmentManager = getFragmentManager();
FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
CollectionOverviewFragment collectionOverviewFragment = new CollectionOverviewFragment();
fragmentTransaction.add(1, collectionOverviewFragment);
fragmentTransaction.commit();*/
        /*CollectionOverviewFragment cof = new CollectionOverviewFragment();
setContentView();
*/
        setContentView(R.layout.activity_collection_summary);

        int fileFormatsInt;

        toTranslateTextView = (TextView) findViewById( R.id.TextViewToTranslate);
        translatedTextTextView = (TextView) findViewById( R.id.TextViewTranslatedText);
        showResultButton = (Button) findViewById( R.id.ButtonShowResult);
        nextTextButton = (Button) findViewById( R.id.ButtonNextText);
        rightButton = (Button) findViewById( R.id.ButtonRight);
        wrongButton = (Button) findViewById( R.id.ButtonWrong);


        // set layout
        calibrateScreenElements();

        int px = Math.round(65 * getApplicationContext().getResources().getDisplayMetrics().density);
        rightButton.setHeight( px);
        wrongButton.setHeight( px);

        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();

        ViewGroup.LayoutParams params = rightButton.getLayoutParams();
        params.width = (int) (configuration.screenWidthDp * displayMetrics.density / 2);
        rightButton.setLayoutParams( params);

        params = wrongButton.getLayoutParams();
        params.width = (int) (configuration.screenWidthDp * displayMetrics.density / 2);
        wrongButton.setLayoutParams( params);


        Intent intent = getIntent();
        String fileLocation = intent.getStringExtra( "fileLocation");
        String fileName = intent.getStringExtra( "fileName");

        IOWrapper iow = new IOWrapper();
        Log.d("ParleyDrone information", "start parsing");

        fileFormatsInt = iow.getFileFromInternalExternalStorage( fileName, fileLocation);

        if( fileFormatsInt != -1) {
            Log.d("ParleyDrone information", "start parsing");
            fileFormats = iow.getFileFormats(fileFormatsInt);
            Log.d("ParleyDrone information", "finished parsing");

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

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
            finish();

        for( int i = 0; i < fileFormats.entryList.size(); i++) {
            if( fileFormats.entryList.get(i).translationList.size() > 1) {
                entryIdsForExercise.add( new PracticeStructure( i));
            }
        }

        sizeOfEntriesForExercise = entryIdsForExercise.size();

        if( sizeOfEntriesForExercise >= 1)
            loadNextEntry();
        else
            finish();


        toTranslateId = 0;
        translatedTextId = 1;

        toTranslateTextView.setText( fileFormats.entryList.get(currentEntryIdForExercise).translationList.get(toTranslateId).text);

        showResultButton.setOnClickListener( new View.OnClickListener() {
            public void onClick( View arg) {
                translatedTextTextView.setText( fileFormats.entryList.get( currentEntryIdForFileFormats).translationList.get( translatedTextId).text);
                translatedTextTextView.setVisibility( View.VISIBLE);

                //nextTextButton.setVisibility( View.VISIBLE);
            }
        });

        nextTextButton.setOnClickListener( new View.OnClickListener() {
            public void onClick( View arg) {
                translatedTextTextView.setVisibility( View.INVISIBLE);
                loadNextEntry();
            }
        });

        rightButton.setOnClickListener( new View.OnClickListener() {
            public void onClick(View view) {
                if( entryIdsForExercise.get( currentEntryIdForExercise).isWrong == false) {
                    right++;
                }

                translatedTextTextView.setVisibility( View.INVISIBLE);

                PracticeStructure localPracticeStructure = entryIdsForExercise.get( currentEntryIdForExercise);
                localPracticeStructure.isFinished = true;
                entryIdsForExercise.set(currentEntryIdForExercise, localPracticeStructure);

                loadNextEntry();
            }
        });

        wrongButton.setOnClickListener( new View.OnClickListener() {
            public void onClick(View view) {
                // set the existing isWrong argument
                PracticeStructure ps = entryIdsForExercise.get( currentEntryIdForExercise);
                ps.isWrong = true;
                ps.isFinished = false;
                entryIdsForExercise.set( currentEntryIdForExercise, ps);

                // create a new structure
                PracticeStructure newPs = new PracticeStructure( currentEntryIdForFileFormats);
                newPs.isWrong = true;
                newPs.isFinished = false;
                entryIdsForExercise.add( newPs);

                translatedTextTextView.setVisibility( View.INVISIBLE);

                loadNextEntry();
            }
        });

    }

    private void loadNextEntry() {
        boolean isSearching = true;
        int breakCount = 0;
        int random;

        while( isSearching) {
            random = (int) Math.round( Math.random() * (entryIdsForExercise.size() -1));

            if( entryIdsForExercise.get( random).isFinished == false) {
                isSearching = false;

                currentEntryIdForExercise = random;
                currentEntryIdForFileFormats = entryIdsForExercise.get(currentEntryIdForExercise).entryId;

                toTranslateTextView.setText( fileFormats.entryList.get( currentEntryIdForFileFormats).translationList.get( toTranslateId).text);
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
                showResult();
            }
        }
        //nextTextButton.setVisibility( View.INVISIBLE);
    }

    private void showResult() {
        Intent intent = new Intent( this, ResultActivity.class);
        intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra( "result", right);
        intent.putExtra("full", sizeOfEntriesForExercise);
        startActivity( intent);
    }

    private void calibrateScreenElements() {
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();

        ViewGroup.LayoutParams rightButtonLayoutParams = this.rightButton.getLayoutParams();
        rightButtonLayoutParams.width = ((int)(configuration.screenWidthDp * displayMetrics.density / 2));
        rightButton.setLayoutParams(rightButtonLayoutParams);

        ViewGroup.LayoutParams wrongButtonLayoutParams = this.wrongButton.getLayoutParams();
        wrongButtonLayoutParams.width = ((int)(configuration.screenWidthDp * displayMetrics.density / 2));
        wrongButton.setLayoutParams( wrongButtonLayoutParams);
    }

    private void endActivity() {
        Intent intent = new Intent( this, ParleyDrone.class);
        intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity( intent);
    }

    public void onBackPressed() {
        endActivity();
    }

    public void onConfigurationChanged(Configuration conf) {
        super.onConfigurationChanged( conf);
        calibrateScreenElements();
    }

    public void onSaveInstanceState( Bundle outState) {
        super.onSaveInstanceState( outState);
    }
}