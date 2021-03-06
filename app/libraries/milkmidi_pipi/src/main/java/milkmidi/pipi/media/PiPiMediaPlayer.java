package milkmidi.pipi.media;

import java.util.Timer;
import java.util.TimerTask;

import milkmidi.pipi.core.IDestroy;
import android.content.Context;
import android.media.MediaPlayer;
// http://stackoverflow.com/questions/6884590/android-how-to-create-fade-in-fade-out-sound-effects-for-any-music-file-that-my
public class PiPiMediaPlayer implements IDestroy{
	private MediaPlayer mMediaPlayer;
//	private Context mContext;
	private int iVolume;

	private final static int INT_VOLUME_MAX = 100;
	private final static int INT_VOLUME_MIN = 0;
	private final static float FLOAT_VOLUME_MAX = 1;
	private final static float FLOAT_VOLUME_MIN = 0;

	private OnMediaFadeOutComplete mFadeOutComplete;
	public void setOnMediaFadeOutComplete(OnMediaFadeOutComplete listener){
		this.mFadeOutComplete = listener;
	}
	private PiPiMediaPlayer( Context context , int resid ) {
//		this.mContext = context;
		this.mMediaPlayer = MediaPlayer.create( context,	resid );		
	}
	
	public static PiPiMediaPlayer create(Context context, int resid){
		 return new PiPiMediaPlayer( context , resid );
	}
	public void setLooping( boolean looping){
		mMediaPlayer.setLooping( looping );
	}
	/*public void load( int resid, boolean looping ) {
		mediaPlayer = MediaPlayer.create( this.mMediaPlayer, resid );
		mediaPlayer.setLooping( looping );
	}*/

	/*public void load( String path, boolean looping ) {
		mediaPlayer = MediaPlayer.create( context,	Uri.fromFile( new File( path ) ) );
		mediaPlayer.setLooping( looping );
	}

	*/

	public void fadeIn() {
		this.fadeIn( 600 );
	}
	public void fadeIn( int fadeDuration ) {
		// Set current volume, depending on fade or not
		if ( fadeDuration > 0 )
			iVolume = INT_VOLUME_MIN;
		else
			iVolume = INT_VOLUME_MAX;

		updateVolume( 0 );

		// Play music
		if ( !mMediaPlayer.isPlaying() )
			mMediaPlayer.start();

		// Start increasing volume in increments
		if ( fadeDuration > 0 ) {
			final Timer timer = new Timer( true );
			TimerTask timerTask = new TimerTask() {
				@Override
				public void run() {
					updateVolume( 1 );
					if ( iVolume == INT_VOLUME_MAX ) {
						timer.cancel();
						timer.purge();
					}
				}
			};

			// calculate delay, cannot be zero, set to 1 if zero
			int delay = fadeDuration / INT_VOLUME_MAX;
			if ( delay == 0 )
				delay = 1;

			timer.schedule( timerTask, delay, delay );
		}
	}
	
	
	public void stop(){
		if ( this.mMediaPlayer.isPlaying() ) {
			this.mMediaPlayer.stop();			
		}
	}
	
	

	public void fadeOut(){
		this.fadeOut( 500 );
	}	
	public void fadeOut( int fadeDuration ) {
		// Set current volume, depending on fade or not
		if ( fadeDuration > 0 )
			iVolume = INT_VOLUME_MAX;
		else
			iVolume = INT_VOLUME_MIN;

		updateVolume( 0 );

		// Start increasing volume in increments
		if ( fadeDuration > 0 ) {
			final Timer timer = new Timer( true );
			TimerTask timerTask = new TimerTask() {
				@Override
				public void run() {
					updateVolume( -1 );
					if ( iVolume == INT_VOLUME_MIN ) {
						// Pause music
						if ( mMediaPlayer.isPlaying() )
							mMediaPlayer.pause();
						timer.cancel();
						timer.purge();
						
						if ( mFadeOutComplete !=null ) {
							mFadeOutComplete.onMediaFadeOutComplete();
						}
					}
				}
			};

			// calculate delay, cannot be zero, set to 1 if zero
			int delay = fadeDuration / INT_VOLUME_MAX;
			if ( delay == 0 )
				delay = 1;

			timer.schedule( timerTask, delay, delay );
		}
	}

	private void updateVolume( int change ) {
		// increment or decrement depending on type of fade
		iVolume = iVolume + change;

		// ensure iVolume within boundaries
		if ( iVolume < INT_VOLUME_MIN )
			iVolume = INT_VOLUME_MIN;
		else if ( iVolume > INT_VOLUME_MAX )
			iVolume = INT_VOLUME_MAX;

		// convert to float value
		float fVolume = 1 - ((float) Math.log( INT_VOLUME_MAX - iVolume ) / (float) Math
				.log( INT_VOLUME_MAX ));

		// ensure fVolume within boundaries
		if ( fVolume < FLOAT_VOLUME_MIN )
			fVolume = FLOAT_VOLUME_MIN;
		else if ( fVolume > FLOAT_VOLUME_MAX )
			fVolume = FLOAT_VOLUME_MAX;

		mMediaPlayer.setVolume( fVolume, fVolume );
	}

	@Override
	public void destroy() {		
		if ( isDestroyed ) {
			return;
		}
		this.stop();
		this.mMediaPlayer.release();
		this.mMediaPlayer = null;
//		this.mContext = null;
	}

	private boolean isDestroyed = false;
	@Override
	public boolean getDestroyed() {
		return isDestroyed;
	}
	
	public interface OnMediaFadeOutComplete{
		void onMediaFadeOutComplete();
	}
}