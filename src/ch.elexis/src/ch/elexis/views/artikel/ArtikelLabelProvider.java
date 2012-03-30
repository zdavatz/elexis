/*******************************************************************************
 * Copyright (c) 2006-2009, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *  $Id: ArtikelLabelProvider.java 5330 2009-05-30 11:24:09Z rgw_ch $
 *******************************************************************************/

package ch.elexis.views.artikel;

import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import ch.elexis.Desk;
import ch.elexis.Hub;
import ch.elexis.data.Artikel;
import ch.elexis.preferences.PreferenceConstants;
import ch.elexis.util.viewers.DefaultLabelProvider;

public class ArtikelLabelProvider extends DefaultLabelProvider implements ITableColorProvider {
	
	@Override
	public Image getColumnImage(Object element, int columnIndex){
		if (element instanceof Artikel) {
			return null;
		} else {
			return Desk.getImage(Desk.IMG_ACHTUNG);
		}
	}
	
	@Override
	public String getColumnText(Object element, int columnIndex){
		if (element instanceof Artikel) {
			Artikel art = (Artikel) element;
			String ret = art.getInternalName();
			if (art.isLagerartikel()) {
				ret += " (" + Integer.toString(art.getTotalCount()) + ")"; //$NON-NLS-1$ //$NON-NLS-2$
			}
			return ret;
		}
		return super.getColumnText(element, columnIndex);
	}
	
	/**
	 * Lagerartikel are shown in blue, arrticles that should be ordered are shown in red
	 */
	public Color getForeground(Object element, int columnIndex){
		if (element instanceof Artikel) {
			Artikel art = (Artikel) element;
			
			if (art.isLagerartikel()) {
				int trigger =
					Hub.globalCfg.get(PreferenceConstants.INVENTORY_ORDER_TRIGGER,
						PreferenceConstants.INVENTORY_ORDER_TRIGGER_DEFAULT);
				
				int ist = art.getIstbestand();
				int min = art.getMinbestand();
				
				boolean order = false;
				switch (trigger) {
				case PreferenceConstants.INVENTORY_ORDER_TRIGGER_BELOW:
					order = (ist < min);
					break;
				case PreferenceConstants.INVENTORY_ORDER_TRIGGER_EQUAL:
					order = (ist <= min);
					break;
				default:
					order = (ist < min);
				}
				
				if (order) {
					return Desk.getColor(Desk.COL_RED);
				} else {
					return Desk.getColor(Desk.COL_BLUE);
				}
			}
		}
		
		return null;
	}
	
	public Color getBackground(Object element, int columnIndex){
		
		return null;
	}
}
