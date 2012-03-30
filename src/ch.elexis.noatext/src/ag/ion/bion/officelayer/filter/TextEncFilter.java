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
 * Last changes made by $Author: markus $, $Date: 2007-04-03 12:40:19 +0200 (Di, 03 Apr 2007) $
 */
package ag.ion.bion.officelayer.filter;

import ag.ion.bion.officelayer.document.IDocument;

import ag.ion.noa.filter.AbstractFilter;

/**
 * Contains information in order to export an OpenOffice.org document 
 * to encoded text.
 * 
 * @author Markus Krüger
 * @author Andreas Bröcker
 * @version $Revision: 11479 $
 */
public class TextEncFilter extends AbstractFilter implements IFilter {
	
	/** Global filter for encoded text.*/
	public static final IFilter FILTER = new TextEncFilter();
	
	private static final String FILE_EXTENSION = "txt";
	
  //----------------------------------------------------------------------------
	/**
	* Returns definition of the filter.
	* 
	* @param document document to be exported
	* 
	* @return definition of the filter
  * 
  * @author Markus Krüger
  * @author Andreas Bröcker
	*/
  public String getFilterDefinition(IDocument document) {
    if(document.getDocumentType().equals(IDocument.WRITER)) {
      return "Text (encoded)";
    }
    else if(document.getDocumentType().equals(IDocument.GLOBAL)) {
      return "Text (encoded)(StarWriter/GlobalDocument)";
    }
    else if(document.getDocumentType().equals(IDocument.WEB)) {
      return "Text (encoded)(StarWriter/Web)";
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
      return FILE_EXTENSION;
    }
    else if(documentType.equals(IDocument.GLOBAL)) {
      return FILE_EXTENSION;
    }
    else if(documentType.equals(IDocument.WEB)) {
      return FILE_EXTENSION;
    }
    return null;
	}
  //----------------------------------------------------------------------------
	
}