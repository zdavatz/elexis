/*******************************************************************************
 * Copyright (c) 2011, G. Weirich and Elexis
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

public class ElexisStorageException extends ElexisCoreException {
	private static final long serialVersionUID = 3673532379036364696L;
	
	public ElexisStorageException(String message){
		super(message);
	}
}
