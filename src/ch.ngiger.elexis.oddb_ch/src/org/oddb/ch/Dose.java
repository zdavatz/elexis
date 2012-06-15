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

import org.oddb.ch.Dose;

//see http://ch.oddb.org/resources/downloads/datadesc/oddb.yaml.txt

public class Dose {
	String not_normalized; // Ursprüngliche Text-repräsentation
	float val; // Wert/Grösse der Dosis (normalisiert, siehe Glossar)
	String unit; // Einheit der Dosis
	Dose scale; // Skalierungsfaktor (Umkehrung des Normalisierungsfaktors)
	
	public String toString(){
		StringBuffer sb = new StringBuffer("");
		if (not_normalized == null)
			sb.append(String.format("%1$f %s" , val, unit));
		else {
			sb.append("TODO"); // TODO:
		}
		
		return sb.toString();
	}
	public Dose(){
		super();
	}
	
	public Dose(int i){
		super();
		val = (float) i;	
		unit = "";
	}
	
	public String getNot_normalized(){
		return not_normalized;
	}
	public void setNot_normalized(String not_normalized){
		this.not_normalized = not_normalized;
	}
	public Float getVal(){
		return val;
	}
	public void setVal(Float val){
		this.val = val;
	}
	public String getUnit(){
		return unit;
	}
	public void setUnit(String unit){
		this.unit = unit;
	}
	public Dose getScale(){
		return scale;
	}
	public void setScale(Dose scale){
		this.scale = scale;
	}
	
}
