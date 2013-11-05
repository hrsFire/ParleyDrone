package at.rhomberg.parleydrone;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import at.rhomberg.fileformats.FileFormats;
import at.rhomberg.iowrapper.IOWrapper;

public class CollectionSummaryActivity extends Activity {

    private TextView toTranslateTextView, translatedTextTextView;
    private Button showResultButton, nextTextButton, rightButton, wrongButton;
    private int lastEntryIdForExercise;
    private int lastEntryIdForRepeat = 0;
    private ArrayList<Integer> entryIdsForExercise = new ArrayList<Integer>();
    private ArrayList<Integer> entryIdsForRepeat = new ArrayList<Integer>();
    private int toTranslateId, translatedTextId;
    private FileFormats fileFormats;
    private int right = 0;
    private boolean isNotRepeating = true;


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
                if( sharedPreferences.getString("lastOpenedCollections", "")  != "")
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
                entryIdsForExercise.add( i);
            }
        }

        if( entryIdsForExercise.size() >= 1)
            lastEntryIdForExercise = 0;
        else
            finish();


        toTranslateId = 0;
        translatedTextId = 1;

        toTranslateTextView.setText( fileFormats.entryList.get(lastEntryIdForExercise).translationList.get(toTranslateId).text);

        showResultButton.setOnClickListener( new View.OnClickListener() {
            public void onClick( View arg) {
                translatedTextTextView.setVisibility(View.VISIBLE);
                translatedTextTextView.setText( fileFormats.entryList.get(lastEntryIdForExercise).translationList.get(translatedTextId).text);
                //nextTextButton.setVisibility( View.VISIBLE);
            }
        });

        nextTextButton.setOnClickListener( new View.OnClickListener() {
            public void onClick( View arg) {
                loadNextEntry();
            }
        });

        rightButton.setOnClickListener( new View.OnClickListener() {
            public void onClick(View view) {
                if( isNotRepeating) {
                    right++;
                }

                loadNextEntry();
            }
        });

        wrongButton.setOnClickListener( new View.OnClickListener() {
            public void onClick(View view) {
                entryIdsForRepeat.add( lastEntryIdForExercise);
                loadNextEntry();
            }
        });

    }

    private void loadNextEntry() {
        lastEntryIdForExercise++;
        // "" strings must be fixed
        if( (lastEntryIdForExercise < entryIdsForExercise.size()) && !fileFormats.entryList.get(lastEntryIdForExercise).translationList.get(translatedTextId).text.equals("")) {
            toTranslateTextView.setText( fileFormats.entryList.get(lastEntryIdForExercise).translationList.get(toTranslateId).text);
            translatedTextTextView.setVisibility(View.INVISIBLE);
        }
        else if( lastEntryIdForRepeat < entryIdsForRepeat.size()) {
            toTranslateTextView.setText( fileFormats.entryList.get(entryIdsForRepeat.get( lastEntryIdForRepeat)).translationList.get(toTranslateId).text);
            translatedTextTextView.setVisibility(View.INVISIBLE);

            isNotRepeating = false;
            lastEntryIdForRepeat++;
        }
        else {
            showResult();
        }

        //nextTextButton.setVisibility( View.INVISIBLE);
    }

    private void showResult() {
        Intent intent = new Intent( this, ResultActivity.class);
        intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra( "result", right);
        intent.putExtra( "full", entryIdsForExercise.size());
        startActivity( intent);
    }

    private void endActivity() {
        Intent intent = new Intent( this, ParleyDrone.class);
        intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity( intent);
    }

    public void onBackPressed() {
        endActivity();
    }

    public void onCreateView() {

    }

    public void onSaveInstanceState( Bundle outState) {
        super.onSaveInstanceState( outState);
    }
}