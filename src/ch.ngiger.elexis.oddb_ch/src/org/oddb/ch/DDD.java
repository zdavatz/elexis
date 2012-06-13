package org.oddb.ch;

import java.util.Map;

public class DDD {
	public String getAdministration_route(){
		return administration_route;
	}
	public void setAdministration_route(String administration_route){
		this.administration_route = administration_route;
	}
	public Dose getDose(){
		return dose;
	}
	public void setDose(Dose dose){
		this.dose = dose;
	}
	public String getNote(){
		return note;
	}
	public void setNote(String note){
		this.note = note;
	}
	String administration_route; // (String, NOT NULL)  -> Route of Administration, Codiert gemäss WHO. Diverse Werte.
	Dose dose; //              (Dose)                 -> Empfohlene Tagesdosis für diese Route of Administration gemäss WHO.
	String note; //              (String)               -> Bemerkung zu dieser DDD. Sprache: en


}
