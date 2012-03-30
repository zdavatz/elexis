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
package ag.ion.bion.officelayer.internal.text;

import ag.ion.bion.officelayer.clone.CloneException;
import ag.ion.bion.officelayer.clone.ICloneService;
import ag.ion.bion.officelayer.text.AbstractTextComponent;
import ag.ion.bion.officelayer.text.ICharacterProperties;
import ag.ion.bion.officelayer.text.ICharacterPropertyStore;
import ag.ion.bion.officelayer.text.IParagraph;
import ag.ion.bion.officelayer.text.IParagraphProperties;
import ag.ion.bion.officelayer.text.IParagraphPropertyStore;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.ITextRange;
import ag.ion.bion.officelayer.text.TextException;

import com.sun.star.beans.XPropertySet;

import com.sun.star.container.XEnumeration;
import com.sun.star.container.XEnumerationAccess;

import com.sun.star.text.XText;
import com.sun.star.text.XTextContent;
import com.sun.star.text.XTextRange;

import com.sun.star.uno.Any;
import com.sun.star.uno.UnoRuntime;

/**
 * Paragraph of a text document.
 * 
 * @author Andreas Bröcker
 * @version $Revision: 11559 $
 */
public class Paragraph extends AbstractTextComponent implements IParagraph {
  
  private XTextContent xTextContent = null;

  //----------------------------------------------------------------------------
  /**
   * Constructs new Paragraph.
   * 
   * @param textDocument text document to be used
   * @param xTextContent OpenOffice.org XTextContent interface
   *  
   * @throws IllegalArgumentException if the OpenOffice.org interface or the document is not valid
   * 
   * @author Andreas Bröcker
   * @author Sebastian Rüsgen
   */
  public Paragraph(ITextDocument textDocument, XTextContent xTextContent) throws IllegalArgumentException {
    super(textDocument);
    if(xTextContent == null)
      throw new IllegalArgumentException("Submitted OpenOffice.org XTextContent interface is not valid.");
    this.xTextContent = xTextContent; 
  }  
  //----------------------------------------------------------------------------
  /**
   * Returns OpenOffice.org XTextContent interface.
   * 
   * @return OpenOffice.org XTextContent interface
   * 
   * @author Andreas Bröcker
   */
  public XTextContent getXTextContent() {
    return xTextContent;
  }
  //----------------------------------------------------------------------------
  /**
   * Returns text range of the text table.
   * 
   * @return text range of the text table
   * 
   * @author Markus Krüger
   * @date 06.08.2007
   */
  public ITextRange getTextRange() {
    return new TextRange(textDocument, xTextContent.getAnchor());
  }
  //----------------------------------------------------------------------------
  /**
   * Returns properties of the paragraph.
   * 
   * @return properties of the paragraph
   * 
   * @author Andreas Bröcker
   */
  public IParagraphProperties getParagraphProperties() {
    XPropertySet xPropertySet = (XPropertySet)UnoRuntime.queryInterface(XPropertySet.class, xTextContent);
    return new ParagraphProperties(xPropertySet);
  }
  //----------------------------------------------------------------------------
  /**
   * Returns character properties belonging to the paragraph
   * 
   * @return characterproperties of the paragraph
   * 
   * @author Sebastian Rüsgen
   */
  public ICharacterProperties getCharacterProperties() {
  	XPropertySet xPropertySet = (XPropertySet)UnoRuntime.queryInterface(XPropertySet.class, xTextContent);
    return new CharacterProperties(xPropertySet);
  }
  //----------------------------------------------------------------------------
  /**
   * Gets the property store of the paragraph
   * 
   * @return the paragprah property store
   * 
   * @throws TextException if any error occurs 
   * 
   * @author Sebastian Rüsgen
   */
  public IParagraphPropertyStore getParagraphPropertyStore() throws TextException{
  	return new ParagraphPropertyStore(this);
  }
  //----------------------------------------------------------------------------
  /**
   * Gets the character property store of the paragraph
   * 
   * @return the paragraph's character property store
   * 
   * @throws TextException if any error occurs getting the store
   * 
   * @author Sebastian Rüsgen
   */
  public ICharacterPropertyStore getCharacterPropertyStore() throws TextException{
  	return new CharacterPropertyStore(this);
  }
  //----------------------------------------------------------------------------
  /**
   * Gets the clone service of the element.
   * 
   * @return the clone service
   * 
   * @throws CloneException if the clone service could not be returned
   * 
   * @author Markus Krüger
   */
  public ICloneService getCloneService() throws CloneException {
  	return new ParagraphCloneService(this, textDocument);
  }
  //----------------------------------------------------------------------------
  /**
   * Gets the text contained in this pragraph
   * 
   * @return the paragraph text or null if text cannot be gained
   * 
   * @throws TextException if there occurs an error while fetching the text
   * 
   * @author Sebastian Rüsgen 
   */
  public String getParagraphText() throws TextException {
  	StringBuffer buffer = new StringBuffer();
  	XEnumerationAccess contentEnumerationAccess = (XEnumerationAccess)UnoRuntime.queryInterface(XEnumerationAccess.class, xTextContent);
  	XEnumeration enumeration = contentEnumerationAccess.createEnumeration();
  	
  	while (enumeration.hasMoreElements()) {
  		try {
  			Any any = (Any)enumeration.nextElement();
  			XTextRange content= (XTextRange)any.getObject();
  			
  			// since one paragraph can be made out of several portions, we have to put'em together
  			buffer.append(content.getString());
  		}
  		catch(Exception exception) {
  			System.out.println("Error getting elements from enumeration while search paragraph text.");
  		}
  	}
  	
  	return buffer.toString();
  }
  //---------------------------------------------------------------------------- 
  /**
   * Sets new text to the paragraph.
   * 
   * @param text the text that should be placed
   * 
   * @author Sebastian Rüsgen
   */
  public void setParagraphText(String text) {
  	if (text != null) {
	  	XTextRange anchor = xTextContent.getAnchor();
			XText xText = anchor.getText();
			xText.insertString(xTextContent.getAnchor(),text,false);
  	}
  }
  //----------------------------------------------------------------------------
  
}