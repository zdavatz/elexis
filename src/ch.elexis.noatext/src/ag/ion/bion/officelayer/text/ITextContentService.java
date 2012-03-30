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
 * Last changes made by $Author: markus $, $Date: 2007-07-09 18:22:59 +0200 (Mo, 09 Jul 2007) $
 */
package ag.ion.bion.officelayer.text;

import ag.ion.noa.graphic.GraphicInfo;

/**
 * Content service of a text document.
 * 
 * @author Andreas Bröcker
 * @version $Revision: 11508 $
 */
public interface ITextContentService {
  
  //----------------------------------------------------------------------------
  /**
   * Constructs new paragraph.
   * 
   * @return new paragraph
   * 
   * @throws TextException if the paragraph can not be constructed
   * 
   * @author Andreas Bröcker
   */
  public IParagraph constructNewParagraph() throws TextException;  
  //----------------------------------------------------------------------------
  /**
   * Constructs new image.
   * 
   * @param graphicInfo the graphic information to construct image with
   * 
   * @return new image
   * 
   * @throws TextException if the image can not be constructed
   * 
   * @author Markus Krüger
   * @date 09.07.2007
   */
  public ITextDocumentImage constructNewImage(GraphicInfo graphicInfo) throws TextException;  
  //----------------------------------------------------------------------------
  /**
   * Inserts content.
   * 
   * @param textContent text content to be inserted
   * 
   * @throws TextException if the text content can not be inserted
   * 
   * @author Andreas Bröcker
   */
  public void insertTextContent(ITextContent textContent) throws TextException;
  //----------------------------------------------------------------------------
  /**
   * Inserts content at submitted position.
   * 
   * @param textRange position to be used
   * @param textContent text content to be inserted
   * 
   * @throws TextException if the text content can not be inserted
   * 
   * @author Andreas Bröcker
   */
  public void insertTextContent(ITextRange textRange, ITextContent textContent) throws TextException;
  //----------------------------------------------------------------------------
  /**
   * Inserts new text content before other text content.
   * 
   * @param newTextContent text content to be inserted
   * @param textContent available text content
   * 
   * @throws TextException if the text content can not be inserted
   * 
   * @author Andreas Bröcker
   */
  public void insertTextContentBefore(ITextContent newTextContent, ITextContent textContent) throws TextException;
  //----------------------------------------------------------------------------
  /**
   * Inserts new text content after other text content.
   * 
   * @param newTextContent text content to be inserted
   * @param textContent available text content
   * 
   * @throws TextException if the text content can not be inserted
   * 
   * @author Andreas Bröcker
   */
  public void insertTextContentAfter(ITextContent newTextContent, ITextContent textContent) throws TextException;
  //----------------------------------------------------------------------------
  /**
   * Removes content.
   * 
   * @param textContent text content to be removed
   * 
   * @throws TextException if the text content can not be removed
   * 
   * @author Miriam Sutter
   */
  public void removeTextContent(ITextContent textContent) throws TextException;
  //----------------------------------------------------------------------------
}