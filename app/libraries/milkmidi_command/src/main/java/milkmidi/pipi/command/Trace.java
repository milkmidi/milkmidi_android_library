package milkmidi.pipi.command;

import android.util.Log;

public class Trace extends Command {

	
	
	
	
	private String mTag;
	private String mMsg;
	public Trace(String tag , String msg) {
		this.mTag = tag;
		this.mMsg = msg;
	}

	@Override
	public void execute() {		
		Log.i(mTag, mMsg);		
		executeComplete();
	}

}
