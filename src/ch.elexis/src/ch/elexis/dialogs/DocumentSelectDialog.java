/*******************************************************************************
 * Copyright (c) 2005-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 * 
 *  $Id: DocumentSelectDialog.java 5688 2009-08-28 06:26:36Z rgw_ch $
 *******************************************************************************/
package ch.elexis.dialogs;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ch.elexis.Desk;
import ch.elexis.Hub;
import ch.elexis.StringConstants;
import ch.elexis.data.Brief;
import ch.elexis.data.Person;
import ch.elexis.data.Query;
import ch.elexis.util.SWTHelper;
import ch.elexis.util.viewers.DefaultLabelProvider;
import ch.rgw.tools.StringTool;

/**
 * Select a Document or a template Usage: DocumentSelector dsl=new
 * DocumentSelector(shell,mandant,TYPE_xxx); if(dsl.open()==Dialog.OK){ doSomethingWith(dsl.result);
 * }
 * 
 * @author gerry
 * 
 */
public class DocumentSelectDialog extends TitleAreaDialog {
	private static final String DELETE_DOCUMENT =
		Messages.getString("DocumentSelectDialog.deleteDocument"); //$NON-NLS-1$
	private static final String DELETE_TEMPLATE =
		Messages.getString("DocumentSelectDialog.deleteTemplate"); //$NON-NLS-1$
	private static final String OPEN_DOCUMENT =
		Messages.getString("DocumentSelectDialog.openDocument"); //$NON-NLS-1$
	/**
	 * select an existing document out of the list of all documtents of the given mandator
	 */
	public static final int TYPE_LOAD_DOCUMENT = 0;
	/** create a new document using one of the templates of the given mandator */
	public static final int TYPE_CREATE_DOC_WITH_TEMPLATE = 1;
	/** open a user template of the given mandator for editing or export */
	public static final int TYPE_LOAD_TEMPLATE = 2;
	/** open a system template of the given mandator for editing or export */
	public static final int TYPE_LOAD_SYSTEMPLATE = 4;
	
	static final int TEMPLATE = TYPE_LOAD_TEMPLATE | TYPE_LOAD_SYSTEMPLATE;
	Person rel;
	int type;
	Brief result;
	Text tBetreff;
	String betreff;
	TableViewer tv;
	private MenuManager menu;
	private Action editNameAction;
	private Action deleteTemplateAction;
	private Action deleteTextAction;
	
	/**
	 * Create a new DocumentSelector. If the user clicks OK, the selected Brief will be in result.
	 * 
	 * @param p
	 *            the mandator whose templates/letters should be displayed
	 * @param typ
	 *            type of the selector to display (see TYPE_ constants)
	 */
	public DocumentSelectDialog(Shell shell, Person p, int typ){
		super(shell);
		rel = p;
		type = typ;
	}
	
	@Override
	public void create(){
		super.create();
		setTitleImage(Desk.getImage(Desk.IMG_LOGO48));
		
		makeActions();
		switch (type) {
		case TYPE_LOAD_DOCUMENT:
			setTitle(OPEN_DOCUMENT);
			setMessage(Messages.getString("DocumentSelectDialog.pleaseSelectDocument")); //$NON-NLS-1$
			getShell().setText(OPEN_DOCUMENT);
			break;
		case TYPE_CREATE_DOC_WITH_TEMPLATE:
			setTitle(Messages.getString("DocumentSelectDialog.createLetterWithTemplate")); //$NON-NLS-1$
			setMessage(Messages.getString("DocumentSelectDialog.selectTemplate")); //$NON-NLS-1$
			getShell().setText(Messages.getString("DocumentSelectDialog.schooseTemplateForLetter")); //$NON-NLS-1$
			break;
		case TYPE_LOAD_TEMPLATE:
			setTitle(Messages.getString("DocumentSelectDialog.openTemplate")); //$NON-NLS-1$
			setMessage(Messages.getString("DocumentSelectDialog.pleaseSelectTemplateFromList")); //$NON-NLS-1$
			getShell().setText(Messages.getString("DocumentSelectDialog.openTemplate")); //$NON-NLS-1$
			break;
		case TYPE_LOAD_SYSTEMPLATE:
			setTitle(Messages.getString("DocumentSelectDialog.loadSysTemplate")); //$NON-NLS-1$
			setMessage(Messages.getString("DocumentSelectDialog.sysTemplateExplanation")); //$NON-NLS-1$
			getShell().setText(Messages.getString("DocumentSelectDialog.loadTemplate")); //$NON-NLS-1$
		}
	}
	
