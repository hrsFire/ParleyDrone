package at.rhomberg.parleydrone;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import at.rhomberg.fileexplorer.FileExplorer;
import at.rhomberg.manager.FileManager;
import at.rhomberg.manager.LanguageManager;

public class ParleyDrone extends Activity {

	private Button createNewCollectionButton;
	private Button openAviableCollectionButton;
	private Button loadCollectionsNetworkButton;
	private LinearLayout ParleyDroneLayout;

    public static String PACKAGE_NAME = "ParleyDrone";
    public static String BUILD_VERSION = "";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        // change the language of the program
        LanguageManager languageManager = new LanguageManager();

        PreferenceManager.setDefaultValues( this, R.xml.parleydrone_preferences, false);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if( sharedPreferences.getString(getString(R.string.settings_language), "").equals("")) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString( getString( R.string.settings_language), "" + languageManager.detectLanguage( getResources().getConfiguration().locale.toString()));
            editor.commit();
        }

        languageManager.loadLanguage(this);

        // set layout
		setContentView(R.layout.activity_parley_drone);


        try {
            BUILD_VERSION = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
        }

        createNewCollectionButton = (Button) findViewById( R.id.ButtoncreateNewCollection);
		openAviableCollectionButton = (Button) findViewById( R.id.ButtonopenAviableCollection);
		loadCollectionsNetworkButton = (Button) findViewById(R.id.ButtonloadCollectionsNetwork);
		ParleyDroneLayout = (LinearLayout) findViewById( R.id.ParleyDroneLayout);
		
		// set OnClickListener
		createNewCollectionButton.setOnClickListener( new OnClickListener() {
			public void onClick( View arg) {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("file/*");
			    //startActivityForResult( intent, PICKFILE_RESULT_CODE);
				Toast.makeText( ParleyDrone.this, R.string.not, Toast.LENGTH_SHORT).show();
			}
		});
		
		openAviableCollectionButton.setOnClickListener( new OnClickListener() {
			public void onClick( View arg) {
                Intent intent = new Intent( ParleyDrone.this, FileExplorer.class);

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ParleyDrone.this);
                int storageId = Integer.valueOf( sharedPreferences.getString( getString( R.string.settings_preferredStorage), "0"));

                String fileLocation = "";

                if( storageId != 0) {
                    if( storageId == 1) {
                        fileLocation = "/";
                    }
                    else if( storageId == 2) {
                        fileLocation = Environment.getExternalStorageDirectory().toString();
                    }
                    else if( storageId == 3) {
                        fileLocation = Environment.getExternalStorageDirectory().toString();
                    }
                }

                intent.putExtra( "fileLocation", fileLocation);
                startActivityForResult(intent, 2);
			}
		});
		
		loadCollectionsNetworkButton.setOnClickListener( new OnClickListener() {
			public void onClick( View arg) {
				Toast.makeText( ParleyDrone.this, R.string.not, Toast.LENGTH_SHORT).show();
			}
		});
		
		// add last opened collections
		
		/*int screenHeight = 0;
		
		if( Build.VERSION.SDK_INT >= 11) {
			Point size = new Point();
			try {
				this.getWindowManager().getDefaultDisplay().getRealSize( size);
				screenHeight = size.y;
			} catch( NoSuchMethodError e) {
			}
		} else {
			DisplayMetrics metrics = new DisplayMetrics();
			this.getWindowManager().getDefaultDisplay().getMetrics( metrics);
			screenHeight = metrics.heightPixels;
		}*/
		
		Button[] buttonArray = new Button[5];

        JSONArray jsonArray = null;
        try {
            if( sharedPreferences.getString("lastOpenedCollections", "")  != "")
                jsonArray = new JSONArray( sharedPreferences.getString("lastOpenedCollections", ""));
            else
                jsonArray = new JSONArray();
        } catch (JSONException e) {
            e.printStackTrace();
        }

		for( byte i = 1; i < jsonArray.length(); i++) { // add preference how many last open
			Button bt = new Button( this);


            try {
                bt.setText( jsonArray.get(i).toString());
                final JSONArray finalJsonArray = jsonArray;
                final byte finalI = i;
                bt.setOnClickListener( new OnClickListener() {
                    public void onClick(View view) {
                        try {
                            String[] file = finalJsonArray.get(finalI).toString().split("/");
                            String fileName = file[file.length-1];
                            StringBuilder fileLocation = new StringBuilder();

                            for( int i = 1; i < file.length - 1; i++) {
                                fileLocation.append( "/" + file[i]);
                            }

                            FileManager fileManager = new FileManager();
                            if( fileManager.isFileValid(fileLocation.toString(), fileName)) {
                                Intent intent = new Intent( ParleyDrone.this, CollectionSummaryActivity.class);
                                intent.putExtra( "fileLocation", fileLocation.toString());
                                intent.putExtra( "fileName", fileName);

                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity( intent);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
            bt.setId(i);
			
			ParleyDroneLayout.addView( bt);
			/*bt.getHeight();
			screenHeight -= bt.getHeight();
			
			if( screenHeight <= 0)
				break;
			ParleyDroneLayout.removeView( bt);*/
		}
	}

    protected void onActivityResult( int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( requestCode == 2) {
            if( data != null) {
                String fileLocation = data.getStringExtra( "fileLocation");
                String fileName = data.getStringExtra( "fileName");
                Toast.makeText( this, fileLocation + "/" + fileName, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent( ParleyDrone.this, CollectionSummaryActivity.class);
                intent.putExtra( "fileLocation", fileLocation);
                intent.putExtra( "fileName", fileName);

                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity( intent);
            }
            else
                Toast.makeText(this, R.string.fileExplorerNoResult, Toast.LENGTH_SHORT).show();
        }
    }

	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.parley_drone, menu);
		return true;
	}

	public boolean onOptionsItemSelected( MenuItem menu) {
		Intent intentMenu = new Intent( this, SettingsPreference.class);
		startActivity( intentMenu);
		//finish();
		return true;
	}
	
	public void onStart() {
		super.onStart();
	}
	
	public void onResume() {
		super.onResume();

        // destroy class
        ExerciseInformationLoader exerciseInformationLoader = new ExerciseInformationLoader();
        exerciseInformationLoader.destroyClass();
    }

    public void onWindowFocusChanged( boolean hasFocus) {
    }
	
	public void onStop() {
		super.onStop();
	}
}
