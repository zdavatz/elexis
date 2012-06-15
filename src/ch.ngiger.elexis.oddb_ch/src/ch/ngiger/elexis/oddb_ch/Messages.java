/*******************************************************************************
 * Copyright (c) 2012 Niklaus Giger <niklaus.giger@member.fsf.org>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Niklaus Giger <niklaus.giger@member.fsf.org> - initial API and implementation
 ******************************************************************************/
package ch.ngiger.elexis.oddb_ch;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "ch.ngiger.elexis.oddb_ch.messages"; //$NON-NLS-1$
	public static final String OddbImporter_WindowTitle = null;
	public static String OddbImporter_ClearAllData;
	public static String OddbImporter_ModeCreateNew;
	public static String OddbImporter_ModeUpdateAdd;
	public static String OddbImporter_PleaseSelectFile;
	public static String OddbImporter_ReadOddb;
	public static String OddbDetailDisplay_PriceUnit;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
	private Messages(){}
}
