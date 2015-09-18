package milkmidi.pipi.signals;

import milkmidi.pipi.core.IDestroy;





public interface ISignal<T> extends IDestroy{
	ISlot<T> add(OnSignalListener<T> observer);
	ISlot<T> addOnce(OnSignalListener<T> observer);
	void dispatch(T data);
	void remove(OnSignalListener<T> observer);	
	void clear();		
	
	
	
	public static interface OnSignalListener<T> {
		void onSignal(T data);
	}

}

