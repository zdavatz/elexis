/*******************************************************************************
 * Copyright (c) 2005-2011, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *  $Id$
 *******************************************************************************/
package ch.elexis.core;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ElexisCoreException extends Exception {
	private static final long serialVersionUID = 6473597065058652393L;
	
	public ElexisCoreException(String text){
		super(text);
	}
	
	public ElexisCoreException(String text, Logger log){
		super(text);
		log.log(Level.SEVERE, text);
	}
}
