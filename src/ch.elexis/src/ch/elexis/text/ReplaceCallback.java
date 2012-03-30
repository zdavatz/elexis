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
 *  $Id: ReplaceCallback.java 5321 2009-05-28 12:06:28Z rgw_ch $
 *******************************************************************************/

package ch.elexis.text;

public interface ReplaceCallback {
	public Object replace(String in);
}
