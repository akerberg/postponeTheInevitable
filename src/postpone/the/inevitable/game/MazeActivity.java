package postpone.the.inevitable.game;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.microedition.khronos.opengles.GL10;

import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.TextMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ColorMenuItemDecorator;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.view.RenderSurfaceView;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.color.Color;
import org.andengine.util.modifier.IModifier;

import postpone.the.inevitable.db.Level;
import postpone.the.inevitable.db.LevelDataSource;
import postpone.the.inevitable.game.AbstractTower.TOWER_OFFSET;
import postpone.the.inevitable.game.AbstractTower.TOWER_TYPE;
import postpone.the.inevitable.menu.AchievementData;
import postpone.the.inevitable.pathfinder.PathFinder;
import postpone.the.inevitable.pathfinder.SearchNode;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Typeface;
import android.opengl.GLES20;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

public class MazeActivity extends SimpleBaseGameActivity implements IOnMenuItemClickListener, IOnSceneTouchListener {

	// =======================================================
	// Not andengine specific stuff
	// =======================================================
	private LevelDataSource levelDataSource;
	private Level mlvl;
	private boolean soundEnabled = false;
	
	private boolean levelFinished = false;
	
	public PathFinder pathfinder;
	
	private final Point startPoint = new Point(0,4);
	private final Point endPoint = new Point(13, 4);
	
	//Last higlighted node in the maze. Used for visual feedback when moving the towers over nodes.
	private SearchNode highLightedNod;
	private Rectangle higlightHelperX;
	private Rectangle higlightHelperY;
	
	//To be able to deactivate towers we use these
	private StoneTower towerStone;
	private TotemTower towerTotem;
	private TemporaryTower towerTemp;
	private Enemy enemyReference;
	
	//Text
	private Text goldText;
	private Text diamondText;
	private Text finalText;
	private Text newBestTime;
	private Text levelCompleted;
	private Text targetTime;
	private Text endTime;
	
	//Game ended
	private Rectangle background;
	private Rectangle border;
	private Rectangle background2;
	
	private boolean enemyStartsMoving = false;
	
	private CustomTimer ourTimer;
	private TimerHandler ourTimerHandler;
	
	//Custom toast
	private SequenceEntityModifier toastIem;
	private SequenceEntityModifier toastIem2;
	private SequenceEntityModifier toastIem3;
	private Rectangle toastBackground;
	private Rectangle toastBorder;
	private Text toastText;

	// ===========================================================
	// Constants
	// ===========================================================

	public static final int MAZE_LEFT = 20; 
	public static final int MAZE_RIGHT = 672;
	public static final int MAZE_BOT = 100;
	public static final int MAZE_TOP = 460; 
	
	public static final int MAZE_COLUMNS = 14; 
	public static final int MAZE_ROWS = 9; 
	public static final int MAZE_COLUMNS_SIZE = 48; 
	public static final int MAZE_ROWS_SIZE = 40; 
	
	public static final int CAMERA_WIDTH = 800;
	public static final int CAMERA_HEIGHT = 480;

	//Menu
	private MenuScene mMenuScene;
	
	protected static final int MENU_RESUME = 1;
	protected static final int MENU_SOUND = 2;
	protected static final int MENU_QUIT = 3;

	private TextMenuItem soundMenuText;

	private Camera mCamera;
	private BitmapTextureAtlas mBitmapTextureAtlas;
	private TiledTextureRegion mDifferentObstacles;
	private TiledTextureRegion mStartWaveButton;
	private TiledTextureRegion mEndButton;
	private ITextureRegion mTower1;
	private ITextureRegion mTower2;
	private ITextureRegion mTower3;
	
	private Font mFontLargerText;
	private Font mFontSmallText;
	private Font mFontOrdinaryText;
	
	private TiledTextureRegion mEnemy;	
	private TiledTextureRegion mExplosion1;
	private TiledTextureRegion mExplosion2;
	
	private ITextureRegion mParallaxLayerBack;
	private ITextureRegion mParallaxLayerMid;
	
	private TiledSprite fastForwardButton;
	private TiledSprite endButton;
	
	public final Scene pScene = new Scene();
	
	private Sound mClickSound;
	private Sound mLaserSound;
	private Sound mBuySound;
	private Sound mSpawnSound;
	public static final int ID_CLICK_SOUND = 0;
	public static final int ID_LASER_SOUND = 1;
	public static final int ID_BUY_SOUND = 2;
	public static final int ID_SPAWN_SOUND = 3;
	
	// ===========================================================
	
	public static final int ID_TOWER_ERROR_1 = 0;
	public static final int ID_TOWER_ERROR_2 = 1;

	//Strings resources
	private final static String string_tower_error_1 = "There is already an obstacle at this position.";
	private final static String string_tower_error_2 = "A tower at this position would block the path.";
	private final static String string_finalText = "LEVEL ";
	private final static String string_newBestTime = "New best time!";
	private final static String string_levelCompleted = "Target time reached. Level completed";
	private final static String string_targetTime = "Target time:";
	private final static String string_oldBestTime = "You did not beat your best time";
	private final static String string_menu_sound_on = "SOUND: ON";
	private final static String string_menu_sound_off = "SOUND: OFF";
	private final static String string_menu_quit = "EXIT LEVEL";
	private final static String string_menu_resume = "RESUME";
	
	public int map_theme = 0;
	
	private final DecimalFormat dec = new DecimalFormat("##0.0");
	
	private int timeLeftBeforeLevelStarted = 0;
	
	private TOWER_OFFSET tower_marker_offset;
	
