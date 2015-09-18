package milkmidi.pipi.signals;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;



/**
 * @author milkmidi
 * @version 1.0.0
 * 2013 12 17
 * @param <T>
 */
public class Signal<T> implements ISignal<T> {	
	private boolean mDestroyed=false;
	@Override
	public boolean getDestroyed() { return this.mDestroyed; }
	
	
	private Map<OnSignalListener<T> , ISlot<T>> mMap;	
	
	public Signal() {		
		mMap = new HashMap<OnSignalListener<T>, ISlot<T>>();
	}

	@Override
	public ISlot<T> add(OnSignalListener<T> observer) {
		synchronized ( this ) {
			return registerListener( observer,false );	
		}					
	}
	@Override
	public ISlot<T> addOnce(OnSignalListener<T> observer) {
		synchronized ( this ) {
			return registerListener( observer,true );			
		}
	}
	@Override
	public void remove(OnSignalListener<T> observer) {
		synchronized ( this ) {
			mMap.remove( observer );				
		}
	}
	@Override
	public void clear() {
		synchronized ( this ) {
			mMap.clear();
		}
	}
	

	@Override
	public void dispatch(T data) {
		synchronized ( this ) {
			Iterator<Entry<OnSignalListener<T>, ISlot<T>>> it = mMap.entrySet().iterator();
			while ( it.hasNext() ) {
				Entry<OnSignalListener<T>, ISlot<T>> entry = it.next();
				ISlot<T> slot = entry.getValue();
				slot.execute( data );
				if ( slot.getOnce() ) {
					it.remove();
				}
			}
		}
	}


	protected ISlot<T> registerListener(OnSignalListener<T> observer, Boolean once){
		ISlot<T> slot =  mMap.get( observer );
		if ( slot == null ) {
			slot = new Slot(observer,this,once);
			mMap.put( observer, slot );
		}else{
			if(slot.getOnce() != once){
				throw new IllegalAccessError("'You cannot addOnce() then add() the same listener without removing the relationship first.'");
			}
		}
		return slot;
	}
	

	
	


	@Override
	public void destroy() {
		if (mDestroyed) {
			return;
		}
		clear();
		mDestroyed = true;
	}
	
	
	
	class Slot implements ISlot<T> {
		private boolean mEnabled = true;
		private OnSignalListener<T> mObserver;
		private ISignal<T> mSignal;
		private boolean mOnce;
		public Slot(OnSignalListener<T> observer, ISignal<T> signal,Boolean once){
			this.mObserver = observer;
			this.mSignal = signal;
			this.mOnce = once;
		}
		
		@Override
		public void execute(T data){
			if (!mEnabled) return;
//			if (mOnce) remove();
			this.mObserver.onSignal( data );			
		}		
		
		@Override
		public void setEnabled( Boolean value ) {
			mEnabled = value;
		}
		@Override
		public Boolean getEnabled() {
			return mEnabled;
		}

		@Override
		public void remove() {
			mSignal.remove( mObserver );
		}

		@Override
		public Boolean getOnce() {
			return mOnce;
		}
	}

}
