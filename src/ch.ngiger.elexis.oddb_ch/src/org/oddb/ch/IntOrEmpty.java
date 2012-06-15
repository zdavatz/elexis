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
//This is trick to enable a null constructor for boolean

public class IntOrEmpty {
	
	private int value;
	
	public IntOrEmpty() {
		setValue(0);
	}

	public IntOrEmpty(int val) {
		value = val;
	}

	public int getValue(){
		return value;
	}

	public void setValue(int value){
		this.value = value;
	}
	
}
