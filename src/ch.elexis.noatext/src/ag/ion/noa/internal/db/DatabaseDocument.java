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
 * Last changes made by $Author: andreas $, $Date: 2006-10-04 14:14:28 +0200 (Mi, 04 Okt 2006) $
 */
package ag.ion.noa.internal.db;

import ag.ion.bion.officelayer.document.AbstractDocument;
import ag.ion.bion.officelayer.document.IDocument;

import ag.ion.noa.db.IDatabaseDocument;

import com.sun.star.lang.XComponent;

import com.sun.star.sdb.XOfficeDatabaseDocument;

import com.sun.star.uno.UnoRuntime;

/**
 * OpenOffice.org database document.
 * 
 * @author Andreas Bröcker
 * @version $Revision: 10398 $
 * @date 16.03.2006
 */
public class DatabaseDocument extends AbstractDocument implements IDatabaseDocument {

	private XOfficeDatabaseDocument xOfficeDatabaseDocument = null;
	
  //----------------------------------------------------------------------------
	/**
	 * Constructs new DatabaseDocument.
	 * 
	 * @param xOfficeDatabaseDocument XOfficeDatabaseDocument OpenOffice.org interface to 
	 * be used
	 * 
	 * @author Andreas Bröcker
	 * @date 16.03.2006
	 */
	public DatabaseDocument(XOfficeDatabaseDocument xOfficeDatabaseDocument) {
		super((XComponent)UnoRuntime.queryInterface(XComponent.class, xOfficeDatabaseDocument));
		this.xOfficeDatabaseDocument =  xOfficeDatabaseDocument;
	}	
  //----------------------------------------------------------------------------
	/**
	 * Returns XOfficeDatabaseDocument OpenOffice.org interface.
	 * 
	 * @return XOfficeDatabaseDocument OpenOffice.org interface
	 * 
	 * @author Andreas Bröcker
	 * @date 16.03.2006
	 */
	public XOfficeDatabaseDocument getOfficeDatabaseDocument() {
		return xOfficeDatabaseDocument;
	}
  //----------------------------------------------------------------------------
  /**
   * Returns type of the document.
   * 
   * @return type of the document
   * 
   * @author Andreas Bröcker
	 * @date 16.03.2006
   */
	public String getDocumentType() {
		return IDocument.BASE;
	}
  //----------------------------------------------------------------------------
  /**
   * Closes the document.
   * 
   * @author Andreas Bröcker
   * @date 20.03.2006
   */
  public void close() {    
    removeDocumentListeners();
    removeModifyListeners();
    /**
     * The nanuallay call of the garbage collection was added in order to stabilize
     * OpenOffice.org. Maybe the calls can be removed later.
     */
    System.gc();
    removeCloseListeners();
  }
  //----------------------------------------------------------------------------

}