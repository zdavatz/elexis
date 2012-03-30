/*******************************************************************************
 * Copyright (c) 2006-2011, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    M. Descher - initial implementation
 *******************************************************************************/
package ch.elexis.coolbar;

import java.util.List;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.PlatformUI;

import ch.elexis.Desk;
import ch.elexis.Hub;
import ch.elexis.actions.ElexisEvent;
import ch.elexis.actions.ElexisEventDispatcher;
import ch.elexis.actions.ElexisEventListenerImpl;
import ch.elexis.data.Mandant;
import ch.elexis.preferences.PreferenceConstants;

/**
 * This class implements the {@link Mandant} selection button bar within the application toolbar
 * (coolbar). The list of colors represents the available colors to distinguish the currently
 * selected mandant.
 */
public class MandantSelectionContributionItem extends ContributionItem {
	
	
	private ToolItem item;
	private Menu menu;
	private Mandant[] mandants;
	private MenuItem[] menuItems;
	private ToolBar fParent;
		
	private ElexisEventListenerImpl eeli_mandant = new ElexisEventListenerImpl(Mandant.class,
		ElexisEvent.EVENT_MANDATOR_CHANGED) {
		public void runInUi(ElexisEvent ev){
			ICoolBarManager icb = ((ApplicationWindow) PlatformUI.getWorkbench().getActiveWorkbenchWindow()).getCoolBarManager2();
			Mandant m = (Mandant) ev.getObject();
			if (m != null && m.getMandantLabel() != null && item != null) {
				item.setText(m.getMandantLabel());
				fParent.setBackground(getColorForMandator(m));
				for (int i = 0; i < menuItems.length; i++) {
					String id = (String) menuItems[i].getData();
					if (m.getId().equalsIgnoreCase(id)) {
						// fParent.setBackground(Display.getCurrent().getSystemColor(colors[i %
// cl]));
						fParent.pack();
						// TODO: Anordnung Elemente in Coolbar speicherbar?
						// TODO: Programmatische Anordnung Elemente coolbar
						menuItems[i].setSelection(true);
						icb.update(true);
					} else {
						menuItems[i].setSelection(false);
					}
				}
			}
		}
	};
	
	public MandantSelectionContributionItem(){
		ElexisEventDispatcher.getInstance().addListeners(eeli_mandant);
	}
	
	@Override
	public void fill(ToolBar parent, int index){
		List<Mandant> qre = Hub.getMandantenList();
		mandants = qre.toArray(new Mandant[] {});
		if(mandants.length<2) return;
			
		fParent = parent;
		menu = new Menu(fParent);
		item = new ToolItem(parent, SWT.DROP_DOWN);
		item.setToolTipText("Aktuell ausgewählter Mandant bzw. Mandantenauswahl");
		
		menuItems = new MenuItem[mandants.length];
		
		for (int i = 0; i < mandants.length; i++) {
			final Mandant m = mandants[i];
			menuItems[i] = new MenuItem(menu, SWT.RADIO);
			menuItems[i].setText(m.getMandantLabel());
			menuItems[i].setImage(getBoxSWTColorImage(getColorForMandator(m)));
			menuItems[i].setData(m.getId());
			menuItems[i].addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e){
					Hub.setMandant(m);
				}
			});
		}
		

		item.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event){
				if (event.detail == SWT.ARROW || event.type == SWT.Selection) {
					Rectangle rect = item.getBounds();
					Point pt = new Point(rect.x, rect.y + rect.height);
					pt = fParent.toDisplay(pt);
					menu.setLocation(pt.x, pt.y);
					menu.setVisible(true);
				}
			}
		});
	}
	
	private Image getBoxSWTColorImage(Color color){
		Display display = Display.getCurrent();
		Image image = new Image(display, 16, 16);
		GC gc = new GC(image);
		gc.setBackground(color);
		gc.fillRoundRectangle(0, 0, 16, 16, 8, 8);
		gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
		gc.dispose();
		return image;
	}
	
	private Color getColorForMandator(Mandant m){
		return Desk.getColorFromRGB(Hub.globalCfg.get(
			PreferenceConstants.USR_MANDATOR_COLORS_PREFIX + m.getLabel(), Desk.COL_GREY60));
	}

	@Override
	public void dispose(){
		ElexisEventDispatcher.getInstance().removeListeners(eeli_mandant);
	}
}
