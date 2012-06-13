/*******************************************************************************
 * Copyright (c) 2006-2009, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *  $Id$
 *******************************************************************************/

package ch.ngiger.elexis.oddb_ch.views;

import org.eclipse.swt.SWT;

import ch.ngiger.elexis.oddb_ch.data.ArtikelFactory;
import ch.ngiger.elexis.oddb_ch.data.OddbArtikel;
import ch.ngiger.elexis.oddb_ch.model.OddbLoader;
import ch.elexis.util.viewers.CommonViewer;
import ch.elexis.util.viewers.DefaultControlFieldProvider;
import ch.elexis.util.viewers.DefaultLabelProvider;
import ch.elexis.util.viewers.SimpleWidgetProvider;
import ch.elexis.util.viewers.ViewerConfigurer;
import ch.elexis.views.artikel.ArtikelContextMenu;
import ch.elexis.views.codesystems.CodeSelectorFactory;

public class OddbSelector extends CodeSelectorFactory {
	/*
	 * AbstractDataLoaderJob dataloader;
	 * 
	 * public OddbSelector() { dataloader=(AbstractDataLoaderJob)
	 * JobPool.getJobPool().getJob("ODDB"); if(dataloader==null){ dataloader=new
	 * ListLoader("ODDB",new Query<OddbArtikel>(OddbArtikel.class),new String[]{"SubID","Name"});
	 * JobPool.getJobPool().addJob(dataloader); JobPool.getJobPool().activate("ODDB",Job.SHORT); }
	 * }
	 */
	@Override
	public ViewerConfigurer createViewerConfigurer(CommonViewer cv){
		new ArtikelContextMenu((OddbArtikel) new ArtikelFactory()
			.createTemplate(OddbArtikel.class), cv);
		return new ViewerConfigurer(
		// new LazyContentProvider(cv,dataloader,null),
			new OddbLoader(cv), new DefaultLabelProvider(), new DefaultControlFieldProvider(cv,
				new String[] {
					"Name" //$NON-NLS-1$ //$NON-NLS-2$
				}), new ViewerConfigurer.DefaultButtonProvider(), new SimpleWidgetProvider(
				SimpleWidgetProvider.TYPE_LAZYLIST, SWT.NONE, null));
	}
	
	@Override
	public Class getElementClass(){
		return OddbArtikel.class;
	}
	
	@Override
	public void dispose(){
	// TODO Automatisch erstellter Methoden-Stub
	
	}
	
	@Override
	public String getCodeSystemName(){
		return OddbArtikel.ODDB_NAME; //$NON-NLS-1$
	}
	
}
