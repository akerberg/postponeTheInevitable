package postpone.the.inevitable.menu;

import java.text.DecimalFormat;

import postpone.the.inevitable.game.MazeActivity;
import postpone.the.inevitable.game.Utils;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LevelButton extends ImageButton implements OnClickListener {

	//Private variables
	private int levelNbr = -1;
	private double time = 0;
	private double target_time = 0;
	private String date = "";
	private boolean levelCompleted = false;
	private boolean locked = false;
	
	private final Paint paint1 = new Paint();
	private final Paint paint2 = new Paint();
	private final Paint paint2_stroke = new Paint();

	private Bitmap completeImage;
	private Bitmap padLockImage;
	private final Typeface typeface;
	
	private String levelNumberString = "";
	private String timeString = "";
	
	//Buttons size
    private int height = this.getHeight();
    private int width = this.getWidth();
    
	private final DecimalFormat dec;

	//Should not be used
	public LevelButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		typeface = null;
		dec = null;
	}
	
    //Constructor
	public LevelButton(Context context, AttributeSet attrs, Typeface type , final DecimalFormat dec) {
		super(context, attrs);

		typeface = type;
		this.dec = dec;
		
		//Make the text, screen independent
		final int textSize1 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 13, getResources().getDisplayMetrics());
		final int textSize2 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 28, getResources().getDisplayMetrics());
		final int textSize2_stroke = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());

		// set's the paint's colour
		paint1.setColor(Color.WHITE);
		// set's paint's text size
		paint1.setTextSize(textSize1);
		// smooth's out the edges of what is being drawn
		paint1.setAntiAlias(true);
		//Set typeface bold
		paint1.setTypeface(type);
		// Text is centered on position
		paint1.setTextAlign(Align.CENTER);
		
		// set's the paint's colour
		paint2.setColor(Color.WHITE);
		// set's paint's text size
		paint2.setTextSize(textSize2);
		// smooth's out the edges of what is being drawn
		paint2.setAntiAlias(true);
		//Set typeface bold
		paint2.setTypeface(type);
		// Text is centered on position
		paint2.setTextAlign(Align.CENTER);

		//Stroke 
		paint2_stroke.setStyle(Style.STROKE);
		paint2_stroke.setStrokeWidth(textSize2_stroke);
		paint2_stroke.setAntiAlias(true);
		paint2_stroke.setTypeface(type);
		paint2_stroke.setColor(Color.BLACK);
		paint2_stroke.setTextAlign(Align.CENTER);
		paint2_stroke.setTextSize(textSize2);
		
		int margin = 5;
		// Get the screen's density scale
		final float scale = getResources().getDisplayMetrics().density;
		// Convert the dps to pixels, based on density scale
		margin = (int) (margin * scale + 0.5f);		
		
    	this.setBackgroundResource(R.drawable.levelbutton);
    	LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1f);
    	layoutParams.setMargins(margin,margin,margin,margin);
    	this.setLayoutParams(layoutParams);
    	
		//images
    	completeImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.check);
		padLockImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.lock);
		
		//The button has its own on click listener
		this.setOnClickListener(this);
	}
	
	 @Override
	    public void onSizeChanged (int w, int h, int oldw, int oldh){
	        super.onSizeChanged(w, h, oldw, oldh);
			//Get size of view
			height = this.getHeight();
			width = this.getWidth();
			completeImage = getResizedBitmap(completeImage, height*4/5, width*4/5);
			padLockImage = getResizedBitmap(padLockImage, height*4/5, width*4/5);
			
			paint1.setTextSize(height/6);
			paint2.setTextSize(height/3);
			paint2_stroke.setTextSize(height/3);
	    }


	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		//Display an image on button if the level is locked
		if (locked) {
			canvas.drawBitmap(padLockImage, width/10, height/10, null);
		}
		//Display an image on button if the level is completed
		else if (levelCompleted && levelNbr != -1) {
			canvas.drawBitmap(completeImage, width/10, height/10, null);
			canvas.drawText(timeString, width*21/40, height*17/20, paint1);
		}
		else if (levelNbr == -1 && time > 0) {
			canvas.drawText(timeString, width*21/40, height*17/20, paint1);
		}
		
		if (levelNbr != -1) {
			//Position depends on size of view
			canvas.drawText(levelNumberString, width*21/40, height*9/16, paint2);
			canvas.drawText(levelNumberString, width*21/40, height*9/16, paint2_stroke);
		}
		else {
			//Position depends on size of view
			canvas.drawText("?", width*21/40, height*9/16, paint2);
			canvas.drawText("?", width*21/40, height*9/16, paint2_stroke);
		}
		invalidate();
	}	
	
	//Called from the outside to change data on the button
	public void init(int levelNbr, double time, boolean levelCompleted, String date, double target_time, boolean locked) {
		
		this.levelNbr = levelNbr;
		this.levelNumberString = "" + levelNbr;
		
		this.time = time;
		this.timeString = dec.format(time);
		
		this.levelCompleted = levelCompleted;
		this.date = date;
		this.target_time = target_time;
		this.locked = locked;
		
		/*if (locked) {
			paint2.setAlpha(50);
			paint2_stroke.setAlpha(50);
		}
		else {
			paint2.setAlpha(255);
			paint2_stroke.setAlpha(255);
		}*/
		
	}

	@Override
	protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {
	   //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	   super.onMeasure(widthMeasureSpec, widthMeasureSpec);
	}	

	//Resize the image displayed when level is complete
	private final Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth)
	{
	    final int width = bm.getWidth();
	    final int height = bm.getHeight();
	    final float scaleWidth = ((float) newWidth) / width;
	    final float scaleHeight = ((float) newHeight) / height;
	    // create a matrix for the manipulation
	    final Matrix matrix = new Matrix();
	    // resize the bit map
	    matrix.postScale(scaleWidth, scaleHeight);
	    // recreate the new Bitmap
	    return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
	}

	//Open a dialog showing data about the selected level.
	@Override
	public void onClick(View v) {
		
		SoundManager.playClick();
    	
		if (!locked) {
			final Context mContext = LevelButton.this.getContext();
			MainMenu.LEVEL_DIALOG = new Dialog(mContext,R.style.level_dialog_no_background);
			MainMenu.LEVEL_DIALOG.requestWindowFeature(Window.FEATURE_NO_TITLE);
			
			//Populate dialog with relevant level data
			MainMenu.LEVEL_DIALOG.setContentView(R.layout.level_dialog);

			final TextView titletext = (TextView) MainMenu.LEVEL_DIALOG.findViewById(R.id.dialog_title);
			titletext.setTypeface(typeface);
			if (levelNbr != -1) {
				titletext.setText("LEVEL: " + levelNbr);
			}
			else {
				titletext.setText("RANDOM LEVEL");
			}
			
			final TextView timetext = (TextView) MainMenu.LEVEL_DIALOG.findViewById(R.id.dialog_time);
			timetext.setText(mContext.getString(R.string.level_dialog_time) + " " + (time > 0 ? ""+dec.format(time) +"sec": mContext.getString(R.string.level_dialog_time2)));
			
			final TextView datetext = (TextView) MainMenu.LEVEL_DIALOG.findViewById(R.id.dialog_date);
			
			if (time > 0) {
				datetext.setText(mContext.getString(R.string.level_dialog_date) + " " + date);
			}
			else {
				datetext.setVisibility(View.GONE);
			}

			final TextView target_time_text = (TextView) MainMenu.LEVEL_DIALOG.findViewById(R.id.dialog_target_time);
			if (levelNbr != -1) {
				target_time_text.setText(mContext.getString(R.string.level_dialog_taget_time) + " " + target_time +"sec");
			}
			else {
				target_time_text.setVisibility(View.GONE);
			}
			
			final TextView theme_text = (TextView) MainMenu.LEVEL_DIALOG.findViewById(R.id.dialog_theme);
			if (levelNbr != -1) {
				theme_text.setText("Theme: " +Utils.getThemeTitle(Utils.getThemeFromLevelId(levelNbr)));
			}
			else {
				theme_text.setText("Theme: Random");
			}
			
			//Start this level
			final Button playButton = (Button) MainMenu.LEVEL_DIALOG.findViewById(R.id.dialog_start);
			playButton.setTypeface(typeface);
			playButton.setOnClickListener(new OnClickListener(){
	            @Override
	            public void onClick(View v) {
					SoundManager.playClick();
					System.gc();
	    			Intent newIntent = new Intent(mContext, MazeActivity.class);
					newIntent.putExtra("levelNbr", levelNbr);
	    			mContext.startActivity(newIntent);
	            }
	        });
			
			//Access the help webview 
			final Button helpButton = (Button) MainMenu.LEVEL_DIALOG.findViewById(R.id.dialog_help);
			helpButton.setTypeface(typeface);
			helpButton.setOnClickListener(new OnClickListener(){
	            @Override
	            public void onClick(View v) {
					SoundManager.playClick();
	    			Intent newIntent = new Intent(mContext, HelpActivity.class);
	    			mContext.startActivity(newIntent);
	            }
	        });
			
			//Show dialog
			MainMenu.LEVEL_DIALOG.show();
		}
		else {
			Toast.makeText(LevelButton.this.getContext(), 
					LevelButton.this.getContext().getString(R.string.level_locked), Toast.LENGTH_SHORT).show();
		}

	}
	
}
