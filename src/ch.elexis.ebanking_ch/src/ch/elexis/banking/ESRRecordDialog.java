/*******************************************************************************
 * Copyright (c) 2007-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *  $Id: ESRRecordDialog.java 5316 2009-05-20 11:34:51Z rgw_ch $
 *******************************************************************************/
package ch.elexis.banking;

import java.text.MessageFormat;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import ch.elexis.Desk;
import ch.elexis.data.Fall;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.elexis.data.Rechnung;
import ch.elexis.dialogs.KontaktSelektor;
import ch.elexis.util.LabeledInputField;
import ch.elexis.util.LabeledInputField.InputData;
import ch.elexis.util.SWTHelper;
import ch.rgw.tools.Money;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;

/**
 * Change an ESR record manually
 * 
 * @author gerry
 * 
 */
public class ESRRecordDialog extends TitleAreaDialog {
	private ESRRecord rec;
	private boolean bBooked;
	private Button bKeep, bBook, bUnbook, bDelete;
	private Label lFile;
	private LabeledInputField.AutoForm af;
	
	private InputData[] fields =
		{
			new InputData(Messages.ESRRecordDialog_readInDate,
				"Eingelesen", InputData.Typ.DATE, null), //$NON-NLS-1$
			new InputData(Messages.ESRRecordDialog_esrType, "ESRCode", InputData.Typ.STRING, null), //$NON-NLS-1$
			new InputData(Messages.ESRRecordDialog_bookedDate,
				"Verarbeitet", InputData.Typ.DATE, null), //$NON-NLS-1$
			new InputData(Messages.ESRRecordDialog_addedDate,
				"Gutgeschrieben", InputData.Typ.DATE, null), //$NON-NLS-1$
			new InputData(Messages.ESRRecordDialog_receivedDate, "Datum", InputData.Typ.DATE, null), //$NON-NLS-1$
			new InputData(Messages.ESRRecordDialog_amount,
				"BetragInRp", InputData.Typ.CURRENCY, null), //$NON-NLS-1$
			new InputData(Messages.ESRRecordDialog_billNr,
				"RechnungsID", new LabeledInputField.IContentProvider() { //$NON-NLS-1$
					public void displayContent(PersistentObject po, InputData ltf){
						Rechnung rn = rec.getRechnung();
						if (rn == null) {
							ltf.setText("??"); //$NON-NLS-1$
						} else {
							ltf.setText(rn.getNr());
						}
					}
					
					public void reloadContent(PersistentObject po, InputData ltf){
						InputDialog id =
							new InputDialog(getShell(), Messages.ESRRecordDialog_changeBillNr,
								Messages.ESRRecordDialog_pleaseEnterNewBilNr, ltf.getText(), null);
						if (id.open() == Dialog.OK) {
							String rnid =
								new Query<Rechnung>(Rechnung.class).findSingle("RnNummer", "=", id //$NON-NLS-1$ //$NON-NLS-2$
									.getValue());
							int err = 0;
							if (rnid != null) {
								Rechnung r = Rechnung.load(rnid);
								if (r.isAvailable()) {
									Fall fall = r.getFall();
									if (fall.isAvailable()) {
										Patient pat = fall.getPatient();
										Mandant mn = r.getMandant();
										if (pat.isAvailable()) {
											rec.set("RechnungsID", r.getId()); //$NON-NLS-1$
											// ltf.setText(r.getNr());
											rec.set("PatientID", pat.getId()); //$NON-NLS-1$
											if (mn != null && mn.isValid()) {
												rec.set("MandantID", mn.getId()); //$NON-NLS-1$
											}
											af.reload(rec);
										} else {
											err = 4;
										}
									} else {
										err = 3;
									}
									
								} else {
									err = 2;
								}
								
							} else {
								err = 1;
							}
							if (err != 0) {
								SWTHelper.showError(Messages.ESRRecordDialog_billNotFound,
									MessageFormat.format(Messages.ESRRecordDialog_noValidBillFound,
										id.getValue()));
							}
						}
					}
					
				}),
			new InputData(Messages.ESRRecordDialog_patient,
				"PatientID", new LabeledInputField.IContentProvider() { //$NON-NLS-1$
				
					public void displayContent(PersistentObject po, InputData ltf){
						ltf.setText(rec.getPatient().getLabel());
					}
					
					public void reloadContent(PersistentObject po, InputData ltf){
						KontaktSelektor ksl =
							new KontaktSelektor(getShell(), Patient.class,
								Messages.ESRRecordDialog_selectPatient,
								Messages.ESRRecordDialog_pleaseSelectPatient, Patient.DEFAULT_SORT);
						if (ksl.open() == Dialog.OK) {
							Patient actPatient = (Patient) ksl.getSelection();
							rec.set("PatientID", actPatient.getId()); //$NON-NLS-1$
							ltf.setText(actPatient.getLabel());
						}
					}
					
				})
		
		};
	
