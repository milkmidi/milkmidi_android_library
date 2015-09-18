package milkmidi.pipi.command;

import android.os.Handler;

public class Wait extends Command {

	private long mDelay;
	private Handler mHandler = new Handler();
	private InnerRunnable mRunnable;

	public Wait( long delay ) {
		this.mDelay = delay;
		this.mRunnable = new InnerRunnable();
	}

	@Override
	public void execute() {
		this.mHandler.postDelayed( mRunnable, this.mDelay );
	}

	@Override
	public void interrupt() {
		this.mHandler.removeCallbacks( mRunnable );
	}

	@Override
	public void destroy() {
		if ( getDestroyed() ) {
			return;
		}
		this.mHandler = null;
		this.mRunnable = null;
	}

	private class InnerRunnable implements Runnable {
		@Override
		public void run() {
			executeComplete();
		}
	}



}
