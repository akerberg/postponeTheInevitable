package postpone.the.inevitable.menu;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

import postpone.the.inevitable.db.Level;
import postpone.the.inevitable.db.LevelDataSource;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainMenu extends Activity implements OnClickListener {

	private int pageNbr = 0;
	private int oldPageNbr =  0;
	
	private final static int levelsEachPage = 10;
	private final static int unlockedNotCompletedLevels = 5;
	
	//Pages is defined depending on how many levels there are.
	private int pages = 0;
	
    private final LevelDataSource levelDataSource = new LevelDataSource(this);
	
	private ArrayList<Level> allLevels;
	private LevelButton[] allLevelButtons;
	
	public static Dialog LEVEL_DIALOG;
	
	private TextView finalMazeDescription;
	
	//also count number of completed levels
	//Used to show the padlock if the level is not available yet
	private int completedLevels = 0;
	
	private AchievementData achievement1;
	private AchievementData achievement2;
	private AchievementData achievement3;
	private AchievementData achievement4;
	private AchievementData achievement5;
	private AchievementData achievement6;
	private AchievementData achievement7;
	private AchievementData achievement8;
	private AchievementData achievement9;
	private AchievementData achievement10;
	
    private ImageButton achievementsButton;
    private ImageButton achievementsButton2;
    
    private final static DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT);
    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);   
        
        setContentView(R.layout.main);
        
        //Open connection to database and load all levels
		levelDataSource.open();
		allLevels = levelDataSource.getAllLevels();
        
        //Number of pages. Calculated so that if there are more than 1 full page but less than 2 full pages -> 2 pages will be shown 
		pages = LevelDataSource.NBR_OF_MAPS/levelsEachPage + (LevelDataSource.NBR_OF_MAPS%levelsEachPage <= 0 ? 0:1);
		
        //Add the page for the extra level
        pages++;
        
    	final SwipeLayout mainLinear = (SwipeLayout)findViewById(R.id.mainview);

    	//Used to "swipe" to next page
    	final ImageButton leftButton = (ImageButton)findViewById(R.id.LeftButton);
        final OnClickListener leftClick = new OnClickListener(){

            @Override
            public void onClick(View v) {
	    		SoundManager.playClick();
	    		pageNbr = (pageNbr-1) % pages;
    			if (pageNbr < 0)
    				pageNbr = pages-1;
    	    	mainLinear.swipeRight();

            }
        };
    	leftButton.setOnClickListener(leftClick);
        mainLinear.setLeftClickAction(leftClick);

        //Used to "swipe" to next page
        final ImageButton rightButton = (ImageButton)findViewById(R.id.RightButton);
        final OnClickListener rightClick = new OnClickListener(){

            @Override
            public void onClick(View v) {
	    		SoundManager.playClick();
    			pageNbr = (pageNbr+1) % pages;
    	    	mainLinear.swipeLeft();
            }
        };
        rightButton.setOnClickListener(rightClick);
        mainLinear.setRightClickAction(rightClick);
        

        

        //No point in showing the left or right button if there is only one page
        if (pages < 2) {
    		leftButton.setVisibility(View.INVISIBLE);
    		rightButton.setVisibility(View.INVISIBLE);
    	}
        
        //Open settings activity
        final ImageButton settingButton = (ImageButton)findViewById(R.id.settingsButton);
        settingButton.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
				SoundManager.playClick();
    			Intent newIntent = new Intent(MainMenu.this, Settings.class);
    			startActivity(newIntent);
            }
        });

        // Add page markers to the bottom row
        final LinearLayout levelrow = (LinearLayout) findViewById(R.id.levelrow);
        final ImageView[] allItems = new ImageView[pages];
        
        for (int i = 0; i < (pages); i++) {
            final ImageView tmp = new ImageView(this);
            tmp.setImageDrawable(this.getResources().getDrawable(R.drawable.state));
            tmp.setAdjustViewBounds(true);
            tmp.setScaleType(ScaleType.CENTER_INSIDE);
            tmp.setEnabled(false);
            if (i == pageNbr)
            	tmp.setEnabled(true);
            levelrow.addView(tmp);
            allItems[i] = tmp;
            
        }

        //Description of the final maze. Only visible on the last page.
        finalMazeDescription = (TextView) findViewById(R.id.finalMaze);
        
        //Level buttons
        final LinearLayout row1 = (LinearLayout) findViewById(R.id.button_row1);
        final LinearLayout row2 = (LinearLayout) findViewById(R.id.button_row2);
        allLevelButtons = new LevelButton[levelsEachPage];
        final Typeface type = Typeface.createFromAsset(getAssets(),"font/font.ttf"); 
        
    	final DecimalFormat dec = new DecimalFormat("##0.0");
        for (int i = 0; i < allLevelButtons.length; i++) {
        	allLevelButtons[i] = new LevelButton(this, null,type,dec);
            if (i % 2 != 0) {
	            row2.addView(allLevelButtons[i]);
            }
            else {
	            row1.addView(allLevelButtons[i]);
            }
        }
        
        
        //Open achievements activity
        achievementsButton = (ImageButton)findViewById(R.id.achievmentsButton);
        achievementsButton2 = (ImageButton)findViewById(R.id.achievmentsButton2);

        final OnClickListener achievementListener = new OnClickListener(){
            @Override
            public void onClick(View v) {
				SoundManager.playClick();
    			Intent newIntent = new Intent(MainMenu.this, Achievements.class);
    			startActivity(newIntent);
            }
        };          
    	achievementsButton.setOnClickListener(achievementListener);
    	achievementsButton2.setOnClickListener(achievementListener);

        //You MUST call initSounds() before any other SoundManager() method
        SoundManager.getInstance().initSounds(this, 1); 
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        
    	final AnimationListener tmp = new AnimationListener() {
      	   @Override
      	   public void onAnimationStart(Animation arg0) {
      	   }

      	   @Override
      	   public void onAnimationEnd(Animation arg0) {
      		   
      		   //stop animation from repeating forever
      		   if (pageNbr == oldPageNbr) {
      			   return;
      		   }
      		   
      			allItems[oldPageNbr].setEnabled(false);
      			allItems[pageNbr].setEnabled(true);
      			populateButtons();
      			
      			mainLinear.resetAfterSwipe(arg0);

      			oldPageNbr = pageNbr;
      	   }

 			@Override
 			public void onAnimationRepeat(Animation animation) {
 			}
 			
      	};
    	
      	mainLinear.setAnimationListener(tmp);
        mainLinear.setOnClickListener(MainMenu.this);
    }
    
    //Add data to the level buttons
    private void populateButtons() {
    	//Hide all buttons. Only the one populated should be visible
    	for(LevelButton tmp: allLevelButtons) {
    		tmp.setVisibility(View.INVISIBLE);
    	}
    	
    	finalMazeDescription.setVisibility(View.GONE);
    	
    	boolean lvlLocked = false;
    	
    	//If we are on the final page. Only display the random map
    	if (pageNbr == pages-1 ) {
    		allLevelButtons[4].setVisibility(View.VISIBLE);
    		lvlLocked = (allLevels.size() != completedLevels);

    		allLevelButtons[1].setVisibility(View.GONE);
    		allLevelButtons[3].setVisibility(View.GONE);
    		allLevelButtons[5].setVisibility(View.GONE);
    		allLevelButtons[7].setVisibility(View.GONE);
    		allLevelButtons[9].setVisibility(View.GONE);
    		
        	finalMazeDescription.setVisibility(View.VISIBLE);

	        allLevelButtons[4].init(-1, achievement9.floatData, false, achievement9.date, -1,lvlLocked);
    		
    		return;
    	}
    	//Show and populate relevant buttons
	    for(Level level: allLevels) {
	    	if (level.id > pageNbr*levelsEachPage && level.id <= (pageNbr*levelsEachPage + levelsEachPage)) {
	    		allLevelButtons[level.id-1-pageNbr*levelsEachPage].setVisibility(View.VISIBLE);
	    		lvlLocked = !(level.id < completedLevels + unlockedNotCompletedLevels);
	    		allLevelButtons[level.id-1-pageNbr*levelsEachPage].init(level.id, level.time, level.completed,level.completion_date, level.target_time,lvlLocked);        
	    	}
		}
    }

    //Check sound and reestablish database
    @Override
	protected void onResume() {
    	levelDataSource.open();
    	
    	//Check if sound is enabled
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SoundManager.PLAY_SOUND = sp.getBoolean("sound",true);
        
        //Populate buttons. To be sure the user can see his new score when a level has been beaten
		allLevels = levelDataSource.getAllLevels();
        
		checkAchievements();
		populateButtons();
        
		super.onResume();
	}

    //Method that checks all achievements. Should be called whenever the game returns to the main menu
    private void checkAchievements() {
        //Load and check all achievements
    	final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
    	achievement1 = new AchievementData("","","",1,sp);
    	achievement2 = new AchievementData("","","",2,sp);
    	achievement3 = new AchievementData("","","",3,sp);
    	achievement4 = new AchievementData("","","",4,sp);
    	achievement5 = new AchievementData("","","",5,sp);
		achievement6 = new AchievementData("","","",6,sp);
		achievement7 = new AchievementData("","","",7,sp);
    	achievement8 = new AchievementData("","","",8,sp);
    	achievement9 = new AchievementData("","","",9,sp);
    	achievement10 =  new AchievementData("","","",10,sp);
    	completedLevels = 0;
    	
		int first20levels = 0;
		int second20levels = 0;
		int third20levels = 0;
		boolean newAchievementComplete = false;
		
    	for (Level tmp: allLevels) {
    		completedLevels += tmp.completed ? 1:0;
    		if (tmp.id <= 20) {
    			first20levels += tmp.completed ? 1:0;
    		}
    		else if (tmp.id > 20 && tmp.id <= 40) {
    			second20levels += tmp.completed ? 1:0;
    		}
    		else if (tmp.id > 40 && tmp.id <= 60) {
    			third20levels += tmp.completed ? 1:0;
    		}
    	}

    	//Check if there are any updates in all complete levels achievements
		if (first20levels > achievement1.intData) {
			achievement1.saveAchievement(first20levels == 20 ? true:false, first20levels == 20 ? format.format(new Date()): "", first20levels,-1);
			newAchievementComplete = first20levels == 20 || newAchievementComplete ? true : false;
		}
		if (second20levels > achievement3.intData) {
			achievement3.saveAchievement(second20levels == 20 ? true:false, second20levels == 20 ? format.format(new Date()): "", second20levels,-1);
			newAchievementComplete = second20levels == 20 || newAchievementComplete ? true : false;
		}
		if (third20levels > achievement5.intData) {
			achievement5.saveAchievement(third20levels == 20 ? true:false, third20levels == 20 ? format.format(new Date()): "", third20levels,-1);
			newAchievementComplete = third20levels == 20 || newAchievementComplete ? true : false;
		}
		if (completedLevels > achievement8.intData) {
			achievement8.saveAchievement(completedLevels == 60 ? true:false, completedLevels == 60 ? format.format(new Date()): "", completedLevels,-1);
			newAchievementComplete = completedLevels == 60 || newAchievementComplete ? true : false;
		}

		//Check if the random level achievement has been updated and is complete
		if (achievement9.floatData >= 30f && !achievement9.completed) {
			achievement9.saveAchievement(true, achievement9.date, -1, achievement9.floatData);
			newAchievementComplete = true;
		}

		//Check if the longest distance achievement has been updated and is complete
		if (achievement10.floatData >= 330f && !achievement10.completed) {
			achievement10.saveAchievement(true, achievement10.date, achievement10.intData, achievement10.floatData);
			newAchievementComplete = true;
		}

		//Check if the achievement for level 30 has been updated and is complete
		if (achievement4.floatData >= 200f && !achievement4.completed) {
			achievement4.saveAchievement(true, achievement4.date, -1, achievement4.floatData);
			newAchievementComplete = true;
		}
		
		//Check if the achievement for level 55 has been updated and is complete
		if (achievement7.date.length() > 0 && !achievement7.completed) {
			achievement7.saveAchievement(true, achievement7.date, -1, -1);
			newAchievementComplete = true;
		}

		//Check if the achievement for level 4 has been updated and is complete
		if (achievement2.date.length() > 0 && !achievement2.completed) {
			achievement2.saveAchievement(true, achievement2.date, -1, -1);
			newAchievementComplete = true;
		}

		//Check if the achievement for level 45 has been updated and is complete
		if (achievement6.date.length() > 0 && !achievement6.completed) {
			achievement6.saveAchievement(true, achievement6.date, -1, -1);
			newAchievementComplete = true;
		}
		
		if (!newAchievementComplete) {
        	achievementsButton.setVisibility(View.VISIBLE);
        	achievementsButton2.setVisibility(View.GONE);
        }
        else {
        	achievementsButton.setVisibility(View.GONE);
        	achievementsButton2.setVisibility(View.VISIBLE);
        }
    	
    }
    
    
    
    
    //Close database connection and dismiss level dialog if visible
	@Override
	protected void onPause() {
		levelDataSource.close();
		
		if (LEVEL_DIALOG != null) {
			LEVEL_DIALOG.dismiss();
		}
		
		super.onPause();
	}
	
	//Menu. Options menu. Looks better both on sandwich and Gingerbread without icons
    @Override 
    public boolean onCreateOptionsMenu(Menu menu) { 
    	super.onCreateOptionsMenu(menu); 
        menu.clear();
        menu.add(0,1,0,R.string.achievements_title); 
        menu.add(0,2,0,R.string.about_title); 
        menu.add(0,3,0,R.string.settings); 
        return true; 
    }
    
    //Called when optionsmenu is select. Open help and settings menu
    @Override 
    public boolean onOptionsItemSelected(MenuItem item){ 

    	if (item.getItemId()==1) {
			Intent newIntent = new Intent(MainMenu.this, Achievements.class);
			startActivity(newIntent);
    	}
    	else if (item.getItemId()==2) {
    		Intent newIntent = new Intent(MainMenu.this, HelpActivity.class);
    		startActivity(newIntent);	
    	}
    	else if (item.getItemId()==3) {
			Intent newIntent = new Intent(MainMenu.this, Settings.class);
			startActivity(newIntent);
    	}
    	
        return true;
    }
    
	@Override
	public void onClick(View arg0) {
	}
	
}
