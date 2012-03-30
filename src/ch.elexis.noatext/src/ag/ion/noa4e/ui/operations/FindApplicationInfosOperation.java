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
 * Last changes made by $Author: andreas $, $Date: 2006/08/07 11:09:17 $
 */
package ag.ion.noa4e.ui.operations;

import ag.ion.bion.workbench.office.editor.core.EditorCorePlugin;

import ag.ion.bion.officelayer.application.IApplicationAssistant;
import ag.ion.bion.officelayer.application.IApplicationInfo;
import ag.ion.bion.officelayer.application.OfficeApplicationRuntime;

import org.eclipse.jface.operation.IRunnableWithProgress;

import org.eclipse.core.runtime.IProgressMonitor;

import java.lang.reflect.InvocationTargetException;

/**
 * Operation in order to find OpenOffice.org application infos.
 * 
 * @author Andreas Bröcker
 * @version $Revision: 1.2 $
 */
public class FindApplicationInfosOperation implements IRunnableWithProgress {

  private IApplicationInfo[] applicationInfos = IApplicationInfo.EMPTY_APPLICATION_INFOS_ARRAY;
  
  //----------------------------------------------------------------------------
  /**
   * Returns constructed application info objects.
   * 
   * @return constructed application info objects
   * 
   * @author Andreas Bröcker
   */
  public IApplicationInfo[] getApplicationsInfos() {
    return applicationInfos;
  }
  //----------------------------------------------------------------------------
  /**
   * Runs this operation.  Progress should be reported to the given progress monitor.
   * This method is usually invoked by an <code>IRunnableContext</code>'s <code>run</code> method,
   * which supplies the progress monitor.
   * A request to cancel the operation should be honored and acknowledged 
   * by throwing <code>InterruptedException</code>.
   *
   * @param progressMonitor the progress monitor to use to display progress and receive
   *   requests for cancelation
   *   
   * @exception InvocationTargetException if the run method must propagate a checked exception,
   *    it should wrap it inside an <code>InvocationTargetException</code>; runtime exceptions are automatically
   *  wrapped in an <code>InvocationTargetException</code> by the calling context
   * @exception InterruptedException if the operation detects a request to cancel, 
   *  using <code>IProgressMonitor.isCanceled()</code>, it should exit by throwing 
   *  <code>InterruptedException</code>
   *
   * @author Andreas Bröcker
   */
  public void run(IProgressMonitor progressMonitor) throws InvocationTargetException, InterruptedException {
    try {
      IApplicationAssistant applicationAssistant = OfficeApplicationRuntime.getApplicationAssistant(
          EditorCorePlugin.getDefault().getLibrariesLocation());
      OfficeProgressMonitor officeProgressMonitor = new OfficeProgressMonitor(progressMonitor);
      applicationInfos = applicationAssistant.getLocalApplications(officeProgressMonitor);
      progressMonitor.done();
    }
    catch(Throwable throwable) {
      progressMonitor.done();
    }    
  }
  //----------------------------------------------------------------------------
  
}