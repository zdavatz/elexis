/*******************************************************************************
 * Copyright (c) 2005-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    N. Giger - Bypass Login Dialog for development environments
 *    
 *  $Id$
 *******************************************************************************/

package ch.elexis;

import java.io.File;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import ch.elexis.Hub.ShutdownJob;
import ch.elexis.actions.GlobalActions;
import ch.elexis.core.PersistenceException;
import ch.elexis.data.Anwender;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Reminder;
import ch.elexis.util.Log;
import ch.elexis.wizards.DBConnectWizard;
import ch.rgw.io.FileTool;
import ch.rgw.tools.ExHandler;

/**
 * Dies ist eine Eclipse-spezifische Klasse Wichtigste Funktion ist das Festlegen der initialen
 * Perspektive In eventloopException können spezifische Verarbeitungen für nicht abgefangene
 * Exceptions definiert werden (Hier einfach Ausgabe). In eventLoopIdle können Arbeiten eingetragen
 * werden, die immer dann zu eredigen sind, wenn das Programm nichts weiter zu tun hat.
 */
public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {
	
	private static final String PERSPECTIVE_ID = PatientPerspektive.ID;
	private Shell loginshell;
	
	@Override
	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
		final IWorkbenchWindowConfigurer configurer){
		return new ApplicationWorkbenchWindowAdvisor(configurer);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.ui.application.WorkbenchAdvisor#initialize(org.eclipse.ui.application.
	 * IWorkbenchConfigurer)
	 */
	@Override
	public void initialize(final IWorkbenchConfigurer configurer){
		loginshell = new Shell(Desk.getDisplay());
		Log.setAlert(loginshell);
		try {
			if (PersistentObject.connect(Hub.localCfg, loginshell) == false)
				handlePersistenceException();
		} catch (PersistenceException pe) {
			handlePersistenceException();
		}
		
		// Hub.localCfg=new SqlSettings(PersistentObject.getConnection(), "CLIENTCONFIG", "param",
		// "value", "Station='"+NetTool.hostname+"'");
		// look whether we have do to some work before creating the workbench
		try {
			final Class<?> up = Class.forName("ch.elexis.PreStartUpdate"); //$NON-NLS-1$
			Hub.log.log("Found PreStartUpdate, executing", Log.SYNCMARK); //$NON-NLS-1$
			// Object psu=up.newInstance();
			// psu=null;
			Hub.addShutdownJob(new ShutdownJob() {
				
				public void doit() throws Exception{
					File file = new File(FileTool.getBasePath(up), "PreStartUpdate.class"); //$NON-NLS-1$
					if (file.delete()) {
						Hub.log.log("Deleted PreStartUpdate successfully", Log.SYNCMARK); //$NON-NLS-1$
					} else {
						Hub.log.log("Could not delete PreStartUpdate", Log.ERRORS); //$NON-NLS-1$
					}
				}
			});
		} catch (ClassNotFoundException cnf) {
			// nothing
		} catch (Exception ex) {
			Hub.log.log("Error executing PreStartUpdate " + ex.getMessage(), Log.ERRORS); //$NON-NLS-1$
		}
		
		Hub.pin.initializeDisplayPreferences(Desk.getDisplay());
		configurer.setSaveAndRestore(true);
		Log.setAlert(null);
		super.initialize(configurer);
		loginshell.dispose();
	}
	
	@Override
	public void postStartup(){
		Shell shell = getWorkbenchConfigurer().getWorkbench().getActiveWorkbenchWindow().getShell();
		Log.setAlert(shell);
		String username = System.getProperty("ch.elexis.username");
		String password = System.getProperty("ch.elexis.password");
		if (username != null && password != null) {
			Log log = Log.get("LoginDialog"); //$NON-NLS-1$
			log.log("Bypassing LoginDialg username " + username + " " + password, Log.ERRORS);
			if (!Anwender.login(username, password)) {
				log.log("Authentication failed. Exiting", Log.FATALS);
			}
		} else {
			LoginDialog dlg = new LoginDialog(shell);
			dlg.create();
			dlg.getShell().setText(Messages.ApplicationWorkbenchAdvisor_7);
			dlg.setTitle(Messages.ApplicationWorkbenchAdvisor_8);
			dlg.setMessage(Messages.ApplicationWorkbenchAdvisor_9);
			dlg.open();
		}
		
		// check if there is a valid user
		if ((Hub.actUser == null) || !Hub.actUser.isValid()) {
			// no valid user, exit (don't consider this as an error)
			Hub.log.log("Exit because no valid user logged-in", Log.WARNINGS); //$NON-NLS-1$
			PersistentObject.disconnect();
			System.exit(0);
		}
		
		List<Reminder> reminderList = Reminder.findToShowOnStartup(Hub.actUser);
		if(reminderList.size() > 0) {
			StringBuilder sb = new StringBuilder();
			for (Reminder reminder : reminderList) {
				sb.append(reminder.getMessage()).append("\n\n"); //$NON-NLS-1$		
			}

			MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getShell(),
				ch.elexis.views.Messages.getString("ReminderView.importantRemindersOnLogin"),
				sb.toString());
		}
		
	}
	
	@Override
	public String getInitialWindowPerspectiveId(){
		return PERSPECTIVE_ID;
	}
	
	@Override
	public void eventLoopException(final Throwable exception){
		Hub.log.log(Messages.ApplicationWorkbenchAdvisor_10 + exception.getMessage(), Log.ERRORS);
		exception.printStackTrace();
		ExHandler.handle(exception);
		super.eventLoopException(exception);
	}
	
	@Override
	public void eventLoopIdle(final Display display){
		super.eventLoopIdle(display);
	}
	
	@Override
	public boolean preShutdown(){
		GlobalActions.fixLayoutAction.setChecked(false);
		return super.preShutdown();
	}
	
	@Override
	public void postShutdown(){
		Hub.postShutdown();
		super.postShutdown();
	}
	
	private void handlePersistenceException(){
		Log.setAlertLevel(Log.ERRORS);
		Hub.log.log(Messages.ApplicationWorkbenchAdvisor_0
			+ PersistentObject.getConnection().lastErrorString, Log.ERRORS);
		MessageDialog.openError(loginshell, Messages.ApplicationWorkbenchAdvisor_1,
			Messages.ApplicationWorkbenchAdvisor_2
				+ PersistentObject.getConnection().lastErrorString);
		PersistentObject.disconnect();
		WizardDialog wd = new WizardDialog(loginshell, new DBConnectWizard());
		wd.open();
		Hub.localCfg.flush();
		System.exit(-1);
	}
}
