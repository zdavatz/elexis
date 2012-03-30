/*******************************************************************************
 * Copyright (c) 2007-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    M. Imhof - initial implementation
 *    
 *  $Id: EtiketteDruckenDialog.java 6237 2010-03-19 11:24:39Z michael_imhof $
 *******************************************************************************/
package ch.elexis.dialogs;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import ch.elexis.Hub;
import ch.elexis.data.Brief;
import ch.elexis.data.Kontakt;
import ch.elexis.text.TextContainer;
import ch.elexis.text.ITextPlugin.ICallback;
import ch.elexis.util.SWTHelper;

public class EtiketteDruckenDialog extends TitleAreaDialog implements ICallback {
	final Kontakt kontakt;
	final String template;
	
	String title = "Etikette";
	String message = "Etikette ausdrucken";
	
	private TextContainer text = null;
	
	public EtiketteDruckenDialog(final Shell _shell, final Kontakt _kontakt, final String _template){
		super(_shell);
		this.kontakt = _kontakt;
		this.template = _template;
	}
	
	public void setMessage(String newMessage){
		this.message = newMessage;
	}
	
	public void setTitle(String newTitle){
		this.title = newTitle;
	}
	
	@Override
	protected Control createDialogArea(Composite parent){
		Composite ret = new Composite(parent, SWT.NONE);
		ret.setLayout(new FillLayout());
		ret.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		
		text = new TextContainer(getShell());
		text.getPlugin().createContainer(ret, this);
		text.getPlugin().showMenu(false);
		text.getPlugin().showToolbar(false);
		text.createFromTemplateName(null, template, Brief.UNKNOWN, kontakt, title);
		if (text.getPlugin().isDirectOutput()) {
			text.getPlugin().print(null, null, true);
			okPressed();
		}
		return ret;
	}
	
	@Override
	public void create(){
		super.create();
		super.setTitle(title);
		super.setMessage(message);
		getShell().setText("Etikette");
		getShell().setSize(800, 700);
	}
	
	@Override
	public void save(){
	// Do nothing
	}
	
	@Override
	public boolean saveAs(){
		return false;
	}
	
	public boolean doPrint(){
		if (text == null) {
			// text container is not initialized
			return false;
		}
		
		String printer = Hub.localCfg.get("Drucker/Etiketten/Name", "");
		String tray = Hub.localCfg.get("Drucker/Etiketten/Schacht", null);
		
		return text.getPlugin().print(printer, tray, false);
	}
}
