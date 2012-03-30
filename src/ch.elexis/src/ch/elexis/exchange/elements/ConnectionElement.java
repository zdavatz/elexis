/*******************************************************************************
 * Copyright (c) 2008-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 * 
 *  $Id: ConnectionElement.java 5877 2009-12-18 17:34:42Z rgw_ch $
 *******************************************************************************/

package ch.elexis.exchange.elements;

/**
 * A connection e.g. phone or mail
 * 
 * @author gerry
 * 
 */

public class ConnectionElement extends XChangeElement {
	public static final String XMLNAME = "connection";
	
	@Override
	public String getXMLName(){
		return XMLNAME;
	}
	
}
