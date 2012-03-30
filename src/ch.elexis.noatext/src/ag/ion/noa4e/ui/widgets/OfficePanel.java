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
 * Last changes made by $Author: markus $, $Date: 2007-03-19 11:50:22 +0100 (Mo, 19 Mrz 2007) $
 */
package ag.ion.noa4e.ui.widgets;

import ag.ion.bion.officelayer.application.IOfficeApplication;

import ag.ion.noa4e.ui.NOAUIPlugin;

import ag.ion.noa4e.ui.operations.AsyncProgressMonitorWrapper;
import ag.ion.noa4e.ui.operations.LoadDocumentOperation;

import ag.ion.bion.workbench.office.editor.core.EditorCorePlugin;

import ag.ion.bion.officelayer.desktop.IFrame;

import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.document.IDocumentDescriptor;

import org.eclipse.jface.wizard.ProgressMonitorPart;

import org.eclipse.osgi.framework.debug.Debug;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;

import org.eclipse.swt.awt.SWT_AWT;

import org.eclipse.swt.custom.StackLayout;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Link;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;

import ch.elexis.util.SWTHelper;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Panel;

import java.lang.reflect.InvocationTargetException;

import java.net.URL;

/**
 * The office panel can be used in order to integrate the OpenOffice.org User Interface into the SWT
 * environment.
 * 
 * @author Andreas Bröcker
 * @version $Revision: 11473 $
 * @date 28.06.2006
 */
public class OfficePanel extends Composite {
	
	private IOfficeApplication officeApplication = null;
	
	private IFrame officeFrame = null;
	private StackLayout stackLayout = null;
	private Frame officeAWTFrame = null;
	private ProgressMonitorPart progressMonitorPart = null;
	
	private Composite baseComposite = null;
	private Composite progressComposite = null;
	private Composite officeComposite = null;
	
	private IDocument document = null;
	private IStatus lastLoadingStatus = null;
	
	private Thread loadingThread = null;
	
	private String currentDocumentPath = null;
	
	private boolean buildAlwaysNewFrames = false;
	private boolean showProgressIndicator = true;
	
	// ----------------------------------------------------------------------------
	/**
	 * Constructs new OfficePanel.
	 * 
	 * @param parent
	 *            parent to be used
	 * @param style
	 *            style to be used
	 * 
	 * @author Andreas Bröcker
	 * @date 28.06.2006
	 */
	public OfficePanel(Composite parent, int style){
		super(parent, style);
		officeApplication = getOfficeApplication();
		buildControls();
	}
	
	// ----------------------------------------------------------------------------
	/**
	 * Returns current office frame. Returns null if an office frame is not available.
	 * 
	 * @return current office frame or null if an office frame is not available
	 * 
	 * @author Andreas Bröcker
	 * @date 28.06.2006
	 */
	public IFrame getFrame(){
		return officeFrame;
	}
	
	// ----------------------------------------------------------------------------
	/**
	 * Returns current document. Returns null if a document is not available.
	 * 
	 * @return current document. Returns null if a document is not available.
	 * 
	 * @author Markus Krüger
	 * @date 19.03.2007
	 */
	public IDocument getDocument(){
		return document;
	}
	
	// ----------------------------------------------------------------------------
	/**
	 * Sets focus to the office panel.
	 * 
	 * @author Andreas Bröcker
	 * @date 28.06.2006
	 */
	public boolean setFocus(){
		if (officeFrame != null) {
			officeFrame.setFocus();
			return true;
		}
		return super.setFocus();
	}
	
	// ----------------------------------------------------------------------------
	/**
	 * Sets information whether a new frame should be builded for new loaded documents. The default
	 * value is <code>false</code>.
	 * 
	 * @param buildAlwaysNewFrames
	 *            information whether a new frame should be builded for new loaded documents
	 * 
	 * @author Andreas Bröcker
	 * @date 28.06.2006
	 */
	public void setBuildAlwaysNewFrames(boolean buildAlwaysNewFrames){
		this.buildAlwaysNewFrames = buildAlwaysNewFrames;
	}
	
	// ----------------------------------------------------------------------------
	/**
	 * Sets information whether a progress indicator should be shown during document loading. The
	 * default value is <code>true</code>.
	 * 
	 * @param showProgressIndicator
	 *            information whether a progress indicator should be shown during document loading
	 * 
	 * @author Andreas Bröcker
	 * @date 28.06.2006
	 */
	public void showProgressIndicator(boolean showProgressIndicator){
		this.showProgressIndicator = showProgressIndicator;
	}
	
