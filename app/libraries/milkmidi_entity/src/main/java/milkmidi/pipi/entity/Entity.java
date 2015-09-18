package milkmidi.pipi.entity;

import android.text.TextUtils;
import android.util.Log;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import milkmidi.pipi.entity.core.IEntity;
import milkmidi.pipi.entity.core.IEntityComponent;

/**
 * @author milkmidi
 * @version 1.0.3
 */
public final class Entity {
	
	public static boolean debug = false;
	
	private static final String VERSION = "1.0.1";

	public static IEntity obtain() {
		return obtain("Default");
	}
	public static IEntity obtain( Logger logger) {
		return obtain("Default" , logger);
	}
	
	public static IEntity obtain(String name) {
		return new InnerEntity( name );
	}
	public static IEntity obtain(String name , Logger logger) {
		return new InnerEntity( name , logger );
	}
	public interface Logger{		
		public void println(int priority, String tag, String msg, Throwable tr);
	}

	private static class DefaultLogger implements Logger{
		@Override
		public void println(int priority, String tag, String msg, Throwable tr){
			if ( !debug ) {
				return;
			}
			
			switch (priority) {
				case android.util.Log.DEBUG:
					android.util.Log.d( tag, msg, tr );
					break;
				case android.util.Log.VERBOSE:
					android.util.Log.v( tag, msg, tr );
					break;
				case android.util.Log.INFO:
					android.util.Log.i( tag, msg, tr );
					break;
				case android.util.Log.ERROR:
					android.util.Log.e( tag, msg, tr );
					break;
				case android.util.Log.WARN:
					android.util.Log.w( tag, msg, tr );
					break;
			}
			
		}

	}
	private static class InnerEntity implements IEntity {
		private static final String	TAG	= "[Entity]";
		
		private Map<String, IEntityComponent> mEntityMap;		
		private boolean	mDestroyed	= false;
		@Override
		public boolean getDestroyed() {		return this.mDestroyed;		}
		
		private String mName;
		private Logger mLogger;
		public InnerEntity( String name , Logger logger) {
			super();
			this.mName = name;
			this.mLogger = logger;
			this.mEntityMap = new HashMap<String, IEntityComponent>();
			trace("Entity VERSION:"+VERSION);
			trace("Entity constructor name:"+name);
		}
		
		public InnerEntity( String name) {
			this( name , new DefaultLogger());			
		}

		@Override
		public void addComponent( IEntityComponent component, String componentName ) {
			synchronized ( this ) {				
				if ( TextUtils.isEmpty( componentName ) ) {
		            Log.w( TAG, "AddComponent A component name was not specified. This might cause problems later.");
	            }            
	            if (component.getOwner() != null){
	                Log.e( TAG, "AddComponent The component " + componentName + " already has an owner. (" + mName + ")");
	                return;
	            }            
	            if ( mEntityMap.containsKey(componentName) ) {
	                Log.e(TAG, "AddComponent component with name " + componentName + " already exists on this entity (" + mName + ").");
	                return;
	            }				
				traced("addComponent",componentName);				
				component.register( this, componentName );
				mEntityMap.put( componentName, component );				
			}
		}	
	

		@Override
		public void removeComponent( IEntityComponent component ) {
			synchronized ( this ) {
				if ( component == null ) {
					Log.e( TAG , "removeComponent The component is null");
					return;
				}
				if ( !component.getOwner().equals( this ) ) {
					Log.e( TAG , "removeComponent The component " + component.getName() + " is not owned by this entity. (" + mName + ")");
	                return;
				}				
				if ( !mEntityMap.containsKey( component.getName() )) {
	                Log.e( TAG , "removeComponent The component " + component.getName() + " was not found on this entity. (" + mName + ")");
	                return;
	            }
				traced("removeComponent", component.getName() );				
				component.unregister();				
				mEntityMap.remove( component.getName() );	
				
			}
		}
		@Override
		public void removeComponent( String name ) {
			removeComponent( mEntityMap.get( name ) );			
		}
		
		@Override
		public void dispatchEvent( String type ) {
			this.dispatchEvent(type, null);			
		}

		@Override
		public void dispatchEvent( String type, Object value ) {
			synchronized ( this ) {
				for ( String key : mEntityMap.keySet() ) {
					IEntityComponent component = mEntityMap.get( key );
					component.onDispatchEvent(type,value);						
				}
			}
		}


		@Override
		public IEntityComponent lookupComponentByName( String name ) {
			return mEntityMap.get( name );
		}

		/*@Override
		public Object getProperty( PropertyReference reference, Object defaultValue ) {
			if ( reference == null || reference.getProperty() == null || reference.getProperty() == "" )
				return null;
			String name = reference.getProperty();
			String method = reference.getMethod();
			
			IEntityComponent component = lookupComponentByName( name );
			if ( debug ) {
				trace("getProperty", name, method);
			}
			try {
//				Method m = component.getClass().getDeclaredMethod( "get" + method );
				Method m = component.getClass().getDeclaredMethod( method );
				return m.invoke( component );
			} catch ( NoSuchMethodException err ) {
				err.printStackTrace();
			} catch ( IllegalArgumentException err ) {
				err.printStackTrace();
			} catch ( IllegalAccessException err ) {
				err.printStackTrace();
			} catch ( InvocationTargetException err ) {
				err.printStackTrace();
			}
			return defaultValue;
		}

		@Override
		public void setProperty( PropertyReference reference, Object value ) {
			if ( reference == null || reference.getProperty() == null || reference.getProperty() == "" )
				return;
			String name = reference.getProperty();
			String method = reference.getMethod();
			IEntityComponent component = lookupComponentByName( name );
//			trace("setProperty",reference,value,value.getClass());	
			if ( debug ) {
				trace("setProperty", reference);
			}
			Class<?>[] param = parseClass(value);
			try {
//				Method m = component.getClass().getDeclaredMethod("set" + method , param );
				Method m = component.getClass().getDeclaredMethod( method , param );
				m.invoke( component, value );
			} catch ( NoSuchMethodException err ) {
				err.printStackTrace();
			} catch ( IllegalArgumentException err ) {
				err.printStackTrace();
			} catch ( IllegalAccessException err ) {
				err.printStackTrace();
			} catch ( InvocationTargetException err ) {
				err.printStackTrace();
			}
		}
		private static Class<?>[] parseClass(Object value){			
			Class<?>[] param = new Class[1];
			if (value instanceof Integer) {
				param[0] = Integer.TYPE;				
			}else if (value instanceof String) {
				param[0] = String.class;
			}else{
				param[0] = value.getClass();
			}			
			return param;
		}*/
		
		@Override
		public void destroy() {
			if ( mDestroyed ) {
				return;
			}
			synchronized ( this ) {
				for ( String key : mEntityMap.keySet() ) {
					IEntityComponent component = mEntityMap.get( key );
					component.destroy();						
				}				
				mDestroyed = true;
			}
			traced("destroy");
		}

		protected void traced( Object... objects ) {
			this.mLogger.println( android.util.Log.DEBUG, TAG, Arrays.toString( objects ), null );
		}
		protected void trace( Object... objects ) {
			this.mLogger.println( android.util.Log.INFO, TAG, Arrays.toString( objects ), null );
		}

		
	}
}
