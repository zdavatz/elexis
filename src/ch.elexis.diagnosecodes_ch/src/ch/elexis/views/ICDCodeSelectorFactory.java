/*******************************************************************************
 * Copyright (c) 2006, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *    $Id: ICDCodeSelectorFactory.java 5018 2009-01-23 16:33:17Z rgw_ch $
 *******************************************************************************/

package ch.elexis.views;

import java.util.List;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import ch.elexis.diagnosecodes_schweiz.Messages;
import ch.elexis.Desk;
import ch.elexis.actions.JobPool;
import ch.elexis.actions.LazyTreeLoader;
import ch.elexis.data.ICD10;
import ch.elexis.data.Query;
import ch.elexis.util.*;
import ch.elexis.util.viewers.CommonViewer;
import ch.elexis.util.viewers.DefaultControlFieldProvider;
import ch.elexis.util.viewers.SimpleWidgetProvider;
import ch.elexis.util.viewers.TreeContentProvider;
import ch.elexis.util.viewers.ViewerConfigurer;
import ch.elexis.views.codesystems.CodeSelectorFactory;

public class ICDCodeSelectorFactory extends CodeSelectorFactory {
	LazyTreeLoader dataloader;
	
	public ICDCodeSelectorFactory(){
		dataloader = (LazyTreeLoader) JobPool.getJobPool().getJob("ICD"); //$NON-NLS-1$
		if (dataloader == null) {
			
			Query<ICD10> check = new Query<ICD10>(ICD10.class);
			/*
			 * check.add("Code","=","xyz"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ List
			 * l=check.execute(); if(l==null){ if(ICD10.createTable()==false){
			 * MessageDialog.openError
			 * (Desk.theDisplay.getActiveShell(),Messages.ICDCodeSelectorFactory_errorLoading
			 * ,Messages.ICDCodeSelectorFactory_couldntCreate); } check.clear(); }
			 */
			dataloader =
				new LazyTreeLoader<ICD10>("ICD", check, "parent", new String[] { "Code", "Text"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			JobPool.getJobPool().addJob(dataloader);
		}
		JobPool.getJobPool().activate("ICD", Job.SHORT); //$NON-NLS-1$
	}
	
	public ViewerConfigurer createViewerConfigurer(CommonViewer cv){
		return new ViewerConfigurer(new TreeContentProvider(cv, dataloader),
			new ViewerConfigurer.TreeLabelProvider(), new DefaultControlFieldProvider(cv,
				new String[] {
					"Code", "Text"}), //$NON-NLS-1$ //$NON-NLS-2$
			new ViewerConfigurer.DefaultButtonProvider(), new SimpleWidgetProvider(
				SimpleWidgetProvider.TYPE_TREE, SWT.NONE, null));
	}
	
	@Override
	public Class getElementClass(){
		return ICD10.class;
	}
	
	@Override
	public void dispose(){}
	
	@Override
	public String getCodeSystemName(){
		return "ICD-10"; //$NON-NLS-1$
	}
	
}
