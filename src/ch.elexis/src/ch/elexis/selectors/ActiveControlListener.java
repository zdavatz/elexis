/*******************************************************************************
 * Copyright (c) 2009-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 * $Id: CommonViewer.java 4799 2008-12-10 17:40:59Z psiska $
 *******************************************************************************/

package ch.elexis.selectors;

public interface ActiveControlListener {
	/**
	 * Contents of field has chsnged.
	 * 
	 * @param ac
	 *            the field that changed or NULL: Any of the observed field(s) have changed
	 */
	public void contentsChanged(ActiveControl ac);
	
	public void titleClicked(ActiveControl field);
	
	public void invalidContents(ActiveControl field);
}
