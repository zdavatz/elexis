package org.oddb.ch;

import java.util.Map;

public class Section {
	String subheading     ; //   (String)                -> Abschnitt-Titel
	Paragraph[] paragraphs ; //       (Array (Text::Paragraph)) -> Absätze
	public String getSubheading(){
		return subheading;
	}
	public void setSubheading(String subheading){
		this.subheading = subheading;
	}
	public Paragraph[] getParagraphs(){
		return paragraphs;
	}
	public void setParagraphs(Paragraph[] paragraphs){
		this.paragraphs = paragraphs;
	}


}
