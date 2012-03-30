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
 *  $Id: ArtikelPerspektive.java 5317 2009-05-24 15:00:37Z rgw_ch $
 *******************************************************************************/
package ch.elexis;

import org.eclipse.swt.SWT;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import ch.elexis.views.KompendiumView;
import ch.elexis.views.LagerView;
import ch.elexis.views.artikel.ArtikelView;

/**
 * Diese Klasse erzeugt das Anfangslayout für die Funktion "Artikel"
 */
public class ArtikelPerspektive implements IPerspectiveFactory {
	public static final String ID = "ch.elexis.ArtikelPerspektive"; //$NON-NLS-1$
	
	public void createInitialLayout(IPageLayout layout){
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		layout.setFixed(false);
		
		IFolderLayout ifr = layout.createFolder("right", SWT.RIGHT, 1.0f, editorArea); //$NON-NLS-1$
		ifr.addView(ArtikelView.ID);
		ifr.addView(LagerView.ID);
		ifr.addView(KompendiumView.ID);
		
		// layout.addView(Artikelliste.ID,SWT.RIGHT,0.5f,editorArea);
		// layout.addView(Artikeldetail.ID,SWT.RIGHT,0.5f,editorArea);
		
	}
	
}
