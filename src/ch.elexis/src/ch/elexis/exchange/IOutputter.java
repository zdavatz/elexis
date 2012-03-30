/*******************************************************************************
 * Copyright (c) 2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 * 
 *  $Id: IOutputter.java 6058 2010-02-03 15:02:13Z rgw_ch $
 *******************************************************************************/
package ch.elexis.exchange;

import org.eclipse.swt.graphics.Image;

/**
 * Ach.elexis.exchange to output something
 * 
 * @author gerry
 * 
 */
public interface IOutputter {
	/** unique ID */
	public String getOutputterID();
	
	/** human readable description */
	public String getOutputterDescription();
	
	/** Image to symbolize this outputter (should be 16x16 or 24x24 Pixel) */
	public Image getSymbol();
}
