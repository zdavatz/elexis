package org.oddb.ch;

import java.util.Map;

public class Format {
	String[] values; // (Array (Symbol)) -> mögliche Werte: alle Kombinationen von :bold, :italic und
// :symbol. Wenn Symbol, dann ist der Betreffende Text im Symbol-Font darzustellen.
	int start; // (Integer NOT NULL) -> 0-N Char-Position innerhalb des Paragraphs an welchem das
// Format beginnt.
	int end; // (Integer NOT NULL) -> 1-N, -1. Wenn -1, gilt das Format bis zum Ende des Paragraphs.
	
	private int counter;
	
	public Format(){
		counter++;
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
