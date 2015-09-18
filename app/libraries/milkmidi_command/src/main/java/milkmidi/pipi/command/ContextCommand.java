package milkmidi.pipi.command;

import android.app.Activity;
import android.content.Context;

public abstract class ContextCommand extends Command {

	private Context mContext;
	protected final Context getContext(){	return this.mContext;	}	
	protected final Activity getActivity(){		return (Activity) this.mContext;	}
	
	public ContextCommand( Context context ) {
		super();
		this.mContext = context;			
	}
	
	@Override
	public synchronized void destroy() {
		super.destroy();
		this.mContext = null;
	}

}
