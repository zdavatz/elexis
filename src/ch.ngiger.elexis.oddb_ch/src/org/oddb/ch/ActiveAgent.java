package org.oddb.ch;

import java.util.Map;

import org.oddb.ch.Dose;
import org.oddb.ch.Substance;

//see http://ch.oddb.org/resources/downloads/datadesc/oddb.yaml.txt

public class ActiveAgent {
	Substance substance; // Substanz/Wirkstoff
	/* TODO:
	Map <String, String> dose;
	public Map<String, String> getDose(){
		return dose;
	}
	public void setDose(Map<String, String> dose){
		this.dose = dose;
	}
	*/
	
	Dose dose; // Dosis
	public Dose getDose(){
		return dose;
	}
	public void setDose(Dose dose){
		this.dose = dose;
	}
	
	public Substance getSubstance(){
		return substance;
	}
	public void setSubstance(Substance substance){
		this.substance = substance;
	}
	
}
