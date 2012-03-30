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
package ag.ion.noa.internal.search;

import ag.ion.noa.search.ISearchResult;

import ag.ion.bion.officelayer.internal.text.TextRange;

import ag.ion.bion.officelayer.text.ITextRange;

import ag.ion.bion.officelayer.util.Assert;

import com.sun.star.container.XIndexAccess;

import com.sun.star.text.XTextRange;

import com.sun.star.uno.Any;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Result of a search.
 * 
 * @author Andreas Bröcker
 * @version $Revision: 10398 $
 * @date 09.07.2006
 */ 
public class SearchResult implements ISearchResult {

	private XIndexAccess xIndexAccess = null;
	private XInterface	 xInterface		= null;	
	
  //----------------------------------------------------------------------------
	/**
	 * Constructs new SearchResult.
	 * 
	 * @author Andreas Bröcker
	 * @date 09.07.2006
	 */
	public SearchResult() {
	}
  //----------------------------------------------------------------------------
	/**
	 * Constructs new SearchResult.
	 * 
	 * @param xIndexAccess OpenOffice.org XIndexAccess interface
	 * 
	 * @author Andreas Bröcker
	 * @date 09.07.2006
	 */
	public SearchResult(XIndexAccess xIndexAccess) {
		Assert.isNotNull(xIndexAccess, XIndexAccess.class, this);
		this.xIndexAccess = xIndexAccess;
	}
  //----------------------------------------------------------------------------
	/**
	 * Constructs new SearchResult.
	 * 
	 * @param xInterface OpenOffice.org XInterface interface
	 * 
	 * @author Andreas Bröcker
	 * @date 09.07.2006
	 */
	public SearchResult(XInterface xInterface) {
		Assert.isNotNull(xInterface, XInterface.class, this);
		this.xInterface = xInterface;
	}
  //----------------------------------------------------------------------------
	/**
	 * Returns information whether the search result is empty.
	 * 
	 * @return information whether the search result is empty
	 * 
	 * @author Andreas Bröcker
	 * @date 09.07.2006
	 */
	public boolean isEmpty() {
		if(xIndexAccess == null && xInterface == null)
			return true;
		if(xIndexAccess != null) {
			if(xIndexAccess.getCount() != 0)
				return false;
			else 
				return true;
		}
		else
			return false;
	}
  //----------------------------------------------------------------------------
	/**
	 * Returns text ranges of the search result.
	 * 
	 * @return text ranges of the search resul
	 * 
	 * @author Andreas Bröcker
	 * @date 09.07.2006
	 */
	public ITextRange[] getTextRanges() {
		if(xInterface != null) {
			XTextRange textRange = (XTextRange)UnoRuntime.queryInterface(XTextRange.class, xInterface);
			return new ITextRange[] {new TextRange(null, textRange)};
		}
		else {
			List list = new ArrayList();
			for(int i=0, n=xIndexAccess.getCount(); i<n; i++) {
				try {
					Any any = (Any)xIndexAccess.getByIndex(i);
					XTextRange textRange = (XTextRange)UnoRuntime.queryInterface(XTextRange.class, any);
					if(textRange != null)
						list.add(new TextRange(null, textRange));
				}
				catch(Throwable throwable) {
					//do not consume
				}
			}
			return (ITextRange[])list.toArray(new ITextRange[list.size()]);
		}
	}
  //----------------------------------------------------------------------------
	
}