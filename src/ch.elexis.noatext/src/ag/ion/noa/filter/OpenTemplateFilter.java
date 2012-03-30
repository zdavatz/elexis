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
 * Filter for the Open Template format.
 * 
 * @author Andreas Bröcker
 * @version $Revision: 11479 $
 * @date 09.07.2006
 */ 
public class OpenTemplateFilter extends AbstractFilter implements IFilter {
	
	/** Filter for the Open Template format.*/
	public static final IFilter FILTER = new OpenTemplateFilter();
	
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
      return "writer8_template";
    }
		else if(document.getDocumentType().equals(IDocument.CALC)) {
      return "calc8_template";
    }
		else if(document.getDocumentType().equals(IDocument.DRAW)) {
      return "draw8_template";
    }
		else if(document.getDocumentType().equals(IDocument.IMPRESS)) {
      return "impress8_template";
    }
		else if(document.getDocumentType().equals(IDocument.WEB)) {
      return "writerweb8_writer_template";
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
      return "ott";
    }
		else if(documentType.equals(IDocument.CALC)) {
      return "ots";
    }
		else if(documentType.equals(IDocument.DRAW)) {
      return "otg";
    }
		else if(documentType.equals(IDocument.IMPRESS)) {
      return "otp";
    }
		else if(documentType.equals(IDocument.WEB)) {
      return "oth";
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
      return "Open Document Template Text";
    }
		else if(document.getDocumentType().equals(IDocument.CALC)) {
      return "Open Document Template Spreadsheet";
    }
		else if(document.getDocumentType().equals(IDocument.DRAW)) {
      return "Open Document Template Drawing";
    }
		else if(document.getDocumentType().equals(IDocument.IMPRESS)) {
      return "Open Document Template Presentation";
    }
    return null;
	}
	//----------------------------------------------------------------------------
	
}