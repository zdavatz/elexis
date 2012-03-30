/*******************************************************************************
 * Copyright (c) 2009, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 * 
 *  $Id: Plafs.java 5845 2009-11-28 08:44:19Z rgw_ch $
 *******************************************************************************/
package ch.elexis.admin;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.util.HashMap;

import ch.elexis.Hub;
import ch.elexis.preferences.PreferenceConstants;
import ch.elexis.util.PlatformHelper;
import ch.rgw.tools.ExHandler;

/**
 * Stub for later development of plafs: Provide Strings not only depending of the locale but also of
 * the plaf. A client request a String and Plafs returns the value of that String matching the
 * actual plaf
 * 
 * @author Gerry
 * 
 */
public class Plafs {
	private static final HashMap<String, String> p = new HashMap<String, String>();
	
	/**
	 * return a plaf'ed STring
	 * 
	 * @param name
	 *            Name of the String. The String may be prefixed by a namespace, separated with ::
	 * @return that String according to the current plaf
	 */
	public static String get(String name){
		if (p.isEmpty()) {
			load();
		}
		String px = p.get(name);
		if (px == null) {
			String[] str = name.split("::");
			if (str.length > 1) {
				return str[1];
			} else {
				return str[0];
			}
		} else {
			return px;
		}
	}
	
	private static void load(){
		String textBase = Hub.localCfg.get(PreferenceConstants.USR_PLAF, null);
		if (textBase == null) {
			textBase = "/rsc/plaf/modern/strings.plaf";
		} else {
			textBase += "/strings.plaf";
		}
		String fpath = PlatformHelper.getBasePath("ch.elexis") + textBase;
		try {
			FileInputStream file = new FileInputStream(fpath);
			DataInputStream dais = new DataInputStream(file);
			String line;
			while (dais.available() > 0) {
				line = dais.readUTF();
				String[] flds = line.split("=");
				if (flds.length > 1) {
					p.put(flds[0], flds[1]);
				}
			}
			dais.close();
		} catch (EOFException ee) {
			// nope
			p.put("plaf", "none");
		} catch (Exception ex) {
			ExHandler.handle(ex);
			p.put("plaf", "none");
		}
		
	}
}
