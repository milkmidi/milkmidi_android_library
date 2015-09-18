package milkmidi.pipi.manager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import milkmidi.pipi.core.IDestroy;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
//http://www.droidnova.com/creating-sound-effects-in-android-part-1,570.html
public class SoundManager implements IDestroy{
	
	protected static Map<String, SoundManager> instanceMap;;

	private boolean mDestroyed = false;
	@Override 
	public boolean getDestroyed() {		return mDestroyed;	}
	
	private SoundPool mSoundPool;
	private HashMap<String, Integer> mSoundPoolMap;
	private AudioManager mAudioManager;
	private Context mContext;
	
	private final String mKey;
	private boolean mEnabled = true;
	public boolean isEnabled() {return mEnabled;	}
	public void setEnabled(boolean value) {	this.mEnabled = value;	}

	private SoundManager(String key) {
		this.mKey = key;
		init(key);
	}

	protected void init(String key) {
        if (instanceMap.get(key) != null) 
        	throw new RuntimeException(key +" Facade already constructed");      
        instanceMap.put(key, this);    
    }
	
	static synchronized public SoundManager getInstance() {
		return getInstance("default");
	}

	/**
	 * Requests the instance of the Sound Manager and creates it if it does not
	 * exist.
	 * 
	 * @return Returns the single instance of the SoundManager
	 */
	static synchronized public SoundManager getInstance(String key) {
		if (instanceMap==null) {
			instanceMap = new HashMap<String, SoundManager>();
		}
		
		if (instanceMap.get(key) == null) {
			try {
				 new SoundManager(key);
			} catch (Exception e) {
			}
		}
		return instanceMap.get(key);
	}
	

	/**
	 * Initialises the storage for the sounds
	 * 
	 * @param theContext
	 *            The Application context
	 */
	public void initSounds(Context context) {
		mContext = context;
		mSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
		mSoundPoolMap = new HashMap<String, Integer>();
		mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
	}

	/**
	 * Add a new Sound to the SoundPool
	 * 
	 * @param Index
	 *            - The Sound Index for Retrieval
	 * @param SoundID
	 *            - The Android ID for the Sound asset.
	 */
	public void addSound(String name, int soundID) {
		mSoundPoolMap.put(name, mSoundPool.load(mContext, soundID, 1)
		);
	}

	
	
	public void playSound(String name){
		this.playSound(name, 0, 1);
	}
	public void playSound(String name, int loop) {
		this.playSound(name, loop,1);
	}

	/**
	 * Plays a Sound
	 * 
	 * @param index
	 *            - The Index of the Sound to be played
	 * @param speed
	 *            - The Speed to play not, not currently used but included for
	 *            compatibility
	 */
	public void playSound(String name,int loop, float rate) {
		if (!this.mEnabled) {
			return;
		}
		float streamVolume = mAudioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		streamVolume = streamVolume
				/ mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		mSoundPool.play(
				getSoundByName(name) , 
				streamVolume, streamVolume, 1, loop, rate);
		
	}
	private int getSoundByName(String name){
		return Integer.parseInt( String.valueOf(mSoundPoolMap.get(name)));
	}

	/**
	 * Stop a Sound
	 * 
	 * @param index
	 *            - index of the sound to be stopped
	 */
	public void stopSound(String name) {
		if (!this.mEnabled) {
			return;
		}
		mSoundPool.stop(
				getSoundByName(name)
				);
	}

	/**
	 * Deallocates the resources and Instance of SoundManager
	 */
	@Override
	public synchronized void destroy() {
		if (mDestroyed) {
			return;
		}
		mDestroyed = true;
		
		
		Iterator<Entry<String, Integer>> it = mSoundPoolMap.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<String, Integer> pairs = (Map.Entry<String, Integer>)it.next();
	        mSoundPool.unload( pairs.getValue()  );
	    }
	    mSoundPool.release();
		mSoundPool = null;
		mSoundPoolMap.clear();
		mSoundPoolMap = null;
		mAudioManager.unloadSoundEffects();
		mAudioManager = null;
		mContext = null;		
		instanceMap.remove(mKey);	
	}

	
}