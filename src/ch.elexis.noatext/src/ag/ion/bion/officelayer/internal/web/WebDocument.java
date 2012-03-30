/****************************************************************************
 * ubion.ORS - The Open Report Suite                                        *
 *                                                                          *
 * ------------------------------------------------------------------------ *
 *                                                                          *
 * Subproject: NOA (Nice Office Access)                                     *
 *                                                                          *
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
 * Last changes made by $Author: andreas $, $Date: 2006-10-04 14:14:28 +0200 (Mi, 04 Okt 2006) $
 */
package ag.ion.bion.officelayer.internal.web;

import ag.ion.bion.officelayer.document.AbstractDocument;
import ag.ion.bion.officelayer.document.IDocument;

import ag.ion.bion.officelayer.web.IWebDocument;

import com.sun.star.text.XTextDocument;

import com.sun.star.lang.XComponent;

import com.sun.star.uno.UnoRuntime;

/**
 * OpenOffice.org web document.
 * 
 * @author Thomas Renken
 * @author Andreas Bröcker
 * @version $Revision: 10398 $
 */
public class WebDocument extends AbstractDocument implements IWebDocument {

  private XTextDocument xTextDocument = null;
  
  //----------------------------------------------------------------------------
  /**
   * Constructs new OpenOffice.org web document.
   * 
   * @param xTextDocument OpenOffice.org API interface of a web document
   * 
   * @throws IllegalArgumentException if the submitted OpenOffice.org interface is not valid
   * 
   * @author Thomas Renken
   * @author Andreas Bröcker
   */
  public WebDocument(XTextDocument xtextDocument) throws IllegalArgumentException {
    super((XComponent)UnoRuntime.queryInterface(XComponent.class, xtextDocument));
    this.xTextDocument = xtextDocument;
  }  
  //----------------------------------------------------------------------------
  /**
   * Returns OpenOffice.org public XTextDocument interface.
   * 
   * @return OpenOffice.org XTextDocument interface
   * 
   * @author Thomas Renken
   * @author Andreas Bröcker
   */
  public XTextDocument getWebDocument() {
    return xTextDocument;
  }
  //----------------------------------------------------------------------------
  /**
   * Returns type of the document.
   * 
   * @return type of the document
   * 
   * @author Thomas Renken
   * @author Andreas Bröcker
   */
  public String getDocumentType() {
    return IDocument.WEB;
  } 
  //----------------------------------------------------------------------------
  /**
   * Reformats the document.
   * 
   * @author Andreas Bröcker
   */
  public void reformat() {
    //TODO fill with logic
  }
  //----------------------------------------------------------------------------
  
}