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

import java.util.Map;

import org.oddb.ch.Substance;

//see http://ch.oddb.org/resources/downloads/datadesc/oddb.yaml.txt

public class Substance {
	int oid; // Unique Identifier
	Map<String, String> descriptions; // Sprache, Substanzname. siehe Glossar
// (SimpleLanguage::Descriptions)
	String[] synonyms; // Weitere Bezeichnungen
	Substance effective_form; // Wirkform
	String narcotic; // CAS Registry Number
	String swissmedic_code; // Substanz-Code der Swissmedic. siehe Glossar (Swissmedic-Code)
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
	public String[] getSynonyms(){
		return synonyms;
	}
	public void setSynonyms(String[] synonyms){
		this.synonyms = synonyms;
	}
	public Substance getEffective_form(){
		return effective_form;
	}
	public void setEffective_form(Substance effective_form){
		this.effective_form = effective_form;
	}
	public String getNarcotic(){
		return narcotic;
	}
	public void setNarcotic(String narcotic){
		this.narcotic = narcotic;
	}
	public String getSwissmedic_code(){
		return swissmedic_code;
	}
	public void setSwissmedic_code(String swissmedic_code){
		this.swissmedic_code = swissmedic_code;
	}
	
}
