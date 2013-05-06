package at.rhomberg.parleydrone;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

public class ParleyDrone extends Activity {

		protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_parley_drone);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.parley_drone, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected( MenuItem menu) {
		Intent intentMenu = new Intent( this, SettingsPreference.class);
		startActivity( intentMenu);
		finish();
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
