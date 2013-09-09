package at.rhomberg.parleydrone;

import android.app.Activity;
import android.app.Instrumentation;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import at.rhomberg.fileformats.FileFormats;
import at.rhomberg.iowrapper.IOWrapper;

public class CollectionSummary extends Activity {

    private TextView toTranslateTextView, translatedTextTextView;
    private Button showResultButton, nextTextButton;
    private int lastEntryIdForExercise;
    private ArrayList<Integer> entryIdsForExercise = new ArrayList<Integer>();
    private int toTranslateId, translatedTextId;
    private FileFormats fileFormats;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_collection_summary);

        int fileFormatsInt;

        toTranslateTextView = (TextView) findViewById( R.id.TextViewToTranslate);
        translatedTextTextView = (TextView) findViewById( R.id.TextViewTranslatedText);
        showResultButton = (Button) findViewById( R.id.ButtonShowResult);
        nextTextButton = (Button) findViewById( R.id.ButtonNextText);

        IOWrapper iow = new IOWrapper();
        Log.d("ParleyDrone info", "start parsing");
        fileFormatsInt = iow.getFileFromInternalExternalStorage("test1.kvtml", Environment.getExternalStorageDirectory().toString());
        if( fileFormatsInt != -1) {
            Log.d("ParleyDrone info", "start parsing");
            fileFormats = iow.getFileFormats(fileFormatsInt);
            Log.d("ParleyDrone info", "finished parsing");
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
        showResultButton.setOnClickListener( new OnClickListener() {
            public void onClick( View arg) {
                translatedTextTextView.setVisibility(View.VISIBLE);
                translatedTextTextView.setText( fileFormats.entryList.get(lastEntryIdForExercise).translationList.get(translatedTextId).text);
                //nextTextButton.setVisibility( View.VISIBLE);
            }
        });
        nextTextButton.setOnClickListener( new OnClickListener() {
            public void onClick( View arg) {
                lastEntryIdForExercise++;
                if( lastEntryIdForExercise < entryIdsForExercise.size()) {
                    if( !fileFormats.entryList.get(lastEntryIdForExercise).translationList.get(translatedTextId).text.equals("")) {
                        toTranslateTextView.setText( fileFormats.entryList.get(lastEntryIdForExercise).translationList.get(toTranslateId).text);
                        translatedTextTextView.setVisibility(View.INVISIBLE);
                    }
                    else
                        finish();
                }
                else
                    finish();
                //nextTextButton.setVisibility( View.INVISIBLE);
            }
        });
    }
}