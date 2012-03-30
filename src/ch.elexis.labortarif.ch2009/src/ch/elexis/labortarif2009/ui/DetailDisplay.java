/*******************************************************************************
 * Copyright (c) 2009, G. Weirich and medelexis AG
 * All rights reserved.
 * $Id: DetailDisplay.java 131 2009-06-14 15:20:46Z  $
 *******************************************************************************/

package ch.elexis.labortarif2009.ui;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.forms.widgets.Form;

import ch.elexis.Desk;
import ch.elexis.data.PersistentObject;
import ch.elexis.labortarif2009.data.Labor2009Tarif;
import ch.elexis.selectors.DisplayPanel;
import ch.elexis.selectors.FieldDescriptor;
import ch.elexis.selectors.FieldDescriptor.Typ;
import ch.elexis.util.SWTHelper;
import ch.elexis.views.IDetailDisplay;

public class DetailDisplay implements IDetailDisplay {
	Form form;
	DisplayPanel panel;
	FieldDescriptor<?>[] fields =
		{
			new FieldDescriptor<Labor2009Tarif>(Messages.DetailDisplay_chapter,
				Labor2009Tarif.FLD_CHAPTER, Typ.STRING, null),
			new FieldDescriptor<Labor2009Tarif>(Messages.DetailDisplay_code,
				Labor2009Tarif.FLD_CODE, Typ.STRING, null),
			new FieldDescriptor<Labor2009Tarif>(Messages.DetailDisplay_fachbereich,
				Labor2009Tarif.FLD_FACHBEREICH, Typ.STRING, null),
			new FieldDescriptor<Labor2009Tarif>(Messages.DetailDisplay_name,
				Labor2009Tarif.FLD_NAME, Typ.STRING, null),
			new FieldDescriptor<Labor2009Tarif>(Messages.DetailDisplay_limitation,
				Labor2009Tarif.FLD_LIMITATIO, Typ.STRING, null),
			new FieldDescriptor<Labor2009Tarif>(Messages.DetailDisplay_taxpoints,
				Labor2009Tarif.FLD_TP, Typ.STRING, null)
		};
	
	public void display(Object obj){
		if (obj instanceof Labor2009Tarif) {
			form.setText(((PersistentObject) obj).getLabel());
			panel.setObject((PersistentObject) obj);
		}
	}
	
	public Class<? extends PersistentObject> getElementClass(){
		return Labor2009Tarif.class;
	}
	
	public String getTitle(){
		return "EAL 2009"; //$NON-NLS-1$
	}
	
	public Composite createDisplay(Composite parent, IViewSite site){
		form = Desk.getToolkit().createForm(parent);
		form.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		form.getBody().setLayout(new GridLayout());
		panel = new DisplayPanel(form.getBody(), fields, 1, 1);
		panel.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		return panel;
	}
	
}
