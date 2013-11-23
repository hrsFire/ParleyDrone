package at.rhomberg.parleydrone;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import at.rhomberg.fileformats.Container;
import at.rhomberg.fileformats.FileFormats;
import at.rhomberg.iowrapper.IOWrapper;

public class CollectionOverviewFragment extends Fragment {

    private ArrayList<Integer> entryIdsForExercise = new ArrayList<Integer>();
    private FileFormats fileFormats;
    private static int level;
    private int currentEntryIdForExercise;
    private int fileFormatsInt;

    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        IOWrapper iow = new IOWrapper();
        Log.d("ParleyDrone information", "start parsing");
        fileFormatsInt = iow.getFileFromInternalExternalStorage("test1.kvtml", Environment.getExternalStorageDirectory().toString());
        if( fileFormatsInt != -1) {
            Log.d("ParleyDrone information", "start parsing");
            fileFormats = iow.getFileFormats(fileFormatsInt);
            Log.d("ParleyDrone information", "finished parsing");
        }
        else
            getActivity().finish();

        for( int i = 0; i < fileFormats.entryList.size(); i++) {
            if( fileFormats.entryList.get(i).translationList.size() > 1) {
                entryIdsForExercise.add( i);
            }
        }

        if( entryIdsForExercise.size() >= 1)
            currentEntryIdForExercise = 0;
        else
            getActivity().finish();

        level = 0;
        CheckBoxLayoutStruct checkBoxLayoutStruct = setCollectionList(fileFormats.lessonContainerList);
        if( checkBoxLayoutStruct == null)
            getActivity().finish();

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);

        for( int i = 0; i < checkBoxLayoutStruct.layoutArrayList.size(); i++) {
            layout.addView(checkBoxLayoutStruct.layoutArrayList.get(i));
        }
        getActivity().setContentView( layout);

        View vi;
        vi = (View) layout;
        //View v = inflater.inflate( vi, null);

        return vi;
    }


    private CheckBoxLayoutStruct setCollectionList( ArrayList<Container> lessonContainerList) {

        LayoutStruct layoutStruct = new LayoutStruct();
        CheckBoxStruct checkBoxStruct = new CheckBoxStruct();

        for( int i = 0; i < lessonContainerList.size(); i++) {
            Log.d("ParleyDrone information", "start lesson Container");
            CheckBox checkBox = new CheckBox( getActivity());
            checkBox.setText(lessonContainerList.get(i).name);

            LinearLayout layout = new LinearLayout( getActivity());
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(28 * (int) getResources().getDisplayMetrics().density * level, 0, 0, 0);

            // add the  checkBox to the layout
            layout.addView(checkBox);
            if( checkBoxStruct.checkBoxArrayList == null)
                checkBoxStruct.checkBoxArrayList = new ArrayList<CheckBox>();
            checkBoxStruct.checkBoxArrayList.add( checkBox);

            if( lessonContainerList.get(i).container.size() > 0) {
                Log.d("ParleyDrone information", "container size > 0");
                level++;
                CheckBoxLayoutStruct vls = setCollectionList(lessonContainerList.get(i).container);
                if( vls != null) {
                    Log.d("ParleyDrone information", "vls != null");

                    for( int n = 0; n < vls.layoutArrayList.size(); n++ ) {
                        Log.d("ParleyDrone information", "for loop");
                        checkBoxStruct.checkBoxStruct.checkBoxArrayList.add( vls.checkBoxArrayList.get(n));
                        // add the layout to the main layout
                        layout.addView(vls.layoutArrayList.get(n));
                        layoutStruct.layoutStruct.layoutArrayList.add( vls.layoutArrayList.get(n));
                    }
                }
            }

            // add the layout to the arrayList
            if( layoutStruct.layoutArrayList == null)
                layoutStruct.layoutArrayList = new ArrayList<LinearLayout>();
            layoutStruct.layoutArrayList.add( layout);
        }
        Log.d("ParleyDrone information", "end lessonContainer");

        if( layoutStruct.layoutArrayList.size() == 0) {
            return null;
        }

        CheckBoxLayoutStruct returnCheckBoxLayoutStruct = new CheckBoxLayoutStruct();
        returnCheckBoxLayoutStruct.checkBoxArrayList = checkBoxStruct.checkBoxArrayList;
        returnCheckBoxLayoutStruct.layoutArrayList = layoutStruct.layoutArrayList;
        return returnCheckBoxLayoutStruct;
    }
}

class CheckBoxStruct {
    public ArrayList<CheckBox> checkBoxArrayList;
    public CheckBoxStruct checkBoxStruct;
}

class LayoutStruct {
    public ArrayList<LinearLayout> layoutArrayList;
    public LayoutStruct layoutStruct;
}

class CheckBoxLayoutStruct {
    public ArrayList<CheckBox> checkBoxArrayList;
    public ArrayList<LinearLayout> layoutArrayList;
}