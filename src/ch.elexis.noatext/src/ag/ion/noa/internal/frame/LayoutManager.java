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
 * Copyright 2003-2005 by IOn AG                                            *
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
 * Last changes made by $Author: markus $, $Date: 2006-12-08 15:05:57 +0100 (Fr, 08 Dez 2006) $
 */
package ag.ion.noa.internal.frame;

import ag.ion.noa.frame.ILayoutManager;

import ag.ion.bion.officelayer.util.Assert;

import com.sun.star.frame.XLayoutManager;

/**
 * Layout manager for frames.
 * 
 * @author Andreas Bröcker
 * @author Markus Krüger
 * @version $Revision: 11164 $
 * @date 2006/02/05
 */ 
public class LayoutManager implements ILayoutManager {
	
	private XLayoutManager xLayoutManager = null;
		
  //----------------------------------------------------------------------------
	/**
	 * Constructs new LayoutManager.
	 * 
	 * @param xLayoutManager office XLayoutManager interface
	 * 
	 * @author Andreas Bröcker
	 * @date 2006/02/05
	 */
	public LayoutManager(XLayoutManager xLayoutManager) {
		Assert.isNotNull(xLayoutManager, XLayoutManager.class, this);
		this.xLayoutManager = xLayoutManager;
	}
  //----------------------------------------------------------------------------
	/**
	 * Returns the office XLayoutManager interface.
	 * 
	 * @return office XLayoutManager interface
	 * 
	 * @author Andreas Bröcker
	 * @date 2006/02/05
	 */
	public XLayoutManager getXLayoutManager() {
		return xLayoutManager;
	}
  //----------------------------------------------------------------------------
	/**
	 * Shows UI element with the submitted resource URL.
	 * 
	 * @param resourceURL URL of the UI resource to be shown
	 * 
	 * @return information whether the UI resource is visible after method call
	 * 
	 * @author Andreas Bröcker
	 * @date 2006/02/05
	 */
	public boolean hideElement(String resourceURL) {
		if(resourceURL == null)
			return false;
		return xLayoutManager.hideElement(resourceURL);		
	}
  //----------------------------------------------------------------------------
  /**
   * Hides all bars.
   * 
   * @author Markus Krüger
   * @date 08.12.2006
   */
  public void hideAll() {
    for(int i = 0; i < ALL_BARS_URLS.length; i++) {
      hideElement(ALL_BARS_URLS[i]);
    }
  }
  //----------------------------------------------------------------------------
	/**
	 * Shows UI element with the submitted resource URL.
	 * 
	 * @param resourceURL URL of the UI resource to be shown
	 * 
	 * @return information whether the UI resource is visible after method call
	 * 
	 * @author Andreas Bröcker
	 * @date 2006/02/05
	 */
	public boolean showElement(String resourceURL) {
		if(resourceURL == null)
			return false;
    if(xLayoutManager.getElement(resourceURL) == null)
      xLayoutManager.createElement(resourceURL);
		return xLayoutManager.showElement(resourceURL);		
	}
  //----------------------------------------------------------------------------
	/**
	 * Switches the visibility of all UI elements managed by the 
	 * layout manager.
	 * 
	 * @param visible new visibility state
	 * 
	 * @author Andreas Bröcker
	 * @date 2006/02/05
	 */
	public void setVisible(boolean visible) {
		xLayoutManager.setVisible(visible);
	}
  //----------------------------------------------------------------------------

}