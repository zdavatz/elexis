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
 * $Id: DauerMediView.java 5970 2010-01-27 16:43:04Z rgw_ch $
 *******************************************************************************/
package ch.elexis.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import ch.elexis.Desk;
import ch.elexis.actions.ElexisEvent;
import ch.elexis.actions.ElexisEventDispatcher;
import ch.elexis.actions.ElexisEventListener;
import ch.elexis.actions.GlobalEventDispatcher;
import ch.elexis.actions.GlobalEventDispatcher.IActivationListener;
import ch.elexis.data.Patient;
import ch.elexis.util.SWTHelper;

/**
 * Eine platzsparende View zur Anzeige der Dauermedikation
 * 
 * @author gerry
 * 
 */
public class DauerMediView extends ViewPart implements IActivationListener, ElexisEventListener {
	public final static String ID = "ch.elexis.dauermedikationview"; //$NON-NLS-1$
	private IAction toClipBoardAction;
	FixMediDisplay dmd;
	
	public DauerMediView(){

	}
	
	@Override
	public void createPartControl(Composite parent){
		parent.setLayout(new GridLayout());
		dmd = new FixMediDisplay(parent, getViewSite());
		dmd.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		makeActions();
		getViewSite().getActionBars().getToolBarManager().add(toClipBoardAction);
		GlobalEventDispatcher.addActivationListener(this, this);
	}
	
	public void dispose(){
		GlobalEventDispatcher.removeActivationListener(this, this);
	}
	
	@Override
	public void setFocus(){
	// TODO Auto-generated method stub
	
	}
	
	public void activation(boolean mode){ /* leer */
	}
	
	public void visible(boolean mode){
		if (mode) {
			ElexisEventDispatcher.getInstance().addListeners(this);
			catchElexisEvent(template);
		} else {
			ElexisEventDispatcher.getInstance().removeListeners(this);
		}
		
	}
	
	private void makeActions(){
		toClipBoardAction = new Action(Messages.getString("DauerMediView.copy")) { //$NON-NLS-1$
				{
					setToolTipText(Messages.getString("DauerMediView.copyToClipboard")); //$NON-NLS-1$
					setImageDescriptor(Desk.getImageDescriptor(Desk.IMG_CLIPBOARD));
				}
				
				@Override
				public void run(){
					dmd.toClipBoard(true);
				}
				
			};
		
	}
	
	public void catchElexisEvent(ElexisEvent ev){
		Desk.asyncExec(new Runnable() {
			
			public void run(){
				dmd.reload();
			}
		});
	}
	
	private final ElexisEvent template =
		new ElexisEvent(null, Patient.class, ElexisEvent.EVENT_SELECTED
			| ElexisEvent.EVENT_DESELECTED);
	
	public ElexisEvent getElexisEventFilter(){
		return template;
	}
}
