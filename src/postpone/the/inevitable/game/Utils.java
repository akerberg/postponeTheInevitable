package postpone.the.inevitable.game;

import java.util.Random;

import org.andengine.entity.shape.RectangularShape;

import android.graphics.Point;

public class Utils {

	public final static int THEME_GRASS = 0;
	public final static int THEME_SNOW = 1;
	public final static int THEME_SAND = 2;

	//Returns the background resource depending on theme
	public static String getBackgroundImage(int theme) {
		if (theme == THEME_SNOW) {
			return "background_snow.png";
		}
		else if (theme == THEME_SAND) {
			return "background_sand.png";
		}
		else 
			return "background_grass.png";
	}

	//Returns the cloud resource depending on theme
	public static String getCloudImage(int theme) {
		if (theme == THEME_SNOW) {
			return "clouds_snow.png";
		}
		else if (theme == THEME_SAND) {
			return "clouds_sand.png";
		}
		else 
			return "clouds_grass.png";
	}
	
	//Returns the startwave resource depending on theme
	public static String getStartwaveImage(int theme) {
		if (theme == THEME_SNOW) {
			return "startwave_snow.png";
		}
		else if (theme == THEME_SAND) {
			return "startwave_sand.png";
		}
		else 
			return "startwave_grass.png";
	}
	
	//Returns the towers resource depending on theme
	public static String getTowersImage(int theme) {
		if (theme == THEME_SNOW) {
			return "towers_snow.png";
		}
		else if (theme == THEME_SAND) {
			return "towers_sand.png";
		}
		else 
			return "towers_grass.png";
	}

	//returns the number of frames this image uses in one direction (The same in both x and y)
	public static int getFramesForTowerImage(int theme) {
		if (theme == THEME_SNOW || theme == THEME_SAND) {
			return 3;
		}
		else 
			return 2;		
	}
	
	//Returns the name of the theme
	public static String getThemeTitle(int theme) {
		if (theme == THEME_SNOW) {
			return "Snow";
		}
		else if (theme == THEME_SAND) {
			return "Sand";
		}
		else 
			return "Grass";
	}

	//Returns which theme this level should use depending on levelId
	public static int getThemeFromLevelId(int levelId) {
		
		if (levelId != -1) {
			if ((levelId % 4) == 2) {
				return THEME_SNOW;
			}
			if ((levelId % 4) == 0) {
				return THEME_SAND;
			}
			else {
				return THEME_GRASS;
			}
		}
		else {
			final Random generator = new Random();
			return(generator.nextInt(3));
		}
	}
	
	public static Point getCenterPositionOfRectangle(RectangularShape tmp) {
		return new Point((int)(MazeActivity.CAMERA_WIDTH/2 - tmp.getWidth()/2f), 
				(int)(MazeActivity.CAMERA_HEIGHT/2 - tmp.getHeight()/2f));
		
	}
	
}
