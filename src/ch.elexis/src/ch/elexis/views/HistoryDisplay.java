/*******************************************************************************
 * Copyright (c) 2006-2010, G. Weirich, D. Lutz, P. Schönbucher and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *  $Id: HistoryDisplay.java 5970 2010-01-27 16:43:04Z rgw_ch $
 *******************************************************************************/

package ch.elexis.views;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormText;

import ch.elexis.Desk;
import ch.elexis.Hub;
import ch.elexis.actions.BackgroundJob;
import ch.elexis.actions.ElexisEvent;
import ch.elexis.actions.ElexisEventDispatcher;
import ch.elexis.actions.ElexisEventListener;
import ch.elexis.actions.HistoryLoader;
import ch.elexis.actions.KonsFilter;
import ch.elexis.actions.BackgroundJob.BackgroundJobListener;
import ch.elexis.data.Fall;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Patient;
import ch.elexis.preferences.PreferenceConstants;
import ch.elexis.util.Log;
import ch.rgw.tools.ExHandler;

/**
 * Anzeige der vergangenen Konsultationen. Es sollen einerseits "sofort" die letzten 3 oder 4 Kons
 * angezeigt werden, andererseits aber je nach Anforderung auch frühere nachgeladen werden. Dies ist
 * noch nicht korrekt implemetiert - aktuell werden immer alle Kons. geladen.
 * 
 * @author Gerry
 * 
 */
public class HistoryDisplay extends ScrolledComposite implements BackgroundJobListener,
		ElexisEventListener {
	FormText text;
	ArrayList<Konsultation> lKons;
	StringBuilder sb;
	HistoryLoader loader;
	private boolean bLock;
	HistoryDisplay self = this;
	
	boolean multiline = false;
	
	public HistoryDisplay(Composite parent, final IViewSite site){
		this(parent, site, false);
	}
	
	public HistoryDisplay(Composite parent, final IViewSite site, boolean multiline){
		super(parent, SWT.V_SCROLL | SWT.BORDER);
		this.multiline = multiline;
		lKons = new ArrayList<Konsultation>(20);
		text = Desk.getToolkit().createFormText(this, false);
		text.setWhitespaceNormalized(true);
		text.setColor(Desk.COL_BLUE, Desk.getColorRegistry().get(Desk.COL_BLUE));
		text.setColor(Desk.COL_GREEN, Desk.getColorRegistry().get(Desk.COL_LIGHTGREY));
		setContent(text);
		text.addHyperlinkListener(new HyperlinkAdapter() {
			
			@Override
			public void linkActivated(HyperlinkEvent e){
				String id = (String) e.getHref();
				Konsultation k = Konsultation.load(id);
				ElexisEventDispatcher.fireSelectionEvent(k);
			}
			
		});
		text.setText(Messages.getString("HistoryDisplay.NoPatientSelected"), false, false); //$NON-NLS-1$
		sb = new StringBuilder(1000);
		addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e){
				text.setSize(text.computeSize(self.getSize().x - 15, SWT.DEFAULT));
			}
			
		});
		ElexisEventDispatcher.getInstance().addListeners(this);
	}
	
	@Override
	public void dispose(){
		ElexisEventDispatcher.getInstance().removeListeners(this);
		super.dispose();
	}
	
	public void setFilter(KonsFilter f){
		stop();
		loader.setFilter(f);
	}
	
	public void start(){
		start(null);
	}
	
	public void start(KonsFilter f){
		stop();
		sb.setLength(0);
		loader = new HistoryLoader(sb, lKons, multiline);
		loader.setFilter(f);
		loader.addListener(this);
		loader.schedule();
	}
	
	public void stop(){
		if (loader != null) {
			loader.removeListener(this);
			loader.cancel();
			loader = null;
		}
	}
	
	public void load(Fall fall, boolean clear){
		if (clear) {
			lKons.clear();
		}
		if (fall != null) {
			Konsultation[] kons = fall.getBehandlungen(true);
			for (Konsultation k : kons) {
				lKons.add(k);
			}
		}
	}
	
	public void load(Patient pat){
		if (pat != null) {
			lKons.clear();
			Fall[] faelle = pat.getFaelle();
			for (Fall f : faelle) {
				load(f, false);
			}
		}
	}
	
	public void jobFinished(BackgroundJob j){
		Desk.getDisplay().asyncExec(new Runnable() {
			public void run(){
				String s = (String) loader.getData();
				// System.out.println(s);
				
				// check if widget is valid
				if (!isDisposed()) {
					text.setText(s, true, true);
					text.setSize(text.computeSize(self.getSize().x - 10, SWT.DEFAULT));
				}
				
			}
		});
	}
	
	public void catchElexisEvent(ElexisEvent ev){
		Desk.asyncExec(new Runnable() {
			
			public void run(){
				if (text != null && (!text.isDisposed())) {
					text.setFont(Desk.getFont(PreferenceConstants.USR_DEFAULTFONT));
				}
			}
		});
	}
	
	private final ElexisEvent eetemplate =
		new ElexisEvent(null, null, ElexisEvent.EVENT_USER_CHANGED);
	
	public ElexisEvent getElexisEventFilter(){
		return eetemplate;
	}
}
