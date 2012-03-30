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
package ag.ion.noa.internal.document;

import ag.ion.bion.officelayer.util.Assert;

import ag.ion.noa.document.ISearchService;

import ag.ion.noa.internal.search.SearchResult;

import ag.ion.noa.search.ISearchDescriptor;
import ag.ion.noa.search.ISearchResult;

import com.sun.star.container.XIndexAccess;

import com.sun.star.uno.XInterface;

import com.sun.star.util.XSearchDescriptor;
import com.sun.star.util.XSearchable;

/**
 * Search service for of a document.
 * 
 * @author Andreas Bröcker
 * @version $Revision: 10398 $
 * @date 09.07.2006
 */ 
public class SearchService implements ISearchService {
	
	private XSearchable xSearchable = null;

	private boolean supportFindAll = true;
	
  //----------------------------------------------------------------------------
	/**
	 * Constructs new SearchService.
	 * 
	 * @param xSearchable OpenOffice.org XSearchable interface to be used
	 * 
	 * @author Andreas Bröcker
	 * @date 09.07.2006
	 */
	public SearchService(XSearchable xSearchable) {
		Assert.isNotNull(xSearchable, XSearchable.class, this);
		this.xSearchable = xSearchable;
	}	
  //----------------------------------------------------------------------------
	/**
	 * Looks for the first occurrences of the defined search.
	 * 
	 * @param searchDescriptor search descriptor to be used
	 * 
	 * @return result of the search
	 * 
	 * @author Andreas Bröcker
	 * @date 09.07.2006
	 */
	public ISearchResult findFirst(ISearchDescriptor searchDescriptor) {
		if(searchDescriptor == null || searchDescriptor.getSearchContent() == null)
			return new SearchResult();
		Object object =  xSearchable.findFirst(toXSearchDescriptor(searchDescriptor));
		if(object == null)
			return new SearchResult();
		else
			return new SearchResult((XInterface)object);
	}
  //----------------------------------------------------------------------------
	/**
	 * Looks for all occurrences of the defined search.
	 * 
	 * @param searchDescriptor search descriptor to be used
	 * 
	 * @return result of the search
	 * 
	 * @author Andreas Bröcker
	 * @date 09.07.2006
	 */
	public ISearchResult findAll(ISearchDescriptor searchDescriptor) {
		if(searchDescriptor == null || searchDescriptor.getSearchContent() == null)
			return new SearchResult();
		XIndexAccess xIndexAccess =  xSearchable.findAll(toXSearchDescriptor(searchDescriptor));
		return new SearchResult(xIndexAccess);
	}	
  //----------------------------------------------------------------------------
	/**
	 * Returns information whether a find all search is
	 * supported.
	 * 
	 * @return information whether a find all search is
	 * supported
	 * 
	 * @author Andreas Bröcker
	 * @date 09.07.2006
	 */
	public boolean supportsFindAll() {
		return supportFindAll;
	}
  //----------------------------------------------------------------------------
	/**
	 * Converts the search descriptor.
	 * 
	 * @param searchDescriptor search descriptor to be converted
	 * 
	 * @return converted search descriptor
	 * 
	 * @author Andreas Bröcker
	 * @date 09.07.2006
	 */
	protected XSearchDescriptor toXSearchDescriptor(ISearchDescriptor searchDescriptor) {
		XSearchDescriptor xSearchDescriptor = xSearchable.createSearchDescriptor();
		xSearchDescriptor.setSearchString(searchDescriptor.getSearchContent());
		if(searchDescriptor.isCaseSensitive()) {
			try {
				xSearchDescriptor.setPropertyValue("SearchCaseSensitive", Boolean.TRUE);
			}
			catch(Throwable throwable) {
				//do not consume
			}
		}
		if(searchDescriptor.useCompleteWords()) {
			try {
				xSearchDescriptor.setPropertyValue("SearchWords", Boolean.TRUE);
			}
			catch(Throwable throwable) {
				//do not consume
			}
		}
		if(searchDescriptor.useRegularExpression()) {
			try {
				xSearchDescriptor.setPropertyValue("SearchRegularExpression", Boolean.TRUE);
			}
			catch(Throwable throwable) {
				//do not consume
			}
		}
		if(searchDescriptor.useSimilaritySearch()) {
			try {
				xSearchDescriptor.setPropertyValue("SearchSimilarity", Boolean.TRUE);
			}
			catch(Throwable throwable) {
				//do not consume
			}
		}
		return xSearchDescriptor;
	}
  //----------------------------------------------------------------------------
	
}