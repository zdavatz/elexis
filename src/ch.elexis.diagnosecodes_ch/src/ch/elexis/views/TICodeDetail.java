/*******************************************************************************
 * Copyright (c) 2006-2008, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *    $Id: TICodeDetail.java 4357 2008-09-02 16:31:28Z rgw_ch $
 *******************************************************************************/

package ch.elexis.views;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;

import ch.elexis.Desk;
import ch.elexis.data.TICode;
import ch.elexis.diagnosecodes_schweiz.Messages;
import ch.elexis.util.SWTHelper;

public class TICodeDetail implements IDetailDisplay {
	
	FormToolkit tk = Desk.getToolkit();
	Form form;
	Text tID, tFull;
	
	public Class getElementClass(){
		return TICode.class;
	}
	
	public void display(Object obj){
		if (obj instanceof TICode) {
			TICode tc = (TICode) obj;
			tID.setText(tc.getCode());
			tFull.setText(tc.getText());
		}
	}
	
	public String getTitle(){
		return "TI Code"; //$NON-NLS-1$
	}
	
	public Composite createDisplay(Composite parent, IViewSite site){
		parent.setLayout(new FillLayout());
		form = tk.createForm(parent);
		Composite body = form.getBody();
		body.setLayout(new GridLayout(2, false));
		tk.createLabel(body, "Code"); //$NON-NLS-1$
		tID = tk.createText(body, ""); //$NON-NLS-1$
		tID.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		tk.createLabel(body, Messages.TICodeDetail_fulltext);
		tFull = tk.createText(body, ""); //$NON-NLS-1$
		tFull.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		return body;
	}
	
}
