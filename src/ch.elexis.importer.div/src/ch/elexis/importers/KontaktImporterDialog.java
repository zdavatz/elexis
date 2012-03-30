/*******************************************************************************
 * Copyright (c) 2007-2008, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *  $Id: KontaktImporterDialog.java 6137 2010-02-14 09:45:36Z rgw_ch $
 *******************************************************************************/
package ch.elexis.importers;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import ch.elexis.Desk;
import ch.elexis.util.SWTHelper;

public class KontaktImporterDialog extends TitleAreaDialog {
	KontaktImporterBlatt kib;
	
	public KontaktImporterDialog(final Shell shell){
		super(shell);
	}
	
	@Override
	protected Control createDialogArea(final Composite parent){
		kib = new KontaktImporterBlatt(parent);
		kib.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		return kib;
	}
	
	@Override
	public void create(){
		super.create();
		setTitle(Messages.KontaktImporterDialog_ImportingContact);
		setMessage(Messages.KontaktImporterDialog_PleaseEnterFileTypeAndFile);
		setTitleImage(Desk.getImage(Desk.IMG_LOGO48));
		getShell().setText(Messages.KontaktImporterDialog_ImporterCaption);
	}
	
	@Override
	protected void okPressed(){
	/*
	 * if(kib.doImport()){ super.okPressed(); }
	 */
	}
	
}
