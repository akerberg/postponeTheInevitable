package postpone.the.inevitable.menu;

import postpone.the.inevitable.db.LevelDataSource;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

public class Settings extends PreferenceActivity{
	 
    private final LevelDataSource levelDataSource = new LevelDataSource(this);
	
    @SuppressWarnings("deprecation")
	@Override
    protected void onCreate(final Bundle savedInstanceState) {

    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    	super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);   		
		
        setContentView(R.layout.settings);
        
        //Close activity
        final ImageButton closeButton = (ImageButton)findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
            	
            	//Check if sound is enabled
                final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(Settings.this);
                SoundManager.PLAY_SOUND = sp.getBoolean("sound",true);
                
				SoundManager.playClick();
				finish();
            }
        });         
        
		addPreferencesFromResource(R.layout.preferences);

		//used when reseting all data
		final CheckBoxPreference soundEnabled = (CheckBoxPreference)findPreference("sound");

		//also used when resetting
		final ListPreference offset = (ListPreference)findPreference("offset");
		
		final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        switch (which){
		        case DialogInterface.BUTTON_POSITIVE:

		        	//Yes button clicked
		    		levelDataSource.open();		
			        levelDataSource.reset();
					levelDataSource.close();

	    			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(Settings.this);
	    			pref.edit().clear().commit();

	    			//Set graphical display to default
	    			soundEnabled.setChecked(true);
	    			offset.setValueIndex(1);
	    			
		            CharSequence text = Settings.this.getString(R.string.clear_message);
		            int duration = Toast.LENGTH_SHORT;
		            Toast toast = Toast.makeText(Settings.this, text, duration);
		            toast.show();
		        	
		            break;

		        case DialogInterface.BUTTON_NEGATIVE:
		            //No button clicked
		            break;
		        }
		        dialog.dismiss();
		    }
		};

		//Access a method in the database helper class that erases all user progress
        final Preference clearButton = (Preference)findPreference("clear_data");
        clearButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
	        @Override
	        public boolean onPreferenceClick(Preference arg0) { 

	    		AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
	    		builder.setMessage("This will reset all progress in the game. Continue?").setPositiveButton("Yes", dialogClickListener)
	    		    .setNegativeButton("No", dialogClickListener).show();		
	        	
	        	return true;
	        }
	    });
        
        String versionName = "1.07";
    	try {
			versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			//Ignore
		}
        
		//Open the webview displaying information about the game
        final Preference aboutButton = (Preference)findPreference("about");
        aboutButton.setSummary(String.format(Settings.this.getString(R.string.about_summary), 
        		new Object[] { versionName }));        
        aboutButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
	        @Override
	        public boolean onPreferenceClick(Preference arg0) {
				Intent newIntent = new Intent(Settings.this, HelpActivity.class);
				startActivity(newIntent);
				return true;
	        }
	    });        
        
    }
    
}