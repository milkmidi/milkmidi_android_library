package milkmidi.pipi.util;

public class StringObject {

	private String mValue;
	public String getValue(){
		return this.mValue;
	}
	public StringObject() {
		this("");
	}
	public StringObject(String value) {
		this.mValue = value;
	}
	public String toString(){
		return this.mValue;
	}

}
