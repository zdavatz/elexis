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

import java.util.HashMap;
import java.util.Map;

import org.oddb.ch.GalenicGroup;

//see http://ch.oddb.org/resources/downloads/datadesc/oddb.yaml.txt

public class GalenicForm {
	int oid       ; //Unique Identifier
	GalenicGroup galenic_group     ; //siehe Glossar (GalenicGroup)
	private static int counter;
	
	public GalenicForm(){
		super();
		oid = -2;
		counter++;
		descriptions = null;
		galenic_group = null;
	}
	public int getOid(){
		return oid;
	}
	public void setOid(int oid){
		this.oid = oid;
	}
	Map<String, String> descriptions; // (SimpleLanguage::Descriptions (String, String)) -> Sprache, Bezeichnung. siehe Glossar (SimpleLanguage::Descriptions)
	public Map<String, String> getDescriptions(){
		return descriptions;
	}
	public void setDescriptions(Map<String, String> descriptions){
		this.descriptions = descriptions;
	}
	public GalenicGroup getGalenic_group(){
		return galenic_group;
	}
	public void setGalenic_group(GalenicGroup galenic_group){
		this.galenic_group = galenic_group;
	}
	
}
