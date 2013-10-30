package at.rhomberg.manager;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;

import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.util.Locale;

import at.rhomberg.parleydrone.R;

public class LanguageManager {
    public void loadLanguage( Activity obj) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(obj);
        String language = "";

        try {
            int languageId = Integer.valueOf( sharedPreferences.getString( obj.getString( R.string.settings_language), ""));
            language = obj.getResources().getStringArray( R.array.settings_languageArray)[languageId-1];
        } catch( NumberFormatException e) {
            language = sharedPreferences.getString( obj.getString( R.string.settings_language), "");
        }

        String lang = "";

        if( language.equals("Deutsch") || language.equals(Locale.GERMAN) || language.equals(Locale.GERMANY))
            lang = "de";
        else if( language.equals("English") || language.equals(Locale.ENGLISH))
            lang = "en";
        else
            lang = "en";

        Locale locale = new Locale( lang);
        Resources res = obj.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = locale;
        res.updateConfiguration( conf, dm);
    }

    public int detectLanguage( String language) {
        int lang = 2;

        if( language.equals(Locale.GERMAN) || language.equals(Locale.GERMANY) || language.equals("de_AT") || language.equals("de_CH"))
            lang = 1;
        else if( language.equals(Locale.ENGLISH))
            lang = 2;

        return lang;
    }
}
