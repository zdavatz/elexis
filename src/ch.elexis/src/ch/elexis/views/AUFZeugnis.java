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
 *  $Id: AUFZeugnis.java 6231 2010-03-18 14:03:43Z michael_imhof $
 *******************************************************************************/

package ch.elexis.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import ch.elexis.Desk;
import ch.elexis.actions.GlobalEventDispatcher;
import ch.elexis.actions.GlobalEventDispatcher.IActivationListener;
import ch.elexis.data.AUF;
import ch.elexis.data.Brief;
import ch.elexis.data.Konsultation;
import ch.elexis.text.TextContainer;
import ch.elexis.text.ITextPlugin.ICallback;
import ch.elexis.text.ITextPlugin.PageFormat;

public class AUFZeugnis extends ViewPart implements ICallback, IActivationListener {
	public static final String ID = "ch.elexis.AUFView"; //$NON-NLS-1$
	TextContainer text;
	Brief actBrief;
	
	public AUFZeugnis(){}
	
	@Override
	public void dispose(){
		GlobalEventDispatcher.removeActivationListener(this, this);
		super.dispose();
	}
	
	@Override
	public void createPartControl(Composite parent){
		setTitleImage(Desk.getImage(Desk.IMG_PRINTER));
		text = new TextContainer(getViewSite());
		text.getPlugin().createContainer(parent, this);
		GlobalEventDispatcher.addActivationListener(this, this);
	}
	
	@Override
	public void setFocus(){
		text.setFocus();
	}
	
	public void createAUZ(final AUF auf){
		actBrief =
			text.createFromTemplateName(Konsultation.getAktuelleKons(), "AUF-Zeugnis", Brief.AUZ, //$NON-NLS-1$
				null, null);
		// text.getPlugin().setFormat(PageFormat.A5);
		if (text.getPlugin().isDirectOutput()) {
			text.getPlugin().print(null, null, true);
			getSite().getPage().hideView(this);
		}
	}
	
	public TextContainer getTextContainer(){
		return text;
	}
	
	public void save(){
		if (actBrief != null) {
			actBrief.save(text.getPlugin().storeToByteArray(), text.getPlugin().getMimeType());
		}
	}
	
	public boolean saveAs(){
		return true;
	}
	
	public void activation(boolean mode){
		if (mode == false) {
			save();
		}
	}
	
	public void visible(boolean mode){}
	
}
