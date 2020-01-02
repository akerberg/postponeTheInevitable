package postpone.the.inevitable.menu;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class HelpActivity extends Activity  {

	
	final String linebreak = "\n";
	final String page1_line1 = "The goal is to prolong the time it takes for the enemy to go from start to end point (1 and 2 on image).";
	final String page1_line2 = "The player drags stones and totems (3,4 on image) and drops them on free spots on the game field. It is not possible to place a object at a position which will block the path entirely.";
	final String page1_line3 = "Both the stone and the totem blocks the path of the enemy but the totem also stuns the enemy for 5 seconds. ";
	final String page1_line4 = "Each stone cost a green jewel and totems a blue jewel. Each level have a predefined number of jewels so spend them wisely.";
	final String page1_line5 = "When time left reaches zero the enemy starts walking and it is no longer possible to place any objects.";
	final String page1_line6 = "If the player is done before the time runs out or wish to fast forward when enemy walks through the maze, this is possible using the bottom right button.";
	final String page1_line7 = "If the enemy is delayed long enough (reaching the target time displayed in level section) more levels will be unlocked.";
	final String page1_line8 = "The game is powered by AndEngine and all sounds have been generated in SFXR.";
	
	final String page1 = page1_line1 + linebreak + linebreak + page1_line2 + linebreak + linebreak + page1_line3 + linebreak + linebreak + page1_line5 + 
			linebreak + linebreak + page1_line5 + linebreak + linebreak + page1_line6 + linebreak + linebreak + page1_line7 + linebreak + linebreak + page1_line8;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Be sure to call the super class.
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);   

        setContentView(R.layout.help);
        
		final TextView helpText = (TextView)findViewById(R.id.helpText);
		helpText.setText(page1);
		
		final int imageWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, getResources().getDisplayMetrics());		
		final int imageHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180, getResources().getDisplayMetrics());		

		final ImageView image1 = (ImageView)findViewById(R.id.helpImage1);
		final ImageView image2 = (ImageView)findViewById(R.id.helpImage2);
		
		image1.setImageBitmap(
			    decodeSampledBitmapFromResource(getResources(), R.drawable.screen1, imageWidth, imageHeight));
		image2.setImageBitmap(
			    decodeSampledBitmapFromResource(getResources(), R.drawable.screen2, imageWidth, imageHeight));

        //Close activity
        final ImageButton closeButton = (ImageButton)findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
				SoundManager.playClick();
				finish();
            }
        }); 
		
    }
	
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
            int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }
    
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;
	
	    if (height > reqHeight || width > reqWidth) {
	
	        // Calculate ratios of height and width to requested height and width
	        final int heightRatio = Math.round((float) height / (float) reqHeight);
	        final int widthRatio = Math.round((float) width / (float) reqWidth);
	
	        // Choose the smallest ratio as inSampleSize value, this will guarantee
	        // a final image with both dimensions larger than or equal to the
	        // requested height and width.
	        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
	    }
	
	    return inSampleSize;
    }

}
