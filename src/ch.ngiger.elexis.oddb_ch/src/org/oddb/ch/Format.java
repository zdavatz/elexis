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

public class Format {
	String[] values; // (Array (Symbol)) -> mögliche Werte: alle Kombinationen von :bold, :italic und
// :symbol. Wenn Symbol, dann ist der Betreffende Text im Symbol-Font darzustellen.
	int start; // (Integer NOT NULL) -> 0-N Char-Position innerhalb des Paragraphs an welchem das
// Format beginnt.
	int end; // (Integer NOT NULL) -> 1-N, -1. Wenn -1, gilt das Format bis zum Ende des Paragraphs.
	
	public Format(){
	}
	public String[] getValues(){
		return values;
	}
	public void setValues(String[] values){
		this.values = values;
	}
	public int getStart(){
		return start;
	}
	public void setStart(int start){
		this.start = start;
	}
	public int getEnd(){
		return end;
	}
	public void setEnd(int end){
		this.end = end;
	}
	
}
