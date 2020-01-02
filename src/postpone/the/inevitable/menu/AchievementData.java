package postpone.the.inevitable.menu;

import java.text.DecimalFormat;

import android.content.SharedPreferences;

public class AchievementData {

	public final String achievementName;
	public final String achievementDescription;
	public final String progress;
	public final boolean completed;
	public final String date;
	public final int intData;
	public final float floatData;
	
	private final SharedPreferences sp;
	private final int achievementId;
	
	private final static DecimalFormat dec = new DecimalFormat("##0.0");
	
	public AchievementData(String achievementName, String achievementDescription, String progress, int achievementId,
			SharedPreferences sp) {
		
		
		if (sp == null || achievementId == -1) {
	        
			this.achievementName = "";
			this.achievementId = -1;
			this.completed = false;
	        this.date = achievementDescription;
	        this.achievementDescription = "";
	        this.intData = 0;
	        this.floatData = 0;
	        this.sp = null;
			this.progress = "";
			
		}
		else {
			this.achievementName = achievementName;
			this.achievementId = achievementId;
			this.achievementDescription = achievementDescription;	
			this.completed = sp.getBoolean("achievement" + achievementId + "_completed",false);
	        this.date = sp.getString("achievement" + achievementId + "_date", "");
	        this.intData = sp.getInt("achievement" + achievementId + "_int", -1);
	        this.floatData = sp.getFloat("achievement" + achievementId + "_float", -1);
	        
	        //The progress text should only contain a integer
	        if (progress.length() > 0 && this.intData != -1 && this.floatData == -1) {
		        this.progress = String.format(progress, 
		        		new Object[] { intData });        
	        }
	        //The progress text should only contain a float
	        else if (progress.length() > 0 && this.intData == -1 && this.floatData != -1) {
		        this.progress = String.format(progress, 
		        		new Object[] { dec.format(floatData) });        
	        }
	        //The progress text should contain a integer and a float
	        else if (progress.length() > 0 && this.intData != -1 && this.floatData != -1) {
		        this.progress = String.format(progress, 
		        		new Object[] { dec.format(floatData), intData });        
	        }
	        else {
	        	this.progress = "";
	        }
	        this.sp = sp;
	        
		}
	}
	
	
	public void saveAchievement(boolean completed, String date, int intData, float floatData) {
		
		if (sp != null || achievementId != -1) {
			sp.edit().putBoolean("achievement" + achievementId + "_completed", completed).commit();
			sp.edit().putString("achievement" + achievementId + "_date", date).commit();
			sp.edit().putInt("achievement" + achievementId + "_int", intData).commit();
			sp.edit().putFloat("achievement" + achievementId + "_float", floatData).commit();
		}
	}
	
}
