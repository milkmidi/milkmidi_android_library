package milkmidi.pipi.entity.core;

public class PropertyReference{

	private String mProperty;	
	public void setProperty(String value){
		this.mProperty = value;
	}
	public String getProperty() {	return mProperty;	}

	private String mMethod;
	public void setMethod( String value){
		this.mMethod = value;
	}
	public String getMethod(){	return this.mMethod;	}
	

	
	public PropertyReference( String property , String method ) {
		this.mProperty = property;
		this.mMethod = method;
	}

	
	public String toString(){
		return this.mProperty+"."+this.mMethod;
	}
	
	
}
