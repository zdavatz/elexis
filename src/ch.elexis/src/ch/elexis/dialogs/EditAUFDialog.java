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
 *  $Id: EditAUFDialog.java 6044 2010-02-01 15:18:50Z rgw_ch $
 *******************************************************************************/

package ch.elexis.dialogs;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ch.elexis.Desk;
import ch.elexis.actions.ElexisEventDispatcher;
import ch.elexis.data.AUF;
import ch.elexis.data.Fall;
import ch.elexis.util.SWTHelper;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;

import com.tiff.common.ui.datepicker.DatePicker;

/**
 * Eine AUF erstellen oder ändern
 * 
 * @author gerry
 */
public class EditAUFDialog extends TitleAreaDialog {
	private AUF auf;
	private Fall fall;
	private DatePicker dpVon, dpBis;
	private Text tProzent, tGrund, tZusatz;
	TimeTool tt = new TimeTool();
	
	public EditAUFDialog(Shell shell, AUF a, Fall fall){
		super(shell);
		auf = a;
		this.fall = fall;
	}
	
	@Override
	protected Control createDialogArea(Composite parent){
		Composite ret = new Composite(parent, SWT.NONE);
		ret.setLayout(new GridLayout(2, true));
		ret.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		new Label(ret, SWT.NONE).setText(Messages.getString("EditAUFDialog.from")); //$NON-NLS-1$
		new Label(ret, SWT.NONE).setText(Messages.getString("EditAUFDialog.until")); //$NON-NLS-1$
		dpVon = new DatePicker(ret, SWT.NONE);
		dpBis = new DatePicker(ret, SWT.NONE);
		new Label(ret, SWT.NONE).setText(Messages.getString("EditAUFDialog.percent")); //$NON-NLS-1$
		new Label(ret, SWT.NONE).setText(Messages.getString("EditAUFDialog.reason")); //$NON-NLS-1$
		tProzent = new Text(ret, SWT.BORDER);
		tGrund = new Text(ret, SWT.BORDER);
		tProzent.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		tGrund.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		
		Label lbZusatz = new Label(ret, SWT.NONE);
		lbZusatz.setText(Messages.getString("EditAUFDialog.additional")); //$NON-NLS-1$
		lbZusatz.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 2, 1));
		tZusatz = new Text(ret, SWT.MULTI);
		tZusatz.setLayoutData(SWTHelper.getFillGridData(2, true, 1, true));
		if (auf != null) {
			dpVon.setDate(auf.getBeginn().getTime());
			dpBis.setDate(auf.getEnd().getTime());
			tGrund.setText(auf.getGrund());
			tProzent.setText(auf.getProzent());
			tZusatz.setText(auf.getZusatz());
		} else {
			if (fall == null) {
				fall = (Fall) ElexisEventDispatcher.getSelected(Fall.class);
			}
			if (fall != null) {
				tGrund.setText(fall.getGrund());
			} else {
				setMessage("Bitte wählen Sie zuerst einen Fall für diese AUF aus");
			}
			tProzent.setText("100"); //$NON-NLS-1$
			dpVon.setDate(tt.getTime());
			dpBis.setDate(tt.getTime());
		}
		return ret;
	}
	
	@Override
	public void create(){
		super.create();
		setTitle(Messages.getString("EditAUFDialog.auf") + " - " + ElexisEventDispatcher.getSelectedPatient().getLabel()); //$NON-NLS-1$
		if (auf == null) {
			setMessage(Messages.getString("EditAUFDialog.enterNewAUF")); //$NON-NLS-1$
		} else {
			setMessage(Messages.getString("EditAUFDialog.editAufDetails")); //$NON-NLS-1$
		}
		getShell().setText(Messages.getString("EditAUFDialog.auf")); //$NON-NLS-1$
		setTitleImage(Desk.getImage(Desk.IMG_LOGO48));
	}
	
	@Override
	protected void okPressed(){
		TimeTool tt = new TimeTool();
		tt.setTimeInMillis(dpVon.getDate().getTime());
		String von = tt.toString(TimeTool.DATE_GER);
		tt.setTimeInMillis(dpBis.getDate().getTime());
		String bis = tt.toString(TimeTool.DATE_GER);
		String zus = tZusatz.getText();
		// Fall fall = (Fall) ElexisEventDispatcher.getSelected(Fall.class);
		if (auf == null) {
			auf = new AUF(fall, von, bis, tProzent.getText(), tGrund.getText());
			if (!StringTool.isNothing(zus)) {
				auf.set(AUF.FLD_ZUSATZ, zus);
			}
		} else {
			fall = auf.getFall();
			String[] parms =
				new String[] {
					AUF.FLD_CASE_ID, AUF.FLD_DATE_FROM, AUF.FLD_DATE_UNTIL, AUF.FLD_REASON,
					AUF.FLD_PERCENT, AUF.FLD_ZUSATZ
				};
			String[] vals = new String[] {
				fall.getId(), von, bis, tGrund.getText(), tProzent.getText(), zus
			};
			auf.set(parms, vals);
		}
		super.okPressed();
	}
	
}
