/*******************************************************************************
 * Copyright (c) 2006-2009, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 * 
 *  $Id: IDetailDisplay.java 6041 2010-02-01 13:53:45Z rgw_ch $
 *******************************************************************************/
package ch.elexis.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;

import ch.elexis.data.PersistentObject;

/**
 * Detailansicht eines PersistentObject
 * 
 * @author Gerry
 * 
 */
public interface IDetailDisplay {
	/**
	 * Das Display erzeugen.
	 * 
	 * @param parent
	 *            Hat schon ein FillLayout.
	 * @param site
	 *            Heimat-Viewsite dieses DetailDisplay
	 */
	public Composite createDisplay(Composite parent, IViewSite site);
	
	public Class<? extends PersistentObject> getElementClass();
	
	public void display(Object obj);
	
	public String getTitle();
	
}
