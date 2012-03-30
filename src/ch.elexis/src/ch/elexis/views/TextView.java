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
 *  $Id: TextView.java 6228 2010-03-18 14:02:57Z michael_imhof $
 *******************************************************************************/

package ch.elexis.views;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.part.ViewPart;

import ch.elexis.Desk;
import ch.elexis.Hub;
import ch.elexis.actions.ElexisEventDispatcher;
import ch.elexis.actions.GlobalEventDispatcher;
import ch.elexis.actions.GlobalEventDispatcher.IActivationListener;
import ch.elexis.admin.AccessControlDefaults;
import ch.elexis.data.Brief;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Patient;
import ch.elexis.data.Person;
import ch.elexis.dialogs.DocumentSelectDialog;
import ch.elexis.text.ITextPlugin;
import ch.elexis.text.TextContainer;
import ch.elexis.util.Log;
import ch.elexis.util.SWTHelper;
import ch.elexis.util.ViewMenus;
import ch.rgw.io.FileTool;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.MimeTool;

public class TextView extends ViewPart implements IActivationListener {
	public final static String ID = "ch.elexis.TextView"; //$NON-NLS-1$
	TextContainer txt;
	// CommonViewer cv;
	Composite textContainer = null;
	private Brief actBrief;
	private Log log = Log.get("TextView"); //$NON-NLS-1$
	private IAction briefLadenAction, loadTemplateAction, loadSysTemplateAction,
			saveTemplateAction, showMenuAction, showToolbarAction, importAction, newDocAction,
			exportAction;
	private ViewMenus menus;
	
	public TextView(){}
	
	@Override
	public void createPartControl(Composite parent){
		txt = new TextContainer(getViewSite());
		textContainer = txt.getPlugin().createContainer(parent, new SaveHandler());
		if (textContainer == null) {
			SWTHelper
				.showError(
					Messages.getString("TextView.couldNotCreateTextView"), Messages.getString("TextView.couldNotLoadTextPlugin")); //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			makeActions();
			menus = new ViewMenus(getViewSite());
			// menus.createToolbar(briefNeuAction);
			menus.createMenu(newDocAction, briefLadenAction, loadTemplateAction,
				loadSysTemplateAction, saveTemplateAction, null, showMenuAction, showToolbarAction,
				null, importAction, exportAction);
			GlobalEventDispatcher.addActivationListener(this, this);
			setName();
		}
	}
	
	@Override
	public void setFocus(){
		if (textContainer != null) {
			textContainer.setFocus();
		}
	}
	
	public TextContainer getTextContainer(){
		return txt;
	}
	
	@Override
	public void dispose(){
		GlobalEventDispatcher.removeActivationListener(this, this);
		actBrief = null;
		super.dispose();
	}
	
	public boolean openDocument(Brief doc){
		if (txt.open(doc) == true) {
			actBrief = doc;
			setName();
			return true;
		} else {
			actBrief = null;
			setName();
			String ext = MimeTool.getExtension(doc.getMimeType());
			if (ext.length() == 0) {
				ext = "ods";
			}
			try {
				File tmp = File.createTempFile("elexis", "brief." + ext);
				tmp.deleteOnExit();
				ByteArrayInputStream bais = new ByteArrayInputStream(doc.loadBinary());
				FileOutputStream fos = new FileOutputStream(tmp);
				FileTool.copyStreams(bais, fos);
				return Program.launch(tmp.getAbsolutePath());
			} catch (IOException e) {
				ExHandler.handle(e);
			}
			
			return false;
		}
	}
	
	/**
	 * Ein Document von Vorlage erstellen.
	 * 
	 * @param template
	 *            die Vorlage
	 * @param subject
	 *            Titel, kann null sein
	 * @return true bei erfolg
	 */
	public boolean createDocument(Brief template, String subject){
		if (template == null) {
			SWTHelper
				.showError(
					Messages.getString("TextView.noTemplateSelected"), Messages.getString("TextView.pleaseSelectTemplate")); //$NON-NLS-1$ //$NON-NLS-2$
			return false;
		}
		actBrief =
			txt.createFromTemplate(Konsultation.getAktuelleKons(), template, Brief.UNKNOWN, null,
				subject);
		setName();
		if (actBrief == null) {
			return false;
		}
		return true;
	}
	
