/*******************************************************************************
 * Copyright (c) 2006-2010, G. Weirich, D. Lutz, P. Schönbucher and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 * 
 *  $Id: FaelleView.java 6005 2010-01-31 10:49:59Z rgw_ch $
 *******************************************************************************/

package ch.elexis.views;

import static ch.elexis.actions.GlobalActions.delFallAction;
import static ch.elexis.actions.GlobalActions.makeBillAction;
import static ch.elexis.actions.GlobalActions.neuerFallAction;
import static ch.elexis.actions.GlobalActions.openFallaction;
import static ch.elexis.actions.GlobalActions.reopenFallAction;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.IFilter;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import ch.elexis.Desk;
import ch.elexis.actions.ElexisEvent;
import ch.elexis.actions.ElexisEventDispatcher;
import ch.elexis.actions.ElexisEventListenerImpl;
import ch.elexis.actions.GlobalEventDispatcher;
import ch.elexis.actions.ObjectFilterRegistry;
import ch.elexis.actions.GlobalEventDispatcher.IActivationListener;
import ch.elexis.actions.ObjectFilterRegistry.IObjectFilterProvider;
import ch.elexis.data.Fall;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Patient;
import ch.elexis.util.SWTHelper;
import ch.elexis.util.ViewMenus;
import ch.elexis.util.viewers.DefaultLabelProvider;

/**
 * Eine alternative, platzsparendere Fälle-View
 */
public class FaelleView extends ViewPart implements IActivationListener {
	public static final String ID = "ch.elexis.schoebufaelle"; //$NON-NLS-1$
	TableViewer tv;
	ViewMenus menus;
	private IAction konsFilterAction;
	private final FallKonsFilter filter = new FallKonsFilter();
	
	private final ElexisEventListenerImpl eeli_pat = new ElexisEventListenerImpl(Patient.class) {
		public void runInUi(ElexisEvent ev){
			tv.refresh();
		}
	};
	
	private final ElexisEventListenerImpl eeli_fall =
		new ElexisEventListenerImpl(Fall.class, ElexisEvent.EVENT_CREATE | ElexisEvent.EVENT_DELETE
			| ElexisEvent.EVENT_RELOAD | ElexisEvent.EVENT_SELECTED | ElexisEvent.EVENT_UPDATE) {
			
			public void runInUi(final ElexisEvent ev){
				if (ev.getType() == ElexisEvent.EVENT_SELECTED) {
					tv.refresh(true);
					if (konsFilterAction.isChecked()) {
						filter.setFall((Fall) ev.getObject());
					}
				} else {
					tv.refresh(true);
				}
			}
		};
	
	public FaelleView(){
		makeActions();
	}
	
	@Override
	public void createPartControl(final Composite parent){
		setPartName(Messages.getString("FaelleView.partName")); //$NON-NLS-1$
		parent.setLayout(new GridLayout());
		tv = new TableViewer(parent);
		tv.getControl().setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		tv.setContentProvider(new FaelleContentProvider());
		tv.setLabelProvider(new FaelleLabelProvider());
		tv.addSelectionChangedListener(GlobalEventDispatcher.getInstance().getDefaultListener());
		menus = new ViewMenus(getViewSite());
		menus.createToolbar(neuerFallAction, konsFilterAction);
		menus.createViewerContextMenu(tv, delFallAction, openFallaction, reopenFallAction,
			makeBillAction);
		GlobalEventDispatcher.addActivationListener(this, this);
		tv.setInput(getViewSite());
	}
	
	@Override
	public void dispose(){
		GlobalEventDispatcher.removeActivationListener(this, this);
		super.dispose();
	}
	
	@Override
	public void setFocus(){
	// TODO Auto-generated method stub
	
	}
	
	class FaelleLabelProvider extends DefaultLabelProvider {
		
		@Override
		public Image getColumnImage(final Object element, final int columnIndex){
			if (element instanceof Fall) {
				Fall fall = (Fall) element;
				if (fall.isValid()) {
					return Desk.getImage(Desk.IMG_OK);
				} else {
					return Desk.getImage(Desk.IMG_FEHLER);
				}
			}
			return super.getColumnImage(element, columnIndex);
		}
		
	}
	
	class FaelleContentProvider implements IStructuredContentProvider {
		
		public Object[] getElements(final Object inputElement){
			Patient act = (Patient) ElexisEventDispatcher.getSelected(Patient.class);
			if (act == null) {
				return new Object[0];
			} else {
				return act.getFaelle();
			}
			
		}
		
		public void dispose(){
		// TODO Auto-generated method stub
		
		}
		
		public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput){

		}
		
	}
	
	public void activation(final boolean mode){

	}
	
	public void visible(final boolean mode){
		if (mode) {
			tv.refresh(true);
			ElexisEventDispatcher.getInstance().addListeners(eeli_fall, eeli_pat);
		} else {
			ElexisEventDispatcher.getInstance().removeListeners(eeli_fall, eeli_pat);
		}
	}
	
	private void makeActions(){
		konsFilterAction = new Action(Messages.getString("FaelleView.FilterConsultations"), //$NON-NLS-1$
			Action.AS_CHECK_BOX) {
			{
				setToolTipText(Messages.getString("FaelleView.ShowOnlyConsOfThisCase")); //$NON-NLS-1$
				setImageDescriptor(Desk.getImageDescriptor(Desk.IMG_FILTER));
			}
			
			@Override
			public void run(){
				if (!isChecked()) {
					ObjectFilterRegistry.getInstance().unregisterObjectFilter(Konsultation.class,
						filter);
				} else {
					ObjectFilterRegistry.getInstance().registerObjectFilter(Konsultation.class,
						filter);
					filter.setFall((Fall) ElexisEventDispatcher.getSelected(Fall.class));
				}
			}
			
		};
	}
	
	class FallKonsFilter implements IObjectFilterProvider, IFilter {
		
		Fall mine;
		boolean bDaempfung;
		
		void setFall(final Fall fall){
			mine = fall;
			ElexisEventDispatcher.reload(Konsultation.class);
		}
		
		public void activate(){
			bDaempfung = true;
			konsFilterAction.setChecked(true);
			bDaempfung = false;
		}
		
		public void changed(){
		// don't mind
		}
		
		public void deactivate(){
			bDaempfung = true;
			konsFilterAction.setChecked(false);
			bDaempfung = false;
		}
		
		public IFilter getFilter(){
			return this;
		}
		
		public String getId(){
			return "ch.elexis.FallFilter"; //$NON-NLS-1$
		}
		
		public boolean select(final Object toTest){
			if (mine == null) {
				return true;
			}
			if (toTest instanceof Konsultation) {
				Konsultation k = (Konsultation) toTest;
				if (k.getFall().equals(mine)) {
					return true;
				}
			}
			return false;
		}
		
	}
	/*
	 * private final ElexisEvent template = new ElexisEvent(null, null, ElexisEvent.EVENT_SELECTED |
	 * ElexisEvent.EVENT_DESELECTED | ElexisEvent.EVENT_RELOAD);
	 * 
	 * public ElexisEvent getElexisEventFilter(){ return template; }
	 */
}
