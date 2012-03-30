/*******************************************************************************
 * Copyright (c) 2006-2010, G. Weirich, SGAM.informatics and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 * 
 *  $Id: AnalysesElement.java 5877 2009-12-18 17:34:42Z rgw_ch $
 *******************************************************************************/

package ch.elexis.exchange.elements;

public class AnalysesElement extends XChangeElement {
	
	@Override
	public String getXMLName(){
		return FindingElement.ENCLOSING;
	}
	
}
