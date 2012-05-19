package com.example.sudoku;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;


public class PuzzleView extends View {
	
	private static final String TAG="Sudoku";
	private final Game game;
	public PuzzleView(Context context) {
		super(context);
		this.game=(Game)context;
		setFocusable(true);
		setFocusableInTouchMode(true);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		//draw the background;
		Paint background = new Paint();
		background.setColor(getResources().getColor(R.color.puzzle_background));
		canvas.drawRect(0, 0, getWidth(), getHeight(), background);
		
		//draw the board
			//draw colors for grid lines
		Paint dark=new Paint();
		dark.setColor(getResources().getColor(R.color.puzzle_dark));
		Paint hilite=new Paint();
		hilite.setColor(getResources().getColor(R.color.puzzle_hilite));
		Paint light=new Paint();
		light.setColor(getResources().getColor(R.color.puzzle_light));
		
			//draw the minor grid lines
		for(int i=0;i<9;i++){
			canvas.drawLine(0, i*height, getWidth(), i*height, light);
			canvas.drawLine(0, i*height+1, getWidth(), i*height+1, hilite);
			canvas.drawLine(i*width, getHeight(), i*width, getHeight(), light);
			canvas.drawLine(i*width+1, getHeight(), i*width+1, getHeight(), hilite);
		}
		
			//draw the major grid lines
		for(int i=0;i<9;i++){
			if(i%3!=0)
				continue;
			canvas.drawLine(0, i*height, getWidth(), i*height, dark);
			canvas.drawLine(0, i*height+1, getWidth(), i*height+1, hilite);
			canvas.drawLine(i*width, getHeight(), i*width, getHeight(), dark);
			canvas.drawLine(i*width+1, getHeight(), i*width+1, getHeight(), hilite);
		}
		
		//draw the numbers
			//define color and style for numbers
		Paint foreground=new Paint(Paint.ANTI_ALIAS_FLAG);
		foreground.setColor(getResources().getColor(R.color.puzzle_foreground));
		foreground.setStyle(Style.FILL);
		foreground.setTextSize(height*0.75f);
		foreground.setTextScaleX(width/height);
		foreground.setTextAlign(Paint.Align.CENTER);
		
			//draw the number in the center of tile
		FontMetrics fm=foreground.getFontMetrics();
			//centering the x:use alignment (and x at midpoint)
		float x=width/2;
			//centering the y:measure ascent and descent first
		float y=height/2-(fm.ascent+fm.descent)/2;
		for(int i=0;i<9;i++)
			for(int j=0;j<9;j++){
				canvas.drawText(this.game.getTileString(i,j), i*width+x, j*height+y, foreground);
			}
		
		//draw the selection
		Log.d(TAG, "select Rect="+selRect);
		Paint selected=new Paint();
		selected.setColor(getResources().getColor(R.color.puzzle_selected));
		canvas.drawRect(selRect, selected);
		
	 if(Settings.getHints(getContext()))
	 {//draw the hints
			//pick a hint color based on #moves left
		Paint hint=new Paint();
		int c[]={getResources().getColor(R.color.puzzle_hint_0),
				getResources().getColor(R.color.puzzle_hint_1),
				getResources().getColor(R.color.puzzle_hint_2)};
		Rect r=new Rect();
		for(int i=0;i<9;i++)
			for(int j=0;j<9;j++){
				int movesleft=9-game.getUsedTiles(i,j).length;
				if(movesleft<c.length){
					getRect(i, j, r);
					hint.setColor(c[movesleft]);
					canvas.drawRect(r, hint);
				}
			}
	 }
	 
	}
	
	@Override
	public boolean onKeyDown(int keyCode,KeyEvent event) {
		Log.d(TAG, "on keyDown:keycode"+keyCode+", keyEvent:"+event);
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_UP:
			select(selx, sely-1);
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			select(selx, sely+1);
		case KeyEvent.KEYCODE_DPAD_LEFT:
			select(selx-1, sely);
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			select(selx+1, sely);
		case KeyEvent.KEYCODE_0:
		case KeyEvent.KEYCODE_SPACE: setSelectedTile(0); break;
		case KeyEvent.KEYCODE_1: setSelectedTile(1); break;
		case KeyEvent.KEYCODE_2: setSelectedTile(2); break;
		case KeyEvent.KEYCODE_3: setSelectedTile(3); break;
		case KeyEvent.KEYCODE_4: setSelectedTile(4); break;
		case KeyEvent.KEYCODE_5: setSelectedTile(5); break;
		case KeyEvent.KEYCODE_6: setSelectedTile(6); break;
		case KeyEvent.KEYCODE_7: setSelectedTile(7); break;
		case KeyEvent.KEYCODE_8: setSelectedTile(8); break;
		case KeyEvent.KEYCODE_9: setSelectedTile(9); break;
		case KeyEvent.KEYCODE_ENTER:
		case KeyEvent.KEYCODE_DPAD_CENTER:
		game.showKeypadOrError(selx, sely);
		break;
		default:
			return super.onKeyDown(keyCode, event);
		}
		return true;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event){
		if(event.getAction()!=MotionEvent.ACTION_DOWN)
			return super.onTouchEvent(event);
		
		select((int)(event.getX()/width), (int)(event.getY()/height));
		game.showKeypadOrError(selx,sely);
		Log.d(TAG, "on Touch event:"+event+",selx:"+selx+",sely:"+sely);
		return true;
	}
	
	public void setSelectedTile(int tile) {
		if(game.setTileIfValid(selx,sely,tile)){
			invalidate();
		}else {
			//number is invalid
			Log.d(TAG, "set selected tile invalid:tile:"+tile);
			startAnimation(AnimationUtils.loadAnimation(game, R.anim.shake));
		}
	}
	
	private void select(int x,int y) {
		invalidate(selRect);
		selx=Math.min(Math.max(x, 0), 8);
		sely=Math.min(Math.max(y, 0), 8);
		getRect(selx, sely, selRect);
		invalidate(selRect);
		
	}
	
	@Override
	protected void onSizeChanged(int w,int h,int oldw,int oldh) {
		width=w/9f;
		height=h/9f;
		getRect(selx,sely,selRect);
		Log.d(TAG, "onSizeChanged: width,"+width+"height,"+height);
		super.onSizeChanged(w, h, oldw, oldh);
	}
	
	private void getRect(int x,int y,Rect rect){
		rect.set((int)(x*width), (int)(y*height), 
				(int)(x*width+width), (int)(y*height+height));
	}
	
	private float width;
	private float height;
	private int selx;
	private int sely;
	private final Rect selRect=new Rect();
}
