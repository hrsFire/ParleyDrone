package at.rhomberg.parleydrone;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ParleyDrone extends Activity {

	private Button createNewCollectionButton;
	private Button openAviableCollectionButton;
	private Button loadCollectionsNetworkButton;
	private LinearLayout ParleyDroneLayout;
	
	@SuppressLint("NewApi")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_parley_drone);
		
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
				Toast.makeText( ParleyDrone.this, R.string.not, Toast.LENGTH_SHORT).show();
			}
		});
		
		loadCollectionsNetworkButton.setOnClickListener( new OnClickListener() {
			public void onClick( View arg) {
				Toast.makeText( ParleyDrone.this, R.string.not, Toast.LENGTH_SHORT).show();
			}
		});
		
		// add last opened collections
		
		int screenHeight = 0;
		
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
		}
		
		Button[] buttonArray = new Button[10];
		
		for( byte i = 0; i < 10/*256*/; i++) { // add preference how many last open
			Button bt = new Button( this);
			bt.setText( "first");
			bt.setId(i);
			
			ParleyDroneLayout.addView( bt);
			bt.getHeight();
			screenHeight -= bt.getHeight();
			
			if( screenHeight <= 0)
				break;
			ParleyDroneLayout.removeView( bt);
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
	
	public void onStart( Bundle savedInstanceState) {
		super.onStart();
	}
	
	public void onResume( Bundle savedInstanceState) {
		super.onResume();
	}
	
	public void onStop( Bundle savedInstanceState) {
		super.onStop();
	}
}
