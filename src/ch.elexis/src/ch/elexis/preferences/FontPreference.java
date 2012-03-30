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
 *    $Id: FontPreference.java 6043 2010-02-01 14:34:06Z rgw_ch $
 *******************************************************************************/
package ch.elexis.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FontFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.Desk;
import ch.elexis.Hub;
import ch.elexis.actions.ElexisEvent;
import ch.elexis.actions.ElexisEventDispatcher;
import ch.elexis.data.Anwender;

public class FontPreference extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	
	public FontPreference(){
		super(Messages.FontPreference_schriftarten, GRID);
		setPreferenceStore(new SettingsPreferenceStore(Hub.userCfg));
	}
	
	@Override
	protected void createFieldEditors(){
		addField(new FontFieldEditor(PreferenceConstants.USR_DEFAULTFONT,
			Messages.FontPreference_standardschriftart, "Elexis", getFieldEditorParent())); //$NON-NLS-1$
	}
	
	public void init(IWorkbench workbench){
	// TODO Auto-generated method stub
	
	}
	
	@Override
	public boolean performOk(){
		boolean ret = super.performOk();
		Desk.updateFont(PreferenceConstants.USR_DEFAULTFONT);
		ElexisEventDispatcher.getInstance().fire(
			new ElexisEvent(Hub.actUser, Anwender.class, ElexisEvent.EVENT_USER_CHANGED));
		return ret;
	}
	
}