	// ----------------------------------------------------------------------------
	/**
	 * Loads document into the office panel.
	 * 
	 * @param fork
	 *            information whether the loading should be done in an own thread
	 * @param documentPath
	 *            path of the document to be loaded
	 * @param documentDescriptor
	 *            document descriptor to be used
	 * 
	 * @author Andreas Bröcker
	 * @date 28.06.2006
	 */
	public final void loadDocument(boolean fork, final String documentPath,
		final IDocumentDescriptor documentDescriptor){
		if (isDisposed())
			return;
		
		if (documentPath != null
			&& (currentDocumentPath == null || !currentDocumentPath.equals(documentPath))) {
			try {
				currentDocumentPath = documentPath;
				if (document != null && buildAlwaysNewFrames)
					document.close();
				
				if (officeFrame == null || buildAlwaysNewFrames)
					officeFrame = activateNewFrame();
				
				if (!fork) {
					IProgressMonitor progressMonitor = getProgressMonitor();
					if (showProgressIndicator)
						showProgressIndicator();
					loadDocument(documentPath, documentDescriptor, progressMonitor);
					if (document != null)
						lastLoadingStatus = Status.OK_STATUS;
					if (showProgressIndicator) {
						hideProgressIndicator();
						showOfficeFrame();
					}
				} else {
					final Display display = Display.getCurrent();
					loadingThread = new Thread() {
						AsyncProgressMonitorWrapper asyncProgressMonitorWrapper = null;
						
						public void run(){
							display.asyncExec(new Runnable() {
								public void run(){
									if (!isDisposed())
										if (showProgressIndicator)
											showProgressIndicator();
								}
							});
							
							asyncProgressMonitorWrapper =
								new AsyncProgressMonitorWrapper(getProgressMonitor(), getDisplay());
							try {
								loadDocument(documentPath, documentDescriptor,
									asyncProgressMonitorWrapper);
								if (document != null)
									lastLoadingStatus = Status.OK_STATUS;
								display.asyncExec(new Runnable() {
									public void run(){
										if (showProgressIndicator) {
											hideProgressIndicator();
											showOfficeFrame();
										}
									}
								});
							} catch (CoreException coreException) {
								if (showProgressIndicator) {
									hideProgressIndicator();
									showOfficeFrame();
								}
								lastLoadingStatus = coreException.getStatus();
							}
						}
					};
					loadingThread.start();
				}
			} catch (Throwable throwable) {
				if (showProgressIndicator) {
					hideProgressIndicator();
					showOfficeFrame();
				}
				lastLoadingStatus =
					new Status(IStatus.ERROR, NOAUIPlugin.PLUGIN_ID, IStatus.ERROR, throwable
						.getMessage(), throwable);
			}
		}
	}
	
	// ----------------------------------------------------------------------------
	/**
	 * Disposes the office panel.
	 * 
	 * @author Andreas Bröcker
	 * @date 28.06.2006
	 */
	public void dispose(){
		if (officeFrame != null) {
			try {
				officeFrame.close();
			} catch (Throwable throwable) {
				// do not consume
			}
		}
		super.dispose();
	}
	
	// ----------------------------------------------------------------------------
	/**
	 * Returns status of the last document loading. Returns null if a status is not available.
	 * 
	 * @return status of the last document loading or null if a status is not available
	 * 
	 * @author Andreas Bröcker
	 * @date 28.06.2006
	 */
	public IStatus getLastLoadingStatus(){
		return lastLoadingStatus;
	}
	
	// ----------------------------------------------------------------------------
	/**
	 * Sets the layout which is associated with the receiver to be the argument which may be null.
	 * 
	 * @param layout
	 *            the receiver's new layout or null
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created
	 *                the receiver</li>
	 *                </ul>
	 * 
	 * @author Andreas Bröcker
	 * @date 28.06.2006
	 */
	public void setLayout(Layout layout){}
	
	// ----------------------------------------------------------------------------
	/**
	 * Is called after a document loading operation was done. This method can be overwriten by
	 * subclasses in order to do some work after a document loading operation was done.
	 * 
	 * @author Andreas Bröcker
	 * @date 28.06.2006
	 */
	protected void documentLoadingOperationDone(){}
	
	// ----------------------------------------------------------------------------
	/**
	 * Returns progress monitor. Subclasses can overwrite this method in order to provide their own
	 * progress monitor.
	 * 
	 * @return progress monitor
	 * 
	 * @author Andreas Bröcker
	 * @date 28.06.2006
	 */
	protected IProgressMonitor getProgressMonitor(){
		if (progressMonitorPart != null)
			return progressMonitorPart;
		return new NullProgressMonitor();
	}
	
