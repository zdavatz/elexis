package org.oddb.ch;

import java.util.Map;

//see http://ch.oddb.org/resources/downloads/datadesc/oddb.yaml.txt

public class CommercialForm {
	int oid; // Unique Identifier
	Map<String, String> descriptions; // Sprache, Bezeichnung. siehe Glossar (SimpleLanguage::Descriptions)
	// - descriptions      (SimpleLanguage::Descriptions (String, String)) -> Sprache, Bezeichnung. siehe Glossar (SimpleLanguage::Descriptions)
	public int getOid(){
		return oid;
	}
	public String toString(){
		return descriptions.get("de");
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
