package org.oddb.ch;

import java.util.Map;

import org.oddb.ch.CommercialForm;
import org.oddb.ch.Composition;
import org.oddb.ch.Dose;

public class Part {
	Composition composition; // Zusammensetzung dieses Bestandteils
	public Dose multi; // _5_ x 10 + 10 Ampullen à 15 ml
	int count; // 5 x _10_ + 10 Ampullen à 15 ml
	int addition;
// private Addition addition; // 5 x 10 + _10_ Ampullen à 15 ml
	CommercialForm commercial_form; // 5 x 10 + 10 _Ampullen_ à 15 ml
	Dose measure; // 5 x 10 + 10 Ampullen à _15 ml_
	
	public class Addition {
		public int value;
		
		public Addition(int val){
			value = val;
		}
		
		public Addition(){
			value = 0;
		}
	}
	
	/*
	 * public Addition getAddition(){ return this.addition; }
	 * 
	 * public void setAddition(Addition addition){ this.addition = addition; }
	 */
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
		if (count > 0)
			sb.append(String.format(" x %d", count));
		// if (addition.value > 0)
		// sb.append(String.format(" + %d", addition.value));
		if (addition > 0)
			sb.append(String.format(" + %d", addition));
		if (commercial_form != null)
			sb.append(" " + commercial_form.toString());
		if (measure != null)
			sb.append(" à " + measure.toString());
		return sb.toString();
	}
	
	static private int counter;
	
	public Part(){
		super();
		counter++;
		composition = null;
		addition = 0;
		// addition = new Addition();
		count = 0;
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
		System.out.println("Integer creator");
		// this.multi = multi;
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
	
	public int getCount(){
		return count;
	}
	
	public void setCount(int count){
		this.count = count;
	}
	
	public CommercialForm getCommercial_form(){
		return commercial_form;
	}
	
	public void setCommercial_form(CommercialForm commercial_form){
		this.commercial_form = commercial_form;
	}
	
}
