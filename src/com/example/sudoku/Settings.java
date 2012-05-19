package com.example.sudoku;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Settings extends PreferenceActivity{
	
	/*option names and values*/
	private static final String OPT_MUSIC="music";
	private static final boolean OPT_MUSIC_DEF=true;
	private static final String OPT_HINTS="hint";
	private static final boolean OPT_HINTS_DEF=true;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	//initialize
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.settings);
    }
    
    /*get the current value of the option*/
    public static boolean getMusic(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getBoolean(OPT_MUSIC, OPT_MUSIC_DEF);
	}
    
    public static boolean getHints(Context context){
    	return PreferenceManager.getDefaultSharedPreferences(context)
    			.getBoolean(OPT_HINTS, OPT_HINTS_DEF);
    }
}
