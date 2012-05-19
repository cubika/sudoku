package com.example.sudoku;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

public class Game extends Activity {
	private int puzzle[]= new int[9*9];
	private PuzzleView puzzleview;
	
	private int used[][][]=new int[9][9][];
	
    public void onCreate(Bundle savedInstanceState) {
    	//initialize
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        
        int diff=getIntent().getIntExtra(KEY_DIFFICULTY, DIFFICULTY_EASY);
        puzzle=getPuzzle(diff);
        calculateUsedTiles();
        
        puzzleview=new PuzzleView(this);
        setContentView(puzzleview);
        puzzleview.requestFocus();
    }
    
    //open the keypad if there are any valid moves
    protected void showKeypadOrError(int x,int y) {
		int used[]=getUsedTiles(x,y);
		if(used.length==9){
			Toast toast=Toast.makeText(this,
					R.string.no_moves_label, Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}else {
			Log.d(TAG, "show kepad :used"+toPuzzleString(used));
			Dialog v=new keypad(this, used, puzzleview);
			v.show();
		}
		
	}
    
    protected boolean setTileIfValid(int x,int y,int value) {
		int tiles[]=getUsedTiles(x,y);
		if(value!=0){
			for(int tile:tiles){
				if(value==tile)
					return false;
			}
		}
		setTile(x, y, value);
        calculateUsedTiles();
        return true;
	}
    
    //return cached used tiles visible from given coordinations
    protected int[] getUsedTiles(int x,int y) {
		return used[x][y];
	}
    
    //compute the two dimensional array of used tiles
    private void calculateUsedTiles(){
    	for(int i=0;i<9;i++)
    		for(int j=0;j<9;j++){
    			used[i][j]=calculateUsedTiles(i,j);
    		}
    }
	
    //compute the used tiles for the position
    private int[] calculateUsedTiles(int x,int y) {
		int c[]=new int[9];
		
		//horizontal
		for(int i=0;i<9;i++){
			if(y==i) continue;
			int t=getTile(x,i);
			if(t!=0)
				c[t-1]=t;
		}
		
		//vertical
		for(int i=0;i<9;i++){
			if(x==i) continue;
			int t=getTile(i,y);
			if(t!=0)
				c[t-1]=t;
		}
		
		//the same cell block
		int startx=(x/3)*3;
		int starty=(y/3)*3;
		for(int i=startx;i<startx+3;i++)
			for(int j=starty;j<starty+3;j++){
				if(i==x && j==y)
					continue;
				int t=getTile(i,j);
				if(t!=0)
					c[t-1]=t;
			}
		
		//compress
		int usedlength=0;
		for(int t:c){
			if(t!=0)
				usedlength++;
		}
		
		int c1[]=new int[usedlength];
		usedlength=0;
		for(int t:c){
			if(t!=0)
				c1[usedlength++]=t;
		}
		
		return c1;
	}
    
    //根据选择难度赋值puzzle初始数组
    private int[] getPuzzle(int diff) {
    	String puz;
		switch (diff) {
		case DIFFICULTY_CONTINUE:
			puz=getPreferences(MODE_PRIVATE).getString(PRE_PUZZLE, easyPuzzle);
			break;
		case DIFFICULTY_EASY:
			puz=easyPuzzle;
			break;
		case DIFFICULTY_MEDIUM:
			puz=mediumPuzzle;
			break;
		case DIFFICULTY_HARD:
			puz=hardPuzzle;
			break;
		default:
			puz=easyPuzzle;
			break;
		}
		return fromPuzzleString(puz);
	}
    
    //将puzzle字符串变为一个数组
    static protected int[] fromPuzzleString(String str){
    	int[] puz=new int[str.length()];
    	for(int i=0;i<str.length();i++){
    		puz[i]=str.charAt(i)-'0';
    	}
    	return puz;
    }
    
    //将used数组变为字符串
    static private String toPuzzleString(int used[]){
    	StringBuilder buf=new StringBuilder();
    	for(int element:used){
    		buf.append(element);
    	}
    	return buf.toString();
    }
    
    private void setTile(int x,int y,int value){
    	puzzle[y*9+x]=value;
    }
    
    private int getTile(int x,int y) {
		return puzzle[y*9+x];
	}
    
    protected String getTileString(int x,int y) {
		int v=getTile(x, y);
		if(v==0)
			return "";
		else 
			return String.valueOf(v);
	}
    
	@Override
	protected void onResume(){
		super.onResume();
		Music.play(this, R.raw.game);
	}
	
	@Override
	protected void onStop(){
		super.onStop();
		Music.stop(this);
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		Log.d(TAG, "on Pause");
		Music.stop(this);
		
		//save the current puzzle
		getPreferences(MODE_PRIVATE).edit().putString(PRE_PUZZLE,
				toPuzzleString(puzzle)).commit();
	}
    
	private static final String TAG="Sudoku";
	
	public static final String KEY_DIFFICULTY
	="org.example.sudoku.difficulty";
	public static final int DIFFICULTY_EASY=0;
	public static final int DIFFICULTY_MEDIUM=1;
	public static final int DIFFICULTY_HARD=2;
	
	private static final String PRE_PUZZLE="puzzle";
	protected static final int DIFFICULTY_CONTINUE=-1;
	
	private final String easyPuzzle =
			"360000000004230800000004200" +
			"070460003820000014500013020" +
			"001900000007048300000000045";
			private final String mediumPuzzle =
			"650000070000506000014000005" +
			"007009000002314700000700800" +
			"500000630000201000030000097";
			private final String hardPuzzle =
			"009000000080605020501078000" +
			"000000700706040102004000000" +
			"000720903090301080000000600";
	
}
