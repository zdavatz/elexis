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
 *  $Id: ETFDropReceiver.java 6135 2010-02-13 11:03:49Z rgw_ch $
 *******************************************************************************/
package ch.elexis.text;

import java.util.Hashtable;

import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.graphics.Point;

import ch.elexis.Desk;
import ch.elexis.actions.ElexisEventDispatcher;
import ch.elexis.data.Konsultation;
import ch.elexis.data.PersistentObject;
import ch.elexis.util.IKonsExtension;
import ch.elexis.util.PersistentObjectDropTarget.IReceiver;

public class ETFDropReceiver implements IReceiver {
	EnhancedTextField etf;
	
	Hashtable<Class<?>, IKonsExtension> targets;
	
	ETFDropReceiver(final EnhancedTextField et){
		etf = et;
		targets = new Hashtable<Class<?>, IKonsExtension>();
	}
	
	public void addReceiver(final Class<?> clazz, final IKonsExtension rec){
		targets.put(clazz, rec);
	}
	
	public void removeReceiver(final Class<?> clazz, final IKonsExtension rec){
		targets.remove(clazz);
	}
	
	public boolean accept(final PersistentObject o){
		/*
		 * if(targets.get(o.getClass())!=null){ return true; } return false;
		 */
		return true;
	}
	
	public void dropped(final PersistentObject o, final DropTargetEvent ev){
		Point point = Desk.getDisplay().getCursorLocation();
		Point mapped = Desk.getDisplay().map(null, etf.text, point);
		Point maxOffset = etf.text.getLocationAtOffset(etf.text.getCharCount());
		int pos = etf.text.getCharCount();
		if (mapped.y < maxOffset.y) {
			pos = etf.text.getOffsetAtLocation(new Point(0, mapped.y));
		}
		IKonsExtension rec = targets.get(o.getClass());
		if (rec != null) {
			rec.insert(o, pos);
		} else {
			Konsultation actKons =
				(Konsultation) ElexisEventDispatcher.getSelected(Konsultation.class);
			if (actKons != null) {
				etf.text.insert(o.getLabel());
				actKons.updateEintrag(etf.getContentsAsXML(), false);
			}
			
		}
		
	}
	
}
