/*******************************************************************************
 * Copyright (c) 2008-2009, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *  $Id: KontaktDetailDialog.java 6044 2010-02-01 15:18:50Z rgw_ch $
 *******************************************************************************/

package ch.elexis.dialogs;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import ch.elexis.data.*;
import ch.elexis.util.LabeledInputField;
import ch.elexis.util.SWTHelper;
import ch.rgw.tools.StringTool;

/**
 * Edit Kontakt details. Can be called either with an existing Kontakt or with a String[2] that
 * define a contact (may all be null)
 * 
 * @author gerry
 * 
 */
public class KontaktDetailDialog extends TitleAreaDialog {
	private static final String LBL_MAIL = Messages.getString("KontaktDetailDialog.labelMail"); //$NON-NLS-1$
	private static final String LBL_FAX = Messages.getString("KontaktDetailDialog.labelFax"); //$NON-NLS-1$
	private static final String LBL_PHONE = Messages.getString("KontaktDetailDialog.labelPhone"); //$NON-NLS-1$
	private static final String LBL_PLACE = Messages.getString("KontaktDetailDialog.labelPlace"); //$NON-NLS-1$
	private static final String LBL_ZIP = Messages.getString("KontaktDetailDialog.labelZip"); //$NON-NLS-1$
	private static final String LBL_STREET = Messages.getString("KontaktDetailDialog.labelStreet"); //$NON-NLS-1$
	private static final String LBL_ZUSATZ = Messages.getString("KontaktDetailDialog.labelZusatz"); //$NON-NLS-1$
	private static final String LBL_SEX = Messages.getString("KontaktDetailDialog.labelSex"); //$NON-NLS-1$
	private static final String LBL_BIRTHDATE =
		Messages.getString("KontaktDetailDialog.labelBirthdate"); //$NON-NLS-1$
	private static final String LBL_FIRSTNAME =
		Messages.getString("KontaktDetailDialog.labelFirstname"); //$NON-NLS-1$
	private static final String LBL_NAME = Messages.getString("KontaktDetailDialog.labelName"); //$NON-NLS-1$
	Kontakt k;
	LabeledInputField liName, liVorname, liGebDat, liSex, liStrasse, liPlz, liOrt, liTel, liFax,
			liMail;
	String[] vals;
	int type = 0;
	ButtonAdapter ba = new ButtonAdapter();
	
	public KontaktDetailDialog(Shell parentShell, Kontakt kt){
		super(parentShell);
		k = kt;
	}
	
	public KontaktDetailDialog(Shell parentShell, String[] v){
		super(parentShell);
		vals = v;
	}
	
