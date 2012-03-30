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
 * Last changes made by $Author: markus $, $Date: 2007-08-07 14:36:58 +0200 (Di, 07 Aug 2007) $
 */
package ag.ion.bion.officelayer.text;

import ag.ion.bion.officelayer.clone.ICloneServiceProvider;

/**
 * Paragraph of a text document.
 * 
 * @author Andreas Bröcker
 * @version $Revision: 11559 $
 */
public interface IParagraph extends ITextContent, ICloneServiceProvider {

  //----------------------------------------------------------------------------
  /**
   * Returns properties of the paragraph.
   * 
   * @return properties of the paragraph
   * 
   * @author Andreas Bröcker
   */
  public IParagraphProperties getParagraphProperties();
  //----------------------------------------------------------------------------
  /**
   * Returns text range of the text table.
   * 
   * @return text range of the text table
   * 
   * @author Markus Krüger
   * @date 06.08.2007
   */
  public ITextRange getTextRange();
  //----------------------------------------------------------------------------
  /**
   * Returns character properties belonging to the paragraph
   * 
   * @return characterproperties of the paragraph
   * 
   * @author Sebastian Rüsgen
   */
  public ICharacterProperties getCharacterProperties();
  //----------------------------------------------------------------------------
  /**
   * Gets the property store of the paragraph
   * 
   * @return the paragprah property store
   * 
   * @author Sebastian Rüsgen
   */
  public IParagraphPropertyStore getParagraphPropertyStore() throws TextException;
  //----------------------------------------------------------------------------
  /**
   * Gets the character property store of the paragraph
   * 
   * @return the paragprah's character property store
   * 
   * @author Sebastian Rüsgen
   */
  public ICharacterPropertyStore getCharacterPropertyStore() throws TextException;
  //----------------------------------------------------------------------------
  /**
   * Gets the text contained in this pragraph
   * 
   * @return the paragraph text
   * 
   * @author Sebastian Rüsgen 
   */
  public String getParagraphText() throws TextException;
  //---------------------------------------------------------------------------- 
  /**
   * Sets new text to the paragraph.
   * 
   * @param text the text that should be placed
   * 
   * @author Sebastian Rüsgen
   */
  public void setParagraphText(String text);
  //----------------------------------------------------------------------------
}