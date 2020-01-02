package postpone.the.inevitable.menu;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

public class SwipeLayout extends RelativeLayout {

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private GestureDetector gestureDetector;
    private View.OnTouchListener gestureListener;    	
	
    private final static int ANIMATION_TIME = 150;
    private TranslateAnimation animLeft;
    private TranslateAnimation animRight;
    private TranslateAnimation animLeft2;
    private TranslateAnimation animRight2;

	private OnClickListener rightClick = null;
	private OnClickListener leftClick = null;    
    
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public SwipeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public SwipeLayout(Context context) {
		super(context);
		init(context);
	}

	public SwipeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	private void init(Context context) {
        // Gesture detection
        gestureDetector = new GestureDetector(context,new MyGestureDetector());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };
        
        this.setOnTouchListener(gestureListener);
        
        final DisplayMetrics metrics = context.getResources().getDisplayMetrics();
    	final int width = metrics.widthPixels;
        
    	//Test to see if this can solve some of the garbage issues
    	animLeft = new TranslateAnimation(0, -width, 0, 0);
        animRight = new TranslateAnimation(0, width, 0, 0);
        animLeft.setFillAfter(true);
        animLeft.setFillBefore(true);
        animLeft.setDuration(ANIMATION_TIME);
        animRight.setFillAfter(true);
        animRight.setFillBefore(true);
        animRight.setDuration(ANIMATION_TIME);
    	animLeft2 = new TranslateAnimation(width, 0, 0, 0);
        animRight2 = new TranslateAnimation(-width, 0, 0, 0);
        animLeft2.setFillAfter(true);
        animLeft2.setFillBefore(true);
        animLeft2.setDuration(ANIMATION_TIME);
        animRight2.setFillAfter(true);
        animRight2.setFillBefore(true);
        animRight2.setDuration(ANIMATION_TIME);
	}
	
	/**
	 * Start animation to the left
	 */
	public void swipeLeft() {
		this.startAnimation(animLeft);
	}
	
	/**
	 * Start animation to the right
	 */
	public void swipeRight() {
		this.startAnimation(animRight);
	}
	
	/**
	 * Reset animation. Shows screen again
	 */
	public void resetAfterSwipe(Animation arg0) {

		if (arg0.equals(animLeft)) {
			this.startAnimation(animLeft2);
  			}
  			else { 
  				this.startAnimation(animRight2);
  			}
	}
	
	public void setAnimationListener(Animation.AnimationListener listener) {
      	animLeft.setAnimationListener(listener);
      	animRight.setAnimationListener(listener);		
	}
	
	
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (gestureDetector.onTouchEvent(ev)) return true;
                else return super.onTouchEvent(ev);
    }
    
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (gestureDetector.onTouchEvent(ev)) return true;
                else return super.onInterceptTouchEvent(ev);
    }
    
    class MyGestureDetector extends SimpleOnGestureListener {
    	
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
            	
            	if (rightClick == null || leftClick == null) {
            		return false;
            	}
            	
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;
                // right to left swipe
                if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                	rightClick.onClick(null);
                }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                	leftClick.onClick(null);
                }
            } catch (Exception e) {
                // nothing
            }
            return false;
        }

    }

	public void setRightClickAction(OnClickListener rightClick) {
		this.rightClick = rightClick;
	}

	public void setLeftClickAction(OnClickListener leftClick) {
		this.leftClick = leftClick;
	}
}
