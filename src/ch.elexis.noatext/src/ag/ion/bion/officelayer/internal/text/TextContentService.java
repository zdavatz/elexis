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
 * Last changes made by $Author: markus $, $Date: 2007-07-09 18:46:56 +0200 (Mo, 09 Jul 2007) $
 */
package ag.ion.bion.officelayer.internal.text;

import ag.ion.bion.officelayer.text.IParagraph;
import ag.ion.bion.officelayer.text.ITextContent;
import ag.ion.bion.officelayer.text.ITextContentService;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.ITextDocumentImage;
import ag.ion.bion.officelayer.text.ITextRange;
import ag.ion.bion.officelayer.text.ITextTable;
import ag.ion.bion.officelayer.text.TextException;
import ag.ion.noa.graphic.GraphicInfo;

import com.sun.star.beans.XPropertySet;
import com.sun.star.container.XNameContainer;
import com.sun.star.lang.XMultiServiceFactory;

import com.sun.star.text.XRelativeTextContentInsert;
import com.sun.star.text.XText;
import com.sun.star.text.XTextContent;
import com.sun.star.text.XTextRange;
import com.sun.star.text.XTextTable;

import com.sun.star.uno.AnyConverter;
import com.sun.star.uno.UnoRuntime;

import java.util.HashMap;
import java.util.Map;

/**
 * Content service implementation of a text document.
 * 
 * @author Andreas Bröcker
 * @version $Revision: 11509 $
 */
public class TextContentService implements ITextContentService {

  private ITextDocument textDocument = null;
  
  private XText                 xText                = null;
  private XMultiServiceFactory  xMultiServiceFactory = null;
  private XNameContainer        xBitmapContainer     = null; 
  
  private Map<ITextDocumentImage,String> imageToImageIds = null;
    