	private void makeActions(){
		briefLadenAction = new Action(Messages.getString("TextView.openLetter")) { //$NON-NLS-1$
				@Override
				public void run(){
					Patient actPatient = (Patient) ElexisEventDispatcher.getSelected(Patient.class);
					DocumentSelectDialog bs =
						new DocumentSelectDialog(getViewSite().getShell(), actPatient,
							DocumentSelectDialog.TYPE_LOAD_DOCUMENT);
					if (bs.open() == Dialog.OK) {
						openDocument(bs.getSelectedDocument());
					}
				}
				
			};
		
		loadSysTemplateAction = new Action(Messages.getString("TextView.openSysTemplate")) { //$NON-NLS-1$
				@Override
				public void run(){
					DocumentSelectDialog bs =
						new DocumentSelectDialog(getViewSite().getShell(), Hub.actMandant,
							DocumentSelectDialog.TYPE_LOAD_SYSTEMPLATE);
					if (bs.open() == Dialog.OK) {
						openDocument(bs.getSelectedDocument());
					}
				}
			};
		loadTemplateAction = new Action(Messages.getString("TextView.openTemplate")) { //$NON-NLS-1$
				@Override
				public void run(){
					DocumentSelectDialog bs =
						new DocumentSelectDialog(getViewSite().getShell(), Hub.actMandant,
							DocumentSelectDialog.TYPE_LOAD_TEMPLATE);
					if (bs.open() == Dialog.OK) {
						openDocument(bs.getSelectedDocument());
					}
				}
			};
		saveTemplateAction = new Action(Messages.getString("TextView.saveAsTemplate")) { //$NON-NLS-1$
				@Override
				public void run(){
					if (actBrief != null) {
						txt.saveTemplate(actBrief.get(Messages.getString("TextView.Subject"))); //$NON-NLS-1$
					} else {
						txt.saveTemplate(null);
					}
				}
			};
		
		showMenuAction = new Action(Messages.getString("TextView.showMenu"), Action.AS_CHECK_BOX) { //$NON-NLS-1$			
				public void run(){
					txt.getPlugin().showMenu(isChecked());
				}
			};
		
		showToolbarAction =
			new Action(Messages.getString("TextView.Toolbar"), Action.AS_CHECK_BOX) { //$NON-NLS-1$
				public void run(){
					txt.getPlugin().showToolbar(isChecked());
				}
			};
		importAction = new Action(Messages.getString("TextView.importText")) { //$NON-NLS-1$
				@Override
				public void run(){
					try {
						FileDialog fdl = new FileDialog(getViewSite().getShell());
						String filename = fdl.open();
						if (filename != null) {
							File file = new File(filename);
							if (file.exists()) {
								actBrief = null;
								setPartName(filename);
								FileInputStream fis = new FileInputStream(file);
								txt.getPlugin().loadFromStream(fis, false);
							}
							
						}
						
					} catch (Throwable ex) {
						ExHandler.handle(ex);
					}
				}
			};
		
		exportAction = new Action(Messages.getString("TextView.exportText")) { //$NON-NLS-1$
				@Override
				public void run(){
					try {
						if (actBrief == null) {
							SWTHelper.alert("Fehler",
								"Es ist kein Dokument zum exportieren geladen");
						} else {
							FileDialog fdl = new FileDialog(getViewSite().getShell(), SWT.SAVE);
							fdl.setFilterExtensions(new String[] {
								"*.odt", "*.xml", "*.*"
							});
							fdl.setFilterNames(new String[] {
								"OpenOffice.org Text", "XML File", "All files"
							});
							String filename = fdl.open();
							if (filename != null) {
								if (FileTool.getExtension(filename).equals("")) {
									filename += ".odt";
								}
								File file = new File(filename);
								byte[] contents = actBrief.loadBinary();
								ByteArrayInputStream bais = new ByteArrayInputStream(contents);
								FileOutputStream fos = new FileOutputStream(file);
								FileTool.copyStreams(bais, fos);
								fos.close();
								bais.close();
								
							}
						}
						
					} catch (Throwable ex) {
						ExHandler.handle(ex);
					}
				}
			};
		newDocAction = new Action(Messages.getString("TextView.newDocument")) { //$NON-NLS-1$
				{
					setImageDescriptor(Desk.getImageDescriptor(Desk.IMG_NEW));
				}
				
				public void run(){
					actBrief = null;
					setName();
					txt.getPlugin().createEmptyDocument();
				}
				
			};
		briefLadenAction.setImageDescriptor(Hub.getImageDescriptor(Messages
			.getString("TextView.15"))); //$NON-NLS-1$
		briefLadenAction.setToolTipText("Brief zum Bearbeiten öffnen"); //$NON-NLS-1$
		// briefNeuAction.setImageDescriptor(Hub.getImageDescriptor("rsc/schreiben.gif"));
		// briefNeuAction.setToolTipText("Einen neuen Brief erstellen");
		showMenuAction.setToolTipText(Messages.getString("TextView.showMenuBar")); //$NON-NLS-1$
		showMenuAction.setImageDescriptor(Hub.getImageDescriptor("rsc/menubar.ico")); //$NON-NLS-1$
		showMenuAction.setChecked(true);
		showToolbarAction.setImageDescriptor(Hub.getImageDescriptor("rsc/toolbar.ico")); //$NON-NLS-1$
		showToolbarAction.setToolTipText(Messages.getString("TextView.showToolbar")); //$NON-NLS-1$
		showToolbarAction.setChecked(true);
	}
	
