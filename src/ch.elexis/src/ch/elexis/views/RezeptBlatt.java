/*******************************************************************************
 * Copyright (c) 2006-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 * 
 *  $Id: RezeptBlatt.java 6227 2010-03-18 14:02:17Z michael_imhof $
 *******************************************************************************/

package ch.elexis.views;

import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import ch.elexis.Desk;
import ch.elexis.actions.ElexisEventDispatcher;
import ch.elexis.actions.GlobalEventDispatcher;
import ch.elexis.actions.GlobalEventDispatcher.IActivationListener;
import ch.elexis.data.Brief;
import ch.elexis.data.Konsultation;
import ch.elexis.data.OutputLog;
import ch.elexis.data.Patient;
import ch.elexis.data.Prescription;
import ch.elexis.data.Rezept;
import ch.elexis.exchange.IOutputter;
import ch.elexis.text.TextContainer;
import ch.elexis.text.ITextPlugin.ICallback;
import ch.rgw.tools.StringTool;

public class RezeptBlatt extends ViewPart implements ICallback, IActivationListener, IOutputter {
	public final static String ID = "ch.elexis.RezeptBlatt"; //$NON-NLS-1$
	TextContainer text;
	Brief actBrief;
	
	public RezeptBlatt(){

	}
	
	@Override
	public void dispose(){
		GlobalEventDispatcher.removeActivationListener(this, this);
		super.dispose();
	}
	
	@Override
	public void createPartControl(Composite parent){
		text = new TextContainer(getViewSite());
		text.getPlugin().createContainer(parent, this);
		GlobalEventDispatcher.addActivationListener(this, this);
	}
	
	@Override
	public void setFocus(){
	// TODO Automatisch erstellter Methoden-Stub
	
	}
	
	public boolean createList(Rezept rp, String template, String replace){
		actBrief =
			text.createFromTemplateName(Konsultation.getAktuelleKons(), template, Brief.RP,
				(Patient) ElexisEventDispatcher.getSelected(Patient.class), null);
		List<Prescription> lines = rp.getLines();
		String[][] fields = new String[lines.size()][];
		int[] wt = new int[] {
			10, 70, 20
		};
		for (int i = 0; i < fields.length; i++) {
			Prescription p = lines.get(i);
			fields[i] = new String[3];
			fields[i][0] = p.get(Messages.getString("RezeptBlatt.number")); //$NON-NLS-1$
			String bem = p.getBemerkung();
			if (StringTool.isNothing(bem)) {
				fields[i][1] = p.getSimpleLabel();
			} else {
				fields[i][1] = p.getSimpleLabel() + "\n" + bem; //$NON-NLS-1$
			}
			fields[i][2] = p.getDosis();
			
		}
		rp.setBrief(actBrief);
		if (text.getPlugin().insertTable(replace, 0, fields, wt)) {
			if (text.getPlugin().isDirectOutput()) {
				text.getPlugin().print(null, null, true);
				getSite().getPage().hideView(this);
			}
			return true;
		}
		return false;
	}
	
	public boolean createRezept(Rezept rp){
		if (createList(
			rp,
			Messages.getString("RezeptBlatt.TemplateNamePrescription"), Messages.getString("RezeptBlatt.4"))) { //$NON-NLS-1$ //$NON-NLS-2$
			new OutputLog(rp, this);
			return true;
		}
		return false;
	}
	
	public boolean createEinnahmeliste(Patient pat, Prescription[] pres){
		Rezept rp = new Rezept(pat);
		for (Prescription p : pres) {
			/*
			 * rp.addLine(new RpZeile(" ",p.getArtikel().getLabel(),"",
			 * p.getDosis(),p.getBemerkung()));
			 */
			rp.addPrescription(new Prescription(p));
		}
		return createList(rp,
			Messages.getString("RezeptBlatt.TemplateNameList"), Messages.getString("RezeptBlatt.6")); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public void save(){
		if (actBrief != null) {
			actBrief.save(text.getPlugin().storeToByteArray(), text.getPlugin().getMimeType());
		}
	}
	
	public boolean saveAs(){
		// TODO Automatisch erstellter Methoden-Stub
		return false;
	}
	
	public void activation(boolean mode){
		if (mode == false) {
			save();
		}
		
	}
	
	public void visible(boolean mode){

	}
	
	public String getOutputterDescription(){
		return "Druckerausgabe erstellt";
	}
	
	public String getOutputterID(){
		return "ch.elexis.RezeptBlatt";
	}
	
	public Image getSymbol(){
		return Desk.getImage(Desk.IMG_PRINTER);
	}
}
