/****************************************************************************
 *                                                                          *
 * NOA (Nice Office Access)                                                 *
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
 *  http://www.ion.ag                                                       *
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
 * Abstract base filter.
 * 
 * @author Andreas Bröcker
 * @version $Revision: 11479 $
 * @date 09.07.2006
 */ 
public abstract class AbstractFilter implements IFilter {
	
  //----------------------------------------------------------------------------
	/**
	 * Returns information whether the submitted document
	 * is supported by the filter.
	 * 
	 * @param document document to be used
	 * 
	 * @return information whether the submitted document
	 * is supported by the filter
	 * 
	 * @author Andreas Bröcker
	 * @date 08.07.2006
	 */
	public boolean isSupported(IDocument document) {
		if(getFilterDefinition(document) == null)
			return false;
		else
			return true;
	}
	//----------------------------------------------------------------------------
	/**
	 * Returns information whether the filter constructs
	 * a document which can not be interpreted again.
	 * 
	 * @return information whether the filter constructs
	 * a document which can not be interpreted again
	 * 
	 * @author Andreas Bröcker
	 * @date 08.07.2006
	 */
	public boolean isExternalFilter() {
		return false;
	}
	//----------------------------------------------------------------------------
  /**
   * Returns file extension of the filter. Returns null
   * if the document is not supported by the filter.
   * 
   * @param document document to be used
   * 
   * @return file extension of the filter
   * 
   * @author Markus Krüger
   * @date 03.04.2007
   */
  public String getFileExtension(IDocument document) {
    if(document == null)
      return null;
    return getFileExtension(document.getDocumentType());
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
		return getFilterDefinition(document);
	}
  //----------------------------------------------------------------------------
}
