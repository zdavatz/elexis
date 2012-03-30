/*******************************************************************************
 * Copyright (c) 2006-2009, G. Weirich and Sgam.informatics
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *  $Id: MultilineFieldEditor.java 1281 2006-11-14 16:59:49Z rgw_ch $
 *******************************************************************************/

package ch.elexis.preferences.inputs;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.swt.widgets.Composite;

import ch.elexis.Hub;
import ch.rgw.tools.StringTool;

public class StringListFieldEditor extends ListEditor {
	String title, message;
	
	/**
	 * create a new StringList
	 * 
	 * @param name
	 *            name of the preference this editor's values are stored
	 * @param title
	 *            title or the input dialog
	 * @param inputMessage
	 *            message of the input dialog
	 * @param input
	 *            label of the text field
	 * @param parent
	 */
	public StringListFieldEditor(String name, String title, String inputMessage, String input,
		Composite parent){
		super(name, input, parent);
		this.title = title;
		message = inputMessage;
	}
	
	protected String createList(String[] items){
		return StringTool.join(items, ","); //$NON-NLS-1$
	}
	
	@Override
	protected String getNewInputObject(){
		InputDialog id =
			new InputDialog(Hub.plugin.getWorkbench().getActiveWorkbenchWindow().getShell(), title,
				message, StringTool.leer, null); //$NON-NLS-1$
		id.open();
		return id.getValue();
	}
	
	@Override
	protected String[] parseString(String stringList){
		return stringList.split(","); //$NON-NLS-1$
	}
	
}