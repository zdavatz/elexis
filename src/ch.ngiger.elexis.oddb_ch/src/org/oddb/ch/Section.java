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
