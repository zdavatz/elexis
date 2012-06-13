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
 *  $Id$
 *******************************************************************************/

package ch.ngiger.elexis.oddb_ch.views;

import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import ch.elexis.Desk;
import ch.ngiger.elexis.oddb_ch.data.OddbArtikel;
import ch.elexis.util.LabeledInputField;
import ch.elexis.views.IDetailDisplay;
import ch.elexis.views.artikel.Artikeldetail;

public class OddbDetailDisplay implements IDetailDisplay {
	
	FormToolkit tk = Desk.getToolkit();
	ScrolledForm form;
	LabeledInputField.AutoForm tblArtikel;
	LabeledInputField ifName;
	Text tName;
	OddbArtikel act;
	
	public Composite createDisplay(final Composite parent, final IViewSite site){
		parent.setLayout(new FillLayout());
		form = tk.createScrolledForm(parent);
		Composite ret = form.getBody();
		TableWrapLayout twl = new TableWrapLayout();
		ret.setLayout(twl);
		
		ifName = new LabeledInputField(ret, "Name");
		ifName.setLayoutData(new TableWrapData(TableWrapData.FILL));
		tName = (Text) ifName.getControl();
		tName.addFocusListener(new FocusAdapter() {
			
			@Override
			public void focusLost(final FocusEvent e){
				if (act != null) {
					act.setInternalName(tName.getText());
				}
				super.focusLost(e);
			}
			
		});
		tblArtikel =
			new LabeledInputField.AutoForm(ret, Artikeldetail.getFieldDefs(parent.getShell()));
		
		TableWrapData twd = new TableWrapData(TableWrapData.FILL_GRAB);
		twd.grabHorizontal = true;
		tblArtikel.setLayoutData(twd);
		
		return ret;
	}
	
	public Class getElementClass(){
		return OddbArtikel.class;
	}
	
	public void display(Object obj){
		if (obj instanceof OddbArtikel) {
			act = (OddbArtikel) obj;
			form.setText(act.getLabel());
			tblArtikel.reload(act);
			ifName.setText(act.getInternalName());
		}
	}
	
	public String getTitle(){
		return OddbArtikel.ODDB_NAME; //$NON-NLS-1$
	}
	
}
