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
//see http://ch.oddb.org/resources/downloads/datadesc/oddb.yaml.txt

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
