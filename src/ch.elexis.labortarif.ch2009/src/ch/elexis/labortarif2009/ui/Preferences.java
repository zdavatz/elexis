/*******************************************************************************
 * Copyright (c) 2009, G. Weirich and medelexis AG
 * All rights reserved.
 * $Id: Preferences.java 132 2009-06-14 17:34:31Z  $
 *******************************************************************************/

package ch.elexis.labortarif2009.ui;

import java.util.LinkedList;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.Hub;
import ch.elexis.StringConstants;
import ch.elexis.labortarif2009.data.Importer;
import ch.elexis.labortarif2009.data.Labor2009Tarif;
import ch.elexis.labortarif2009.data.Importer.Fachspec;
import ch.elexis.preferences.PreferenceConstants;
import ch.elexis.preferences.inputs.MultiplikatorEditor;
import ch.elexis.util.SWTHelper;
import ch.rgw.io.Settings;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.StringTool;

public class Preferences extends PreferencePage implements IWorkbenchPreferencePage {
	private static final String SPECNUM = "specnum"; //$NON-NLS-1$
	public static final String FACHDEF = "abrechnung/labor2009/fachdef"; //$NON-NLS-1$
	public static final String OPTIMIZE = "abrechnung/labor2009/optify"; //$NON-NLS-1$
	int langdef = 0;
	Settings cfg = Hub.mandantCfg;
	LinkedList<Button> buttons = new LinkedList<Button>();
	
	public Preferences(){
		String lang = JdbcLink.wrap(Hub.localCfg.get( // d,f,i
			PreferenceConstants.ABL_LANGUAGE, "d").toUpperCase()); //$NON-NLS-1$
		if (lang.startsWith("F")) { //$NON-NLS-1$
			langdef = 1;
		} else if (lang.startsWith("I")) { //$NON-NLS-1$
			langdef = 2;
		}
		
	}
	
	@Override
	protected Control createContents(Composite parent){
		Composite ret = new Composite(parent, SWT.NONE);
		ret.setLayout(new GridLayout());
		new Label(ret, SWT.NONE).setText(Messages.Preferences_pleaseEnterMultiplier);
		MultiplikatorEditor me = new MultiplikatorEditor(ret, Labor2009Tarif.MULTIPLICATOR_NAME);
		me.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		Fachspec[] specs = Importer.loadFachspecs(langdef);
		Group group = new Group(ret, SWT.BORDER);
		group.setText(Messages.Preferences_specialities);
		group.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		group.setLayout(new GridLayout());
		String[] olddef = cfg.getStringArray(FACHDEF);
		for (Fachspec spec : specs) {
			Button b = new Button(group, SWT.CHECK);
			b.setText(spec.name);
			b.setData(SPECNUM, spec.code);
			b.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
			if (olddef != null && StringTool.getIndex(olddef, Integer.toString(spec.code)) != -1) {
				b.setSelection(true);
			}
			buttons.add(b);
		}
		final Button bOptify = new Button(ret, SWT.CHECK);
		bOptify.setSelection(Hub.localCfg.get(OPTIMIZE, true));
		bOptify.setText(Messages.Preferences_automaticallyCalculatioAdditions);
		bOptify.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				Hub.localCfg.set(OPTIMIZE, bOptify.getSelection());
			}
		});
		return ret;
	}
	
	public void init(IWorkbench workbench){
	// TODO Auto-generated method stub
	
	}
	
	@Override
	protected void performApply(){
		LinkedList<String> bb = new LinkedList<String>();
		for (Button b : buttons) {
			if (b.getSelection()) {
				bb.add(((Integer) b.getData(SPECNUM)).toString());
			}
		}
		Hub.mandantCfg.set(FACHDEF, StringTool.join(bb, StringConstants.COMMA));
		super.performApply();
	}
	
}
