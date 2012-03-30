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
 *  $Id: PatientMenuPopulator.java 5873 2009-12-17 22:51:30Z rgw_ch $
 *******************************************************************************/
package ch.elexis.views;

import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import ch.elexis.Hub;
import ch.elexis.actions.RestrictedAction;
import ch.elexis.admin.AccessControlDefaults;
import ch.elexis.data.Patient;
import ch.elexis.dialogs.AssignStickerDialog;
import ch.elexis.exchange.IDataSender;
import ch.elexis.exchange.XChangeException;
import ch.elexis.util.Extensions;
import ch.elexis.util.SWTHelper;
import ch.elexis.util.ViewMenus.IMenuPopulator;
import ch.rgw.tools.ExHandler;

public class PatientMenuPopulator implements IMenuPopulator {
	IAction exportKGAction, delPatAction, stickerAction;
	PatientenListeView mine;
	
	public IAction[] fillMenu(){
		LinkedList<IAction> ret = new LinkedList<IAction>();
		ret.add(stickerAction);
		if (Hub.acl.request(AccessControlDefaults.KONTAKT_DELETE)) {
			ret.add(delPatAction);
		}
		if (Hub.acl.request(AccessControlDefaults.KONTAKT_EXPORT)) {
			ret.add(exportKGAction);
		}
		delPatAction.setEnabled(Hub.acl.request(AccessControlDefaults.KONTAKT_DELETE));
		exportKGAction.setEnabled(Hub.acl.request(AccessControlDefaults.KONTAKT_EXPORT));
		return ret.toArray(new IAction[0]);
	}
	
	PatientMenuPopulator(PatientenListeView plv){
		mine = plv;
		stickerAction =
			new RestrictedAction(AccessControlDefaults.KONTAKT_ETIKETTE, Messages
				.getString("PatientMenuPopulator.StickerAction")) { //$NON-NLS-1$
				{
					setToolTipText(Messages.getString("PatientMenuPopulator.StickerToolTip")); //$NON-NLS-1$
				}
				
				@Override
				public void doRun(){
					Patient p = mine.getSelectedPatient();
					AssignStickerDialog aed = new AssignStickerDialog(Hub.getActiveShell(), p);
					aed.open();
				}
				
			};
		delPatAction = new Action(Messages.getString("PatientMenuPopulator.DeletePatientAction")) { //$NON-NLS-1$
				@Override
				public void run(){
					// access rights guard
					if (!Hub.acl.request(AccessControlDefaults.KONTAKT_DELETE)) {
						SWTHelper
							.alert(
								Messages
									.getString("PatientMenuPopulator.DeletePatientRefusalCaption"), Messages.getString("PatientMenuPopulator.DeletePatientRefusalBody")); //$NON-NLS-1$ //$NON-NLS-2$
						return;
					}
					
					Patient p = mine.getSelectedPatient();
					if (p != null) {
						if (MessageDialog.openConfirm(mine.getViewSite().getShell(), Messages
							.getString("PatientMenuPopulator.DeletePatientConfirm"), p.getLabel()) == true) { //$NON-NLS-1$
							if (p.delete(false) == false) {
								SWTHelper.alert(Messages
									.getString("PatientMenuPopulator.DeletePatientRejectCaption"), //$NON-NLS-1$
									Messages
										.getString("PatientMenuPopulator.DeletePatientRejectBody")); //$NON-NLS-1$
							} else {
								mine.reload();
							}
						}
					}
				}
				
			};
		exportKGAction =
			new Action(
				Messages.getString("PatientMenuPopulator.ExportEMRAction"), Action.AS_DROP_DOWN_MENU) { //$NON-NLS-1$
				Menu menu = null;
				{
					setToolTipText(Messages.getString("PatientMenuPopulator.ExportEMRToolTip")); //$NON-NLS-1$
					setMenuCreator(new IMenuCreator() {
						
						public void dispose(){
							if (menu != null) {
								menu.dispose();
								menu = null;
							}
							
						}
						
						public Menu getMenu(Control parent){
							menu = new Menu(parent);
							createMenu();
							return menu;
						}
						
						public Menu getMenu(Menu parent){
							menu = new Menu(parent);
							createMenu();
							return menu;
						}
						
					});
				}
				
				void createMenu(){
					Patient p = mine.getSelectedPatient();
					if (p != null) {
						List<IConfigurationElement> list =
							Extensions.getExtensions("ch.elexis.Transporter"); //$NON-NLS-1$
						for (final IConfigurationElement ic : list) {
							String name = ic.getAttribute("name"); //$NON-NLS-1$
							System.out.println(name);
							String handler = ic.getAttribute("AcceptableTypes"); //$NON-NLS-1$
							if (handler == null) {
								continue;
							}
							if (handler.contains("ch.elexis.data.Patient") //$NON-NLS-1$
								|| (handler.contains("ch.elexis.data.*"))) { //$NON-NLS-1$
								MenuItem it = new MenuItem(menu, SWT.NONE);
								it.setText(ic.getAttribute("name")); //$NON-NLS-1$
								it.addSelectionListener(new SelectionAdapter() {
									@Override
									public void widgetSelected(SelectionEvent e){
										Patient pat = mine.getSelectedPatient();
										try {
											IDataSender sender =
												(IDataSender) ic
													.createExecutableExtension("ExporterClass"); //$NON-NLS-1$
											sender.store(pat);
											sender.finalizeExport();
											SWTHelper
												.showInfo(
													Messages
														.getString("PatientMenuPopulator.EMRExported"),//$NON-NLS-1$ 
													MessageFormat
														.format(
															Messages
																.getString("PatientMenuPopulator.ExportEmrSuccess"), //$NON-NLS-1$
															pat.getLabel()));
										} catch (CoreException e1) {
											ExHandler.handle(e1);
										} catch (XChangeException xx) {
											SWTHelper
												.showError(
													Messages
														.getString("PatientMenuPopulator.ErrorCaption"), //$NON-NLS-1$ 
													MessageFormat
														.format(
															Messages
																.getString("PatientMenuPopulator.ExportEmrFailure"), //$NON-NLS-1$
															pat.getLabel()));
											
										}
									}
								});
								
							}
						}
					}
				}
			};
	}
}
