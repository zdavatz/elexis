/*******************************************************************************
 * Copyright (c) 2007-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *  $Id: ACLPreferenceTree.java 5024 2009-01-23 16:36:39Z rgw_ch $
 *******************************************************************************/
package ch.elexis.scripting;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.program.Program;

import ch.elexis.Desk;
import ch.elexis.dialogs.BrowserInputDialog;
import ch.elexis.dialogs.ChoiceDialog;
import ch.elexis.util.Log;
import ch.elexis.util.SWTHelper;
import ch.rgw.tools.TimeTool;

public class Util {
	private static Log log = Log.get("Script utility");
	
	public static void display(String title, String contents){
		SWTHelper.showInfo(title, contents);
	}
	
	public static void log(String text){
		log.log(text, Log.WARNINGS);
	}
	
	public static int compareDates(String d1, String d2){
		TimeTool tt1 = new TimeTool();
		TimeTool tt2 = new TimeTool();
		if (tt1.set(d1) && tt2.set(d2)) {
			return tt1.compareTo(tt2);
		}
		return 0;
	}
	
	public static String inputWithURL(String title, String message, String url, Integer width,
		Integer height){
		BrowserInputDialog dlg = new BrowserInputDialog(Desk.getTopShell(), url, title, message);
		dlg.create();
		dlg.getShell().setSize(width, height);
		if (dlg.open() == Dialog.OK) {
			return dlg.getValue();
		}
		return null;
	}
	
	public static int inputWithChoice(String title, String message, String[] choices){
		ChoiceDialog cd = new ChoiceDialog(Desk.getTopShell(), title, message, choices);
		if (cd.open() == Dialog.OK) {
			return cd.getResult();
		}
		return -1;
	}
	
	public static boolean isNumeric(String input){
		if (input == null) {
			return false;
		}
		return input.matches("[0-9\\.]+");
	}
	
	public static boolean isNumeric(double d){
		return true;
	}
	
	public static boolean isNumeric(int i){
		return true;
	}
}
