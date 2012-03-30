/*******************************************************************************
 * Copyright (c) 2006-2007, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *  $Id: MedicalSelector.java 5014 2009-01-23 16:31:33Z rgw_ch $
 *******************************************************************************/
package ch.elexis.artikel_ch.views;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;

import ch.elexis.actions.AbstractDataLoaderJob;
import ch.elexis.actions.JobPool;
import ch.elexis.actions.ListLoader;
import ch.elexis.artikel_ch.data.ArtikelFactory;
import ch.elexis.artikel_ch.data.Medical;
import ch.elexis.artikel_ch.model.MedicalLoader;
import ch.elexis.data.Query;
import ch.elexis.util.viewers.CommonViewer;
import ch.elexis.util.viewers.DefaultControlFieldProvider;
import ch.elexis.util.viewers.LazyContentProvider;
import ch.elexis.util.viewers.SimpleWidgetProvider;
import ch.elexis.util.viewers.ViewerConfigurer;
import ch.elexis.views.artikel.ArtikelContextMenu;
import ch.elexis.views.artikel.ArtikelLabelProvider;
import ch.elexis.views.codesystems.CodeSelectorFactory;

public class MedicalSelector extends CodeSelectorFactory {
	/*
	 * AbstractDataLoaderJob dataloader;
	 * 
	 * public MedicalSelector() { dataloader=(AbstractDataLoaderJob)
	 * JobPool.getJobPool().getJob("Medicals"); if(dataloader==null){ dataloader=new
	 * ListLoader("Medicals",new Query<Medical>(Medical.class),new String[]{"Name"});
	 * JobPool.getJobPool().addJob(dataloader); }
	 * JobPool.getJobPool().activate("Medicals",Job.SHORT); }
	 */
	@Override
	public ViewerConfigurer createViewerConfigurer(CommonViewer cv){
		new ArtikelContextMenu((Medical) new ArtikelFactory().createTemplate(Medical.class), cv);
		ViewerConfigurer vc =
			new ViewerConfigurer(
			// new LazyContentProvider(cv,dataloader,null),
				new MedicalLoader(cv), new ArtikelLabelProvider(), new MedicalControlFieldProvider(
					cv, new String[] {
						"Name"
					}), new ViewerConfigurer.DefaultButtonProvider(), new SimpleWidgetProvider(
					SimpleWidgetProvider.TYPE_LAZYLIST, SWT.NONE, null));
		
		return vc;
	}
	
	@Override
	public Class getElementClass(){
		return Medical.class;
	}
	
	@Override
	public void dispose(){
	// TODO Automatisch erstellter Methoden-Stub
	
	}
	
	@Override
	public String getCodeSystemName(){
		return "Medicals";
	}
	
}
