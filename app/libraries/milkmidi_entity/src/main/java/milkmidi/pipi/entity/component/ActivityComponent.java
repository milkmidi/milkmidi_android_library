package milkmidi.pipi.entity.component;

import android.app.Activity;
import android.content.Context;
import milkmidi.pipi.entity.core.EntityComponent;

public abstract class ActivityComponent extends EntityComponent {
	
	public static final String ON_RESUME = "onResume";
	public static final String ON_PAUSE = "onPause";
	
	private Context mContext;
	public Context getContext(){	
		return this.mContext;	
	}	
	public Activity getActivity(){	
		return (Activity) this.mContext;	
	}
	
	public ActivityComponent( Context context) {
		this.mContext = context;
	}
	

	@Override
	protected void atDestroy() {
		this.mContext = null;
	}
	

}
