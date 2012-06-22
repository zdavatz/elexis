/*******************************************************************************
 * Copyright (c) 2012 Niklaus Giger <niklaus.giger@member.fsf.org>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Niklaus Giger <niklaus.giger@member.fsf.org> - initial API and implementation
 ******************************************************************************/
package org.oddb.ch;

import java.util.List;
import java.util.Map;

import org.oddb.ch.Dose;
//see http://ch.oddb.org/resources/downloads/datadesc/oddb.yaml.txt

public class AtcClass {
	/*
	 * - code              (String(1-7), NOT NULL)-> Atc-Code. siehe Glossar (AtcCode)
- descriptions      (SimpleLanguage::Descriptions (String, String)) -> Sprache, Bezeichnung. siehe Glossar (SimpleLanguage::Descriptions)
- guidelines        (Text::Document)       -> Guidelines der WHO zum Atc-Code. Sprache: en
- ddd_guidelines    (Text::Document)       -> Guidelines der WHO zu den DailyDoses. Sprache: en
- ddds              (Hash (DDD.administration_route, DDD) -> DailyDrugDose Informationen der WHO. siehe Glossar (DailyDrugDose)

	 */
	Map<String, String> descriptions; // (SimpleLanguage::Descriptions (String, String)) -> Sprache, Bezeichnung. siehe Glossar (SimpleLanguage::Descriptions)
	Document guidelines; //        (Text::Document)       -> Guidelines der WHO zum Atc-Code. Sprache: en
	Document ddd_guidelines; //    (Text::Document)       -> Guidelines der WHO zu den DailyDoses. Sprache: en
//	Map<String, DDD> ddds; //              (Hash (DDD.administration_route, DDD) -> DailyDrugDose Informationen der WHO. siehe Glossar (DailyDrugDose)
	List<DDD> ddds; //              (Hash (DDD.administration_route, DDD) -> DailyDrugDose Informationen der WHO. siehe Glossar (DailyDrugDose)
	String code ; //Atc-Code. siehe Glossar (AtcCode)
	String administration_route ; //Route of Administration, Codiert gemäss WHO. Diverse Werte.
	Dose dose     ; //Empfohlene Tagesdosis für diese Route of Administration gemäss WHO.
	String note  ; //Bemerkung zu dieser DDD. Sprache: en
	public Document getGuidelines(){
		return guidelines;
	}
	public void setGuidelines(Document guidelines){
		this.guidelines = guidelines;
	}
	public Document getDdd_guidelines(){
		return ddd_guidelines;
	}
	public void setDdd_guidelines(Document ddd_guidelines){
		this.ddd_guidelines = ddd_guidelines;
	}
//	public Map<String, DDD> getDdds(){
	public List<DDD> getDdds(){
		return ddds;
	}
//	public void setDdds(Map<String, DDD> ddds){
	public void setDdds(List<DDD> ddds){
			this.ddds = ddds;
	}
	public Map<String, String> getDescriptions(){
		return descriptions;
	}
	public void setDescriptions(Map<String, String> descriptions){
		this.descriptions = descriptions;
	}
	public String getCode(){
		return code;
	}
	public void setCode(String code){
		this.code = code;
	}
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
	
}
