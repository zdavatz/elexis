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

import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.application.OfficeApplicationException;

import ag.ion.bion.officelayer.runtime.IOfficeProgressMonitor;

import ag.ion.noa4e.ui.NOAUIPlugin;

import org.eclipse.jface.operation.IRunnableWithProgress;

import org.eclipse.swt.awt.SWT_AWT;

import org.eclipse.core.runtime.IProgressMonitor;

import java.lang.reflect.InvocationTargetException;

/**
 * Operation in order to activate OpenOffice.org application.
 * 
 * @author Andreas Bröcker
 * @version $Revision: 1.2 $
 */
public class ActivateOfficeApplicationOperation implements IRunnableWithProgress {

  private IOfficeApplication officeApplication = null;
  
  private InternalThread internalThread = null;
  
  private IOfficeProgressMonitor officeProgressMonitor = null;
  
  //----------------------------------------------------------------------------
  /**
   * Internal thread class in order start the office application.
   * 
   * @author Andreas Bröcker
   */
  private class InternalThread extends Thread {
       
    private OfficeApplicationException officeApplicationException = null;
    
    private boolean done = false;
    
    //----------------------------------------------------------------------------
    /**
     * Executes thread logic.
     * 
     * @author Andreas Bröcker
     */
    public void run() {
      try {
        officeApplication.activate(officeProgressMonitor);
        done = true;
      }
      catch(OfficeApplicationException officeApplicationException) {
        this.officeApplicationException = officeApplicationException;
      }
    }
    //----------------------------------------------------------------------------
    /**
     * Returns OfficeApplicationException exception. Returns null if
     * no exception was thrown.
     * 
     * @return OfficeApplicationException - returns null if
     * no exception was thrown
     * 
     * @author Andreas Bröcker
     */
    public OfficeApplicationException getOfficeApplicationException() {
      return officeApplicationException;
    }
    //----------------------------------------------------------------------------
    /**
     * Returns information whether the thread has finished his
     * work.
     * 
     * @return information whether the thread has finished his
     * work
     * 
     * @author Andreas Bröcker
     */
    public boolean done() {
      if(officeApplicationException != null) 
        return true;
      return done;
    }
    //----------------------------------------------------------------------------
  }
  //----------------------------------------------------------------------------
    
  //----------------------------------------------------------------------------
  /**
   * Constructs new StartOfficeApplicationOperation.
   * 
   * @param officeApplication office application to be started
   * 
   * @author Andreas Bröcker
   */
  public ActivateOfficeApplicationOperation(IOfficeApplication officeApplication) {
  	assert officeApplication != null;
    this.officeApplication = officeApplication;
  }
  //----------------------------------------------------------------------------
  /**
   * Returns OfficeApplicationException exception. Returns null if
   * no exception was thrown.
   * 
   * @return OfficeApplicationException - returns null if
   * no exception was thrown
   * 
   * @author Andreas Bröcker
   */
  public OfficeApplicationException getOfficeApplicationException() {
    return internalThread.getOfficeApplicationException();
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
   *  it should wrap it inside an <code>InvocationTargetException</code>; runtime exceptions are automatically
   *  wrapped in an <code>InvocationTargetException</code> by the calling context
   * @exception InterruptedException if the operation detects a request to cancel, 
   *  using <code>IProgressMonitor.isCanceled()</code>, it should exit by throwing 
   *  <code>InterruptedException</code>
   * 
   * @author Andreas Bröcker
   */
  public void run(IProgressMonitor progressMonitor) throws InvocationTargetException, InterruptedException {
    /**
     * Tricky workaround. The OpenOffice.org OfficeBean library can not find the JRE/ JDK library
     * jawt itself. The class SWT_AWT uses this native library too. Therefore code from SWT_AWT is used
     * to load the library.
     */
    String os = System.getProperty("os.name"); //$NON-NLS-1$
    if(os.toLowerCase().indexOf("windows") != -1) { //$NON-NLS-1$
      try {
        SWT_AWT.new_Shell(NOAUIPlugin.getDefault().getWorkbench().getDisplay(), new java.awt.Canvas());
      }
      catch(Throwable throwable) {
        //do nothing
      }
    }
    
    internalThread = new InternalThread();
    officeProgressMonitor = new OfficeProgressMonitor(progressMonitor);
    internalThread.start();      
    while(!internalThread.done()) {      
      Thread.sleep(150);   
      if(progressMonitor.isCanceled())
        throw new InterruptedException(Messages.ActivateOfficeApplicationOperation_exception_message_interrupted);
    }
    
    if(progressMonitor.isCanceled())
      throw new InterruptedException(Messages.ActivateOfficeApplicationOperation_exception_message_interrupted);

    progressMonitor.done();    
  }  
  //----------------------------------------------------------------------------
 
}