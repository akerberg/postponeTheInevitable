package postpone.the.inevitable.menu;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.SparseIntArray;

public class SoundManager {

	private static final int SOUND_CLICK = 0;
	public static boolean PLAY_SOUND = true;
	
    private static SoundManager _instance = null;
    private SoundPool mSoundPool = null;
    private SparseIntArray mSoundIdMap = null;
    private AudioManager  mAudioManager = null;

    // Can not instantiate class 
    private SoundManager()  {  }

    /**
     * Gets the sole instance of the SoundManager class or creates it
     * if it does not exist.
     *
     * @return Returns the single instance of the SoundManager
     */
    static synchronized public SoundManager getInstance() {
        if (null == _instance) {
          _instance = new SoundManager();
        }

        return _instance;
     }

    /**
     * Initialises the sound manager 
     *
     * @param context The Application context
     * @param maxStreams the maximum number of simultaneous sounds that can be played
     */
    public void initSounds(Context context, int maxStreams) {

         if (null != mSoundPool) {
             mSoundPool.release();
         }
         mSoundPool = new SoundPool(maxStreams, AudioManager.STREAM_MUSIC, 0);

         if (null != mSoundIdMap) {
             mSoundIdMap.clear();
         }
         mSoundIdMap = new SparseIntArray();
         
         mAudioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
         
         SoundManager.getInstance().loadSound(context, SOUND_CLICK, R.raw.click);
    }

    /**
     * Loads a sound into the SoundPool and assigns the provided Id to it for
     * for future usage with playSound() 
     *
     * @param context - The application context
     * @param soundId - The Id that you are attributing to this sound
     * @param resourceId - The resource Id of this sound
     */
    public void loadSound(Context context, int soundId, int resourceId) {
        mSoundIdMap.put(soundId, mSoundPool.load(context, resourceId, 1));
    }

    /**
     * Plays a Sound using the Id provided in LoadSound
     * 
     * @param id        The Id of the Sound to be played
     * @param priority  The priority of the sound, used if too many streams are created   
     * @param loop      The 0 based number of times the sound should loop 
     * @param rate      The frequency at which to play the sound.  Between 0.5 and 2.0, 1.0 being normal speed.
     * 
     * @return          The stream id, used with other SoundPool methods such as pause()
     */
    public final int playSound(int id, int priority, int loop, float rate) {
    
         final float streamVolumeCurrent = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);  
         final float streamVolumeMax = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);      
         final float streamVolume = streamVolumeCurrent / streamVolumeMax;  
         
         return mSoundPool.play(mSoundIdMap.get(id), streamVolume, streamVolume, priority, loop, rate);
    }

    /**
     * Clean up everything, release memory...
     */
    public void cleanup() {
        mSoundPool.release();
        mSoundPool = null;
    
        mSoundIdMap.clear();
        mAudioManager.unloadSoundEffects();
    
        _instance = null;
    }
    
    //Public method that plays the click sound used in menus
    public static void playClick() {
    	if (PLAY_SOUND) {
    		SoundManager.getInstance().playSound(SOUND_CLICK, 1, 0, 1f);
    	}
    }
	    
}