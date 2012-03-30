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
 *    $Id: DBConnectSecondPage.java 5331 2009-05-30 13:01:05Z rgw_ch $
 *******************************************************************************/

package ch.elexis.wizards;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import ch.elexis.util.SWTHelper;

public class DBConnectSecondPage extends WizardPage {
	Text name, pwd;
	
	public DBConnectSecondPage(String pageName){
		super(Messages.getString("DBConnectSecondPage.0")); //$NON-NLS-1$
		setTitle(Messages.getString("DBConnectSecondPage.Credentials")); //$NON-NLS-1$
		setMessage(Messages.getString("DBConnectSecondPage.username1") + //$NON-NLS-1$
			Messages.getString("DBConnectSecondPage.username2")); //$NON-NLS-1$
	}
	
	public DBConnectSecondPage(String pageName, String title, ImageDescriptor titleImage){
		super(pageName, title, titleImage);
		// TODO Automatisch erstellter Konstruktoren-Stub
	}
	
	public void createControl(Composite parent){
		Composite form = new Composite(parent, SWT.NONE);
		form.setLayout(new GridLayout(1, false));
		new Label(form, SWT.NONE).setText(Messages
			.getString("DBConnectSecondPage.databaseUsername")); //$NON-NLS-1$
		name = new Text(form, SWT.BORDER);
		name.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		new Label(form, SWT.NONE).setText(Messages
			.getString("DBConnectSecondPage.databasePassword")); //$NON-NLS-1$
		pwd = new Text(form, SWT.BORDER);
		pwd.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		setControl(form);
	}
	
}
