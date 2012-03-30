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
package ch.elexis.text;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.actions.ActionFactory;

import ch.elexis.ApplicationActionBarAdvisor;
import ch.elexis.Desk;
import ch.elexis.ElexisException;
import ch.elexis.StringConstants;
import ch.elexis.actions.ElexisEvent;
import ch.elexis.actions.ElexisEventDispatcher;
import ch.elexis.actions.ElexisEventListener;
import ch.elexis.preferences.PreferenceConstants;
import ch.elexis.services.GlobalServiceDescriptors;
import ch.elexis.text.IRangeHandler.OUTPUT;
import ch.elexis.text.model.SSDRange;
import ch.elexis.text.model.SimpleStructuredDocument;
import ch.elexis.util.Extensions;
import ch.elexis.util.IKonsExtension;
import ch.rgw.tools.GenericRange;
import ch.rgw.tools.StringTool;

/**
 * This is a pop-in replacement for EnhancedTextField that can handle SimpleStructuredDocument
 * contents and for backwards compatibility also Samdas
 * 
 * @author Gerry Weirich
 */

public class EnhancedTextField2 extends AbstractRichTextDisplay {
	private StyledText st;
	private List<SSDRange> ranges;
	private final ElexisEventListener eeli_user = new UserChangeListener();
	private IMenuListener globalMenuListener;
	
	public EnhancedTextField2(Composite parent){
		super(parent);
	}
	
	@Override
	public void insertRange(SSDRange range){
		if (ranges == null) {
			ranges = new LinkedList<SSDRange>();
		}
		ranges.add(range);
		StyleRange sr = new StyleRange();
		sr.start = range.getPosition();
		sr.length = range.getLength();
		sr.data = range;
	}
	
	/**
	 * Contents will always be saved as SimpleStructuredDocument
	 */
	@Override
	public String getContentsAsXML(){
		return getContents().toXML(false);
	}
	
	@Override
	public String getContentsPlaintext(){
		return st.getText();
	}
	
	public SimpleStructuredDocument getContents(){
		SimpleStructuredDocument sd = new SimpleStructuredDocument();
		sd.insertText(st.getText(), 0);
		StyleRange[] ranges = st.getStyleRanges(true);
		for (StyleRange sr : ranges) {
			StringBuilder id = new StringBuilder();
			if (sr.underline) {
				id.append(SSDRange.STYLE_UNDERLINE).append(StringConstants.COMMA);
			}
			if ((sr.fontStyle & SWT.BOLD) != 0) {
				id.append(SSDRange.STYLE_BOLD).append(StringConstants.COMMA);
			}
			if ((sr.fontStyle & SWT.ITALIC) != 0) {
				id.append(SSDRange.STYLE_ITALIC).append(StringConstants.COMMA);
			}
			if (id.length() > 1) {
				id.deleteCharAt(id.length() - 1);
			}
			SSDRange r = new SSDRange(sr.start, sr.length, SSDRange.TYPE_MARKUP, id.toString());
			sd.addRange(r);
		}
		return sd;
	}
	
	@Override
	public GenericRange getSelectedRange(){
		Point pt = st.getSelection();
		return new GenericRange(pt.x, pt.y);
	}
	
	@Override
	public String getWordUnderCursor(){
		return StringTool.getWordAtIndex(st.getText(), st.getCaretOffset());
	}
	
	public void connectGlobalActions(IViewSite site){
		makeActions();
		IActionBars actionBars = site.getActionBars();
		actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(), copyAction);
		actionBars.setGlobalActionHandler(ActionFactory.CUT.getId(), cutAction);
		actionBars.setGlobalActionHandler(ActionFactory.PASTE.getId(), pasteAction);
		globalMenuListener = new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager){
				if (st.getSelectionCount() == 0) {
					copyAction.setEnabled(false);
					cutAction.setEnabled(false);
				} else {
					copyAction.setEnabled(true);
					cutAction.setEnabled(true);
				}
				
			}
		};
		ApplicationActionBarAdvisor.editMenu.addMenuListener(globalMenuListener);
		ElexisEventDispatcher.getInstance().addListeners(eeli_user);
	}
	
	public void disconnectGlobalActions(IViewSite site){
		IActionBars actionBars = site.getActionBars();
		actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(), null);
		actionBars.setGlobalActionHandler(ActionFactory.CUT.getId(), null);
		actionBars.setGlobalActionHandler(ActionFactory.PASTE.getId(), null);
		ApplicationActionBarAdvisor.editMenu.removeMenuListener(globalMenuListener);
		ElexisEventDispatcher.getInstance().removeListeners(eeli_user);
		
	}
	
	void doFormat(SimpleStructuredDocument ssd) throws ElexisException{
		st.setText(ssd.getPlaintext());
		for (SSDRange r : ssd.getRanges()) {
			IRangeHandler renderer = renderers.get(r.getType());
			if (renderer == null) {
				renderer =
					(IRangeHandler) Extensions.findBestService(
						GlobalServiceDescriptors.TEXT_CONTENTS_EXTENSION, r.getType());
				if (renderer != null) {
					renderers.put(r.getType(), renderer);
				}
			}
			if (renderer == null
				|| (!renderer.canRender(r.getType(), IRangeHandler.OUTPUT.STYLED_TEXT))) {
				String hint = r.getHint();
			} else {
				Object rendered = renderer.doRender(r, OUTPUT.STYLED_TEXT, this);
				if (rendered instanceof StyleRange) {
					StyleRange sr = (StyleRange) rendered;
					st.setStyleRange(sr);
					
				}
			}
		}
		
	}
	
	class UserChangeListener implements ElexisEventListener {
		ElexisEvent filter = new ElexisEvent(null, null, ElexisEvent.EVENT_USER_CHANGED);
		
		public void catchElexisEvent(ElexisEvent ev){
			Desk.asyncExec(new Runnable() {
				public void run(){
					st.setFont(Desk.getFont(PreferenceConstants.USR_DEFAULTFONT));
					
				}
			});
		}
		
		public ElexisEvent getElexisEventFilter(){
			return filter;
		}
		
	}
	
	private void makeActions(){
		// copyAction=ActionFactory.COPY.create();
		cutAction = new Action(Messages.EnhancedTextField_cutAction) {
			@Override
			public void run(){
				st.cut();
			}
			
		};
		pasteAction = new Action(Messages.EnhancedTextField_pasteAction) {
			@Override
			public void run(){
				st.paste();
			}
		};
		copyAction = new Action(Messages.EnhancedTextField_copyAction) {
			@Override
			public void run(){
				st.copy();
			}
		};
		
	}
	
}