	@Override
	protected Control createDialogArea(Composite parent){
		Composite ret = new Composite(parent, SWT.NONE);
		ret.setLayout(new GridLayout(3, true));
		ret.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		if (k == null) {
			Composite cType = new Composite(ret, SWT.BORDER);
			cType.setLayoutData(SWTHelper.getFillGridData(3, true, 1, false));
			cType.setLayout(new FillLayout());
			Button bPerson = new Button(cType, SWT.RADIO);
			bPerson.setText(Messages.getString("KontaktDetailDialog.textPerson")); //$NON-NLS-1$
			Button bOrg = new Button(cType, SWT.RADIO);
			bOrg.setText(Messages.getString("KontaktDetailDialog.textOrganization")); //$NON-NLS-1$
			bPerson.addSelectionListener(ba);
			bOrg.addSelectionListener(ba);
			liName = SWTHelper.createLabeledField(ret, LBL_NAME, LabeledInputField.Typ.TEXT);
			liVorname =
				SWTHelper.createLabeledField(ret, LBL_FIRSTNAME, LabeledInputField.Typ.TEXT);
			liGebDat = SWTHelper.createLabeledField(ret, LBL_BIRTHDATE, LabeledInputField.Typ.TEXT);
			liSex = SWTHelper.createLabeledField(ret, LBL_SEX, LabeledInputField.Typ.TEXT);
			if (vals != null) {
				/*
				 * liGebDat.setText(vals[2]==null ? "" : vals[2]); liSex.setText(vals[3]==null ? ""
				 * : vals[3]); liStrasse.setText(vals[4]); liPlz.setText(vals[5]);
				 */
				liName.setText(StringTool.unNull(vals[0]));
				liVorname.setText(StringTool.unNull(vals[1]));
			}
		} else {
			if (k.istPerson()) {
				Person p = Person.load(k.getId());
				liName = SWTHelper.createLabeledField(ret, LBL_NAME, LabeledInputField.Typ.TEXT);
				liVorname =
					SWTHelper.createLabeledField(ret, LBL_FIRSTNAME, LabeledInputField.Typ.TEXT);
				liGebDat =
					SWTHelper.createLabeledField(ret, LBL_BIRTHDATE, LabeledInputField.Typ.TEXT);
				liSex = SWTHelper.createLabeledField(ret, LBL_SEX, LabeledInputField.Typ.TEXT);
				liName.setText(p.getName());
				liVorname.setText(p.getVorname());
				liGebDat.setText(p.getGeburtsdatum());
				liSex.setText(p.getGeschlecht());
			} else {
				liName = SWTHelper.createLabeledField(ret, LBL_NAME, LabeledInputField.Typ.TEXT);
				liVorname =
					SWTHelper.createLabeledField(ret, LBL_ZUSATZ, LabeledInputField.Typ.TEXT);
				liName.setText(k.get(Kontakt.FLD_NAME1));
				liVorname.setText(k.get(Kontakt.FLD_NAME2));
			}
		}
		liStrasse = SWTHelper.createLabeledField(ret, LBL_STREET, LabeledInputField.Typ.TEXT);
		liPlz = SWTHelper.createLabeledField(ret, LBL_ZIP, LabeledInputField.Typ.TEXT);
		liOrt = SWTHelper.createLabeledField(ret, LBL_PLACE, LabeledInputField.Typ.TEXT);
		liTel = SWTHelper.createLabeledField(ret, LBL_PHONE, LabeledInputField.Typ.TEXT);
		liFax = SWTHelper.createLabeledField(ret, LBL_FAX, LabeledInputField.Typ.TEXT);
		liMail = SWTHelper.createLabeledField(ret, LBL_MAIL, LabeledInputField.Typ.TEXT);
		if (k != null) {
			Anschrift an = k.getAnschrift();
			liStrasse.setText(an.getStrasse());
			liPlz.setText(an.getPlz());
			liOrt.setText(an.getOrt());
			liTel.setText(k.get(Kontakt.FLD_PHONE1));
			liFax.setText(k.get(LBL_FAX));
			liMail.setText(k.get(LBL_MAIL));
		}
		return ret;
	}
	
	@Override
	public void create(){
		super.create();
		getShell().setText(Messages.getString("KontaktDetailDialog.showDetails")); //$NON-NLS-1$
		if (k != null) {
			setTitle(k.getLabel());
		} else {
			setTitle(Messages.getString("KontaktDetailDialog.newContact")); //$NON-NLS-1$
		}
		setMessage(Messages.getString("KontaktDetailDialog.enterData")); //$NON-NLS-1$
	}
	
	@Override
	protected void okPressed(){
		if (k == null) {
			if (type == 0) {
				SWTHelper
					.showError(
						Messages.getString("KontaktDetailDialog.typeOfContact"), Messages.getString("KontaktDetailDialog.enterType")); //$NON-NLS-1$ //$NON-NLS-2$
				return;
			} else if (type == 1) {
				k =
					new Person(liName.getText(), liVorname.getText(), liGebDat.getText(), liSex
						.getText());
			} else {
				k = new Organisation(liName.getText(), liVorname.getText());
			}
		} else {
			if (k.istPerson()) {
				Person p = Person.load(k.getId());
				p.set(LBL_NAME, liName.getText());
				p.set(LBL_FIRSTNAME, liVorname.getText());
				p.set(LBL_BIRTHDATE, liGebDat.getText());
				p.set(LBL_SEX, liSex.getText());
			} else {
				Organisation o = Organisation.load(k.getId());
				o.set(LBL_NAME, liName.getText());
				o.set(LBL_ZUSATZ, liVorname.getText());
			}
		}
		Anschrift an = k.getAnschrift();
		an.setStrasse(liStrasse.getText());
		an.setPlz(liPlz.getText());
		an.setOrt(liOrt.getText());
		k.setAnschrift(an);
		k.set(Kontakt.FLD_PHONE1, liTel.getText());
		k.set(LBL_MAIL, liMail.getText());
		super.okPressed();
	}
	
	class ButtonAdapter extends SelectionAdapter {
		
		@Override
		public void widgetSelected(SelectionEvent e){
			if (((Button) e.getSource()).getText().equals(
				Messages.getString("KontaktDetailDialog.textPerson"))) { //$NON-NLS-1$
				type = 1;
				liGebDat.setEnabled(true);
				liSex.setEnabled(true);
				liVorname.setLabel(LBL_FIRSTNAME);
			} else {
				type = 2;
				liGebDat.setEnabled(false);
				liSex.setEnabled(false);
				liVorname.setLabel(LBL_ZUSATZ);
			}
		}
		
	}
	
}
