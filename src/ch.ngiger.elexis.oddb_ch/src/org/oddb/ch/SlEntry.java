package org.oddb.ch;

import java.util.Date;
import java.util.List;
import java.util.Map;

//see http://ch.oddb.org/resources/downloads/datadesc/oddb.yaml.txt

public class SlEntry {
	String bsv_dossier; // BSV-Dossier-Nummer
	Date introduction_date; // Datum der Aufnahme in die Spezialitätenliste
	String limitation; // Limitation Ja/Nein
	String limitation_points; // Limitationspunkte
	//List<String> limitation_text; // Limitationstexte in 3 Sprachen: de/it/fr
//	List<String> limitation_text; // Limitationstexte in 3 Sprachen: de/it/fr
	LimitationText limitation_text ; // Limitationstexte in 3 Sprachen: de/it/fr
	// ging nicht mit Map<String, String>
	// ging nicht mit List<String>
	public String getBsv_dossier(){
		return bsv_dossier;
	}
	public void setBsv_dossier(String bsv_dossier){
		this.bsv_dossier = bsv_dossier;
	}
	public Date getIntroduction_date(){
		return introduction_date;
	}
	public void setIntroduction_date(Date introduction_date){
		this.introduction_date = introduction_date;
	}
	public String isLimitation(){
		return limitation;
	}
	public void setLimitation(String limitation){
		this.limitation = limitation;
	}
	public String getLimitation_points(){
		return limitation_points;
	}
	public void setLimitation_points(String limitation_points){
		this.limitation_points = limitation_points;
	}
	public LimitationText getLimitation_text(){
		return limitation_text;
	}
	public void setLimitation_text(LimitationText limitation_text){
		this.limitation_text = limitation_text;
	}
	
}
