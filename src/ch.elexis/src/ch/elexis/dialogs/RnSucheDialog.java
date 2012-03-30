/*******************************************************************************
 * Copyright (c) 2006-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 * $Id: RnSucheDialog.java 5941 2010-01-17 14:47:05Z rgw_ch $
 *******************************************************************************/
package ch.elexis.dialogs;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import ch.elexis.util.LabeledInputField;
import ch.elexis.util.SWTHelper;

public class RnSucheDialog extends TitleAreaDialog {
	LabeledInputField liNummer, liName;
	String nummer, name;
	
	public RnSucheDialog(Shell shell){
		super(shell);
	}
	
	public String getNummer(){
		return nummer;
	}
	
	public String getName(){
		return name;
	}
	
	@Override
	protected Control createDialogArea(Composite parent){
		LabeledInputField.Tableau tbl = new LabeledInputField.Tableau(parent);
		tbl.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		liNummer = tbl.addComponent(Messages.getString("RnSucheDialog.number")); //$NON-NLS-1$
		liName = tbl.addComponent(Messages.getString("RnSucheDialog.name")); //$NON-NLS-1$
		return tbl;
	}
	
	@Override
	public void create(){
		super.create();
		setTitle(Messages.getString("RnSucheDialog.findBill")); //$NON-NLS-1$
		getShell().setText(Messages.getString("RnSucheDialog.findBill")); //$NON-NLS-1$
		setMessage(Messages.getString("RnSucheDialog.enterCriteria")); //$NON-NLS-1$
	}
	
	@Override
	protected void okPressed(){
		nummer = liNummer.getText();
		name = liName.getText();
		super.okPressed();
	}
	
}
