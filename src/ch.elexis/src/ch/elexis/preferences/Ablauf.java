/*******************************************************************************
 * Copyright (c) 2005-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *  $Id: Ablauf.java 5319 2009-05-26 14:55:24Z rgw_ch $
 *******************************************************************************/
package ch.elexis.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.Hub;

/**
 * Einstellungen für den Programmablauf. Logstufen etc.
 * 
 * @author Gerry
 */
public class Ablauf extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	
	public Ablauf(){
		super(GRID);
		setPreferenceStore(new SettingsPreferenceStore(Hub.localCfg));
		setDescription(Messages.Ablauf_0);
	}
	
	@Override
	protected void createFieldEditors(){
		EmptyFileFieldEditor ffe =
			new EmptyFileFieldEditor(PreferenceConstants.ABL_LOGFILE, Messages.Ablauf_1,
				getFieldEditorParent());
		ffe.setFileExtensions(new String[] {
			Messages.Ablauf_2
		});
		
		addField(ffe);
		
		addField(new StringFieldEditor(PreferenceConstants.ABL_LOGFILE_MAX_SIZE,
			Messages.Ablauf_31, getFieldEditorParent()));
		
		addField(new RadioGroupFieldEditor(PreferenceConstants.ABL_LOGLEVEL, Messages.Ablauf_3, 2,
			new String[][] {
				{
					Messages.Ablauf_4, "1"}, //$NON-NLS-1$
				{
					Messages.Ablauf_6, "2"}, //$NON-NLS-1$
				{
					Messages.Ablauf_8, "3"}, //$NON-NLS-1$
				{
					Messages.Ablauf_10, "4"}, //$NON-NLS-1$
				{
					Messages.Ablauf_12, "5"} //$NON-NLS-1$
			}, getFieldEditorParent()));
		
		addField(new RadioGroupFieldEditor(PreferenceConstants.ABL_LOGALERT, Messages.Ablauf_14, 2,
			new String[][] {
				{
					Messages.Ablauf_15, "0"}, //$NON-NLS-1$
				{
					Messages.Ablauf_17, "1"}, //$NON-NLS-1$
				{
					Messages.Ablauf_19, "2"}, //$NON-NLS-1$
				{
					Messages.Ablauf_21, "3"} //$NON-NLS-1$
			}, getFieldEditorParent()));
		
		addField(new StringFieldEditor(PreferenceConstants.ABL_TRACE, Messages.Ablauf_23,
			getFieldEditorParent()));
		
		addField(new RadioGroupFieldEditor(PreferenceConstants.ABL_LANGUAGE,
			Messages.Ablauf_preferredLang, 1, new String[][] {
				{
					Messages.Ablauf_german, "d" //$NON-NLS-1$
				}, {
					Messages.Ablauf_french, "f" //$NON-NLS-1$
				}, {
					Messages.Ablauf_italian, Messages.Ablauf_24
				}
			}, getFieldEditorParent()));
		
		addField(new IntegerFieldEditor(PreferenceConstants.ABL_CACHELIFETIME,
			Messages.Ablauf_cachelifetime, getFieldEditorParent()));
		
		addField(new IntegerFieldEditor(PreferenceConstants.ABL_HEARTRATE,
			Messages.Ablauf_heartrate, getFieldEditorParent()));
	}
	
	public void init(final IWorkbench workbench){

	}
	
	static class EmptyFileFieldEditor extends FileFieldEditor {
		public EmptyFileFieldEditor(final String abl_logfile, final String string,
			final Composite fieldEditorParent){
			super(abl_logfile, string, fieldEditorParent);
		}
		
		@Override
		protected boolean checkState(){
			return true;
		}
	}
	
	@Override
	public boolean performOk(){
		if (super.performOk()) {
			Hub.localCfg.flush();
			return true;
		}
		return false;
	}
	
}
