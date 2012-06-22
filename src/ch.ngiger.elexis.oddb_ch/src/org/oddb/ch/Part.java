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

import org.oddb.ch.CommercialForm;
import org.oddb.ch.Composition;
import org.oddb.ch.Dose;
//see http://ch.oddb.org/resources/downloads/datadesc/oddb.yaml.txt

public class Part {
	Composition composition; // Zusammensetzung dieses Bestandteils
	public Dose multi; // _5_ x 10 + 10 Ampullen à 15 ml
	Dose count; // 5 x _10_ + 10 Ampullen à 15 ml
	int addition;
	CommercialForm commercial_form; // 5 x 10 + 10 _Ampullen_ à 15 ml
	Dose measure; // 5 x 10 + 10 Ampullen à _15 ml_
	
	public int getAddition(){
		return addition;
	}
	
	public void setAddition(int addition){
		this.addition = addition;
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer("");
		if (composition != null)
			sb.append(composition.toString() + " ");
		if (multi != null)
			sb.append(multi.toString());
		if (count != null && count.val > 0.0)
			sb.append(String.format(" x %f", count.val));
		if (measure != null)
			sb.append(" à " + measure.toString());
		return sb.toString();
		/* If I use the parts below I get about 54 errors while importing the oddb.yaml!!!!
		 */
		/*
		if (addition != null && addition.getValue() > 0)
			sb.append(String.format(" + %d", addition));
		if (commercial_form != null)
			sb.append(" " + commercial_form.toString());
			*/
	}
	
	static private int counter;
	
	public Part(){
		super();
		counter++;
		composition = null;
		addition = 0;
		count = null;
		commercial_form = null;
		measure = null;
	}
	
	public Dose getMulti(){
		return multi;
	}
	
	public void setMulti(Dose multi){
		this.multi = multi;
	}
	
	public void setMulti(int multi){
		this.multi = new Dose(multi);
	}
	
	public Dose getMeasure(){
		return measure;
	}
	
	public void setMeasure(Dose measure){
		this.measure = measure;
	}
	
	// Dose multiDose;
	public Composition getComposition(){
		return composition;
	}
	
	public void setComposition(Composition composition){
		this.composition = composition;
	}
	
	public Dose getCount(){
		return count;
	}
	
	public void setCount(int count){
		this.count = new Dose(count);
	}
	public void setCount(Dose count){
		this.count = count;
	}
	
	public CommercialForm getCommercial_form(){
		return commercial_form;
	}
	
	public void setCommercial_form(CommercialForm commercial_form){
		this.commercial_form = commercial_form;
	}
	
}
