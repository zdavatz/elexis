/*******************************************************************************
 * Copyright (c) 2007-2009, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *  $Id: MoneyField.java 5355 2009-06-14 10:35:19Z rgw_ch $
 *******************************************************************************/
package ch.elexis.selectors;

import org.eclipse.swt.widgets.Composite;

public class MoneyField extends TextField {
	
	public MoneyField(Composite parent, int displayBits, String displayName){
		super(parent, displayBits, displayName);
	}
	
}
