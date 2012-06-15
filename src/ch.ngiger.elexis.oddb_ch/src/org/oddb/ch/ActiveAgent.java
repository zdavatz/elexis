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

import org.oddb.ch.Dose;
import org.oddb.ch.Substance;

//see http://ch.oddb.org/resources/downloads/datadesc/oddb.yaml.txt

public class ActiveAgent {
	Substance substance; // Substanz/Wirkstoff
	Dose dose; // Dosis
	public ActiveAgent()
	{
		super();
	}
	public Dose getDose(){
		return dose;
	}
	public void setDose(Dose dose){
		this.dose = dose;
	}
	
	public Substance getSubstance(){
		return substance;
	}
	public void setSubstance(Substance substance){
		this.substance = substance;
	}
	
}
