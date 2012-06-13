package org.oddb.ch;

import java.util.Map;

public class Chapter {
	public String getHeading(){
		return heading;
	}
	public void setHeading(String heading){
		this.heading = heading;
	}
	public Section[] getSections(){
		return sections;
	}
	public void setSections(Section[] sections){
		this.sections = sections;
	}
	String heading; // (String) -> Titel
	Section[] sections; // (Array (Text::Section)) -> Abschnitte
	
}