	@Override
	protected Control createDialogArea(Composite parent){
		Composite ret = new Composite(parent, SWT.NONE);
		ret.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		ret.setLayout(new GridLayout());
		if ((type & TEMPLATE) != 0) {
			new Label(ret, SWT.NONE).setText(Messages.getString("DocumentSelectDialog.subject")); //$NON-NLS-1$
			tBetreff = SWTHelper.createText(ret, 1, SWT.NONE);
			new Label(ret, SWT.SEPARATOR | SWT.HORIZONTAL);
		}
		tv = new TableViewer(ret, SWT.V_SCROLL);
		tv.setContentProvider(new IStructuredContentProvider() {
			
			public Object[] getElements(Object inputElement){
				Query<Brief> qbe = new Query<Brief>(Brief.class);
				if (type == TYPE_LOAD_DOCUMENT) {
					qbe.add(Brief.FLD_TYPE, Query.NOT_EQUAL, Brief.TEMPLATE);
				} else {
					String sys = type == TYPE_LOAD_SYSTEMPLATE ? Query.EQUALS : Query.NOT_EQUAL;
					qbe.add(Brief.FLD_TYPE, Query.EQUALS, Brief.TEMPLATE);
					qbe.add(Brief.FLD_KONSULTATION_ID, sys, "SYS"); //$NON-NLS-1$
					qbe.startGroup();
					qbe.add(Brief.FLD_DESTINATION_ID, Query.EQUALS, Hub.actMandant.getId());
					qbe.or();
					qbe.add(Brief.FLD_DESTINATION_ID, Query.EQUALS, StringTool.leer);
					qbe.endGroup();
				}
				qbe.and();
				qbe.add("deleted", Query.NOT_EQUAL, StringConstants.ONE); //$NON-NLS-1$
				
				if (type != TYPE_LOAD_DOCUMENT) {
					qbe.orderBy(false, Brief.FLD_SUBJECT);
				} else {
					qbe.orderBy(false, Brief.FLD_DATE);
				}
				List<Brief> l = qbe.execute();
				return l.toArray();
			}
			
			public void dispose(){}
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput){}
		});
		tv.setLabelProvider(new DefaultLabelProvider());
		makeActions();
		menu = new MenuManager();
		menu.add(editNameAction);
		menu.add((type & TEMPLATE) != 0 ? deleteTemplateAction : deleteTextAction);
		tv.getControl().setMenu(menu.createContextMenu(tv.getControl()));
		tv.getControl().setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		tv.setInput(this);
		return ret;
	}
	
	@Override
	protected void okPressed(){
		IStructuredSelection sel = (IStructuredSelection) tv.getSelection();
		if ((sel != null) && (!sel.isEmpty())) {
			result = (Brief) sel.getFirstElement();
			if ((type & TEMPLATE) != 0) {
				betreff = tBetreff.getText();
			}
			if (StringTool.isNothing(betreff)) {
				betreff = result.getBetreff();
			}
		}
		super.okPressed();
	}
	
	public Brief getSelectedDocument(){
		return result;
	}
	
	public String getBetreff(){
		return betreff;
	}
	
	private void makeActions(){
		editNameAction =
			new Action(Messages.getString("DocumentSelectDialog.changeSubjectAction")) { //$NON-NLS-1$
				@Override
				public void run(){

				}
			};
		deleteTemplateAction = new Action(DELETE_TEMPLATE) {
			@Override
			public void run(){
				Brief sel = (Brief) ((IStructuredSelection) tv.getSelection()).getFirstElement();
				if (MessageDialog.openConfirm(getShell(), DELETE_TEMPLATE,
					MessageFormat.format(Messages
						.getString("DocumentSelectDialog.reallyDeleteTemplate"), sel.getBetreff())) //$NON-NLS-1$
				== true) {
					sel.delete();
					tv.refresh();
				}
			}
		};
		deleteTextAction = new Action(DELETE_DOCUMENT) {
			@Override
			public void run(){
				Brief sel = (Brief) ((IStructuredSelection) tv.getSelection()).getFirstElement();
				if (MessageDialog.openConfirm(getShell(), DELETE_DOCUMENT,
					MessageFormat.format(Messages
						.getString("DocumentSelectDialog.reallyDeleteDocument"), sel.getBetreff())) //$NON-NLS-1$
				== true) {
					sel.set("geloescht", StringConstants.ONE); //$NON-NLS-1$
					tv.refresh();
				}
			}
		};
		editNameAction =
			new Action(Messages.getString("DocumentSelectDialog.changeSubjectAction")) { //$NON-NLS-1$
				@Override
				public void run(){
					Brief sel =
						(Brief) ((IStructuredSelection) tv.getSelection()).getFirstElement();
					InputDialog inp =
						new InputDialog(getShell(), Messages
							.getString("DocumentSelectDialog.changeSubjectAction"), //$NON-NLS-1$
							Messages.getString("DocumentSelectDialog.changeSubjectAction"), //$NON-NLS-1$
							sel.getBetreff(), null);
					int inputResult = inp.open();
					if (inputResult == InputDialog.OK) {
						sel.setBetreff(inp.getValue());
						tv.refresh();
					}
				}
			};
	}
}
