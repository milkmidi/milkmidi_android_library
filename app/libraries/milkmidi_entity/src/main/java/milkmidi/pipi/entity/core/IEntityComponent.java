package milkmidi.pipi.entity.core;

public interface IEntityComponent {

	void register(IEntity owner , String name);
	void unregister();
	void reset();
	void destroy();
	boolean isRegistered();
	IEntity getOwner();
	String getName();
	void onDispatchEvent(String type , Object value);
}
