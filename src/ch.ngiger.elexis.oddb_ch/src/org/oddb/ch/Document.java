package org.oddb.ch;

import java.util.Map;

public class Document {
	Map <String, String> descriptions;

	public Map<String, String> getDescriptions(){
		return descriptions;
	}

	public void setDescriptions(Map<String, String> descriptions){
		this.descriptions = descriptions;
	}
}
