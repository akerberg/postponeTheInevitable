package postpone.the.inevitable.menu;

import java.util.ArrayList;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ListView;

public class Achievements extends Activity {

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.achievement);
        
        //Close activity
        final ImageButton closeButton = (ImageButton)findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
				SoundManager.playClick();
				finish();
            }
        });        
        
        
        final ListView list=(ListView)findViewById(R.id.list);
        
        final ArrayList<AchievementData> data = new ArrayList<AchievementData>();

        //Find all achievements data
		final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        
        data.add(new AchievementData(
        		"Complete level 1-20", 
        		"To complete this achievement you have to reach the target time on level 1-20.",
        		"Progress: %s/20",
        		1, sp));
        
        data.add(new AchievementData(
        		"Builder, Level 4", 
        		"To complete this achievement you have to place the tower and manually start the level in less than 4 seconds. All this while successfully reaching the target time.",
        		"",
        		2, sp));
        
        data.add(new AchievementData(
        		"Complete level 21-40", 
        		"To complete this achievement you have to reach the target time on level 21-40.",
        		"Progress: %s/20",
        		3, sp));
        
        data.add(new AchievementData(
        		"Long walk. Level 30",
        		"To complete this achievement the enemy has to walk over 200 meters on level 30.", 
        		"Longest distance: %s meter",
        		4, sp));
        
        data.add(new AchievementData(
        		"Complete level 41-60", 
        		"To complete this achievement you have to reach the target time on level 41-60.",
        		"Progress: %s/20",
        		5, sp));
        
        data.add(new AchievementData(
        		"Builder, Level 45", 
        		"To complete this achievement you have to place the towers and manually start the level in less than 15 seconds. All this while successfully reaching the target time.",
        		"",
        		6, sp));
        
        data.add(new AchievementData(
        		"No totems, Level 55", 
        		"To complete this achievement you have to complete level 55 without using a singe totem tower.",
        		"",
        		7, sp));
        
        data.add(new AchievementData(
        		"Complete all ordinary levels", 
        		"To complete this achievement you have to reach the target time on all ordinary levels. When this achievement is completed the final level will be unlocked.",
        		"Progress: %s/60",
        		8, sp));
        
        data.add(new AchievementData(
        		"Random level",
        		"To complete this achievement you have to complete the random level with a best time over 30 seconds.",
        		"Best time: %ssec",
        		9, sp));

        data.add(new AchievementData(
        		"Long walk", 
        		"To complete this achievement the enemy has to walk over 330 meters on an single ordinary level.", 
        		"Longest distance: %s meter, level: %s",
        		10, sp));

        
        int completedAchievements = 0;
        for (final AchievementData row: data) {
        	completedAchievements += row.completed ? 1:0;
        }
        
        
        //Dummy
        data.add(new AchievementData(
        		"", "Achievements complete: " + completedAchievements + "/10", "", -1, null));
        
        final AchievementAdapter adapter=new AchievementAdapter(this, data);
        list.setAdapter(adapter);        

    }
	
}