	//Preferences
	private SharedPreferences sp;
	private AchievementData achievement2;
	private AchievementData achievement4;
	private AchievementData achievement6;
	private AchievementData achievement7;
	private AchievementData achievement9;
	private AchievementData achievement10;
	
	private TOWER_TYPE currentlyMovingTower = TOWER_TYPE.NO_TOWER;
	
	/*
	 * This solution seems to try to regain missing speed aswell
	 * @Override
    public Engine onCreateEngine(final EngineOptions pEngineOptions) {
        return new FixedStepEngine(pEngineOptions, 90);
    }*/	
	
	
	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public EngineOptions onCreateEngineOptions() {

		//Load preferences
		sp = PreferenceManager.getDefaultSharedPreferences(MazeActivity.this);
		achievement2 = new AchievementData("","","",2,sp);
		achievement4 = new AchievementData("","","",4,sp);
		achievement6 = new AchievementData("","","",6,sp);
		achievement7 = new AchievementData("","","",7,sp);
		achievement9 = new AchievementData("","","",9,sp);
		achievement10 = new AchievementData("","","",10,sp);
		tower_marker_offset = TOWER_OFFSET.values()[Integer.parseInt(sp.getString("offset","1"))];		

		//Should sound be enabled
		soundEnabled = sp.getBoolean("sound",true);

		//Get the current level
        final Integer levelNbr = (Integer) this.getIntent().getExtras().get("levelNbr");
		
        //Open connection to database and load the level
        levelDataSource = new LevelDataSource(this);
		levelDataSource.open();
		
		if (levelNbr == -1) {
			//Generate a new random level
			mlvl = new Level();
		}
		else {
			mlvl = levelDataSource.getSpecificLevels(levelNbr);
		}
		
		try {
			if (levelNbr == -1) {
				mlvl.generateRandomMap();
			}
			else {
				mlvl.generateMap();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		pathfinder = new PathFinder(mlvl);

		//Set money
		PlayerData.gold = mlvl.currency1;
		PlayerData.diamond = mlvl.currency2;
		
		//Set theme
		map_theme = Utils.getThemeFromLevelId(mlvl.id);
		
		//Create camera
		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		
		final EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera);
		engineOptions.getAudioOptions().setNeedsSound(true);
		
		return engineOptions;
	}

	@Override
	public void onCreateResources() {
		this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 1024, 1024, TextureOptions.DEFAULT);
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		
		////////////////////
		// 
 		// xx223333666 
		// xxxx3333666
		// 7777777799x
		// 77777777AAx
		// 77777777111
		// 88888888111
		// AA1122xxxxx
		// 1:mDifferentObstacles
		// 2:mStartwavebutton
		// 4:mTower1
		// 5:mTower2
		// 6:mExplosion
		// 7:mParallaxLayerBack
		// 8:mParallaxLayerMid
		// 9:mEnemy
		// 3:mEndButton
		// A:mSpawn
		// B:mTower3

		//1, Load the different obstacles available
		this.mDifferentObstacles = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, Utils.getTowersImage(map_theme), 800, 504, Utils.getFramesForTowerImage(map_theme), Utils.getFramesForTowerImage(map_theme));
		//2, Load the start button
		this.mStartWaveButton = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, Utils.getStartwaveImage(map_theme), 100, 0, 2, 2);
		//6, Explosion
		this.mExplosion1 = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "explosion1.png", 724, 0, 2, 2);
		//7,8, Parallax background
		this.mParallaxLayerBack = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, Utils.getBackgroundImage(map_theme), 0, 256);
		this.mParallaxLayerMid = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, Utils.getCloudImage(map_theme), 0, 736);
		//9, Enemy
		this.mEnemy = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "enemy.png", 800, 256, 3, 4);
		//A, Spawn
		this.mExplosion2 = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "spawn.png", 800, 384, 2, 2);
		//3, end button
		this.mEndButton = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "ingameendbutton.png", 228, 0, 1, 2);

		//4,Tower 1
		this.mTower1 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "tower1.png", 0, 786);
		//5,Tower 2
		this.mTower2 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "tower2.png", 120, 786);
		//A,Tower 3
		this.mTower3 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "tower3.png", 240, 786);
		
		this.mBitmapTextureAtlas.load();
		
		//Fonts
		final ITexture fontTexture1 = new BitmapTextureAtlas(this.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
		final ITexture fontTexture2 = new BitmapTextureAtlas(this.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
		FontFactory.setAssetBasePath("font/");
		this.mFontLargerText = FontFactory.createFromAsset(this.getFontManager(), fontTexture1, this.getAssets(), "font.ttf", 36, true, android.graphics.Color.WHITE);
		this.mFontLargerText.load();
		this.mFontSmallText = FontFactory.createFromAsset(this.getFontManager(), fontTexture2, this.getAssets(), "font.ttf", 18, true, android.graphics.Color.BLACK);
		this.mFontSmallText.load();
		
		this.mFontOrdinaryText = FontFactory.create(this.getFontManager(), this.getTextureManager(), 256, 256, TextureOptions.BILINEAR, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 20, android.graphics.Color.WHITE);
		this.mFontOrdinaryText.load();

		//Sounds
		SoundFactory.setAssetBasePath("mfx/");
		try {
			this.mBuySound = SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), this, "buy.wav");
			this.mClickSound = SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), this, "click.wav");
			this.mLaserSound = SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), this, "explosion.wav");
			this.mSpawnSound = SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), this, "spawn.wav");
		} catch (final IOException e) {
			//Debug.e(e);
		}

		
	}

	@Override
	public Scene onCreateScene() {

		//this.mEngine.registerUpdateHandler(new FPSLogger());

		this.mMenuScene = this.createMenuScene();

		final AutoParallaxBackground autoParallaxBackground = new AutoParallaxBackground(0, 0, 0, 5);
		autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(0.0f, new Sprite(0, CAMERA_HEIGHT - this.mParallaxLayerBack.getHeight(), this.mParallaxLayerBack, this.getVertexBufferObjectManager())));
		autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(-3.0f, new Sprite(0, 20, this.mParallaxLayerMid, this.getVertexBufferObjectManager())));
		pScene.setBackground(autoParallaxBackground);

		//Countdown timer
		final Text countdownText = new Text(500, 5, this.mFontSmallText, "TIME LEFT: " + PlayerData.countDown, "TIME LEFT: XXXX".length(), this.getVertexBufferObjectManager());
		pScene.attachChild(countdownText);
		
		//Timer
		ourTimer = new CustomTimer(PlayerData.countDown,countdownText,this);
		ourTimerHandler = new TimerHandler(1 / 1.0f, true, ourTimer);
		
		pScene.registerUpdateHandler(ourTimerHandler);
		
		//Gold left text
		goldText = new Text(710, 5, this.mFontSmallText, String.valueOf(PlayerData.gold), "00".length(), this.getVertexBufferObjectManager());
		pScene.attachChild(goldText);
		
		//Diamond left text
		diamondText = new Text(770, 5, this.mFontSmallText, String.valueOf(PlayerData.diamond), "00".length(), this.getVertexBufferObjectManager());
		pScene.attachChild(diamondText);
		
		//For each node in the map
		for (int x = 0; x < mlvl.Width(); x++)
	    {
	        for (int y = 0; y < mlvl.Height(); y++)
	        {
	            SearchNode node = pathfinder.getNode(x,y);
	            if (node != null) {
	            	node.nodeSprite = new TiledSprite(MAZE_LEFT + x*MAZE_COLUMNS_SIZE, MAZE_BOT + y*MAZE_ROWS_SIZE, this.mDifferentObstacles.deepCopy(), this.getVertexBufferObjectManager());
	            	node.nodeSprite.setHeight(MAZE_ROWS_SIZE);
	            	node.nodeSprite.setWidth(MAZE_COLUMNS_SIZE);
	            	if (!node.Walkable) {
            			node.nodeSprite.setCurrentTileIndex(node.towerType.ordinal());
	            	}
	            	else {
            			node.nodeSprite.setCurrentTileIndex(0);
            			node.nodeSprite.setVisible(false);
	            	}
	            	pScene.attachChild(node.nodeSprite);
	            }
	        }
	    }

	    //////////////////////////////////////////////////
    	//Represent a temporary dragable tower
	    //////////////////////////////////////////////////
    	towerTemp = new TemporaryTower(this.mTower3, this, this.getVertexBufferObjectManager(), tower_marker_offset);
    	pScene.attachChild(towerTemp);
    	pScene.registerTouchArea(towerTemp);		
		
	    //////////////////////////////////////////////////
		//Represent the dragable tower
		//////////////////////////////////////////////////
    	towerStone = new StoneTower(this.mTower1, this, this.getVertexBufferObjectManager(), towerTemp, tower_marker_offset);
    	pScene.attachChild(towerStone);
    	pScene.registerTouchArea(towerStone); 
    	
		//Represent the dragable totem tower
		//////////////////////////////////////////////////
    	towerTotem = new TotemTower(this.mTower2, this, this.getVertexBufferObjectManager(), towerTemp, tower_marker_offset);
    	pScene.attachChild(towerTotem);
    	pScene.registerTouchArea(towerTotem);    	
    	///////////////////////////////////
    	
    	//Helper to easier see where the tower is placed (X)
		higlightHelperX = new Rectangle(0, 0, MazeActivity.MAZE_COLUMNS_SIZE, MazeActivity.CAMERA_HEIGHT, this.getVertexBufferObjectManager());
		higlightHelperX.setVisible(false);
		this.pScene.attachChild(higlightHelperX);
		
    	//Helper to easier see where the tower is placed (Y)
		higlightHelperY = new Rectangle(0, 0, MazeActivity.CAMERA_WIDTH, MazeActivity.MAZE_ROWS_SIZE, this.getVertexBufferObjectManager());
		higlightHelperY.setVisible(false);
		this.pScene.attachChild(higlightHelperY);
		
    	//For fallback we also register this entire scene. This is because if the phone looses track of the finger we need to be notified
    	this.pScene.setOnSceneTouchListener(this);
    	
		//Start wave
    	fastForwardButton = new TiledSprite(705,370, this.mStartWaveButton, this.getVertexBufferObjectManager()){

    			private boolean endThis = false;
    		
		        public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
		                
		        		if (endThis)
		        			return true;
		        	
		        		if (!enemyStartsMoving) {     	
			        		if(pSceneTouchEvent.getAction() == MotionEvent.ACTION_DOWN) {
			        			playSound(ID_CLICK_SOUND);
		                        this.setCurrentTileIndex(1);          
			                }
			                else if(pSceneTouchEvent.getAction() == MotionEvent.ACTION_UP) {
		                        this.setCurrentTileIndex(2);
		                        timeLeftBeforeLevelStarted = ourTimer.timeLeft;
		                        ourTimer.timeLeft = 0;
			                }
		        		}
		        		else {
			        		if(pSceneTouchEvent.getAction() == MotionEvent.ACTION_DOWN) {
			        			playSound(ID_CLICK_SOUND);
			        			this.setCurrentTileIndex(3);          
			                }
			                else if(pSceneTouchEvent.getAction() == MotionEvent.ACTION_UP) {
			                	this.setVisible(false);
		                        enemyReference.fastForward = true;
		                		ourTimerHandler.setTimerSeconds(0.02f);
		                        endThis = true;
			                }
		        		}
		                return true;
		        }
		};		
		pScene.attachChild(fastForwardButton);
		pScene.registerTouchArea(fastForwardButton);

		///////////////////		
		// Enemy
		
		final AnimatedSprite spawnAnimation = new AnimatedSprite(0, 0, this.mExplosion2, this.getVertexBufferObjectManager());
		pScene.attachChild(spawnAnimation);
		spawnAnimation.setVisible(false);
		
		this.enemyReference = new Enemy(this.mEnemy,this.pathfinder,this.mExplosion1, this, this.getVertexBufferObjectManager(), spawnAnimation);
		pScene.attachChild(enemyReference);
		/////////////////////////////////////

		//End
		background = new Rectangle(0, 0, MazeActivity.CAMERA_WIDTH, MazeActivity.CAMERA_HEIGHT, this.getVertexBufferObjectManager());
		background.setColor(0, 0, 0);
		background.setVisible(false);
		this.pScene.attachChild(background);
		
		border = new Rectangle(163, 93, 469, 335, this.getVertexBufferObjectManager());
		border.setColor(0.3f, 0.3f, 0.3f);
		border.setVisible(false);
		this.pScene.attachChild(border);

		background2 = new Rectangle(165, 95, 465, 331, this.getVertexBufferObjectManager());
		background2.setColor(0, 0, 0);
		background2.setVisible(false);
		this.pScene.attachChild(background2);
		
		if (mlvl.id != -1) {
			finalText = new Text(190, 115, this.mFontLargerText, string_finalText + mlvl.id, this.getVertexBufferObjectManager());
		}
		else {
			finalText = new Text(190, 115, this.mFontLargerText, "RANDOM LEVEL", this.getVertexBufferObjectManager());
		}
		
		finalText.setVisible(false);
		finalText.setColor(0.71f, 0.71f, 0.71f);
		finalText.setZIndex(3);
		this.pScene.attachChild(finalText);

		endTime = new Text(190, 190, this.mFontOrdinaryText, "" , 13,this.getVertexBufferObjectManager());
		endTime.setVisible(false);
		endTime.setColor(0.71f, 0.71f, 0.71f);
		endTime.setZIndex(3);
		this.pScene.attachChild(endTime);

		if (mlvl.id != -1) {
			targetTime = new Text(190, 230, this.mFontOrdinaryText, string_targetTime + " " + mlvl.target_time, string_targetTime.length() + 8,this.getVertexBufferObjectManager());
		}
		else {
			targetTime = new Text(190, 230, this.mFontOrdinaryText, "Best time: " + (achievement9.floatData > 0 ? ""+dec.format(achievement9.floatData) : "0"), "Best time: ".length() + 8,this.getVertexBufferObjectManager());
		}
		targetTime.setVisible(false);
		targetTime.setColor(0.71f, 0.71f, 0.71f);
		targetTime.setZIndex(3);
		this.pScene.attachChild(targetTime);
		
		newBestTime = new Text(190, 270, this.mFontOrdinaryText, string_newBestTime, string_oldBestTime.length(), this.getVertexBufferObjectManager());
		newBestTime.setVisible(false);
		newBestTime.setColor(0.71f, 0.71f, 0.71f);
		newBestTime.setZIndex(3);
		this.pScene.attachChild(newBestTime);

		if (mlvl.id != -1) {
			levelCompleted = new Text(190, 310, this.mFontOrdinaryText, string_levelCompleted + "ZZZZZZZ",this.getVertexBufferObjectManager());
		}
		else {
			levelCompleted = new Text(190, 270, this.mFontOrdinaryText, string_levelCompleted + "ZZZZZZZ",98,this.getVertexBufferObjectManager());
		}
		levelCompleted.setVisible(false);
		levelCompleted.setZIndex(3);
		this.pScene.attachChild(levelCompleted);
		
		//end game (back to main menu)
    	endButton = new TiledSprite(495,348, this.mEndButton, this.getVertexBufferObjectManager()){

		        public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
	        		if(pSceneTouchEvent.getAction() == MotionEvent.ACTION_DOWN) {
	        			playSound(ID_CLICK_SOUND);
                        this.setCurrentTileIndex(1);          
	                }
	                else if(pSceneTouchEvent.getAction() == MotionEvent.ACTION_UP) {
			        	/* End Activity. */
						MazeActivity.this.finish();
	                }
					return true;
		        }
		};
		endButton.setVisible(false);
		this.pScene.attachChild(endButton);
		////////////		
		
		pScene.setTouchAreaBindingOnActionDownEnabled(true);
		
		//Deactivate towers
		deactivateTowers();
		
		//Custom toast
	    toastIem = newSequenceEntityModifier();
	    toastIem.setAutoUnregisterWhenFinished(true);
	    
	    toastIem2 = newSequenceEntityModifier();
	    toastIem2.setAutoUnregisterWhenFinished(true);

	    toastIem3 = newSequenceEntityModifier();
	    toastIem3.setAutoUnregisterWhenFinished(true);

		toastBorder = new Rectangle(-100, -100, 1, 1, this.getVertexBufferObjectManager());
		toastBorder.setColor(0.3f, 0.3f, 0.3f);
		toastBorder.setZIndex(4);
		toastBorder.setVisible(false);
		toastBorder.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		this.pScene.attachChild(toastBorder);
	    
		toastBackground = new Rectangle(-100, -100, 1, 1, this.getVertexBufferObjectManager());
		toastBackground.setColor(0, 0, 0);
		toastBackground.setZIndex(4);
		toastBackground.setVisible(false);
		toastBackground.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		this.pScene.attachChild(toastBackground);

		toastText = new Text(-100, -100, this.mFontOrdinaryText, "", string_tower_error_1.length(), this.getVertexBufferObjectManager());
		toastText.setColor(0.71f, 0.71f, 0.71f);
		toastText.setZIndex(4);
		toastText.setVisible(false);
		toastText.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		this.pScene.attachChild(toastText);
				
		return pScene;
		
	}

	// ===========================================================
	// Methods
	// ===========================================================
	
	@Override
	public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
		
		if((pKeyCode == KeyEvent.KEYCODE_MENU && pEvent.getAction() == KeyEvent.ACTION_DOWN) ||
				!levelFinished && pKeyCode == KeyEvent.KEYCODE_BACK && pEvent.getAction() == KeyEvent.ACTION_DOWN) {
			
			playSound(ID_CLICK_SOUND);
			
			if(this.pScene.hasChildScene()) {
				/* Remove the menu and reset it. */
				this.mMenuScene.back();
			} else {
				/* Attach the menu. */
				this.pScene.setChildScene(this.mMenuScene, false, true, true);
			}
			return true;
		} else {
			return super.onKeyDown(pKeyCode, pEvent);
		}
	}
	
	@Override
	public boolean onMenuItemClicked(final MenuScene pMenuScene, final IMenuItem pMenuItem, final float pMenuItemLocalX, final float pMenuItemLocalY) {
		switch(pMenuItem.getID()) {
			case MENU_RESUME:
				playSound(ID_CLICK_SOUND);
				this.mMenuScene.back();
				return true;
			case MENU_SOUND:
				/* Disable sound. */
				soundEnabled = !soundEnabled;
				sp.edit().putBoolean("sound", soundEnabled).commit();
				playSound(ID_CLICK_SOUND);
				soundMenuText.setText(soundEnabled ? string_menu_sound_on : string_menu_sound_off);
				return true;
			case MENU_QUIT:
				/* End Activity. */
				playSound(ID_CLICK_SOUND);
				this.finish();
				return true;
			default:
				return false;
		}
	}
	
	//Highlight nodes when the user drags a tower across the map
	public void highLightNode(float posX, float posY) {

		stopHighLightLastNode();
		
		highLightedNod = pathfinder.getNode((int)posX/MAZE_COLUMNS_SIZE,(int)posY/MAZE_ROWS_SIZE);
		
		boolean startOrEndNode =  highLightedNod.Position.x == 0 && highLightedNod.Position.y == 4 || 
				highLightedNod.Position.x == 13 && highLightedNod.Position.y == 4;

		
		higlightHelperX.setPosition(highLightedNod.nodeSprite.getX(), 0);
		higlightHelperY.setPosition(0,highLightedNod.nodeSprite.getY());
		higlightHelperX.setVisible(true);
		higlightHelperY.setVisible(true);

		highLightedNod.nodeSprite.setVisible(true);
		
		if (highLightedNod.Walkable && !startOrEndNode) {

			//Make node green
			higlightHelperX.setColor(0.4f, 1.0f, 0.4f, 0.3f);
			higlightHelperY.setColor(0.4f, 1.0f, 0.4f, 0.3f);
			highLightedNod.nodeSprite.setColor(0.4f, 1.0f, 0.4f, 0.8f);
		}
		else {
			//make node red
			higlightHelperX.setColor(1.0f, 0.3f, 0.3f, 0.3f);
			higlightHelperY.setColor(1.0f, 0.3f, 0.3f, 0.3f);
			highLightedNod.nodeSprite.setColor(1.0f, 0.3f, 0.3f, 0.8f);
		}
	}
	
	//When a tower is dragged over new node previously highlighted node will reset
	public void stopHighLightLastNode() {
		
		if (highLightedNod != null) {
			
			higlightHelperX.setVisible(false);
			higlightHelperY.setVisible(false);
			
			if (highLightedNod.Walkable) {
				highLightedNod.nodeSprite.setVisible(false);
				highLightedNod.nodeSprite.setColor(1.0f, 1.0f, 1.0f, 1.0f);				
			}
			else {
				highLightedNod.nodeSprite.setVisible(true);
				highLightedNod.nodeSprite.setColor(1.0f, 1.0f, 1.0f, 1.0f);
			}
			highLightedNod = null;
		}
		
	}

	//Return the highlight node
	public SearchNode getHighLightedNode() {
		return highLightedNod;
	}

	//If the user has no money the tower of that type should be disabled
	protected void deactivateTowers() {
		if (PlayerData.gold < 1) {
			this.towerStone.deactivateTower();
		}
		if (PlayerData.diamond < 1) {
			this.towerTotem.deactivateTower();
		}
	}
	
	//When timer reaches zero the enemy starts moving. No more towers can be placed
	public void endEditAndStartMovement() {
		this.towerStone.deactivateTower();
		this.towerTotem.deactivateTower();
		this.towerTemp.deactivateTower();
		stopHighLightLastNode();
		
		this.ourTimerHandler.setTimerSeconds(0.2f);
		fastForwardButton.setCurrentTileIndex(2);

		enemyStartsMoving = true;		
		
	    enemyReference.startMoving(startPoint, endPoint);
	    	    
	}

	public final ArrayList<Point> returnShortestPath() {
        return this.pathfinder.FindPath(startPoint, endPoint);
	}
	
	public void attachChild(AnimatedSprite explosionSprite) {
		this.pScene.attachChild(explosionSprite);
	}
	
	public void detachChild(final AnimatedSprite explosionSprite) {
        /* Removing entities can only be done safely on the UpdateThread.
         * Doing it while updating/drawing can
         * cause an exception with a suddenly missing entity.
         * Alternatively, there is a possibility to run the TouchEvents on the UpdateThread by default, by doing:
         * engineOptions.getTouchOptions().setRunOnUpdateThread(true);
         * when creating the Engine in onLoadEngine(); */
        this.runOnUpdateThread(new Runnable() {
                @Override
                public void run() {
                        /* Now it is save to remove the entity! */
                        pScene.detachChild(explosionSprite);
                }
        });
	}
	
	//Returns true if the user can buy this tower. Also subtracts the correct ammount from player money
	public boolean buyTower(TOWER_TYPE towertype) {
    	boolean returntype = false;
		
		if (towertype == TOWER_TYPE.STONE_TOWER) {
			returntype = PlayerData.buyStoneTower();
		}
		else if (towertype == TOWER_TYPE.TOTEM_TOWER) {
			returntype = PlayerData.buyTotemTower();
		}
		
		if (returntype) {
			this.deactivateTowers();
	    	goldText.setText(String.valueOf(PlayerData.gold));
	    	diamondText.setText(String.valueOf(PlayerData.diamond));
		}
	    return returntype;
	}
	
	//level is complete. Show result screen
	public void finnish() {

		this.runOnUpdateThread(new Runnable() {
            @Override
            public void run() {
            	
            	levelFinished = true;
            	fastForwardButton.setVisible(false);
        		final DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT);
    			
    			float roundedTime = (float) (Math.round(Enemy.TIMER*10.0)/10.0);
    			
            	//The random level
            	if (mlvl.id == -1) {

            		if (roundedTime >= achievement9.floatData) {
            			levelCompleted.setColor(0.6f,1f,0.6f);
            			
            			if (roundedTime >= 30 && !achievement9.completed) {
            				levelCompleted.setText("New best time!\nRandom level achievement unlocked.");
            			}
            			else if (!achievement9.completed) {
            				levelCompleted.setText("New best time!\nA best time over 30 sec gives an achievement.");
            			}
            			else {
            				levelCompleted.setText("New best time!");
            			}
            				
            	    	achievement9.saveAchievement(achievement9.completed, format.format(new Date()), -1, roundedTime);
            		}
            		else {
            			
            			if (achievement9.floatData < 30 && !achievement9.completed) {
                			levelCompleted.setText("No new best time. Try again!\nA best time over 30 sec gives an achievement.");
            			}
            			else {
                			levelCompleted.setText("No new best time. Try again!");
            			}
            			
                		levelCompleted.setColor(1.0f,0.6f,0.6f);
            		}

            	}
            	//Rest of the levels
            	else {
            		
	            	if (roundedTime >= mlvl.target_time) {
	        			levelCompleted.setColor(0.6f,1f,0.6f);
	        			levelCompleted.setText("Target time reached. Level completed");
	        			
	            		//Check if distance has been updated
	            		if ((Enemy.DISTANCE_WALKED/10) > achievement10.floatData) {
	            	    	achievement10.saveAchievement(achievement10.completed, format.format(new Date()), mlvl.id, Enemy.DISTANCE_WALKED/10);
	            		}

	            		//Check if distance has been updated (lvl 30)
	            		if (mlvl.id == 30 && (Enemy.DISTANCE_WALKED/10) > achievement4.floatData) {
	            			achievement4.saveAchievement(achievement4.completed, format.format(new Date()), mlvl.id, Enemy.DISTANCE_WALKED/10);
	            		}

	            		//Check if no totems has been placed on lvl 55
	            		if (!achievement7.completed && mlvl.id == 55 && PlayerData.diamond == mlvl.currency2) {
	            			achievement7.saveAchievement(achievement7.completed, format.format(new Date()), -1, -1);
	            		}

	            		//Check if placement of towers was done in less than 4 sec. in lvl 4
	            		if (!achievement2.completed && mlvl.id == 4 && timeLeftBeforeLevelStarted > 26) {
	            			achievement2.saveAchievement(achievement2.completed, format.format(new Date()), -1, -1);
	            		}
	            		
	            		//Check if placement of towers was done in less than 15 sec. in lvl 45
	            		if (!achievement6.completed && mlvl.id == 45 && timeLeftBeforeLevelStarted > 14) {
	            			achievement6.saveAchievement(achievement6.completed, format.format(new Date()), -1, -1);
	            		}	            		
	            		
	        		}
	        		else {
	            		levelCompleted.setColor(1.0f,0.6f,0.6f);
	        			levelCompleted.setText("Target time not reached. Try again");
	        		}
	            	
	            	if (roundedTime > mlvl.time) {
	            		//New best time
	            		mlvl.time = roundedTime;
	
	            		//Check if we beat the target time
	            		if (mlvl.time >= mlvl.target_time) {
	            			mlvl.completed = true;
	            		}
	            		
	            		//Save the date of the best time
	            		mlvl.completion_date = format.format(new Date());
	            		
	            		levelDataSource.updateLevel(mlvl);
	            		
	
	            	} else {
	            		newBestTime.setText(string_oldBestTime);
	            	}
            	}

            	
            	if (mlvl.id != -1) {
                	newBestTime.setVisible(true);
            	}

        		targetTime.setVisible(true);
            	
				ourTimer.timerNotCompleted = false;

				background.setVisible(true);
				border.setVisible(true);
				background2.setVisible(true);

				pScene.registerTouchArea(endButton);

				endButton.setVisible(true);
        		finalText.setVisible(true);
        		levelCompleted.setVisible(true);
        		
        		endTime.setText("Time: " +roundedTime);
        		endTime.setVisible(true);
        		
        		ourTimer.setVisible(false);
        		
    			background.registerEntityModifier(new AlphaModifier(1f, 0, 0.7f));
            }
    });
		
	}
	
	/**
	 * Show a toast on the UI thread. Displays the toast with a text from string resources.
	 * @param id
	 */
	public void showMessage(int id) {
		
		removeMessage();
		
		switch (id) {
			case ID_TOWER_ERROR_1:
				toastText.setText(string_tower_error_1);
				break;
			case ID_TOWER_ERROR_2:
				toastText.setText(string_tower_error_2);
				break;
			default:
				break;
			
		}
		
		toastBorder.setSize(toastText.getWidth()+24, toastText.getHeight()+24);
		final Point toastBorderPos = Utils.getCenterPositionOfRectangle(toastBorder);
		toastBorder.setPosition(toastBorderPos.x, toastBorderPos.y);
		toastBackground.setSize(toastText.getWidth()+20, toastText.getHeight()+20);
		final Point toastBackgroundPos = Utils.getCenterPositionOfRectangle(toastBackground);
		toastBackground.setPosition(toastBackgroundPos.x, toastBackgroundPos.y);
		final Point toastTextPos = Utils.getCenterPositionOfRectangle(toastText);
		toastText.setPosition(toastTextPos.x, toastTextPos.y);
		
		toastIem.reset();
		toastIem2.reset();
		toastIem3.reset();
		
		toastBackground.registerEntityModifier(toastIem);
		toastBorder.registerEntityModifier(toastIem2);
		toastText.registerEntityModifier(toastIem3);
	    
	}

	/**
	 * Removes any custom toast
	 */
	public void removeMessage() {
		if (!toastIem.isFinished() || !toastIem2.isFinished() || !toastIem3.isFinished()) {
			toastBackground.unregisterEntityModifier(toastIem);
			toastBorder.unregisterEntityModifier(toastIem2);
			toastText.unregisterEntityModifier(toastIem3);
			toastBackground.setVisible(false);
			toastBorder.setVisible(false);
			toastText.setVisible(false);
		}
	}
		

    //Reestablish database
    @Override
	protected void onResume() {
    	levelDataSource.open();
		super.onResume();
	}
    
    //Close database connection
	@Override
	protected void onPause() {
		levelDataSource.close();
		super.onPause();
	}
	
	//Play one of the loaded sound files
	public void playSound(int id) {
		if (soundEnabled) {
			switch(id) {
				case ID_CLICK_SOUND:
					this.mClickSound.play();			
					break;
				case ID_LASER_SOUND:
					this.mLaserSound.play();
					break;
				case ID_BUY_SOUND:
					this.mBuySound.play();
					break;
				case ID_SPAWN_SOUND:
					this.mSpawnSound.play();
					break;
			}
		}
	}
	
	
	//Pause menu
	protected MenuScene createMenuScene() {
		final MenuScene menuScene = new MenuScene(this.mCamera);

		final Color regular = new Color(1,1,1);
		final Color clicked = new Color(0.8f,0.8f,0.4f);

		final IMenuItem resumeMenuItem = new ColorMenuItemDecorator(new TextMenuItem(MENU_RESUME, this.mFontLargerText, string_menu_resume, this.getVertexBufferObjectManager()), clicked, regular);
		resumeMenuItem.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		menuScene.addMenuItem(resumeMenuItem);
		
		soundMenuText = new TextMenuItem(MENU_SOUND, this.mFontLargerText, soundEnabled ? string_menu_sound_on : string_menu_sound_off, this.getVertexBufferObjectManager());
		final IMenuItem soundMenuItem = new ColorMenuItemDecorator(soundMenuText, clicked, regular);
		soundMenuItem.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		menuScene.addMenuItem(soundMenuItem);
		
		final IMenuItem quitMenuItem = new ColorMenuItemDecorator(new TextMenuItem(MENU_QUIT,  this.mFontLargerText, string_menu_quit, this.getVertexBufferObjectManager()), clicked, regular);
		quitMenuItem.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		menuScene.addMenuItem(quitMenuItem);

		menuScene.buildAnimations();

		menuScene.setBackgroundEnabled(true);

		menuScene.setOnMenuItemClickListener(this);
		return menuScene;
	}

	//Load admob
	@Override
    protected void onSetContentView() {
        
	    final FrameLayout frameLayout = new FrameLayout(this);
	    final FrameLayout.LayoutParams frameLayoutLayoutParams =
	            new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
	                                         FrameLayout.LayoutParams.MATCH_PARENT);
	
	    final AdView adView = new AdView(this, AdSize.BANNER, "ca-app-pub-9639252239100571/1091710243");
	
	    adView.refreshDrawableState();
	    adView.setVisibility(AdView.VISIBLE);
	    final FrameLayout.LayoutParams adViewLayoutParams =
	            new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
	                                         FrameLayout.LayoutParams.WRAP_CONTENT,
	                                         Gravity.LEFT|Gravity.TOP);
	   
	
	    AdRequest adRequest = new AdRequest();
	    adView.loadAd(adRequest);
	
	    this.mRenderSurfaceView = new RenderSurfaceView(this);
		this.mRenderSurfaceView.setRenderer(this.mEngine, this);
	
	    final android.widget.FrameLayout.LayoutParams surfaceViewLayoutParams =
	            new FrameLayout.LayoutParams(super.createSurfaceViewLayoutParams());
	
	    frameLayout.addView(this.mRenderSurfaceView, surfaceViewLayoutParams);
	    frameLayout.addView(adView, adViewLayoutParams);
	
	    this.setContentView(frameLayout, frameLayoutLayoutParams);
	}
	
	@Override
	public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
		if(pSceneTouchEvent.isActionCancel() || pSceneTouchEvent.isActionUp()) {
			currentlyMovingTower = TOWER_TYPE.NO_TOWER;
			towerStone.resetPosition();
			towerTotem.resetPosition();
			towerTemp.resetTemporaryTowerPosition();
			stopHighLightLastNode();
		}
		return false;
	}
	
	@Override
	protected void onDestroy() {
		
		mBitmapTextureAtlas.unload();
		mFontLargerText.unload();
		mFontSmallText.unload();
		mFontOrdinaryText.unload();
		
		pScene.detachChildren();
		pScene.clearTouchAreas();
		pScene.reset();
		pScene.detachSelf();
		
		mMenuScene.detachChildren();
		mMenuScene.clearTouchAreas();
		mMenuScene.reset();
		mMenuScene.detachSelf();
		
		mClickSound.release();
		mLaserSound.release();
		mBuySound.release();
		mSpawnSound.release();
		
		levelDataSource = null;
		mlvl = null;
		pathfinder = null;
		highLightedNod = null;
		higlightHelperX = null;
		higlightHelperY = null;
		towerStone = null;
		towerTotem = null;
		towerTemp = null;
		enemyReference = null;
		goldText = null;
		diamondText = null;
		finalText = null;
		newBestTime = null;
		levelCompleted = null;
		targetTime = null;
		endTime = null;
		background = null;
		border = null;
		background2 = null;
		ourTimer = null;
		ourTimerHandler = null;
		mMenuScene = null;
		soundMenuText = null;
		mCamera = null;
		mBitmapTextureAtlas = null;
		mDifferentObstacles = null;
		mStartWaveButton = null;
		mEndButton = null;
		mTower1 = null;
		mTower2 = null;
		mFontLargerText = null;
		mFontSmallText = null;
		mFontOrdinaryText = null;
		mEnemy = null;
		mExplosion1 = null;
		mExplosion2 = null;
		mParallaxLayerBack = null;
		mParallaxLayerMid = null;
		fastForwardButton = null;
		endButton = null;
		mClickSound = null;
		mLaserSound = null;
		mBuySound = null;
		mSpawnSound = null;

		toastIem = null;
		toastIem2 = null;
		toastIem3 = null;
	    toastBackground = null;
	    toastBorder = null;
		toastText = null;
		
		sp = null;
		achievement2 = null;
		achievement4 = null;
		achievement6 = null;
		achievement7 = null;
		achievement9 = null;
		achievement10 = null;
		tower_marker_offset = null;		
		
		super.onDestroy();
		
	}
	
	/**
	 * Private method that creates a new SequenceEntityModifier
	 * @return
	 */
	private SequenceEntityModifier newSequenceEntityModifier() {
		return new SequenceEntityModifier(
	    		new AlphaModifier(0.2f, 0.0f, 1.0f, new IEntityModifierListener() {

	    			@Override
					public void onModifierStarted(
							IModifier<IEntity> pModifier, IEntity pItem) {
	    				
	    				pItem.setVisible(true);
					}

					@Override
					public void onModifierFinished(
							IModifier<IEntity> pModifier, IEntity pItem) {
					}
	    			}),
		    		new DelayModifier(2.0f),
		    		new AlphaModifier(0.4f, 1.0f, 0.0f, new IEntityModifierListener() {

		    			@Override
						public void onModifierStarted(
								IModifier<IEntity> pModifier, IEntity pItem) {
						}

						@Override
						public void onModifierFinished(
								IModifier<IEntity> pModifier, IEntity pItem) {
							pItem.setVisible(false);
							
						}
		    	    }));
	}
	
	/**
	 * Public method requests exclusivity and then resets all towers except the enclosed towertype
	 * @param type
	 */
	public boolean requestExclusivity(TOWER_TYPE type) {
		
		if (currentlyMovingTower.equals(TOWER_TYPE.NO_TOWER)) {
		
			currentlyMovingTower = type;
			 
			//Remove any visible messages
			removeMessage();
			
			if (type.equals(TOWER_TYPE.STONE_TOWER)) {
				towerTotem.resetPosition();
				towerTemp.resetPosition();
			}
			else if (type.equals(TOWER_TYPE.TOTEM_TOWER)) {
				towerStone.resetPosition();
				towerTemp.resetPosition();
			}
			else if (type.equals(TOWER_TYPE.TEMPORARY_TOWER)) {
				towerStone.resetPosition();
				towerTotem.resetPosition();
			}
			
			return true;
		}
		else {
			return false;
		}
		
	}
	
	/**
	 * Method that release exclusivity
	 * @param type
	 */
	public void releaseExclusivity(TOWER_TYPE type) {
		//We only release if the correct tower asked for it
		if (currentlyMovingTower.equals(type)) {
			currentlyMovingTower = TOWER_TYPE.NO_TOWER;
		}
	}
	
}
