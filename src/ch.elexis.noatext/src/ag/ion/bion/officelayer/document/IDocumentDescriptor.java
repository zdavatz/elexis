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
package ag.ion.bion.officelayer.document;

/**
 * Descriptor of a document. A document descriptor can be used
 * in order to load and store documents.
 * 
 * @author Andreas Bröcker
 * @author Markus Krüger
 * @version $Revision: 10398 $
 */
public interface IDocumentDescriptor {
  
  //----------------------------------------------------------------------------
  /**
   * Sets information whether the document is hidden.
   * 
   * @param hidden information whether the document is hidden
   * 
   * @author Andreas Bröcker
   */
  public void setHidden(boolean hidden);
  //----------------------------------------------------------------------------
  /**
   * Returns information whether the document is hidden.
   * 
   * @return information whether the document is hidden
   * 
   * @author Andreas Bröcker
   */
  public boolean getHidden();  
  //----------------------------------------------------------------------------
  /**
   * Sets information whether the document is a template.
   * 
   * @param asTemplate information whether the document is a template
   * 
   * @author Andreas Bröcker
   */
  public void setAsTemplate(boolean asTemplate);
  //----------------------------------------------------------------------------
  /**
   * Returns information whether the document is a template. The default value
   * is <code>false</code>
   * 
   * @return information whether the document is a template
   * 
   * @author Andreas Bröcker
   */
  public boolean getAsTemplate();
  //----------------------------------------------------------------------------
  /**
   * Sets information whether the document should be opened read only. The default value
   * is <code>false</code>
   * 
   * @param readOnly information whether the document should be opened read only
   * 
   * @author Andreas Bröcker
   * @date 13.06.2006
   */
  public void setReadOnly(boolean readOnly);
  //----------------------------------------------------------------------------
  /**
   * Returns information whether the document should be opened read only. The default value
   * is <code>false</code>
   * 
   * @return information whether the document should be opened read only
   * 
   * @author Andreas Bröcker
   * @date 13.06.2006
   */
  public boolean getReadOnly();
  //----------------------------------------------------------------------------
  /**
   * Sets information whether the document should be opened as preview. The default value
   * is <code>false</code>
   * 
   * @param asPreview information whether the document should be opened as preview
   * 
   * @author Andreas Bröcker
   * @date 13.06.2006
   */
  public void setAsPreview(boolean asPreview);
  //----------------------------------------------------------------------------
  /**
   * Returns information whether the document should be opened as preview. The default value
   * is <code>false</code>
   * 
   * @return information whether the document should be opened as preview
   * 
   * @author Andreas Bröcker
   * @date 13.06.2006
   */
  public boolean getAsPreview();
  //----------------------------------------------------------------------------
  /**
   * Sets the author of the document.
   * 
   * @param author the author of the document to be set
   * 
   * @author Markus Krüger
   * @date 24.08.2006
   */
  public void setAuthor(String author);
  //----------------------------------------------------------------------------
  /**
   * Returns the author of the document.
   * 
   * @return the author of the document
   * 
   * @author Markus Krüger
   * @date 24.08.2006
   */
  public String getAuthor();  
  //----------------------------------------------------------------------------
  /**
   * Sets the comment of the document.
   * 
   * @param comment the comment of the document to be set
   * 
   * @author Markus Krüger
   * @date 24.08.2006
   */
  public void setComment(String comment);
  //----------------------------------------------------------------------------
  /**
   * Returns the comment of the document.
   * 
   * @return the comment of the document
   * 
   * @author Markus Krüger
   * @date 24.08.2006
   */
  public String getComment();  
  //----------------------------------------------------------------------------
  /**
   * Sets the title of the document.
   * 
   * @param title the title of the document to be set
   * 
   * @author Markus Krüger
   * @date 24.08.2006
   */
  public void setTitle(String title);
  //----------------------------------------------------------------------------
  /**
   * Returns the title of the document.
   * 
   * @return the title of the document
   * 
   * @author Markus Krüger
   * @date 24.08.2006
   */
  public String getTitle();  
  //----------------------------------------------------------------------------
  /**
   * Sets the base URL of the document to be used to resolve relative links.
   * 
   * @param baseURL the base URL of the document to be set
   * 
   * @author Markus Krüger
   * @date 24.08.2006
   */
  public void setBaseURL(String baseURL);
  //----------------------------------------------------------------------------
  /**
   * Returns the base URL of the document.
   * 
   * @return the base URL of the document
   * 
   * @author Markus Krüger
   * @date 24.08.2006
   */
  public String getBaseURL();  
  //----------------------------------------------------------------------------
  /**
   * Sets the URL of the document. This is the location of the component in 
   * URL syntax. It must be the full qualified URL .
   * 
   * @param URL the URL of the document to be set
   * 
   * @author Markus Krüger
   * @date 24.08.2006
   */
  public void setURL(String URL);
  //----------------------------------------------------------------------------
  /**
   * Returns the URL of the document. This is the location of the component in 
   * URL syntax. It must be the full qualified URL 
   * 
   * @return the URL of the document
   * 
   * @author Markus Krüger
   * @date 24.08.2006
   */
  public String getURL();  
  //----------------------------------------------------------------------------
  
}