package at.rhomberg.parleydrone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import java.util.Locale;

public class SettingsPreference extends PreferenceActivity implements OnSharedPreferenceChangeListener, OnPreferenceClickListener {
			
	private Preference preferenceScreenObject;
	private int i;
	private static final int PICKFILE_RESULT_CODE = 1;
	
	public void onCreate( Bundle savedInstanceState) {
		
		super.onCreate( savedInstanceState);
		// deprecated in API level 11; use fragements instead
		addPreferencesFromResource( R.xml.parleydrone_preferences);
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener( this);
		
		preferenceScreenObject = findPreference( getString( R.string.settings_licence));
		preferenceScreenObject.setOnPreferenceClickListener( this);
		
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences( this);
		try {
			PackageInfo pInfo = getPackageManager().getPackageInfo( getPackageName(), 0);
			preferenceScreenObject = (Preference) findPreference( getString( R.string.settings_buildVersion));
			preferenceScreenObject.setSummary( preferenceScreenObject.getSummary() + pInfo.versionName);
		} catch (NameNotFoundException e) {
		}
		
		preferenceScreenObject = (EditTextPreference) findPreference( getString( R.string.settings_user));
		preferenceScreenObject.setSummary( sharedPreferences.getString( getString( R.string.settings_user), ""));

		preferenceScreenObject = (ListPreference) findPreference( getString( R.string.settings_language));
		i = Integer.valueOf( sharedPreferences.getString( getString( R.string.settings_language), ""));
		preferenceScreenObject.setSummary( this.getResources().getStringArray( R.array.settings_languageArray)[i-1]);
		
		preferenceScreenObject = (ListPreference) findPreference( getString( R.string.settings_preferedStorage));
		i = Integer.valueOf( sharedPreferences.getString( getString( R.string.settings_preferedStorage), ""));
		preferenceScreenObject.setSummary( this.getResources().getStringArray( R.array.settings_preferedStorageArray)[i-1]);
		
		preferenceScreenObject = (ListPreference) findPreference( getString( R.string.settings_preferedFileFormat));
		i = Integer.valueOf( sharedPreferences.getString( getString( R.string.settings_preferedFileFormat), ""));
		preferenceScreenObject.setSummary( this.getResources().getStringArray( R.array.settings_preferedFileFormatArray)[i-1]);
		
		/*
		Intent intent = new Intent( Intent.ACTION_GET_CONTENT);
		intent.setType( "file/*");
		startActivityForResult( intent, PICKFILE_RESULT_CODE);
		*/
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		
		if( key == getString( R.string.settings_user)) {
			preferenceScreenObject = (EditTextPreference) findPreference( getString( R.string.settings_user));
			preferenceScreenObject.setSummary( sharedPreferences.getString( key, ""));
		}
		else if( key == getString( R.string.settings_language)) {
			preferenceScreenObject = (ListPreference) findPreference( getString( R.string.settings_language));
			i = Integer.valueOf( sharedPreferences.getString( key, ""));
			preferenceScreenObject.setSummary( this.getResources().getStringArray( R.array.settings_languageArray)[i-1]);
			
			// change language of the programm
			Locale locale = new Locale( sharedPreferences.getString( key, ""));
			Locale.setDefault( locale);
			Configuration config = new Configuration();
			config.locale = locale;
			getBaseContext().getResources().updateConfiguration( config, getBaseContext().getResources().getDisplayMetrics());
		}
		else if( key == getString( R.string.settings_preferedStorage)) {
			preferenceScreenObject = (ListPreference) findPreference( getString( R.string.settings_preferedStorage));
			i = Integer.valueOf( sharedPreferences.getString( key, ""));
			preferenceScreenObject.setSummary( this.getResources().getStringArray( R.array.settings_preferedStorageArray)[i-1]);
		}
		else if( key == getString( R.string.settings_preferedFileFormat)) {
			preferenceScreenObject = (ListPreference) findPreference( getString( R.string.settings_preferedFileFormat));
			i = Integer.valueOf( sharedPreferences.getString( key, ""));
			preferenceScreenObject.setSummary( this.getResources().getStringArray( R.array.settings_preferedFileFormatArray)[i-1]);
		}
	}
	
	public void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener( this);
	}
	
	public void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener( this);
	}	
	
	public boolean onPreferenceClick( Preference pref) {
		
		Intent intent = new Intent( this, LicenceActivity.class);
		startActivity( intent);
		return true;
	}
}
