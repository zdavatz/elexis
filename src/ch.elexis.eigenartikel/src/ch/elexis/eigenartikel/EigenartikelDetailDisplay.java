/*******************************************************************************
 * Copyright (c) 2006-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    M. Descher - extracted from elexis main and adapted for usage
 * 
 *  $Id$
 *******************************************************************************/
package ch.elexis.eigenartikel;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import ch.elexis.Desk;
import ch.elexis.actions.ElexisEventDispatcher;
import ch.elexis.data.Artikel;
import ch.elexis.data.Kontakt;
import ch.elexis.data.PersistentObject;
import ch.elexis.dialogs.KontaktSelektor;
import ch.elexis.util.LabeledInputField;
import ch.elexis.util.LabeledInputField.InputData;
import ch.elexis.util.LabeledInputField.InputData.Typ;
import ch.elexis.views.IDetailDisplay;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

public class EigenartikelDetailDisplay implements IDetailDisplay {

	static final public InputData[] getFieldDefs(final Shell shell){
		InputData[] ret =
			new InputData[] {
				new InputData(Messages.EigenartikelDisplay_typ, Artikel.FLD_TYP, Typ.STRING, null),
				new InputData(Messages.EigenartikelDisplay_group, Artikel.FLD_CODECLASS,
					Typ.STRING, null),
				new InputData(Messages.EigenartikelDisplay_buyPrice, Artikel.FLD_EK_PREIS,
					Typ.CURRENCY, null),
				new InputData(Messages.EigenartikelDisplay_sellPrice, Artikel.FLD_VK_PREIS,
					Typ.CURRENCY, null),
				new InputData(Messages.EigenartikelDisplay_maxOnStock, Artikel.MAXBESTAND,
					Typ.STRING, null),
				new InputData(Messages.EigenartikelDisplay_minOnStock, Artikel.MINBESTAND,
					Typ.STRING, null),
				new InputData(Messages.EigenartikelDisplay_actualOnStockPacks, Artikel.ISTBESTAND,
					Typ.STRING, null),
				new InputData(Messages.EigenartikelDisplay_actualOnStockPieces,
					Artikel.FLD_EXTINFO, Typ.INT, Artikel.ANBRUCH),
				new InputData(Messages.EigenartikelDisplay_PiecesPerPack, Artikel.FLD_EXTINFO,
					Typ.INT, Artikel.VERPACKUNGSEINHEIT),
				new InputData(Messages.EigenartikelDisplay_PiecesPerDose, Artikel.FLD_EXTINFO,
					Typ.INT, Artikel.VERKAUFSEINHEIT),
				new InputData(Messages.EigenartikelDisplay_dealer, Artikel.FLD_LIEFERANT_ID,
					new LabeledInputField.IContentProvider() {
						public void displayContent(PersistentObject po, InputData ltf){
							String lbl = ((Artikel) po).getLieferant().getLabel();
							if (lbl.length() > 15) {
								lbl = lbl.substring(0, 12) + "..."; //$NON-NLS-1$
							}
							ltf.setText(lbl);
						}
						
						public void reloadContent(PersistentObject po, InputData ltf){
							KontaktSelektor ksl =
								new KontaktSelektor(shell, Kontakt.class,
									Messages.EigenartikelDisplay_dealer,
									Messages.EigenartikelDisplay_pleaseChooseDealer,
									Kontakt.DEFAULT_SORT);
							if (ksl.open() == Dialog.OK) {
								Kontakt k = (Kontakt) ksl.getSelection();
								((Artikel) po).setLieferant(k);
								String lbl = ((Artikel) po).getLieferant().getLabel();
								if (lbl.length() > 15) {
									lbl = lbl.substring(0, 12) + "..."; //$NON-NLS-1$
								}
								ltf.setText(lbl);
								ElexisEventDispatcher.reload(Artikel.class);
							}
						}
						
					})
			};
		return ret;
	}
	
	FormToolkit tk = Desk.getToolkit();
	ScrolledForm form;
	LabeledInputField.AutoForm tblArtikel;
	
	@Override
	public Composite createDisplay(Composite parent, IViewSite site){
		parent.setLayout(new FillLayout());
		form = tk.createScrolledForm(parent);
		Composite ret = form.getBody();
		TableWrapLayout twl = new TableWrapLayout();
		ret.setLayout(twl);
		tblArtikel = new LabeledInputField.AutoForm(ret, getFieldDefs(parent.getShell()));
		
		TableWrapData twd = new TableWrapData(TableWrapData.FILL_GRAB);
		twd.grabHorizontal = true;
		tblArtikel.setLayoutData(twd);
		return ret;
	}

	@Override
	public Class<? extends PersistentObject> getElementClass(){
		return Eigenartikel.class;
	}

	@Override
	public void display(Object obj){
		if (obj instanceof Eigenartikel) {
			Eigenartikel m = (Eigenartikel) obj;
			form.setText(m.getLabel());
			tblArtikel.reload(m);
		}
	}

	@Override
	public String getTitle(){
		return Messages.EigenartikelDisplay_displayTitle;
	}

	
}
