package org.oddb.ch;

public class Multi {
	public int value;
	public Dose dose;
	public Multi(Dose multi)
	{
		dose = multi;
	}
	public Multi() {
		value = 1;
	}

	public Multi(int val) {
		value = val;
	}

	public int isValue(){
		return value;
	}

	public void setValue(int value){
		this.value = value;
	}

}
