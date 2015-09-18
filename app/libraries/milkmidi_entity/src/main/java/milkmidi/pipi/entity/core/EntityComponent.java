package milkmidi.pipi.entity.core;

import android.util.Log;

import java.util.Arrays;

import milkmidi.pipi.entity.Entity;


public abstract class EntityComponent implements IEntityComponent {
    private final String TAG = "[" + this.getClass().getSimpleName() + "]";

    private boolean mIsRegistered = false;
	@Override public boolean isRegistered(){	return this.mIsRegistered;	}
    private IEntity mOwner;

	@Override public IEntity getOwner() {	return this.mOwner;	}
    private String mName;

	@Override public String getName() {	return this.mName;	}

	private boolean mDestroyed = false;

	public EntityComponent() {
		super();
	}

	@Override
	public final void register( IEntity owner, String name ) {
		if ( this.mIsRegistered ) {
			throw new Error( "Trying to register an already-registered component!" );
		}
		this.mOwner = owner;	
		this.mName = name;		
		this.atAdd();
		this.mIsRegistered = true;
	}

	@Override
	public final void unregister() {
		if (!this.mIsRegistered)
			throw new Error("Trying to unregister an unregistered component!");
		this.mIsRegistered = false;
        this.atRemove();
		this.mOwner = null;
	}

	@Override
	public final void reset() {
		atReset();
	}
	
	@Override
	public void destroy(){
        synchronized ( this ){
            if ( isRegistered() ) {
                unregister();
            }
            if ( !mDestroyed ) {
                mDestroyed = true;
                atDestroy();
            }
        }
	}
	
	@Override
	public void onDispatchEvent( String type, Object value ) {
		
	}
	
	protected final IEntityComponent lookupComponentByName( String name ){
		return getOwner().lookupComponentByName( name );
	}
	protected final void dispatchEvent( String type , Object value){
		getOwner().dispatchEvent( type, value );
	}
	protected final void dispatchEvent( String type ){
		getOwner().dispatchEvent( type );
	}
	

	protected abstract void atAdd();
	protected abstract void atRemove();
	protected abstract void atReset();
	protected abstract void atDestroy();
	
	protected void traced( Object... objects ) {
		if ( Entity.debug ) {
			Log.d(TAG, Arrays.toString(objects));			
		}
	}
	protected void trace( Object... objects ) {
		if ( Entity.debug ) {
			Log.i(TAG, Arrays.toString(objects));			
		}
	}
	
}
