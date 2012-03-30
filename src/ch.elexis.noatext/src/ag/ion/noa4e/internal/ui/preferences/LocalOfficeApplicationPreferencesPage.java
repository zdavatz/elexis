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
 * Last changes made by $Author: andreas $, $Date: 2006/08/07 11:09:18 $
 */
package ag.ion.noa4e.internal.ui.preferences;

import ag.ion.bion.workbench.office.editor.core.EditorCorePlugin;

import ag.ion.bion.officelayer.application.IApplicationAssistant;
import ag.ion.bion.officelayer.application.IApplicationInfo;
import ag.ion.bion.officelayer.application.IApplicationProperties;
import ag.ion.bion.officelayer.application.OfficeApplicationRuntime;

import ag.ion.noa4e.ui.NOAUIPlugin;

import ag.ion.noa4e.ui.wizards.application.LocalApplicationWizard;

import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;

import org.eclipse.ui.forms.widgets.FormToolkit;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;

import org.eclipse.jface.resource.JFaceResources;

import org.eclipse.jface.viewers.TableLayout;

import org.eclipse.jface.wizard.WizardDialog;

import org.eclipse.swt.SWT;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import ch.elexis.Hub;
import ch.elexis.preferences.PreferenceConstants;
import ch.elexis.preferences.SettingsPreferenceStore;

import java.util.Arrays;
import java.util.TreeSet;

/**
 * Preferences page for local OpenOffice.org application.
 * 
 * @author Andreas Bröcker
 * @author Markus Krüger
 * @version $Revision: 1.2 $
 */
public class LocalOfficeApplicationPreferencesPage extends PreferencePage implements IWorkbenchPreferencePage {
  
  /** ID of the page. */
  public static final String PAGE_ID = "ag.ion.noa4e.ui.preferences.LocalOfficeApplicationPreferencePage"; //$NON-NLS-1$
  public static final String PREFS_PREVENT_TERMINATION="openoffice/preventTermination";
  
  private Text    textHome            			= null;
  private Button  buttonPreventTermination  = null;
  
  private Table tableApplicationProperties = null;
  
