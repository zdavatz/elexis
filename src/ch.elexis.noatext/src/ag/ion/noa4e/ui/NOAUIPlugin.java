/****************************************************************************
 *                                                                          *
 * NOA (Nice Office Access)                                     						*
 * ------------------------------------------------------------------------ *
 *                                                                          *
 * The Contents of this file are made available subject to                  *
 * the terms of GNU Lesser General Public License Version 2.1.              *
 *                                                                          * 
 * GNU Lesser General Public License Version 2.1                            *
 * ======================================================================== *
 * Copyright 2003-2006 by IOn AG                                            *
 *                                                                          *
 * This library is free software; you can redistribute it and/or            *
 * modify it under the terms of the GNU Lesser General Public               *
 * License version 2.1, as published by the Free Software Foundation.       *
 *                                                                          *
 * This library is distributed in the hope that it will be useful,          *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of           *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU        *
 * Lesser General Public License for more details.                          *
 *                                                                          *
 * You should have received a copy of the GNU Lesser General Public         *
 * License along with this library; if not, write to the Free Software      *
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston,                    *
 * MA  02111-1307  USA                                                      *
 *                                                                          *
 * Contact us:                                                              *
 *  http://www.ion.ag																												*
 *  http://ubion.ion.ag                                                     *
 *  info@ion.ag                                                             *
 *                                                                          *
 ****************************************************************************/
 
/*
 * Last changes made by $Author: andreas $, $Date: 2006/08/29 09:54:38 $
 */
package ag.ion.noa4e.ui;

import ag.ion.noa4e.internal.ui.preferences.LocalOfficeApplicationPreferencesPage;

import ag.ion.noa4e.ui.operations.ActivateOfficeApplicationOperation;
import ag.ion.noa4e.ui.operations.FindApplicationInfosOperation;

import ag.ion.noa4e.ui.wizards.application.LocalApplicationWizard;

import ag.ion.bion.officelayer.application.IApplicationInfo;
import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.application.OfficeApplicationException;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import org.eclipse.ui.dialogs.PreferencesUtil;

import org.eclipse.ui.forms.widgets.FormToolkit;

import org.eclipse.ui.plugin.AbstractUIPlugin;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;

import org.eclipse.jface.preference.PreferenceDialog;

import org.eclipse.jface.resource.ImageDescriptor;

import org.eclipse.jface.wizard.WizardDialog;

import org.osgi.framework.BundleContext;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;

import ch.elexis.Hub;
import ch.elexis.preferences.PreferenceConstants;

import java.io.File;

import java.lang.reflect.InvocationTargetException;

import java.util.HashMap;
import java.util.Map;

/**
 * The main plugin class to be used in the desktop.
 * 
 * @author Andreas Bröcker
 * @version $Revision: 1.3 $
 * @date 28.06.2006
 */
public class NOAUIPlugin extends AbstractUIPlugin {

	/** ID of the plugin. */
	public static final String PLUGIN_ID = "ag.ion.noa4e.ui";
	
  /** Preferences key of the office home path. */
  public static final String PREFERENCE_OFFICE_HOME 				= "localOfficeApplicationPreferences.applicationPath";
  /** Preferences key of the prevent office termination information. */
  public static final String PREFERENCE_PREVENT_TERMINATION = "localOfficeApplicationPreferences.preventTermintation";
		
  private static final String ERROR_ACTIVATING_APPLICATION = Messages.NOAUIPlugin_message_application_can_not_be_activated;
	
	//The shared instance.
	private static NOAUIPlugin plugin;
	
	private static FormToolkit formToolkit = null;
	
