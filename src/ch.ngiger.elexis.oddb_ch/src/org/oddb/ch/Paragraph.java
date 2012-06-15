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

public class Paragraph {
	Format[] formats;//   (Array (Text::Format))  -> Formatdefinitionen
	String text; //              (String)                -> unformatierter Text
	Boolean preformatted; //      (Boolean)               -> Wenn ja, sollte whitespace 1:1 übernommen werden.
	public Format[] getFormats(){
		return formats;
	}
	public void setFormats(Format[] formats){
		this.formats = formats;
	}
	public String getText(){
		return text;
	}
	public void setText(String text){
		this.text = text;
	}
	public Boolean getPreformatted(){
		return preformatted;
	}
	public void setPreformatted(Boolean preformatted){
		this.preformatted = preformatted;
	}


}
