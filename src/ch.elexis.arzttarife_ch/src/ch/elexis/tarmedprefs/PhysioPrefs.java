/*******************************************************************************
 * Copyright (c) 2009, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 * $Id: PhysioPrefs.java 6143 2010-02-15 20:52:17Z rgw_ch $
 *******************************************************************************/
package ch.elexis.tarmedprefs;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.preferences.inputs.MultiplikatorEditor;

public class PhysioPrefs extends PreferencePage implements IWorkbenchPreferencePage {
	public static final String TP_ID = "Physio"; //$NON-NLS-1$
	
	@Override
	protected Control createContents(final Composite parent){
		return new MultiplikatorEditor(parent, TP_ID);
	}
	
	public void init(final IWorkbench workbench){

	}
	
}
