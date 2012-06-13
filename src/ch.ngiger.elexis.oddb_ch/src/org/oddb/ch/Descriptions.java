package org.oddb.ch;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Descriptions extends HashMap{
	Map <String, String> descriptions;
	private int counter;
	
	public Descriptions(){
		counter++;
	}

	public Map<String, String> getDescriptions(){
		return descriptions;
	}

	public void setDescriptions(Map<String, String> descriptions){
		this.descriptions = descriptions;
	}
}