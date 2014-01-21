package at.rhomberg.parleydrone;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import java.util.ArrayList;

import at.rhomberg.fileformats.FileFormats;

public class CollectionSummaryActivity extends Activity implements AdapterView.OnItemSelectedListener{

    private ExerciseInformationLoader exerciseInformationLoader = new ExerciseInformationLoader();
    private FileFormats fileFormats;
    private Spinner toTranslateSpinner;
    private Spinner translatedTextSpinner;
    private Button buttonStartExercise;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get necessary information
        exerciseInformationLoader.loadExerciseInformation( this);
        fileFormats = exerciseInformationLoader.getFileFormats();


        // set layout
        setContentView( R.layout.activity_collection_summary);


        // create dialog
        Dialog dialog = new Dialog( this);
        dialog.setContentView( R.layout.dialog_start_exercise);
        dialog.setTitle( "Choose");


        toTranslateSpinner = (Spinner) dialog.findViewById( R.id.SpinnerToTranslate);
        translatedTextSpinner = (Spinner) dialog.findViewById( R.id.SpinnerTranslatedText);
        buttonStartExercise = (Button) dialog.findViewById( R.id.ButtonStartExercise);

        String[] spinnerArrayContent = new String[fileFormats.identifierList.size()-1];

        ArrayList<String> spinnerArray = new ArrayList<String>();
        for( int i = 0; i < fileFormats.identifierList.size(); i++)
            spinnerArray.add( fileFormats.identifierList.get(i).name);

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>( this, android.R.layout.simple_spinner_dropdown_item, spinnerArray);
        toTranslateSpinner.setAdapter( spinnerArrayAdapter);
        translatedTextSpinner.setAdapter( spinnerArrayAdapter);

        translatedTextSpinner.setSelection( 1);


        // set listeners
        buttonStartExercise.setOnClickListener( new View.OnClickListener() {
            public void onClick( View view) {
                exerciseInformationLoader.setToTranslateId( toTranslateSpinner.getSelectedItemPosition());
                exerciseInformationLoader.setTranslatedTextId( translatedTextSpinner.getSelectedItemPosition());

                Intent intent = new Intent( CollectionSummaryActivity.this, ExerciseRightWrongActivity.class);

                startActivity( intent);
            }
        });

        // show dialog
        dialog.show();
    }

    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch( view.getId()) {
            case R.id.SpinnerToTranslate:

                break;
            case R.id.SpinnerTranslatedText:

                break;
        }
    }

    public void onNothingSelected(AdapterView<?> adapterView) {
    }
}