/*******************************************************************************
 * Copyright (c) 2005-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 * $Id$
 *******************************************************************************/
package ch.elexis.views;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.ISaveablePart2;
import org.eclipse.ui.part.ViewPart;

import ch.elexis.Desk;
import ch.elexis.Hub;
import ch.elexis.actions.ElexisEvent;
import ch.elexis.actions.ElexisEventDispatcher;
import ch.elexis.actions.ElexisEventListenerImpl;
import ch.elexis.actions.GlobalActions;
import ch.elexis.actions.GlobalEventDispatcher;
import ch.elexis.actions.GlobalEventDispatcher.IActivationListener;
import ch.elexis.commands.EditEigenartikelUi;
import ch.elexis.data.Artikel;
import ch.elexis.data.PersistentObject;
import ch.elexis.dialogs.ArtikelDetailDialog;
import ch.elexis.preferences.PreferenceConstants;
import ch.elexis.util.viewers.CommonViewer;
import ch.elexis.util.viewers.DefaultContentProvider;
import ch.elexis.util.viewers.ViewerConfigurer;
import ch.elexis.util.viewers.CommonViewer.DoubleClickListener;
import ch.elexis.util.viewers.ViewerConfigurer.WidgetProvider;

public class LagerView extends ViewPart implements DoubleClickListener, ISaveablePart2,
		IActivationListener {
	public static final String ID = "ch.elexis.LagerView"; //$NON-NLS-1$
	CommonViewer cv;
	ViewerConfigurer vc;
	ElexisEventListenerImpl eeli_article =
		new ElexisEventListenerImpl(Artikel.class, ElexisEvent.EVENT_RELOAD) {
			public void catchElexisEvent(ElexisEvent ev){
				cv.notify(CommonViewer.Message.update);
			}
		};
	
	@Override
	public void createPartControl(Composite parent){
		parent.setLayout(new GridLayout());
		cv = new CommonViewer();
		vc = new ViewerConfigurer(new DefaultContentProvider(cv, Artikel.class) {
			@Override
			public Object[] getElements(Object inputElement){
				/*
				 * Query<Artikel> qbe=new Query<Artikel>(Artikel.class); qbe.startGroup();
				 * qbe.add("Minbestand","<>","0"); qbe.or(); qbe.add("Maxbestand","<>","0");
				 * qbe.endGroup(); //cv.getConfigurer().getControlFieldProvider ().setQuery(qbe);
				 * List<Artikel> l=qbe.execute();
				 */

				return Artikel.getLagerartikel().toArray();
			}
			
		}, new LagerLabelProvider() {}, null, // new DefaultControlFieldProvider(cv,new
			// String[]{"Name","Lieferant"}),
			new ViewerConfigurer.DefaultButtonProvider(), new LagerWidgetProvider());
		cv.create(vc, parent, SWT.NONE, getViewSite());
		cv.getConfigurer().getContentProvider().startListening();
		cv.addDoubleClickListener(this);
		GlobalEventDispatcher.addActivationListener(this, this);
	}
	
	@Override
	public void setFocus(){
	// cv.getConfigurer().getControlFieldProvider().setFocus();
	}
	
	@Override
	public void dispose(){
		cv.getConfigurer().getContentProvider().stopListening();
		cv.removeDoubleClickListener(this);
		GlobalEventDispatcher.removeActivationListener(this, this);
		super.dispose();
	}
	
	class LagerLabelProvider extends LabelProvider implements ITableLabelProvider,
			ITableColorProvider {
		
		public Image getColumnImage(Object element, int columnIndex){
			// TODO Auto-generated method stub
			return null;
		}
		
		public String getColumnText(Object element, int columnIndex){
			if (element instanceof Artikel) {
				Artikel art = (Artikel) element;
				switch (columnIndex) {
				case 0:
					return art.getPharmaCode();
				case 1:
					return art.getLabel();
				case 2:
					return Integer.toString(art.getIstbestand());
				case 3:
					return Integer.toString(art.getMinbestand());
				case 4:
					return Integer.toString(art.getMaxbestand());
				case 5:
					return Integer.toString(art.getIstbestand()); // TODO
					// Kontrolle
				case 6:
					return art.getLieferant().getLabel();
				default:
					return ""; //$NON-NLS-1$
				}
			} else {
				if (columnIndex == 0) {
					return element.toString();
				}
				return ""; //$NON-NLS-1$
				
			}
			
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
	
	class LagerWidgetProvider implements WidgetProvider {
		String[] columns =
			{
				Messages.getString("LagerView.pharmacode"), Messages.getString("LagerView.name"), Messages.getString("LagerView.istBestand"), Messages.getString("LagerView.minBestand"), Messages.getString("LagerView.maxBestand"), Messages.getString("LagerView.controlled"), Messages.getString("LagerView.dealer") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
			};
		int[] colwidth = {
			60, 300, 40, 40, 40, 40, 200
		};
		
		public StructuredViewer createViewer(Composite parent){
			Table table = new Table(parent, SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.SINGLE);
			for (int i = 0; i < columns.length; i++) {
				TableColumn tc = new TableColumn(table, SWT.LEFT);
				tc.setText(columns[i]);
				tc.setWidth(colwidth[i]);
				tc.setData(i);
				tc.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e){
						cv.getViewerWidget()
							.setSorter(
								new LagerTableSorter((Integer) ((TableColumn) e.getSource())
									.getData()));
					}
					
				});
				
			}
			table.setHeaderVisible(true);
			table.setLinesVisible(true);
			TableViewer ret = new TableViewer(table);
			ret.setSorter(new LagerTableSorter(1));
			return ret;
		}
		
		class LagerTableSorter extends ViewerSorter {
			int col;
			
			LagerTableSorter(int c){
				col = c;
			}
			
			@Override
			public int compare(Viewer viewer, Object e1, Object e2){
				String s1 =
					((LagerLabelProvider) cv.getConfigurer().getLabelProvider()).getColumnText(e1,
						col);
				String s2 =
					((LagerLabelProvider) cv.getConfigurer().getLabelProvider()).getColumnText(e2,
						col);
				return s1.compareTo(s2);
			}
			
		}
	}
	
	public void doubleClicked(PersistentObject obj, CommonViewer cv){
		EditEigenartikelUi.executeWithParams(obj);
	}
	
	public void reloadContents(Class clazz){
		if (clazz.equals(Artikel.class)) {
			cv.notify(CommonViewer.Message.update);
		}
		
	}
	
	/***********************************************************************************************
	 * Die folgenden 6 Methoden implementieren das Interface ISaveablePart2 Wir benötigen das
	 * Interface nur, um das Schliessen einer View zu verhindern, wenn die Perspektive fixiert ist.
	 * Gibt es da keine einfachere Methode?
	 */
	public int promptToSaveOnClose(){
		return GlobalActions.fixLayoutAction.isChecked() ? ISaveablePart2.CANCEL
				: ISaveablePart2.NO;
	}
	
	public void doSave(IProgressMonitor monitor){ /* leer */
	}
	
	public void doSaveAs(){ /* leer */
	}
	
	public boolean isDirty(){
		return true;
	}
	
	public boolean isSaveAsAllowed(){
		return false;
	}
	
	public boolean isSaveOnCloseNeeded(){
		return true;
	}
	
	public void activation(boolean mode){
	// TODO Auto-generated method stub
	
	}
	
	public void visible(boolean mode){
		if (mode) {
			ElexisEventDispatcher.getInstance().addListeners(eeli_article);
			eeli_article.catchElexisEvent(new ElexisEvent(null, Artikel.class,
				ElexisEvent.EVENT_RELOAD));
		} else {
			ElexisEventDispatcher.getInstance().removeListeners(eeli_article);
		}
		
	}
}