	ESRRecordDialog(Shell shell, ESRRecord record){
		super(shell);
		rec = record;
	}
	
	@Override
	protected Control createDialogArea(Composite parent){
		Composite ret = new Composite(parent, SWT.NONE);
		ret.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		TableWrapLayout twl = new TableWrapLayout();
		ret.setLayout(twl);
		af = new LabeledInputField.AutoForm(ret, fields);
		TableWrapData twd = new TableWrapData(TableWrapData.FILL_GRAB);
		twd.grabHorizontal = true;
		af.setLayoutData(twd);
		lFile = new Label(ret, SWT.NONE);
		lFile.setText(Messages.ESRRecordDialog_file + rec.getFile());
		TableWrapData tw3 = new TableWrapData();
		tw3.grabHorizontal = true;
		lFile.setLayoutData(tw3);
		Composite cChoices = new Composite(ret, SWT.BORDER);
		TableWrapData tw2 = new TableWrapData();
		tw2.grabHorizontal = true;
		cChoices.setLayoutData(tw2);
		RowLayout rl = new RowLayout(SWT.VERTICAL);
		rl.fill = true;
		cChoices.setLayout(rl);
		bKeep = new Button(cChoices, SWT.RADIO);
		bKeep.setText(Messages.ESRRecordDialog_dontchange);
		bBook = new Button(cChoices, SWT.RADIO);
		bBook.setText(Messages.ESRRecordDialog_bookRecord);
		bUnbook = new Button(cChoices, SWT.RADIO);
		bUnbook.setText(Messages.ESRRecordDialog_dontBookRecord);
		bDelete = new Button(cChoices, SWT.RADIO);
		bDelete.setText(Messages.ESRRecordDialog_deleteRecord);
		bBooked = !StringTool.isNothing(rec.getGebucht());
		bKeep.setSelection(true);
		
		af.reload(rec);
		ret.pack();
		return ret;
	}
	
	@Override
	public void create(){
		super.create();
		setTitle(Messages.ESRRecordDialog_editRecord);
		setMessage(Messages.ESRRecordDialog_warningEditing);
		setTitleImage(Desk.getImage(Desk.IMG_LOGO48));
		getShell().setText(Messages.ESRRecordDialog_detailsForESRRecord);
		
	}
	
	@Override
	protected void okPressed(){
		if (bBook.getSelection()) {
			if (!bBooked) {
				Money zahlung = rec.getBetrag();
				Rechnung rn = rec.getRechnung();
				rn.addZahlung(zahlung, Messages.ESRRecordDialog_vESRForBill + rn.getNr() + " / " //$NON-NLS-1$
					+ rec.getPatient().getPatCode(), new TimeTool(rec.getValuta()));
				rec.setGebucht(null);
			}
		} else if (bUnbook.getSelection()) {
			if (bBooked) {
				Money zahlung = rec.getBetrag();
				Rechnung rn = rec.getRechnung();
				rn.addZahlung(zahlung.negate(), Messages.ESRRecordDialog_stornoESR + rn.getNr()
					+ " / " //$NON-NLS-1$
					+ rec.getPatient().getPatCode(), null);
				rec.set(Messages.ESRRecordDialog_booked, ""); //$NON-NLS-1$
			}
		}
		super.okPressed();
	}
	
}