  //----------------------------------------------------------------------------
  /**
   * Initializes this preference page for the given workbench.
   * 
   * @param workbench workbnech to be used
   * 
   * @author Andreas Bröcker
   */
  public void init(IWorkbench workbench) {   
    setDescription(Messages.LocalOfficeApplicationPreferencesPage_description_configure_application);
  }
  //----------------------------------------------------------------------------
  /**
   * Creates and returns the SWT control for the customized body of this preference 
   * page under the given parent composite. 
   * 
   * @param parent the parent composite
   * 
   * @return constructed control
   * 
   * @author Andreas Bröcker
   * @author Markus Krüger
   */
  protected Control createContents(Composite parent) {
    FormToolkit formToolkit = NOAUIPlugin.getFormToolkit();
    Composite composite = new Composite(parent, SWT.NULL);
    
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 3;    
    composite.setLayout(gridLayout);    

    Label labelHome = formToolkit.createLabel(composite, Messages.LocalOfficeApplicationPreferencesPage_label_application_home);
    labelHome.setBackground(composite.getBackground());
    
    textHome = formToolkit.createText(composite, ""); //$NON-NLS-1$
    textHome.setEditable(false);
    textHome.setFont(composite.getFont());
    GridData gridData = new GridData(SWT.FILL, SWT.NONE, true, false);
    textHome.setLayoutData(gridData);    
    
    final Link linkDefine = new Link(composite, SWT.NONE);
    linkDefine.setText("<a>" + Messages.LocalOfficeApplicationPreferencesPage_link_define_text + "</a>");     //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    linkDefine.addSelectionListener(new SelectionAdapter(){
      public void widgetSelected(SelectionEvent selectionEvent) {       
        LocalApplicationWizard localApplicationWizard = new LocalApplicationWizard();
        String oldHome = textHome.getText();
        if(oldHome.length() != 0)
          localApplicationWizard.setHomePath(oldHome);
        WizardDialog wizardDialog = new WizardDialog(linkDefine.getShell(), localApplicationWizard);        
        if(wizardDialog.open() == Dialog.OK) {
          String home = localApplicationWizard.getSelectedHomePath();
          if(home != null)
            textHome.setText(home);
          initApplicationProperties(tableApplicationProperties);
        }
      }      
    });    
    
    Label labelNull= formToolkit.createLabel(composite, ""); //$NON-NLS-1$
    gridData = new GridData();
    gridData.horizontalSpan = 3;
    labelNull.setLayoutData(gridData);
    
    Label labelProperties = formToolkit.createLabel(composite, Messages.LocalOfficeApplicationPreferencesPage_label_application_properties_text);
    labelProperties.setBackground(composite.getBackground());
    labelProperties.setFont(JFaceResources.getFontRegistry().getBold(labelProperties.getFont().toString()));
    gridData = new GridData(SWT.FILL, SWT.NONE, true, false);
    gridData.horizontalSpan = 3;
    labelProperties.setLayoutData(gridData);
    
    tableApplicationProperties = formToolkit.createTable(composite, SWT.READ_ONLY);
    gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
    gridData.horizontalSpan =3;
    int tableWidth = (int)(tableApplicationProperties.getDisplay().getClientArea().width * 0.3);
    gridData.widthHint = tableWidth;
    tableApplicationProperties.setLayoutData(gridData);
    
    TableLayout tableLayout = new TableLayout();    
    tableApplicationProperties.setLayout(tableLayout);
   
    TableColumn columnProduct = new TableColumn(tableApplicationProperties, SWT.NONE);
    columnProduct.setText(Messages.LocalOfficeApplicationPreferencesPage_column_name_text);
    int columnProductWidth = (int)(tableWidth * 0.4);
    columnProduct.setWidth(columnProductWidth);
    
    TableColumn columnHome = new TableColumn(tableApplicationProperties, SWT.NONE);
    columnHome.setText(Messages.LocalOfficeApplicationPreferencesPage_column_value_text);
    columnHome.setWidth(tableWidth - columnProductWidth);
    
    tableApplicationProperties.setLinesVisible(true);
    tableApplicationProperties.setHeaderVisible(true);
    
    buttonPreventTermination = formToolkit.createButton(composite, Messages.LocalOfficeApplicationPreferencesPage_prevent_termination_lable, SWT.CHECK);
    buttonPreventTermination.setBackground(composite.getBackground());
    gridData = new GridData();
    gridData.horizontalSpan = 3;
    buttonPreventTermination.setLayoutData(gridData);  
    
    formToolkit.paintBordersFor(composite);
    initPreferenceValues(); 
    initApplicationProperties(tableApplicationProperties);
    return composite; 
  } 
  //----------------------------------------------------------------------------
  /**
   * Notifies that the OK button of this page's container has been pressed. 
   * 
   * @return false to abort the container's OK processing and true to allow 
   * the OK to happen
   * 
   * @author Andreas Bröcker
   * @author Markus Krüger
   */
  public boolean performOk() {    
    IPreferenceStore preferenceStore = new SettingsPreferenceStore(Hub.localCfg);
    preferenceStore.setValue(PREFS_PREVENT_TERMINATION, buttonPreventTermination.getSelection());
    
    String oldPath = preferenceStore.getString(PreferenceConstants.P_OOBASEDIR);
    preferenceStore.setValue(PreferenceConstants.P_OOBASEDIR, textHome.getText());
    
    super.performOk(); 
    if(oldPath.length() != 0 || !oldPath.equals(textHome.getText())) {
      if(EditorCorePlugin.getDefault().getManagedLocalOfficeApplication().isActive()) {
        if(MessageDialog.openQuestion(getShell(), Messages.LocalOfficeApplicationPreferencesPage_dialog_restart_workbench_title, Messages.LocalOfficeApplicationPreferencesPage_dialog_restart_workbench_message))
          NOAUIPlugin.getDefault().getWorkbench().restart();
      }   
    }
    return true;
  } 
  //----------------------------------------------------------------------------
  /**
   * Inits application properties.
   * 
   * @param table table to be used
   * 
   * @author Andreas Bröcker
   */
  private void initApplicationProperties(Table table) {
    try {
      TableItem[] tableItems = table.getItems();
      for(int i=0, n=tableItems.length; i<n; i++) {
        tableItems[i].dispose();
      }
      
      IApplicationAssistant applicationAssistant = OfficeApplicationRuntime.getApplicationAssistant(
          EditorCorePlugin.getDefault().getLibrariesLocation());
      IApplicationInfo applicationInfo = applicationAssistant.findLocalApplicationInfo(textHome.getText());
      if(applicationInfo != null) {
        IApplicationProperties applicationProperties = applicationInfo.getProperties();
        if(applicationProperties != null) {
          String[] names = applicationProperties.getPropertyNames();
          TreeSet treeSet = new TreeSet(Arrays.asList(names));
          names = (String[])treeSet.toArray(new String[treeSet.size()]);
          for(int i=0, n=names.length; i<n; i++) {
            String name = names[i];
            String value = applicationProperties.getPropertyValue(name);
            if(value != null && value.length() != 0) {
              TableItem tableItem = new TableItem(table, SWT.NONE);
              tableItem.setText(0, name);
              tableItem.setText(1, value);
            }
          }
        }
      }
    }
    catch(Throwable throwable) {
      //do not consume
    }    
  }  
  //----------------------------------------------------------------------------
  /**
   * Inits all preference values.
   * 
   * @author Andreas Bröcker
   * @author Markus Krüger
   * 
   * Adapted for Elexis
   * G. Weirich, 6/07
   */
  private void initPreferenceValues() {
	  IPreferenceStore preferenceStore=new SettingsPreferenceStore(Hub.localCfg);
	  String officeHomePath=preferenceStore.getString(PreferenceConstants.P_OOBASEDIR);
	  boolean preventTermination=preferenceStore.getBoolean(PREFS_PREVENT_TERMINATION);
	  /*
  	IPreferenceStore preferenceStore = NOAUIPlugin.getDefault().getPreferenceStore();
  	String officeHomePath = preferenceStore.getString(NOAUIPlugin.PREFERENCE_OFFICE_HOME);
  	boolean preventTermination = preferenceStore.getBoolean(NOAUIPlugin.PREFERENCE_PREVENT_TERMINATION); 
  	*/
    textHome.setText(officeHomePath); 
    buttonPreventTermination.setSelection(preventTermination);
	 
  }
  //----------------------------------------------------------------------------
  /**
   * Returns information whether this preferences page is valid.
   * 
   * @return information whether this preferences page is valid
   * 
   * @author Andreas Bröcker
   */
  public boolean isValid() {
    return true;
  }  
  //----------------------------------------------------------------------------

}