	// ----------------------------------------------------------------------------
	/**
	 * Returns office application.
	 * 
	 * @return office application
	 * 
	 * @author Andreas Bröcker
	 * @date 28.06.2006
	 */
	protected IOfficeApplication getOfficeApplication(){
		return EditorCorePlugin.getDefault().getManagedLocalOfficeApplication();
	}
	
	// ----------------------------------------------------------------------------
	/**
	 * Starts office application.
	 * 
	 * @param officeApplication
	 *            office application to be started
	 * 
	 * @return information whether the office application was started
	 * 
	 * @author Andreas Bröcker
	 * @date 28.06.2006
	 */
	protected IStatus startOfficeApplication(IOfficeApplication officeApplication){
		return NOAUIPlugin.startLocalOfficeApplication(getShell(), officeApplication);
	}
	
	// ----------------------------------------------------------------------------
	/**
	 * Builds progress indicator. Subclasses can overwrite this method in order to provide their own
	 * progress indicator.
	 * 
	 * @param parent
	 *            parent to be used
	 * 
	 * @author Andreas Bröcker
	 * @date 28.06.2006
	 */
	protected void buildProgressIndicator(Composite parent){
		progressComposite = new Composite(parent, SWT.EMBEDDED);
		progressComposite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY));
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginLeft = -5;
		gridLayout.marginBottom = -5;
		gridLayout.marginRight = -5;
		progressComposite.setLayout(gridLayout);
		
		Composite composite = new Composite(progressComposite, SWT.EMBEDDED);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY));
		
		Composite progressIndicator = new Composite(progressComposite, SWT.EMBEDDED);
		GridData gridData = new GridData(SWT.FILL, SWT.NONE, true, false);
		gridData.verticalAlignment = SWT.CENTER;
		progressIndicator.setLayoutData(gridData);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		progressIndicator.setLayout(gridLayout);
		progressMonitorPart = new ProgressMonitorPart(progressIndicator, null);
		gridData = new GridData(SWT.FILL, SWT.NONE, true, false);
		gridData.verticalAlignment = SWT.CENTER;
		progressMonitorPart.setLayoutData(gridData);
		
		Link linkCancel = new Link(progressIndicator, SWT.FLAT);
		linkCancel.setText("<a>" + Messages.OfficePanel_link_text_cancel + "</a>"); //$NON-NLS-1$ //$NON-NLS-3$
		progressMonitorPart.attachToCancelComponent(linkCancel);
	}
	
	// ----------------------------------------------------------------------------
	/**
	 * Shows progress indicator. Subclasses can overwrite this method in order to show their own
	 * progress indicator.
	 * 
	 * @author Andreas Bröcker
	 * @date 28.06.2006
	 */
	protected void showProgressIndicator(){
		if (progressComposite == null)
			buildProgressIndicator(baseComposite);
		stackLayout.topControl = progressComposite;
		baseComposite.layout();
	}
	
	// ----------------------------------------------------------------------------
	/**
	 * Hides the progress indicator. Subclasses can overwrite this method in order to hide their own
	 * progress indicator.
	 * 
	 * @author Andreas Bröcker
	 * @date 28.06.2006
	 */
	protected void hideProgressIndicator(){}
	
	// ----------------------------------------------------------------------------
	/**
	 * Loads document.
	 * 
	 * @param documentPath
	 *            document path to be used
	 * @param documentDescriptor
	 *            document descriptor to be used
	 * @param progressMonitor
	 *            progress monitor to be used
	 * 
	 * @throws CoreException
	 *             if the document can not be loaded
	 * 
	 * @author Andreas Bröcker
	 * @date 28.06.2006
	 */
	private void loadDocument(String documentPath, IDocumentDescriptor documentDescriptor,
		IProgressMonitor progressMonitor) throws CoreException{
		URL url = convertToURL(documentPath);
		try {
			LoadDocumentOperation loadDocumentOperation =
				new LoadDocumentOperation(null, officeApplication, officeFrame, url,
					documentDescriptor);
			loadDocumentOperation.run(progressMonitor);
			document = loadDocumentOperation.getDocument();
			if (document == null) {
				Exception ex = loadDocumentOperation.getException();
				// something went wrong --> inform the user (or at least the supporter/developer)
				String errMsg = "loadDocumentOperation.getDocument() failed"; //$NON-NLS-2$
				if (ex != null) {
					errMsg = ex.getMessage();
				}
				SWTHelper.showError("Error", errMsg + "\n" + url.toString()); //$NON-NLS-2$
			}
		} catch (InvocationTargetException invocationTargetException) {
			documentLoadingOperationDone();
			throw new CoreException(new Status(IStatus.ERROR, NOAUIPlugin.PLUGIN_ID, IStatus.ERROR,
				invocationTargetException.getCause().getMessage(), invocationTargetException
					.getCause()));
		} catch (InterruptedException interruptedException) {
			// the operation was aborted
		}
		documentLoadingOperationDone();
	}
	
	// ----------------------------------------------------------------------------
	/**
	 * Shows office frame.
	 * 
	 * @author Andreas Bröcker
	 * @date 28.06.2006
	 */
	private void showOfficeFrame(){
		if (!baseComposite.isDisposed()) {
			stackLayout.topControl = officeComposite;
			baseComposite.layout();
			officeComposite.layout();
		}
	}
	
	// ----------------------------------------------------------------------------
	/**
	 * Builds controls of the office panel.
	 * 
	 * @author Andreas Bröcker
	 * @date 28.06.2006
	 */
	private void buildControls(){
		super.setLayout(new GridLayout());
		baseComposite = new Composite(this, SWT.EMBEDDED);
		baseComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		stackLayout = new StackLayout();
		stackLayout.marginHeight = -5;
		stackLayout.marginWidth = -5;
		baseComposite.setLayout(stackLayout);
		baseComposite.setBackground(this.getParent().getBackground());
		
		if (!showProgressIndicator)
			buildProgressIndicator(this);
	}
	
	// ----------------------------------------------------------------------------
	/**
	 * Activates a new office frame.
	 * 
	 * @return new builded office frame
	 * 
	 * @throws CoreException
	 *             if a new office frame can not be activated
	 * 
	 * @author Andreas Bröcker
	 * @date 28.06.2006
	 */
	private IFrame activateNewFrame() throws CoreException{
		Control oldOfficeComposite = stackLayout.topControl;
		Frame oldOfficeAWTFrame = officeAWTFrame;
		
		officeComposite = new Composite(baseComposite, SWT.EMBEDDED);
		officeComposite.setBackground(this.getBackground());
		officeComposite.setLayout(new GridLayout());
		try {
			officeAWTFrame = SWT_AWT.new_Frame(officeComposite);
			officeAWTFrame.setVisible(true);
			officeAWTFrame.setBackground(Color.GRAY);
			Panel officeAWTPanel = new Panel();
			officeAWTPanel.setLayout(new BorderLayout());
			officeAWTPanel.setVisible(true);
			officeAWTFrame.add(officeAWTPanel);
			if (!officeApplication.isActive()) {
				IStatus status = startOfficeApplication(officeApplication);
				if (status.getSeverity() == IStatus.ERROR)
					throw new CoreException(status);
			}
			
			if (isDisposed())
				throw new CoreException(new Status(IStatus.ERROR, NOAUIPlugin.PLUGIN_ID,
					IStatus.ERROR, "Widget disposed", null)); //$NON-NLS-1$
			IFrame newOfficeFrame =
				officeApplication.getDesktopService().constructNewOfficeFrame(officeAWTFrame);
			
			if (oldOfficeAWTFrame != null)
				oldOfficeAWTFrame.dispose();
			if (oldOfficeComposite != null)
				oldOfficeComposite.dispose();
			
			stackLayout.topControl = officeComposite;
			baseComposite.layout();
			return newOfficeFrame;
		} catch (Throwable throwable) {
			throw new CoreException(new Status(IStatus.ERROR, NOAUIPlugin.PLUGIN_ID, IStatus.ERROR,
				throwable.getMessage(), throwable));
		}
	}
	
	// ----------------------------------------------------------------------------
	/**
	 * Converts the submitted document path an URL.
	 * 
	 * @param documentPath
	 *            document path to be used
	 * 
	 * @return converted document path
	 * 
	 * @throws CoreException
	 * 
	 * @author Andreas Bröcker
	 * @date 28.06.2006
	 */
	private URL convertToURL(String documentPath) throws CoreException{
		try {
			/*
			if (Debug.DEBUG) { //$NON-NLS-1$ //$NON-NLS-2$
				return new URL("file:/" + documentPath); //$NON-NLS-1$
			}
			*/
			if (System.getProperty("os.name").toLowerCase().indexOf("windows") != -1) { //$NON-NLS-1$ //$NON-NLS-2$
				return new URL("file://///" + documentPath); //$NON-NLS-1$
			}
			return new URL("file:////" + documentPath); //$NON-NLS-1$
		} catch (Throwable throwable) {
			throw new CoreException(new Status(IStatus.ERROR, NOAUIPlugin.PLUGIN_ID, IStatus.ERROR,
				throwable.getMessage(), throwable));
		}
	}
	// ----------------------------------------------------------------------------
	
}