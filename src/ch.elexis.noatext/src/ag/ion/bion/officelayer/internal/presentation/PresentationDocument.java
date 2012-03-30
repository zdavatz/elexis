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
 *  http://www.ion.ag                                                       *
 *  info@ion.ag                                                             *
 *                                                                          *
 ****************************************************************************/
 
/*
 * Last changes made by $Author: andreas $, $Date: 2006-10-04 14:14:28 +0200 (Mi, 04 Okt 2006) $
 */
package ag.ion.bion.officelayer.internal.presentation;

import ag.ion.bion.officelayer.document.AbstractDocument;
import ag.ion.bion.officelayer.document.IDocument;

import ag.ion.bion.officelayer.presentation.IPresentationDocument;

import com.sun.star.presentation.XPresentationSupplier;

import com.sun.star.lang.XComponent;

import com.sun.star.uno.UnoRuntime;

/**
 * OpenOffice.org presentation document representation.
 * 
 * @author Andreas Bröcker
 * @author Markus Krüger
 * @version $Revision: 10398 $
 */
public class PresentationDocument extends AbstractDocument implements IPresentationDocument {
	
  private XPresentationSupplier xPresentationSupplier = null;
  
  //----------------------------------------------------------------------------
  /**
   * Constructs new OpenOffice.org presentation document.
   * 
   * @param xPresentationSupplier OpenOffice.org interface of a presentation document
   * 
   * @throws IllegalArgumentException if the submitted OpenOffice.org interface is not valid
   * 
   * @author Andreas Bröcker
   */
  public PresentationDocument(XPresentationSupplier xPresentationSupplier) throws IllegalArgumentException {
    super((XComponent)UnoRuntime.queryInterface(XComponent.class, xPresentationSupplier));
    this.xPresentationSupplier = xPresentationSupplier;
  }  
  //----------------------------------------------------------------------------
  /**
   * Returns OpenOffice.org XPresentationSupplier interface.
   * 
   * @return OpenOffice.org XPresentationSupplier interface
   * 
   * @author Andreas Bröcker
   */
  public XPresentationSupplier getPresentationSupplier() {
    return xPresentationSupplier;
  }  
  //----------------------------------------------------------------------------
  /**
   * Returns type of the document.
   * 
   * @return type of the document
   * 
   * @author Andreas Bröcker
   */
  public String getDocumentType() {
    return IDocument.IMPRESS;
  } 
  //----------------------------------------------------------------------------
  /**
   * Reformats the document.
   * 
   * @author Markus Krüger
   */
  public void reformat() {
    //TODO fill with logic
  }
  //----------------------------------------------------------------------------

}