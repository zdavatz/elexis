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
 *  $Id: Texterstellung.java 5320 2009-05-27 16:51:14Z rgw_ch $
 *******************************************************************************/
package ch.elexis.preferences;

import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.Hub;
import ch.elexis.util.Extensions;

/**
 * Einstellungen zur Verknüpfung mit einem externen Texterstellungs-Modul
 * 
 * @author Gerry
 */
public class Texterstellung extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	
	public Texterstellung(){
		super(GRID);
		setPreferenceStore(new SettingsPreferenceStore(Hub.localCfg));
		setDescription(Messages.Texterstellung_TextProcessor);
	}
	
	@Override
	protected void createFieldEditors(){
		
		List<IConfigurationElement> list = Extensions.getExtensions("ch.elexis.Text"); //$NON-NLS-1$
		String[][] rows = new String[list.size()][];
		int i = 0;
		for (IConfigurationElement ice : list) {
			rows[i] = new String[2];
			rows[i][1] = ice.getAttribute("name"); //$NON-NLS-1$
			rows[i][0] = Integer.toString(i) + " : " + rows[i][1]; //$NON-NLS-1$
			i += 1;
		}
		addField(new RadioGroupFieldEditor(PreferenceConstants.P_TEXTMODUL,
			Messages.Texterstellung_ExternalProgram, 2,
			/*
			 * new String[][] { { "&0: Keines", "none" }, { "&1: OpenOffice", "OpenOffice" }
			 */
			rows, getFieldEditorParent()));
		
	}
	
	public void init(IWorkbench workbench){}
	
}
