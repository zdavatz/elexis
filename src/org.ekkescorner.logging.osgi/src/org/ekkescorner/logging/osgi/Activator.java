/*******************************************************************************
 * Copyright (c) 2006 - 2010 ekke (ekkehard gentz) rosenheim germany.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * License: EPL (Eclipse Public License)
 * http://ekkes-corner.org, http://gentz-software.de
 * mailto: ekke[at]ekkes-corner.org
 * twitter: [at]ekkescorner
 * 
 * Contributors:
 *    ekke (ekkehard gentz) - initial API and implementation 
 * 
 ***********************************************copyright 2006 - 2010**********/
package org.ekkescorner.logging.osgi;

import org.ekkescorner.logging.osgi.LoggingConstants;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;

/**
 * <code>Activator</code>
 * 
 * @author ekke
 * 
 */
public class Activator implements BundleActivator {
	
	private BundleContext context = null;
	
	// logger name = "org.ekkehard.osgi.over.slf4j.Activator"
	private static final Logger logger = LoggerFactory.getLogger(Activator.class);
	
	// The bundle ID (Bundle-SymbolicName)
	public static final String ID = "org.ekkescorner.logging.starter.runtime"; //$NON-NLS-1$
	
	// The Bundle Marker: a Marker where the name is the osgi bundle symbolic name
	// and an attached IS_BUNDLE - Marker to guarantee that the Log Framework knows its a
// BundleMarker
	public static final Marker bundleMarker = createBundleMarker();
	
	private static final Marker createBundleMarker(){
		Marker bundleMarker = MarkerFactory.getMarker(ID);
		bundleMarker.add(MarkerFactory.getMarker(LoggingConstants.IS_BUNDLE_MARKER));
		return bundleMarker;
	}
	
	/**
	 * 
	 * Implements <code>BundleActivator.start()</code>
	 * 
	 * @param context
	 *            the framework context for the bundle
	 * @throws Exception
	 */
	public void start(BundleContext context) throws Exception{
		
		this.context = context;
		
		// route java.util.logging to slf4j
		SLF4JBridgeHandler.install();
		logger.info(ID+": started SLF4JBridgeHandler");
		
		// print internal state
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		StatusPrinter.print(lc);
		
		// log BundleEvents
		context.addBundleListener(new BundleListener() {
			
			public void bundleChanged(BundleEvent event){
				
				String typeMessage = null;
				switch (event.getType()) {
				case BundleEvent.INSTALLED:
					typeMessage = "installed"; //$NON-NLS-1$
					break;
				case BundleEvent.LAZY_ACTIVATION:
					typeMessage = "lazy activation"; //$NON-NLS-1$
					break;
				case BundleEvent.RESOLVED:
					typeMessage = "resolved"; //$NON-NLS-1$
					break;
				case BundleEvent.STARTED:
					typeMessage = "started"; //$NON-NLS-1$
					break;
				case BundleEvent.STARTING:
					typeMessage = "starting"; //$NON-NLS-1$
					break;
				case BundleEvent.STOPPED:
					typeMessage = "stopped"; //$NON-NLS-1$
					break;
				case BundleEvent.STOPPING:
					typeMessage = "stopping"; //$NON-NLS-1$
					break;
				case BundleEvent.UNINSTALLED:
					typeMessage = "uninstalled"; //$NON-NLS-1$
					break;
				case BundleEvent.UNRESOLVED:
					typeMessage = "unresolved"; //$NON-NLS-1$
					break;
				case BundleEvent.UPDATED:
					typeMessage = "updated"; //$NON-NLS-1$
					break;
				default:
					typeMessage = "unknown bundle event: " + event.getType(); //$NON-NLS-1$
					break;
				}
				
				logger.info(bundleMarker, "BundleEvent: B: {} new state: {}", //$NON-NLS-1$
					event.getBundle().getSymbolicName(), typeMessage);
			}
		});
		
		// log FrameworkEvents
		context.addFrameworkListener(new FrameworkListener() {
			
			public void frameworkEvent(FrameworkEvent event){
				
				switch (event.getType()) {
				case FrameworkEvent.ERROR:
					logger.error(bundleMarker, "FrameworkEvent: ERROR in bundle {}", //$NON-NLS-1$
						event.getBundle().getSymbolicName(), event.getThrowable());
					break;
				case FrameworkEvent.WARNING:
					logger.warn(bundleMarker, "FrameworkEvent: WARNING in bundle {}", //$NON-NLS-1$
						event.getBundle().getSymbolicName());
					break;
				case FrameworkEvent.STARTED:
					logger.info(bundleMarker, "FrameworkEvent bundle {} started", //$NON-NLS-1$
						event.getBundle().getSymbolicName());
					break;
				default:
					// not logged:
					// FrameworkEvent.INFO,
					// FrameworkEvent.PACKAGES_REFRESHED,
					// FrameworkEvent.STARTLEVEL_CHANGED
					break;
				}
				
			}
			
		});
		
	}
	
	/**
	 * 
	 * Implements <code>BundleActivator.stop()</code>.
	 * 
	 * @param bundleContext
	 *            the framework context for the bundle
	 * @throws Exception
	 */
	public void stop(BundleContext context) throws Exception{
		
		this.context = null;
	}
	
	/**
	 * Get the shared context.
	 * 
	 * @return
	 */
	public BundleContext getContext(){
		return context;
	}
	
}
