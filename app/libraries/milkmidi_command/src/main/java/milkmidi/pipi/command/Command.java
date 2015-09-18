package milkmidi.pipi.command;

import android.util.Log;

import java.util.Arrays;
import java.util.Observable;
/**
 * @author milkmidi
 * 2014 1 13
 * @version 1.0.3
 */
public abstract class Command extends Observable  {
	public enum CommandType {
		EXECUTE_COMPLETE, EXECUTE_INTERRUPT
	}
	
	public static boolean debug = false;

	protected final String TAG = "[" + this.getClass().getSimpleName() + "]";
	
	private boolean mDestroyed = false;
	public boolean getDestroyed() {	return mDestroyed;	}

	
	private Object mData;
	public void setData(Object value){	this.mData = value;	}
	public final Object getData(){	return this.mData;	}

	public Command() {		
	}

	abstract public void execute();

	public synchronized void interrupt() {
		trace("interrupt");
		this.atInterrupt();
		this.setChanged();
		this.notifyObservers( CommandType.EXECUTE_INTERRUPT );
	}

	protected void atInterrupt() {
	}

	public synchronized void executeComplete() {
		trace("executeComplete");
		this.atExecuteComplete();
		this.setChanged();
		this.notifyObservers( CommandType.EXECUTE_COMPLETE );
	}

	protected void atExecuteComplete() {
	}

	
	public synchronized void destroy() {
		if ( this.mDestroyed )
			return;
		this.mDestroyed = true;
		this.mData = null;
		trace("destroy");
	}
	
	
	protected final void trace( Object...objects ){
		if ( debug ) {
			Log.i( TAG, Arrays.toString( objects ) );
		}
	}

}