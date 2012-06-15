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