  //----------------------------------------------------------------------------
  /**
   * Constructs new TextContentService.
   * 
   * @param textDocument text document to be used
   * @param xText OpenOffice.org XText interface
   * @param xMultiServiceFactory OpenOffice.org XMultiServiceFactory interface
   *  
   * @throws IllegalArgumentException if the submitted text document or OpenOffice.org XText interface 
   * is not valid
   * 
   * @author Andreas Bröcker
   * @author Sebastian Rüsgen
   */
  public TextContentService(ITextDocument textDocument, XMultiServiceFactory xMultiServiceFactory, XText xText) throws IllegalArgumentException {
    if(xText == null)
      throw new IllegalArgumentException("Submitted OpenOffice.org XText interface is not valid.");
    if(textDocument == null)
      throw new IllegalArgumentException("Submitted text document is not valid.");
    this.xText = xText;
    this.xMultiServiceFactory = xMultiServiceFactory;
    this.textDocument = textDocument;
  }
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
  public IParagraph constructNewParagraph() throws TextException {
    try {
      if(xMultiServiceFactory == null)
        throw new TextException("OpenOffice.org XMultiServiceFactory inteface not valid.");
      Object object = xMultiServiceFactory.createInstance("com.sun.star.text.Paragraph");
      XTextContent xTextContent = (XTextContent)UnoRuntime.queryInterface(XTextContent.class, object);
			
      return new Paragraph(textDocument, xTextContent);
    }
    catch(Exception exception) {
      TextException textException = new TextException(exception.getMessage());
      textException.initCause(exception);
      throw textException;
    }
  }
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
  public ITextDocumentImage constructNewImage(GraphicInfo graphicInfo) throws TextException {
    try {
      if(xMultiServiceFactory == null)
        throw new TextException("OpenOffice.org XMultiServiceFactory inteface not valid."); 

      if(xBitmapContainer == null)
        xBitmapContainer = (XNameContainer)UnoRuntime.queryInterface(XNameContainer.class, 
            xMultiServiceFactory.createInstance("com.sun.star.drawing.BitmapTable")); 

      XTextContent xImage = (XTextContent)UnoRuntime.queryInterface(XTextContent.class, 
          xMultiServiceFactory.createInstance("com.sun.star.text.TextGraphicObject")); 
      
      XPropertySet xProps = (XPropertySet)UnoRuntime.queryInterface(XPropertySet.class, xImage); 

      String tempId = "tempImageId"+System.currentTimeMillis();
      xBitmapContainer.insertByName(tempId, graphicInfo.getUrl()); 
      String internalURL = AnyConverter.toString(xBitmapContainer.getByName(tempId)); 

      xProps.setPropertyValue("AnchorType", graphicInfo.getAnchor()); 
      xProps.setPropertyValue("GraphicURL", internalURL); 
      xProps.setPropertyValue("Width", Integer.valueOf(graphicInfo.getWidth())); 
      xProps.setPropertyValue("Height", Integer.valueOf(graphicInfo.getHeight())); 
      xProps.setPropertyValue("HoriOrient", Short.valueOf(graphicInfo.getHorizontalAlignment())); 
      xProps.setPropertyValue("VertOrient", Short.valueOf(graphicInfo.getVerticalAlignment()));    
      
      ITextDocumentImage textDocumentImage = new TextDocumentImage(textDocument, xImage, graphicInfo);
      
      if(imageToImageIds == null)
        imageToImageIds = new HashMap<ITextDocumentImage,String>();
      imageToImageIds.put(textDocumentImage, tempId);
      
      return textDocumentImage;
    }
    catch(Exception exception) {
      TextException textException = new TextException(exception.getMessage());
      textException.initCause(exception);
      throw textException;
    }
  }  
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
  public void insertTextContent(ITextContent textContent) throws TextException {
    try {     
      xText.insertTextContent(xText.getStart(), textContent.getXTextContent(), true);
      if(textContent instanceof ITextDocumentImage && imageToImageIds != null && xBitmapContainer != null) {
        String id = imageToImageIds.get(textContent);
        if(id != null) {
          imageToImageIds.remove(textContent);          
          xBitmapContainer.removeByName(id); 
        }
        ((ITextDocumentImage)textContent).getGraphicInfo().cleanUp();
      }
    }
    catch(Exception exception) {
      TextException textException = new TextException(exception.getMessage());
      textException.initCause(exception);
      throw textException;
    }
  }
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
  public void removeTextContent(ITextContent textContent) throws TextException {
    try {     
      xText.removeTextContent(textContent.getXTextContent());
    }
    catch(Exception exception) {
      TextException textException = new TextException(exception.getMessage());
      textException.initCause(exception);
      throw textException;
    }
  }
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
  public void insertTextContent(ITextRange textRange, ITextContent textContent) throws TextException {
    try {
    	if (textContent instanceof IParagraph) {
      	XMultiServiceFactory factory = (XMultiServiceFactory)UnoRuntime.queryInterface(XMultiServiceFactory.class, textDocument.getXTextDocument());
      	Object object = factory.createInstance("com.sun.star.text.TextTable");
      	XTextTable xTable = (XTextTable)UnoRuntime.queryInterface(XTextTable.class, object);
      	xTable.initialize(1,1);
      	xText.insertTextContent(textRange.getXTextRange(), xTable, false);
      	XRelativeTextContentInsert xRelativeTextContentInsert = (XRelativeTextContentInsert)UnoRuntime.queryInterface(XRelativeTextContentInsert.class, xText);
        xRelativeTextContentInsert.insertTextContentAfter(textContent.getXTextContent(), xTable);
      	xText.removeTextContent(xTable);
    	}
    	else {
	      XText xText = textRange.getXTextRange().getText();
	      XTextRange targetRange = textRange.getXTextRange();
	      XTextContent xTextContent = textContent.getXTextContent();
				xText.insertTextContent(targetRange, xTextContent , true);
				if(textContent instanceof ITextDocumentImage && imageToImageIds != null && xBitmapContainer != null) {
	        String id = imageToImageIds.get(textContent);
	        if(id != null) {
	          imageToImageIds.remove(textContent);          
	          xBitmapContainer.removeByName(id); 
	        }
	        ((ITextDocumentImage)textContent).getGraphicInfo().cleanUp();
	      }
    	}
    }
    catch(Exception exception) {
      TextException textException = new TextException(exception.getMessage());
      textException.initCause(exception);
      throw textException;
    }    
  }
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
  public void insertTextContentBefore(ITextContent newTextContent, ITextContent textContent) throws TextException {
    try {
      XRelativeTextContentInsert xRelativeTextContentInsert = (XRelativeTextContentInsert)UnoRuntime.queryInterface(XRelativeTextContentInsert.class, xText);
      if(newTextContent instanceof ITextTable) {
        IParagraph paragraph = constructNewParagraph();
        xRelativeTextContentInsert.insertTextContentBefore(paragraph.getXTextContent(), textContent.getXTextContent());
        xText.insertTextContent(paragraph.getXTextContent().getAnchor(), newTextContent.getXTextContent(), false);
        xText.removeTextContent(paragraph.getXTextContent());
      }
      else {
        xRelativeTextContentInsert.insertTextContentBefore(newTextContent.getXTextContent(), textContent.getXTextContent());
        if(textContent instanceof ITextDocumentImage && imageToImageIds != null && xBitmapContainer != null) {
          String id = imageToImageIds.get(textContent);
          if(id != null) {
            imageToImageIds.remove(textContent);          
            xBitmapContainer.removeByName(id); 
          }
          ((ITextDocumentImage)textContent).getGraphicInfo().cleanUp();
        }
      }
    }
    catch(Exception exception) {
      TextException textException = new TextException(exception.getMessage());
      textException.initCause(exception);
      throw textException;
    }    
  }
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
  public void insertTextContentAfter(ITextContent newTextContent, ITextContent textContent) throws TextException {
    try {
      XRelativeTextContentInsert xRelativeTextContentInsert = (XRelativeTextContentInsert)UnoRuntime.queryInterface(XRelativeTextContentInsert.class, xText);
      if(newTextContent instanceof ITextTable) {
        IParagraph paragraph = constructNewParagraph();
        XTextContent oldXTextContent = textContent.getXTextContent();
        xRelativeTextContentInsert.insertTextContentAfter(paragraph.getXTextContent(), oldXTextContent);
        xText.insertTextContent(paragraph.getXTextContent().getAnchor(), newTextContent.getXTextContent(), false);
        xText.removeTextContent(paragraph.getXTextContent());
      }
      else if (newTextContent instanceof IParagraph) {      	
        if (textContent instanceof ITextTable) {
          XTextContent newXTextContent = newTextContent.getXTextContent();
          XTextContent oldXTextContent = textContent.getXTextContent();
          xRelativeTextContentInsert.insertTextContentAfter(newXTextContent, oldXTextContent);
        }
        else {
          XMultiServiceFactory factory = (XMultiServiceFactory)UnoRuntime.queryInterface(XMultiServiceFactory.class, textDocument.getXTextDocument());
          Object object = factory.createInstance("com.sun.star.text.TextTable");
          XTextTable xTable = (XTextTable)UnoRuntime.queryInterface(XTextTable.class, object);
          xTable.initialize(1,1);
          xText.insertTextContent(textContent.getXTextContent().getAnchor(), xTable, false);
          xRelativeTextContentInsert.insertTextContentAfter(newTextContent.getXTextContent(), xTable);
          xText.removeTextContent(xTable);
        }
      }
      else {
      	XTextContent newContent = newTextContent.getXTextContent();
      	XTextContent successor = textContent.getXTextContent();
        xRelativeTextContentInsert.insertTextContentAfter(newContent, successor);
      	//xRelativeTextContentInsert.insertTextContentAfter(textContent.getXTextContent(), newTextContent.getXTextContent());
        if(textContent instanceof ITextDocumentImage && imageToImageIds != null && xBitmapContainer != null) {
          String id = imageToImageIds.get(textContent);
          if(id != null) {
            imageToImageIds.remove(textContent);          
            xBitmapContainer.removeByName(id); 
          }
          ((ITextDocumentImage)textContent).getGraphicInfo().cleanUp();
        }
      }
    }
    catch(Exception exception) {
      TextException textException = new TextException(exception.getMessage());
      textException.initCause(exception);
      throw textException;
    }
  }
  //----------------------------------------------------------------------------
  
}