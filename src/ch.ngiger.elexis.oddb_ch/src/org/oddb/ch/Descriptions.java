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
import java.util.HashMap;
import java.util.Map;

public class Descriptions extends HashMap{
	Map <String, String> descriptions;
	private int counter;
	
	public Descriptions(){
		counter++;
	}

	public Map<String, String> getDescriptions(){
		return descriptions;
	}

	public void setDescriptions(Map<String, String> descriptions){
		this.descriptions = descriptions;
	}
}