  //----------------------------------------------------------------------------
	/**
	 * The constructor.
	 * 
	 * @author Andreas Bröcker
	 * @date 28.06.2006
	 */
	public NOAUIPlugin() {
		plugin = this;
	}
  //----------------------------------------------------------------------------
  /**
   * This method is called upon plug-in activation.
   * 
   * @param context bundle context
   * 
   * @throws Exception if the bundle can not be started
   * 
   * @author Andreas Bröcker
	 * @date 28.06.2006
   */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}
  //----------------------------------------------------------------------------
  /**
   * This method is called when the plug-in is stopped.
   * 
   * @param context bundle context
   * 
   * @throws Exception if the bundle can not be stopped
   * 
   * @author Andreas Bröcker
	 * @date 28.06.2006
   */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}
  //----------------------------------------------------------------------------
  /**
   * Returns the shared instance.
   * 
   * @return shared instance
   * 
   * @author Andreas Bröcker
	 * @date 28.06.2006
   */
	public static NOAUIPlugin getDefault() {
		return plugin;
	}
  //----------------------------------------------------------------------------
	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * 
	 * @return the image descriptor
	 * 
	 * @author Andreas Bröcker
	 * @date 28.06.2006
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("ag.ion.noa4e.ui", path);
	}
  //----------------------------------------------------------------------------
	/**
	 * Returns form toolkit.
	 * 
	 * @return form toolkit
	 * 
	 * @author Andreas Bröcker
	 * @date 28.06.2006
	 */
  public static FormToolkit getFormToolkit() {
    if(formToolkit == null) {
      formToolkit = new FormToolkit(Display.getCurrent());
      formToolkit.getColors().markShared();
    }
    return formToolkit;      
  } 	
  //----------------------------------------------------------------------------
  /**
   * Starts local office application.
   * 
   * @param shell shell to be used
   * @param officeApplication office application to be started
   * 
   * @return information whether the office application was started or not - only 
   * if the status severity is <code>IStatus.OK</code> the application was started 
   * 
   * @author Andreas Bröcker
	 * @date 28.06.2006
   */
  public static IStatus startLocalOfficeApplication(Shell shell, IOfficeApplication officeApplication) {        
    while(true) {
      IStatus status = internalStartApplication(shell, officeApplication);
      if(status.getSeverity() == IStatus.ERROR) {
        if(MessageDialog.openQuestion(shell, Messages.NOAUIPlugin_dialog_change_preferences_title, Messages.NOAUIPlugin_dialog_change_preferences_message)) {
          PreferenceDialog preferenceDialog = PreferencesUtil.createPreferenceDialogOn(shell, LocalOfficeApplicationPreferencesPage.PAGE_ID, null, null);
          if(preferenceDialog.open() == Dialog.CANCEL)
            return Status.CANCEL_STATUS;
          else
            continue;
        }
      }
      try {
        //boolean preventTermination = getDefault().getPreferenceStore().getBoolean(PREFERENCE_PREVENT_TERMINATION);
    	 boolean preventTermination=Hub.localCfg.get(LocalOfficeApplicationPreferencesPage.PREFS_PREVENT_TERMINATION, false);
        if(preventTermination)
          officeApplication.getDesktopService().activateTerminationPrevention();
    	} 
      catch (OfficeApplicationException officeApplicationException) {
        //no prevention
    	}
      return status;
    }
  }
  //----------------------------------------------------------------------------
  /**
   * Internal method in order to start the office application.
   * 
   * @param shell shell to be used
   * @param officeApplication office application to be used
   * 
   * @return status information
   * 
   * @author Andreas Bröcker
	 * @date 28.06.2006
   */
  private static IStatus internalStartApplication(final Shell shell, IOfficeApplication officeApplication) {
    if(officeApplication.isActive())
      return Status.OK_STATUS;
    
    boolean configurationChanged = false;
    boolean canStart = false;
    String home = null;
    
    HashMap configuration = new HashMap(1);  
    //String officeHome = getDefault().getPreferenceStore().getString(PREFERENCE_OFFICE_HOME); 
    String officeHome=Hub.localCfg.get(PreferenceConstants.P_OOBASEDIR, ".");
    if(officeHome.length() != 0) {
      File file = new File(officeHome);
      if(file.canRead()) {
        configuration.put(IOfficeApplication.APPLICATION_HOME_KEY, officeHome);
        canStart = true;
      }
      else {
        MessageDialog.openWarning(shell, Messages.NOAUIPlugin_dialog_warning_invalid_path_title, Messages.NOAUIPlugin_dialog_warning_invalid_path_message);
      }
    }    
    
    if(!canStart) {
      configurationChanged = true;
      IApplicationInfo[] applicationInfos = null;
      boolean configurationCompleted = false;
      try {
        ProgressMonitorDialog progressMonitorDialog = new ProgressMonitorDialog(shell);
        FindApplicationInfosOperation findApplicationInfosOperation = new FindApplicationInfosOperation();
        progressMonitorDialog.run(true, true, findApplicationInfosOperation);
        applicationInfos = findApplicationInfosOperation.getApplicationsInfos();        
        if(applicationInfos.length == 1) {
          if(applicationInfos[0].getMajorVersion() == 2 || 
              (applicationInfos[0].getMajorVersion() == 1 && applicationInfos[0].getMinorVersion() == 9)) {
            configuration.put(IOfficeApplication.APPLICATION_HOME_KEY, applicationInfos[0].getHome());
            configurationCompleted = true;
          }
        }        
      }
      catch(Throwable throwable) {
        //we must search manually
      }
      
      if(!configurationCompleted) {
        LocalApplicationWizard localApplicationWizard = new LocalApplicationWizard(applicationInfos);
        if(home != null && home.length() != 0)
          localApplicationWizard.setHomePath(home);
        WizardDialog wizardDialog = new WizardDialog(shell, localApplicationWizard);
        if(wizardDialog.open() == WizardDialog.CANCEL)
          return Status.CANCEL_STATUS;
        
        configuration.put(IOfficeApplication.APPLICATION_HOME_KEY, localApplicationWizard.getSelectedHomePath());
      }
    }      

    IStatus status = activateOfficeApplication(officeApplication, configuration, shell); 
    if(configurationChanged)
    	getDefault().getPluginPreferences().setValue(PREFERENCE_OFFICE_HOME, configuration.get(IOfficeApplication.APPLICATION_HOME_KEY).toString());

    return status;  
  }  
  //----------------------------------------------------------------------------
  /**
   * Activates office application.
   * 
   * @param officeApplication office application to be activated
   * @param configuration configuration to be used
   * @param shell shell to be used
   * 
   * @return status information of the activation
   *  
   * @author Andreas Bröcker
   * @date 28.08.2006
   */
  private static IStatus activateOfficeApplication(IOfficeApplication officeApplication, Map configuration, Shell shell) {
    IStatus status = Status.OK_STATUS;
    try {
      officeApplication.setConfiguration(configuration);      
      boolean useProgressMonitor = true;
      IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
      if(workbenchWindow == null)
        useProgressMonitor = false;
      else {
        IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();
        if(workbenchPage == null)
          useProgressMonitor = false;
      }
      ActivateOfficeApplicationOperation activateOfficeApplicationOperation = new ActivateOfficeApplicationOperation(officeApplication);
      if(useProgressMonitor) {
        ProgressMonitorDialog progressMonitorDialog = new ProgressMonitorDialog(shell);
        progressMonitorDialog.run(true, true, activateOfficeApplicationOperation);
      }
      else
        activateOfficeApplicationOperation.run(new NullProgressMonitor());
      if(activateOfficeApplicationOperation.getOfficeApplicationException() != null) {
        status = new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, 
            activateOfficeApplicationOperation.getOfficeApplicationException().getMessage(), activateOfficeApplicationOperation.getOfficeApplicationException());
        ErrorDialog.openError(shell, Messages.NOAUIPlugin_title_error, ERROR_ACTIVATING_APPLICATION, status);
      }        
    }
    catch(InvocationTargetException invocationTargetException) {
      status = new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, invocationTargetException.getMessage(), invocationTargetException);
      ErrorDialog.openError(shell, Messages.NOAUIPlugin_title_error, ERROR_ACTIVATING_APPLICATION, status);      
    }
    catch(OfficeApplicationException officeApplicationException) {
      status = new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, officeApplicationException.getMessage(), officeApplicationException);
      ErrorDialog.openError(shell, Messages.NOAUIPlugin_title_error, ERROR_ACTIVATING_APPLICATION, status);
    }  
    catch(InterruptedException interruptedException) {
      return Status.CANCEL_STATUS;
    }
    return status;
  }  
  //----------------------------------------------------------------------------
	
}