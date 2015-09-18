package milkmidi.pipi.entity.core;

public interface IEntity  {

	void addComponent(IEntityComponent component , String name);
	void removeComponent(IEntityComponent component);
	void removeComponent(String name);
	IEntityComponent lookupComponentByName(String name);
	
	/*Object getProperty(PropertyReference property,Object defaultValue);	
	void setProperty(PropertyReference property,Object value);*/
	void destroy();
	boolean getDestroyed();	
	void dispatchEvent(String type);	
	void dispatchEvent(String type, Object value);	
}
