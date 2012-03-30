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
 * Last changes made by $Author: markus $, $Date: 2007-04-03 12:40:19 +0200 (Di, 03 Apr 2007) $
 */
package ag.ion.noa.filter;

import ag.ion.bion.officelayer.document.IDocument;

import ag.ion.bion.officelayer.filter.IFilter;

/**
 * Filter for the OpenOffice.org 1.0 format.
 * 
 * @author Andreas Bröcker
 * @version $Revision: 11479 $
 * @date 09.07.2006
 */ 
public class OpenOfficeFilter extends AbstractFilter implements IFilter {

	/** Filter for the OpenOffice.org 1.0 format.*/
	public static final IFilter FILTER = new OpenOfficeFilter();
	
	//----------------------------------------------------------------------------
	/**
	* Returns definition of the filter. Returns null if the filter
	* is not available for the submitted document.
	* 
	* @param document document to be exported 
	* 
	* @return definition of the filter or null if the filter
	* is not available for the submitted document
	* 
	* @author Andreas Bröcker
	* @date 08.07.2006
	*/
	public String getFilterDefinition(IDocument document) {
		if(document.getDocumentType().equals(IDocument.WRITER)) {
      return "StarOffice XML (Writer)";
    }
		else if(document.getDocumentType().equals(IDocument.GLOBAL)) {
      return "writer_globaldocument_StarOffice_XML_Writer_GlobalDocument";
    }
		else if(document.getDocumentType().equals(IDocument.CALC)) {
      return "StarOffice XML (Calc)";
    }
		else if(document.getDocumentType().equals(IDocument.DRAW)) {
      return "StarOffice XML (Draw)";
    }
		else if(document.getDocumentType().equals(IDocument.IMPRESS)) {
      return "StarOffice XML (Impress)";
    }
		else if(document.getDocumentType().equals(IDocument.MATH)) {
      return "StarOffice XML (Math)";
    }
    return null;
	}
	//----------------------------------------------------------------------------
  /**
   * Returns file extension of the filter. Returns null
   * if the document type is not supported by the filter.
   * 
   * @param documentType document type to be used
   * 
   * @return file extension of the filter
   * 
   * @author Markus Krüger
   * @date 03.04.2007
   */
  public String getFileExtension(String documentType) {
    if(documentType == null)
      return null;
		if(documentType.equals(IDocument.WRITER)) {
      return "sxw";
    }
		else if(documentType.equals(IDocument.GLOBAL)) {
      return "sxg";
    }
		else if(documentType.equals(IDocument.CALC)) {
      return "sxc";
    }
		else if(documentType.equals(IDocument.DRAW)) {
      return "sxd";
    }
		else if(documentType.equals(IDocument.IMPRESS)) {
      return "sxi";
    }
		else if(documentType.equals(IDocument.MATH)) {
      return "sxm";
    }
    return null;
	}
	//----------------------------------------------------------------------------
	/**
	 * Returns name of the filter. Returns null
	 * if the submitted document is not supported by the filter.
	 * 
	 * @param document document to be used
	 * 
	 * @return name of the filter
	 * 
	 * @author Andreas Bröcker
	 * @date 14.07.2006
	 */
	public String getName(IDocument document) {
		if(document.getDocumentType().equals(IDocument.WRITER)) {
      return "OpenOffice.org 1.0 Textdocument";
    }
		else if(document.getDocumentType().equals(IDocument.GLOBAL)) {
      return "OpenOffice.org 1.0 Global Document";
    }
		else if(document.getDocumentType().equals(IDocument.CALC)) {
      return "OpenOffice.org 1.0 Spreadsheet";
    }
		else if(document.getDocumentType().equals(IDocument.DRAW)) {
      return "OpenOffice.org 1.0 Drawing";
    }
		else if(document.getDocumentType().equals(IDocument.IMPRESS)) {
      return "OpenOffice.org 1.0 Presentation";
    }
		else if(document.getDocumentType().equals(IDocument.MATH)) {
      return "OpenOffice.org 1.0 Formula";
    }
    return null;
	}
	//----------------------------------------------------------------------------
	
}