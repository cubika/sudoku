package com.example.sudoku;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;

import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


public class Sudoku extends Activity implements  OnClickListener {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	//initialize
        super.onCreate(savedInstanceState);
        //fill the screen with android widget
        setContentView(R.layout.main);
        
        //set up listener for all the buttons
        View continueButton=findViewById(R.id.continue_button);
        continueButton.setOnClickListener(this);
        View newButton=findViewById(R.id.new_game_button);
        newButton.setOnClickListener(this);
        View aboutButton=findViewById(R.id.about_button);
        aboutButton.setOnClickListener(this);
        View exitButton=findViewById(R.id.exit_button);
        exitButton.setOnClickListener(this);
    }

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.continue_button:
			startGame(Game.DIFFICULTY_CONTINUE);
			break;
		case R.id.about_button:
			Intent i=new Intent(this,About.class);
			startActivity(i);
			break;
		case R.id.new_game_button:
			openNewGameDialog();
			break;
		case R.id.exit_button:
			finish();
			break;
		default:
			break;
		}
	}
	
	
	public boolean onCreateOptionsMenu(Menu menu){
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater=getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()) {
		case R.id.settings:
			startActivity(new Intent(this, Settings.class));
			return true;

		default:
			break;
		}
		return false;
	}
	
	private void openNewGameDialog() {
		new AlertDialog.Builder(this)
		.setTitle(R.string.new_game_title)
		.setItems(R.array.difficulty,
				new DialogInterface.OnClickListener(){

				public void onClick(DialogInterface dialog, int which) {
					startGame(which);
				}
				
					
		})
		.show();
	}
	
	private void startGame(int i){
		Log.d(TAG, "click on:"+i);
		Intent intent=new Intent(Sudoku.this, Game.class);
		intent.putExtra(Game.KEY_DIFFICULTY, i);
		startActivity(intent);
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		Music.play(this, R.raw.main);
	}
	
	@Override
	protected void onStop(){
		super.onStop();
		Music.stop(this);
	}

	private static final String TAG="Sudoku";
}