package org.oddb.ch;

import java.util.HashMap;
import java.util.Map;

import org.oddb.ch.GalenicGroup;

//see http://ch.oddb.org/resources/downloads/datadesc/oddb.yaml.txt

public class GalenicForm {
	int oid       ; //Unique Identifier
//	- descriptions      (SimpleLanguage::Descriptions (String, String)) ; //Sprache, Bezeichnung. siehe Glossar (SimpleLanguage::Descriptions)
	
	//	Descriptions descriptions;
	//public Descriptions getDescriptions(){	return descriptions; }
	// public void setDescriptions(Descriptions descriptions){	this.descriptions = descriptions; }
	GalenicGroup galenic_group     ; //siehe Glossar (GalenicGroup)
	private static int counter;
	
	public GalenicForm(){
		super();
		oid = -2;
		counter++;
//		descriptions = new HashMap<String, String>();
		galenic_group = new GalenicGroup();
	}
	public int getOid(){
		return oid;
	}
	public void setOid(int oid){
		this.oid = oid;
	}
	Map<String, String> descriptions; // (SimpleLanguage::Descriptions (String, String)) -> Sprache, Bezeichnung. siehe Glossar (SimpleLanguage::Descriptions)
	public Map<String, String> getDescriptions(){
		return descriptions;
	}
	public void setDescriptions(Map<String, String> descriptions){
		System.out.println(descriptions.toString());
		this.descriptions = descriptions;
	}
	public GalenicGroup getGalenic_group(){
		return galenic_group;
	}
	public void setGalenic_group(GalenicGroup galenic_group){
		System.out.println(galenic_group.toString());
		this.galenic_group = galenic_group;
	}
	
}
