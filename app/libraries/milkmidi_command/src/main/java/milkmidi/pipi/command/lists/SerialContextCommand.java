package milkmidi.pipi.command.lists;

import android.app.Activity;
import android.content.Context;

public abstract class SerialContextCommand extends SerialCommand {

	private Context mContext;
	protected Context getContext(){		return this.mContext;	}
	
	protected Activity getActivity(){	return (Activity) this.mContext;	}
	public SerialContextCommand(Context context) {
		this.mContext = context;
	}

	@Override
	public void destroy() {
		super.destroy();
		mContext = null;
	}
	
	
	


}
