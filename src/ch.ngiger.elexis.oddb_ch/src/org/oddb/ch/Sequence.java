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

import org.oddb.ch.AtcClass;
import org.oddb.ch.Composition;
import org.oddb.ch.Package;

//see http://ch.oddb.org/resources/downloads/datadesc/oddb.yaml.txt

public class Sequence {
	String seqnr; // Sequenznumerierung aus der Registrationsurkunde
	String name_base; // Hauptname der Sequenz aus der Registrationsurkunde, enthält z.T.
// Dosisangaben.
	String name_descr; // Zusatzname der Sequenz aus der Registrationsurkunde, z.B. 'Tabletten'
	AtcClass atc_class; // Atc-Klassierung der Sequenz (siehe AtcClass)
	String composition_text; // Text der Zusammensetzung.
	Composition[] compositions; // Zusammensetzungen (siehe Composition)
	Map<String, Package> packages; // (Hash (Package.ikscd, Package) -> Packungen der Sequenz (siehe
// Package)
	
	public String toString(){
		StringBuilder sb = new StringBuilder("");
		if (seqnr != null)
			sb.append("seq: "+ seqnr);
		if (name_base != null)
			sb.append(" " +name_base);
		if (name_descr != null)
			sb.append(" " +name_descr);
		if (atc_class != null)
			sb.append(" atc "+ atc_class.code);
		return sb.toString();
	}
	
	public String getSeqnr(){
		return seqnr;
	}
	
	public void setSeqnr(String seqnr){
		this.seqnr = seqnr;
	}
	
	public String getName_base(){
		return name_base;
	}
	
	public void setName_base(String name_base){
		this.name_base = name_base;
	}
	
	public String getName_descr(){
		return name_descr;
	}
	
	public void setName_descr(String name_descr){
		this.name_descr = name_descr;
	}
	
	public AtcClass getAtc_class(){
		return atc_class;
	}
	
	public void setAtc_class(AtcClass atc_class){
		this.atc_class = atc_class;
	}
	
	public String getComposition_text(){
		return composition_text;
	}
	
	public void setComposition_text(String composition_text){
		this.composition_text = composition_text;
	}
	
	public Composition[] getCompositions(){
		return compositions;
	}
	
	public void setCompositions(Composition[] compositions){
		this.compositions = compositions;
	}
	
	public Map<String, Package> getPackages(){
		return packages;
	}
	
	public void setPackages(Map<String, Package> packages){
		this.packages = packages;
	}
}
