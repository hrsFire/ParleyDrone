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

public class ExerciseRightWrongActivity extends Activity {

    private TextView toTranslateTextView, translatedTextTextView;
    private Button showResultButton, nextTextButton, rightButton, wrongButton;
    private ArrayList<PracticeStructure> entryIdsForExercise = new ArrayList<PracticeStructure>();
    private FileFormats fileFormats;
    private int right = 0;
    private ExerciseInformationLoader exerciseInformationLoader = new ExerciseInformationLoader();


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
        setContentView(R.layout.activity_exercise_right_wrong);

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

        // get necessary information
        fileFormats = exerciseInformationLoader.getFileFormats();
        entryIdsForExercise = exerciseInformationLoader.getEntryIdsForExercise();

        toTranslateTextView.setText( fileFormats.entryList.get( exerciseInformationLoader.getCurrentEntryIdForExercise()).translationList.get( exerciseInformationLoader.getToTranslateId()).text);

        showResultButton.setOnClickListener( new View.OnClickListener() {
            public void onClick( View arg) {
                translatedTextTextView.setText( fileFormats.entryList.get( exerciseInformationLoader.getCurrentEntryIdForFileFormats()).translationList.get( exerciseInformationLoader.getTranslatedTextId()).text);
                translatedTextTextView.setVisibility( View.VISIBLE);

                //nextTextButton.setVisibility( View.VISIBLE);
            }
        });

        nextTextButton.setOnClickListener( new View.OnClickListener() {
            public void onClick( View arg) {
                translatedTextTextView.setVisibility( View.INVISIBLE);

                if( exerciseInformationLoader.loadNextEntry())
                    toTranslateTextView.setText( fileFormats.entryList.get( exerciseInformationLoader.getCurrentEntryIdForFileFormats()).translationList.get( exerciseInformationLoader.getToTranslateId()).text);
                else
                    showResult();
            }
        });

        rightButton.setOnClickListener( new View.OnClickListener() {
            public void onClick(View view) {
                if( entryIdsForExercise.get( exerciseInformationLoader.getCurrentEntryIdForExercise()).isWrong == false) {
                    right++;
                }

                translatedTextTextView.setVisibility( View.INVISIBLE);

                PracticeStructure localPracticeStructure = entryIdsForExercise.get( exerciseInformationLoader.getCurrentEntryIdForExercise());
                localPracticeStructure.isFinished = true;
                entryIdsForExercise.set( exerciseInformationLoader.getCurrentEntryIdForExercise(), localPracticeStructure);

                if( exerciseInformationLoader.loadNextEntry())
                    toTranslateTextView.setText( fileFormats.entryList.get( exerciseInformationLoader.getCurrentEntryIdForFileFormats()).translationList.get( exerciseInformationLoader.getToTranslateId()).text);
                else
                    showResult();
            }
        });

        wrongButton.setOnClickListener( new View.OnClickListener() {
            public void onClick(View view) {
                // set the existing isWrong argument
                PracticeStructure ps = entryIdsForExercise.get( exerciseInformationLoader.getCurrentEntryIdForExercise());
                ps.isWrong = true;
                ps.isFinished = false;
                entryIdsForExercise.set( exerciseInformationLoader.getCurrentEntryIdForExercise(), ps);

                // create a new structure
                PracticeStructure newPs = new PracticeStructure( exerciseInformationLoader.getCurrentEntryIdForFileFormats());
                newPs.isWrong = true;
                newPs.isFinished = false;
                entryIdsForExercise.add( newPs);

                translatedTextTextView.setVisibility( View.INVISIBLE);

                if( exerciseInformationLoader.loadNextEntry())
                    toTranslateTextView.setText( fileFormats.entryList.get( exerciseInformationLoader.getCurrentEntryIdForFileFormats()).translationList.get( exerciseInformationLoader.getToTranslateId()).text);
                else
                    showResult();
            }
        });

    }

    private void showResult() {
        Intent intent = new Intent( this, ResultActivity.class);
        intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra( "result", right);
        intent.putExtra("full", exerciseInformationLoader.getSizeOfEntriesForExercise());
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
        /*Intent intent = new Intent( this, ParleyDrone.class);
        intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity( intent);*/
        finish();
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