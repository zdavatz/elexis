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

//see http://ch.oddb.org/resources/downloads/datadesc/oddb.yaml.txt

public class Indication {
	int oid; // Unique Identifier
	// descriptions (SimpleLanguage::Descriptions (String, String)) ; //Sprache, Indikationstext.
// siehe Glossar (SimpleLanguage::Descriptions)
	Map<String, String> descriptions; // Sprache, Indikationstext. siehe Glossar
// (SimpleLanguage::Descriptions)
	public int getOid(){
		return oid;
	}
	public void setOid(int oid){
		this.oid = oid;
	}
	public Map<String, String> getDescriptions(){
		return descriptions;
	}
	public void setDescriptions(Map<String, String> descriptions){
		this.descriptions = descriptions;
	}
	
}
