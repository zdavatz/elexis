/*******************************************************************************
 * Copyright (c) 2009, G. Weirich, medshare and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 * $Id: PhysioDetailDisplay.java 5192 2009-02-24 15:48:29Z rgw_ch $
 *******************************************************************************/
package ch.elexis.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import ch.elexis.Desk;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.PhysioLeistung;
import ch.elexis.util.LabeledInputField;
import ch.elexis.util.LabeledInputField.InputData;

public class PhysioDetailDisplay implements IDetailDisplay {
	Form form;
	FormToolkit tk = Desk.getToolkit();
	LabeledInputField.AutoForm tblLab;
	
	InputData[] data = new InputData[] {
		new InputData("Ziffer"), //$NON-NLS-1$
		new InputData("TP", "TP", InputData.Typ.STRING, null) //$NON-NLS-1$ //$NON-NLS-2$
		};
	
	public Composite createDisplay(Composite parent, IViewSite site){
		form = tk.createForm(parent);
		TableWrapLayout twl = new TableWrapLayout();
		form.getBody().setLayout(twl);
		
		tblLab = new LabeledInputField.AutoForm(form.getBody(), data);
		
		TableWrapData twd = new TableWrapData(TableWrapData.FILL_GRAB);
		twd.grabHorizontal = true;
		tblLab.setLayoutData(twd);
		// GlobalEvents.getInstance().addActivationListener(this,this);
		return form.getBody();
	}
	
	public void display(Object obj){
		PhysioLeistung ll = (PhysioLeistung) obj;
		form.setText(ll.getLabel());
		tblLab.reload(ll);
	}
	
	public Class<? extends PersistentObject> getElementClass(){
		return PhysioLeistung.class;
	}
	
	public String getTitle(){
		return "Physiotherapie";
	}
	
}
