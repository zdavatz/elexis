/*******************************************************************************
 * Copyright (c) 2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 * 
 *  $Id: EnhancedTextField.java 6247 2010-03-21 06:36:34Z rgw_ch $
 *******************************************************************************/
package ch.elexis.text.model;

import java.io.CharArrayReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import ch.elexis.ElexisException;
import ch.elexis.Hub;
import ch.elexis.exchange.XChangeContainer;
import ch.elexis.text.model.Samdas.Record;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.TimeTool;

/**
 * SimpleStructuredText is an XML format to define structured texts. To simplify interpretation for
 * different readers with different capabilities, text and structure are strictly separated.
 * 
 * @author gerry
 * 
 */
public class SimpleStructuredDocument {
	// private static final String ATTR_TYPE = "type";
	private static final String ELEM_RANGE = "range";
	
	private static final String ATTR_TYPE_STRIKETHRU = "strikethru";
	private static final String ATTR_TYPE_UNDERLINED = "underlined";
	private static final String ATTR_TYPE_ITALIC = "italic";
	private static final String ATTR_TYPE_BOLD = "bold";
	private static final String ATTR_TYPE_EMPHASIZED = "emphasized";
	private static final String ATTR_LENGTH = "length";
	private static final String ATTR_SAMDAS_FROM = "from";
	public static final String VERSION = "1.0.0";
	public static final String GENERATOR = "Elexis";
	public static final String ELEM_ROOT = "SimpleStructuredDocument"; //$NON-NLS-1$
	public static final String ELEM_TEXT = "text"; //$NON-NLS-1$
	public static final String ELEM_RECORD = "record"; //$NON-NLS-1$
	private static final int EE_BASE = 100;
	public static final Namespace ns =
		Namespace.getNamespace("SimpleStructuredText", "http://www.elexis.ch/XSD"); //$NON-NLS-1$ //$NON-NLS-2$
	public static final Namespace nsxsi =
		Namespace.getNamespace("xsi", "http://www.w3.org/2001/XML Schema-instance"); //$NON-NLS-1$ //$NON-NLS-2$
	public static final Namespace nsschema =
		Namespace.getNamespace("schemaLocation", "http://www.elexis.ch/XSD sst.xsd"); //$NON-NLS-1$ //$NON-NLS-2$
	
	private final StringBuilder contents;
	private final ArrayList<SSDRange> ranges;
	private final List<SSDChangeListener> textChangeListeners = new ArrayList<SSDChangeListener>();
	
	public SimpleStructuredDocument(){
		contents = new StringBuilder();
		ranges = new ArrayList<SSDRange>();
	}
	
	/**
	 * Parse an input String. Can parse plain text, Samdas or SimpleStructuredDocument
	 * 
	 * @param input
	 * @param bAppend
	 *            if true, new input will appended. If false, current contents will be erased first.
	 * @throws ElexisException
	 *             if an XML input could not be parsed
	 */
	public void loadText(String input, boolean bAppend) throws ElexisException{
		if (!bAppend) {
			contents.setLength(0);
			ranges.clear();
		}
		if (input.startsWith("<")) {
			SAXBuilder builder = new SAXBuilder();
			try {
				CharArrayReader car = new CharArrayReader(input.toCharArray());
				Document doc = builder.build(car);
				Element eRoot = doc.getRootElement();
				if (eRoot.getName().equals(Samdas.ELEM_ROOT)) {
					parseSamdas(eRoot);
				} else if (eRoot.getName().equals(ELEM_ROOT)) {
					parseSSD(eRoot);
				}
			} catch (JDOMException jde) {
				ExHandler.handle(jde);
				throw new ElexisException(getClass(), "Cannot parse input " + jde.getMessage(),
					EE_BASE);
			} catch (IOException e) {
				ExHandler.handle(e);
				throw new ElexisException(getClass(), "Read error " + e.getMessage(), EE_BASE + 1);
			}
		} else {
			contents.append(input);
		}
	}
	
	public String getPlaintext(){
		return contents.toString();
	}
	
