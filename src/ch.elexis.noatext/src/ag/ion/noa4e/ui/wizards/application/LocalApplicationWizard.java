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
package ag.ion.noa4e.ui.wizards.application;

import ag.ion.bion.officelayer.application.IApplicationInfo;

import ag.ion.noa4e.ui.NOAUIPluginImages;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.Wizard;

/**
 * Wizard in order to define the path of a local OpenOffice.org application.
 * 
 * @author Andreas Bröcker
 * @version $Revision: 1.2 $
 */
public class LocalApplicationWizard extends Wizard implements IWizard {

  private LocalApplicationWizardDefinePage localApplicationWizardDefinePage = null;
  
  private IApplicationInfo[] applicationInfos = null;
  
  private String homePath = null;
  
  //----------------------------------------------------------------------------
  /**
   * Constructs new LocalApplicationWizard.
   * 
   * @author Andreas Bröcker
   */
  public LocalApplicationWizard() {
    this(null);
    setNeedsProgressMonitor(true);
  }
  //----------------------------------------------------------------------------
  /**
   * Constructs new LocalApplicationWizard.
   * 
   * @param applicationInfos application infos to be used (can be null)
   * 
   * @author Andreas Bröcker
   */
  public LocalApplicationWizard(IApplicationInfo[] applicationInfos) {
    this.applicationInfos = applicationInfos;
   
    setWindowTitle(Messages.LocalApplicationWizard_title);
    
    //setDefaultPageImageDescriptor(NOAUIPluginImages.getImageDescriptor(NOAUIPluginImages.IMG_WIZBAN_APPLICATION));
  }
  //----------------------------------------------------------------------------  
  /**
   * Sets home path to be edited.
   * 
   * @param homePath home path to be edited
   * 
   * @author Andreas Bröcker
   */
  public void setHomePath(String homePath) {
    this.homePath = homePath;
  }  
  //----------------------------------------------------------------------------
  /**
   * Returns selected home path of an local office application. Returns null
   * if a home path is not available.
   * 
   * @return selected home path of an local office application or null
   * if a home path is not available
   * 
   * @author Andreas Bröcker
   */
  public String getSelectedHomePath() {
    if(localApplicationWizardDefinePage != null)
      return localApplicationWizardDefinePage.getSelectedHomePath();
    return null;
  }
  //----------------------------------------------------------------------------
  /**
   * Performs any actions appropriate in response to the user 
   * having pressed the Finish button, or refuse if finishing
   * now is not permitted.
   *
   * @return <code>true</code> to indicate the finish request
   *   was accepted, and <code>false</code> to indicate
   *   that the finish request was refused
   * 
   * @author Andreas Bröcker
   */
  public boolean performFinish() {
    if(localApplicationWizardDefinePage.getSelectedHomePath() != null)
      return true;
    return false;
  }
  //----------------------------------------------------------------------------
  /**
   * Adds any last-minute pages to this wizard.
   * 
   * @author Andreas Bröcker
   */
  public void addPages() {
    localApplicationWizardDefinePage = new LocalApplicationWizardDefinePage(homePath, applicationInfos);
    addPage(localApplicationWizardDefinePage);
  }
  //----------------------------------------------------------------------------
  
}