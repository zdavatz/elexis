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
package ag.ion.noa.search;

import ag.ion.bion.officelayer.util.Assert;

/**
 * Descriptor of a search.
 * 
 * @author Andreas Bröcker
 * @version $Revision: 10398 $
 * @date 09.07.2006
 */ 
public class SearchDescriptor implements ISearchDescriptor {

	private String searchContent = null;
	
	private boolean useRegularExpression 	= false;
	private boolean isCaseSensitive				= false;
	private boolean useCompleteWords			= false;
	private boolean useSimilaritySearch		= false;
	
  //----------------------------------------------------------------------------
	/**
	 * Constructs new SearchDescriptor.
	 * 
	 * @param searchContent search content to be used
	 * 
	 * @author Andreas Bröcker
	 * @date 09.07.2006
	 */
	public SearchDescriptor(String searchContent) {
		Assert.isNotNull(searchContent, String.class, this);
		this.searchContent = searchContent;
	}
  //----------------------------------------------------------------------------
	/**
	 * Returns search content.
	 * 
	 * @return search content to be looked for
	 * 
	 * @author Andreas Bröcker
	 * @date 09.07.2006
	 */
	public String getSearchContent() {
		return searchContent;
	}
  //----------------------------------------------------------------------------
	/**
	 * Returns information whether the search content
	 * is case sensitive.
	 * 
	 * @return information whether the search content
	 * is case sensitive
	 * 
	 * @author Andreas Bröcker
	 * @date 09.07.2006
	 */
	public boolean useRegularExpression() {
		return useRegularExpression;
	}
	//----------------------------------------------------------------------------
	/**
	 * Sets information whether the search content
	 * is case sensitive
	 * 
	 * @param useRegularExpression information whether the search content
	 * is case sensitive
	 * 
	 * @author Andreas Bröcker
	 * @date 09.07.2006
	 */
	public void setUseRegularExpression(boolean useRegularExpression) {
		this.useRegularExpression = useRegularExpression;
	}
  //----------------------------------------------------------------------------
	/**
	 * Returns information whether the search content
	 * is case sensitive.
	 * 
	 * @return information whether the search content
	 * is case sensitive
	 * 
	 * @author Andreas Bröcker
	 * @date 09.07.2006
	 */
	public boolean isCaseSensitive() {
		return isCaseSensitive;
	}
	//----------------------------------------------------------------------------
	/**
	 * Sets information whether the search content
	 * is case sensitive
	 * 
	 * @param isCaseSensitive information whether the search content
	 * is case sensitive
	 * 
	 * @author Andreas Bröcker
	 * @date 09.07.2006
	 */
	public void setIsCaseSensitive(boolean isCaseSensitive) {
		this.isCaseSensitive = isCaseSensitive;
	}
	//----------------------------------------------------------------------------
	/**
	 * Returns information whether only complete words
	 * should be searched.
	 * 
	 * @return information whether only complete words
	 * should be searched
	 * 
	 * @author Andreas Bröcker
	 * @date 09.07.2006
	 */
	public boolean useCompleteWords() {
		return useCompleteWords;
	}
	//----------------------------------------------------------------------------
	/**
	 * Sets information whether only complete words
	 * should be searched
	 * 
	 * @param useCompleteWords information whether only complete words
	 * should be searched
	 * 
	 * @author Andreas Bröcker
	 * @date 09.07.2006
	 */
	public void setUseCompleteWords(boolean useCompleteWords) {
		this.useCompleteWords = useCompleteWords;
	}	
  //----------------------------------------------------------------------------
	/**
	 * Returns information whether a similarity search 
	 * should be done.
	 * 
	 * @return information whether a similarity search 
	 * should be done
	 * 
	 * @author Andreas Bröcker
	 * @date 13.07.2006
	 */
	public boolean useSimilaritySearch() {
		return useSimilaritySearch;
	}
  //----------------------------------------------------------------------------
	/**
	 * Sets information whether a similarity search 
	 * should be done
	 * 
	 * @param useSimilaritySearch information whether a similarity search 
	 * should be done
	 * 
	 * @author Andreas Bröcker
	 * @date 13.07.2006
	 */
	public void setUseSimilartiySearch(boolean useSimilaritySearch) {
		this.useSimilaritySearch = useSimilaritySearch;
	}	
	//----------------------------------------------------------------------------
	
}