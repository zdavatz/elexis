package org.oddb.ch;

import java.util.HashMap;
import java.util.Map;

//see http://ch.oddb.org/resources/downloads/datadesc/oddb.yaml.txt

public class GalenicGroup {
	int oid; // Unique Identifier
	public String toString()
	{
		StringBuffer sb = new StringBuffer(String.format("gg: %1$d: of %2$d ", oid, counter));
		if (descriptions != null)
			sb.append(descriptions.toString());
		return sb.toString();
	}
	// - descriptions (SimpleLanguage::Descriptions (String, String)) -> Sprache, Bezeichnung. siehe
// Glossar (SimpleLanguage::Descriptions)
//	Map<String, String> descriptions; // Sprache, Bezeichnung. siehe Glossar
// (SimpleLanguage::Descriptions)
	Map<String, String> descriptions; // (SimpleLanguage::Descriptions (String, String)) -> Sprache, Bezeichnung. siehe Glossar (SimpleLanguage::Descriptions)
	public Map<String, String> getDescriptions(){
		return descriptions;
	}
	public void setDescriptions(Map<String, String> descriptions){
		this.descriptions = descriptions;
	}
//Descriptions descriptions;
//public Descriptions getDescriptions(){	return descriptions; }
//public void setDescriptions(Descriptions descriptions){	this.descriptions = descriptions; }
	private static int counter;
	
	public GalenicGroup(){
		counter++;
	}

	public int getOid(){
		return oid;
	}
	public void setOid(int oid){
		this.oid = oid;
	}
	/*
	public Map<String, String> getDescriptions(){
		return descriptions;
	}
	public void setDescriptions(Map<String, String> descriptions){
		this.descriptions = descriptions;
	}
	*/
}