	class SaveHandler implements ITextPlugin.ICallback {
		
		public void save(){
			log.log(Messages.getString("TextView.save"), Log.DEBUGMSG); //$NON-NLS-1$
			if (actBrief != null) {
				actBrief.save(txt.getPlugin().storeToByteArray(), txt.getPlugin().getMimeType());
			}
		}
		
		public boolean saveAs(){
			log.log(Messages.getString("TextView.saveAs"), Log.DEBUGMSG); //$NON-NLS-1$
			InputDialog il =
				new InputDialog(
					getViewSite().getShell(),
					Messages.getString("TextView.saveText"), Messages.getString("TextView.enterTitle"), "", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			if (il.open() == Dialog.OK) {
				actBrief.setBetreff(il.getValue());
				return actBrief.save(txt.getPlugin().storeToByteArray(), txt.getPlugin()
					.getMimeType());
			}
			return false;
		}
		
	}
	
	public void activation(boolean mode){
		if (mode == false) {
			if (actBrief != null) {
				actBrief.save(txt.getPlugin().storeToByteArray(), txt.getPlugin().getMimeType());
			}
			// txt.getPlugin().clear();
		} else {
			loadSysTemplateAction.setEnabled(Hub.acl
				.request(AccessControlDefaults.DOCUMENT_SYSTEMPLATE));
			saveTemplateAction.setEnabled(Hub.acl.request(AccessControlDefaults.DOCUMENT_TEMPLATE));
		}
	}
	
	public void visible(boolean mode){

	}
	
	void setName(){
		String n = ""; //$NON-NLS-1$
		if (actBrief == null) {
			setPartName(Messages.getString("TextView.noLetterSelected")); //$NON-NLS-1$
		} else {
			Person pat = actBrief.getPatient();
			if (pat != null) {
				n = pat.getLabel() + ": "; //$NON-NLS-1$
			}
			n += actBrief.getBetreff();
			setPartName(n);
		}
	}
	
}
