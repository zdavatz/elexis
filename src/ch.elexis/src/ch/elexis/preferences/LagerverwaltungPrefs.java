/*******************************************************************************
 * Copyright (c) 2005-2008, D. Lutz and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    D. Lutz - initial implementation
 *    G. Weirich check illegal values
 *    
 *  $Id: LagerverwaltungPrefs.java 3851 2008-04-30 13:40:34Z rgw_ch $
 *******************************************************************************/
package ch.elexis.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.Hub;

/**
 * Einstellungen für die Lagerverwaltung
 * 
 * @author Daniel Lutz <danlutz@watz.ch>
 */
public class LagerverwaltungPrefs extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {
	
	public LagerverwaltungPrefs(){
		super(GRID);
		setPreferenceStore(new SettingsPreferenceStore(Hub.globalCfg));
		setDescription(Messages.LagerverwaltungPrefs_storageManagement);
		getPreferenceStore().setDefault(PreferenceConstants.INVENTORY_CHECK_ILLEGAL_VALUES,
			PreferenceConstants.INVENTORY_CHECK_ILLEGAL_VALUES_DEFAULT);
	}
	
	@Override
	protected void createFieldEditors(){
		addField(new BooleanFieldEditor(PreferenceConstants.INVENTORY_CHECK_ILLEGAL_VALUES,
			Messages.LagerverwaltungPrefs_checkForInvalid, getFieldEditorParent()));
		addField(new RadioGroupFieldEditor(PreferenceConstants.INVENTORY_ORDER_TRIGGER,
			Messages.LagerverwaltungPrefs_orderCriteria, 1, new String[][] {
				{
					Messages.LagerverwaltungPrefs_orderWhenBelowMi,
					PreferenceConstants.INVENTORY_ORDER_TRIGGER_BELOW_VALUE
				},
				{
					Messages.LagerverwaltungPrefs_orderWhenAtMin,
					PreferenceConstants.INVENTORY_ORDER_TRIGGER_EQUAL_VALUE
				},
			}, getFieldEditorParent()));
		
	}
	
	public void init(final IWorkbench workbench){}
	
	@Override
	public boolean performOk(){
		if (super.performOk()) {
			Hub.globalCfg.flush();
			return true;
		}
		return false;
	}
}
