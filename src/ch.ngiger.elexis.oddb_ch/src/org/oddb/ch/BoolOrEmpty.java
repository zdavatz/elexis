package org.oddb.ch;

public class BoolOrEmpty {
	
	private boolean value;
	
	public BoolOrEmpty() {
		setValue(false);
	}

	public BoolOrEmpty(boolean val) {
		value = val;
	}

	public boolean isValue(){
		return value;
	}

	public void setValue(boolean value){
		this.value = value;
	}
	
}
