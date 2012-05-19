package com.example.sudoku;

import android.app.Activity;
import android.os.Bundle;

public class About extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	//initialize
        super.onCreate(savedInstanceState);
        //fill the screen with android widget
        setContentView(R.layout.about);
    }
}