	private void parseSamdas(Element eRoot){
		Samdas samdas = new Samdas();
		eRoot.detach();
		samdas.setRoot(eRoot);
		Record record = samdas.getRecord();
		List<Samdas.XRef> xrefs = record.getXrefs();
		List<Samdas.Markup> markups = record.getMarkups();
		String text = record.getText();
		contents.append(text);
		for (Samdas.Markup m : markups) {
			SSDRange range =
				new SSDRange(m.getPos(), m.getLength(), SSDRange.TYPE_MARKUP, m.getType());
			ranges.add(range);
		}
		
	}
	
	private void parseSSD(Element eRoot){
		Element eText = eRoot.getChild(ELEM_TEXT, ns);
		contents.append(eText.getText());
		@SuppressWarnings("unchecked")
		List<Element> eRanges = eRoot.getChildren(ELEM_RANGE, ns);
		for (Element el : eRanges) {
			SSDRange range = new SSDRange(el);
			ranges.add(range);
		}
		
	}
	
	/**
	 * Convert the contents to a SimpleStructuredDocument file.
	 * 
	 * @param bCreateHeader
	 *            if false, a representation without header information is created
	 * @return
	 */
	public String toXML(boolean bCreateHeader){
		Document doc = new Document();
		Element eRoot = new Element(ELEM_ROOT, ns);
		if (bCreateHeader) {
			eRoot.setAttribute("created", new TimeTool().toString(TimeTool.DATETIME_XML));
			eRoot.setAttribute("lastEdit", new TimeTool().toString(TimeTool.DATETIME_XML));
			eRoot.setAttribute("createdBy", Hub.actMandant.getPersonalia());
			eRoot.setAttribute("editedBy", Hub.actUser.getPersonalia());
			eRoot.setAttribute("version", VERSION);
			eRoot.setAttribute("generator", Hub.APPLICATION_NAME);
			eRoot.setAttribute("generatorVersion", Hub.Version);
			eRoot.addNamespaceDeclaration(XChangeContainer.nsxsi);
			eRoot.addNamespaceDeclaration(XChangeContainer.nsschema);
			
		}
		Element eText = new Element(ELEM_TEXT, ns);
		eText.setText(contents.toString());
		doc.setRootElement(eRoot);
		eRoot.addContent(eText);
		for (SSDRange r : ranges) {
			Element el = r.toElement();
			eRoot.addContent(el);
			
		}
		XMLOutputter xout = new XMLOutputter(Format.getRawFormat());
		return xout.outputString(doc);
	}
	
	/**
	 * Insert some text
	 * 
	 * @param ins
	 *            the text to insert
	 * @param pos
	 *            start position for insert. If pos is larger than text length, it will be appended
	 *            at the end. ig pos is negative, nothing will be inserted. If ins is null, nothing
	 *            will be inserted.
	 */
	public void insertText(String ins, int pos){
		if (pos < 0 || ins == null) {
			return;
		}
		if (pos > contents.length()) {
			contents.append(ins);
		} else {
			for (SSDRange r : ranges) {
				if (!r.isLocked()) {
					int p = r.getPosition();
					int l = r.getLength();
					if (p < pos) {
						if (p + l < pos) {
							continue; // range is before insert position
						} else {
							r.setLength(l + ins.length());
						}
					} else {
						r.setPosition(p + ins.length());
					}
				}
			}
			contents.insert(pos, ins);
			for (SSDChangeListener tcl : textChangeListeners) {
				tcl.contentsChanged(pos);
			}
		}
	}
	
	/**
	 * remove some text
	 * 
	 * @param pos
	 *            position from which to remove
	 * @param len
	 *            length of text to remove
	 * @return the removed String
	 */
	public String remove(int pos, int len){
		if (pos > contents.length() || pos < 0) {
			return "";
		}
		int end = pos + len;
		for (SSDRange r : ranges) {
			int p = r.getPosition();
			int l = r.getLength();
			if (p < pos) {
				if (p + l > pos) {

				}
			} else {
				r.setPosition(p - len);
			}
		}
		String ret = contents.substring(pos, end);
		contents.delete(pos, end);
		for (SSDChangeListener ssdc : textChangeListeners) {
			ssdc.contentsChanged(pos);
		}
		return ret;
	}
	
	public void addRange(SSDRange r){
		ranges.add(r);
	}
	
	public List<SSDRange> getRanges(){
		return Collections.unmodifiableList(ranges);
	}
	
}
