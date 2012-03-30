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
 * $Id: MigelLoader.java 6044 2010-02-01 15:18:50Z rgw_ch $
 *******************************************************************************/

package ch.elexis.artikel_ch.model;

import ch.elexis.actions.FlatDataLoader;
import ch.elexis.artikel_ch.data.MiGelArtikel;
import ch.elexis.data.Query;
import ch.elexis.util.viewers.CommonViewer;

public class MigelLoader extends FlatDataLoader {
	public MigelLoader(CommonViewer cv){
		super(cv, new Query<MiGelArtikel>(MiGelArtikel.class));
		setOrderFields(MiGelArtikel.FLD_NAME);
	}
}
