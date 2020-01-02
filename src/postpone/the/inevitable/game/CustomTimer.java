package postpone.the.inevitable.game;

import java.text.DecimalFormat;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.text.Text;

public class CustomTimer implements ITimerCallback {

	//Static final variables
	private static final DecimalFormat dec = new DecimalFormat("##0.0");
	private final static String timer_text_countdown = "TIME LEFT:";
	private final static String timer_text = "TIME:";
	
	//Final variables
	private final Text countdownText;
	private final MazeActivity mMain;
	
	public int timeLeft;
	public boolean timerNotCompleted = true;

	private boolean doOnce = false;
	
	//Constructor
	public CustomTimer(int time, Text countdownText, MazeActivity main) {
		timeLeft = time;
		this.countdownText = countdownText;
		mMain = main;
	}
	
	//Timer that countdown before the level starts and the counts up when the enemy starts walking
	@Override
	public void onTimePassed(final TimerHandler pTimerHandler) {
		
		if (timeLeft > 0) {
			timeLeft = timeLeft-1;
			countdownText.setText(timer_text_countdown + " " + timeLeft);
		}
		else {
			if (!doOnce) {
				countdownText.setText(timer_text_countdown + " " + "0");
				mMain.endEditAndStartMovement();
				doOnce = true;
			}
			else if (timerNotCompleted) {
				countdownText.setText(timer_text + " " +dec.format(Enemy.TIMER));
			}
		}
	}
	
	public void updateText() {
		countdownText.setText(timer_text + " " +dec.format(Enemy.TIMER));
	}

	public void setVisible(boolean b) {
		countdownText.setVisible(b);
	}
}
