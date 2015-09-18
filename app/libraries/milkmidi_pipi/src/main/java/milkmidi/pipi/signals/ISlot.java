package milkmidi.pipi.signals;

public interface ISlot<T> {
	
	/**
	 * Whether the listener is called on execution. Defaults to true.
	 */
	void setEnabled(Boolean value);
	Boolean getEnabled();
	
	
	/**
	 * Whether this slot is automatically removed after it has been used once.
	 */
	Boolean getOnce();
	
	void execute(T data);
	/**
	 * Removes the slot from its signal.
	 */
	void remove();
}
