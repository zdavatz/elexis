/*******************************************************************************
 * Copyright (c) 2009-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 * $Id: OddbLoader.java 6044 2010-02-01 15:18:50Z rgw_ch $
 *******************************************************************************/

package ch.ngiger.elexis.oddb_ch.model;

import ch.elexis.actions.FlatDataLoader;
import ch.ngiger.elexis.oddb_ch.data.OddbArtikel;
import ch.elexis.data.Query;
import ch.elexis.util.viewers.CommonViewer;

public class OddbLoader extends FlatDataLoader {
	public OddbLoader(CommonViewer cv){
		super(cv, new Query<OddbArtikel>(OddbArtikel.class));
		setOrderFields(OddbArtikel.FLD_NAME);
	}
}
