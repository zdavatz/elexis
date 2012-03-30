/*******************************************************************************
 * Copyright (c) 2008, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *    $Id: DBImageDescriptor.java 3786 2008-04-19 09:57:12Z rgw_ch $
 *******************************************************************************/

package ch.elexis.data;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;

public class DBImageDescriptor extends ImageDescriptor {
	
	DBImage mine;
	
	public DBImageDescriptor(String name){
		String id = new Query<DBImage>(DBImage.class).findSingle("Titel", "=", name);
		if (id != null) {
			mine = DBImage.load(id);
		}
	}
	
	@Override
	public ImageData getImageData(){
		if (mine != null) {
			return mine.getImage().getImageData();
		}
		return null;
	}
	
}
