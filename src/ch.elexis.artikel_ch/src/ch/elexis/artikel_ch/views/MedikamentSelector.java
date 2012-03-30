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
 *  $Id: MedikamentSelector.java 5014 2009-01-23 16:31:33Z rgw_ch $
 *******************************************************************************/

package ch.elexis.artikel_ch.views;

import org.eclipse.swt.SWT;

import ch.elexis.artikel_ch.data.ArtikelFactory;
import ch.elexis.artikel_ch.data.Medikament;
import ch.elexis.artikel_ch.model.MedikamentLoader;
import ch.elexis.util.viewers.CommonViewer;
import ch.elexis.util.viewers.SimpleWidgetProvider;
import ch.elexis.util.viewers.ViewerConfigurer;
import ch.elexis.views.artikel.ArtikelContextMenu;
import ch.elexis.views.artikel.ArtikelLabelProvider;
import ch.elexis.views.codesystems.CodeSelectorFactory;

public class MedikamentSelector extends CodeSelectorFactory {
	/*
	 * AbstractDataLoaderJob dataloader;
	 * 
	 * public MedikamentSelector() { dataloader=(AbstractDataLoaderJob)
	 * JobPool.getJobPool().getJob("Medikamente"); if(dataloader==null){ dataloader=new
	 * ListLoader("Medikamente",new Query<Medikament>(Medikament.class),new String[]{"Name"});
	 * JobPool.getJobPool().addJob(dataloader); }
	 * JobPool.getJobPool().activate("Medikamente",Job.SHORT); }
	 */
	// MedikamentLoader ml;
	@Override
	public ViewerConfigurer createViewerConfigurer(CommonViewer cv){
		new ArtikelContextMenu((Medikament) new ArtikelFactory().createTemplate(Medikament.class),
			cv);
		return new ViewerConfigurer(
			// new LazyContentProvider(cv,dataloader,null),
			new MedikamentLoader(cv), new ArtikelLabelProvider(),
			new MedikamentControlFieldProvider(cv, new String[] {
				"Name"
			}), new ViewerConfigurer.DefaultButtonProvider(), new SimpleWidgetProvider(
				SimpleWidgetProvider.TYPE_LAZYLIST, SWT.NONE, null));
	}
	
	@Override
	public Class getElementClass(){
		return Medikament.class;
	}
	
	@Override
	public void dispose(){

	}
	
	@Override
	public String getCodeSystemName(){
		return "Medikamente";
	}
	
}
