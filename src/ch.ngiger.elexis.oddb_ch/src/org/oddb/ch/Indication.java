package org.oddb.ch;

import java.util.Map;

//see http://ch.oddb.org/resources/downloads/datadesc/oddb.yaml.txt

public class Indication {
	int oid; // Unique Identifier
	// descriptions (SimpleLanguage::Descriptions (String, String)) ; //Sprache, Indikationstext.
// siehe Glossar (SimpleLanguage::Descriptions)
	Map<String, String> descriptions; // Sprache, Indikationstext. siehe Glossar
// (SimpleLanguage::Descriptions)
	public int getOid(){
		return oid;
	}
	public void setOid(int oid){
		this.oid = oid;
	}
	public Map<String, String> getDescriptions(){
		return descriptions;
	}
	public void setDescriptions(Map<String, String> descriptions){
		this.descriptions = descriptions;
	}
	
}
