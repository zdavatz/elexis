/*******************************************************************************
 * Copyright (c) 2007-2009, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *  $Id: DefaultOutputter.java 5331 2009-05-30 13:01:05Z rgw_ch $
 *******************************************************************************/

package ch.elexis.views.rechnung;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import ch.elexis.data.Fall;
import ch.elexis.data.Rechnung;
import ch.elexis.util.IRnOutputter;
import ch.elexis.util.SWTHelper;
import ch.rgw.tools.Result;

/**
 * This outputter takes the output target from the case's billing syste,
 * 
 * @author Gerry
 * 
 */
public class DefaultOutputter implements IRnOutputter {
	private ArrayList<IRnOutputter> configured = new ArrayList<IRnOutputter>();
	
	public boolean canBill(Fall fall){
		if (fall.getOutputter().getDescription().equals(getDescription())) {
			return false;
		}
		return fall.getOutputter().canBill(fall);
	}
	
	public boolean canStorno(Rechnung rn){
		if (rn == null) {
			return false;
		}
		return rn.getFall().getOutputter().canStorno(rn);
	}
	
	public Control createSettingsControl(Composite parent){
		Label lbl = new Label(parent, SWT.WRAP);
		lbl.setText(Messages.getString("DefaultOutputter.useIdividualPlugins")); //$NON-NLS-1$
		return lbl;
	}
	
	public Result<Rechnung> doOutput(TYPE type, Collection<Rechnung> rnn, final Properties props){
		Result<Rechnung> res = new Result<Rechnung>(null);
		props.setProperty(IRnOutputter.PROP_OUTPUT_METHOD, "asDefault"); //$NON-NLS-1$
		for (Rechnung rn : rnn) {
			Fall fall = rn.getFall();
			final IRnOutputter iro = fall.getOutputter();
			if (!configured.contains(iro)) {
				SWTHelper.SimpleDialog dlg =
					new SWTHelper.SimpleDialog(new SWTHelper.IControlProvider() {
						public Control getControl(Composite parent){
							parent.getShell().setText(iro.getDescription());
							return iro.createSettingsControl(parent);
							
						}
						
						public void beforeClosing(){
							iro.saveComposite();
						}
					});
				if (dlg.open() == Dialog.OK) {
					configured.add(iro);
				} else {
					continue;
				}
			}
			
			res.add(iro.doOutput(type, Arrays.asList(new Rechnung[] {
				rn
			}), props));
		}
		return null;
	}
	
	public String getDescription(){
		return Messages.getString("DefaultOutputter.defaultOutputForCase"); //$NON-NLS-1$
	}
	
	public void saveComposite(){
	// Nothing
	}
}
