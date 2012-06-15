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

import java.util.Date;

//see http://ch.oddb.org/resources/downloads/datadesc/oddb.yaml.txt

public class SlEntry {
	String bsv_dossier; // BSV-Dossier-Nummer
	Date introduction_date; // Datum der Aufnahme in die Spezialitätenliste
	String limitation; // Limitation Ja/Nein
	String limitation_points; // Limitationspunkte
	LimitationText limitation_text ; // Limitationstexte in 3 Sprachen: de/it/fr
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
