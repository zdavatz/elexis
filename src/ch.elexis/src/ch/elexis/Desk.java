/*******************************************************************************
 * Copyright (c) 2008-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 * 
 *    $Id: Desk.java 3916 2008-05-11 14:48:53Z rgw_ch $
 *******************************************************************************/

package ch.elexis;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;

import ch.elexis.preferences.PreferenceConstants;
import ch.elexis.preferences.PreferenceInitializer;
import ch.elexis.preferences.SettingsPreferenceStore;
import ch.rgw.io.FileTool;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.StringTool;

public class Desk implements IApplication {
	private static FormToolkit theToolkit = null;
	
	private static Display theDisplay = null;
	private static ImageRegistry theImageRegistry = null;
	private static ColorRegistry theColorRegistry = null;
	private static HashMap<String, Cursor> cursors = null;
	private static Map<String, String> args = null;
	
	public static final String COL_RED = "rot"; //$NON-NLS-1$
	public static final String COL_GREEN = "gruen"; //$NON-NLS-1$
	public static final String COL_BLUE = "blau"; //$NON-NLS-1$
	public static final String COL_SKYBLUE = "himmelblau"; //$NON-NLS-1$
	public static final String COL_LIGHTBLUE = "hellblau"; //$NON-NLS-1$
	public static final String COL_BLACK = "schwarz"; //$NON-NLS-1$
	public static final String COL_GREY = "grau"; //$NON-NLS-1$
	public static final String COL_WHITE = "weiss"; //$NON-NLS-1$
	public static final String COL_DARKGREY = "dunkelgrau"; //$NON-NLS-1$
	public static final String COL_LIGHTGREY = "hellgrau"; //$NON-NLS-1$
	public static final String COL_GREY60 = "grau60"; //$NON-NLS-1$
	public static final String COL_GREY20 = "grau20"; //$NON-NLS-1$
	
	/** Returning to some home place */
	public static final String IMG_HOME = "home"; //$NON-NLS-1$
	/** An Address label */
	public static final String IMG_ADRESSETIKETTE = "adressetikette"; //$NON-NLS-1$
	/** a label with patient data */
	public static final String IMG_PATIENTETIKETTE = "patientetikette"; //$NON-NLS-1$
	/** a label with some identity number (e.g. for lab orders) */
	public static final String IMG_VERSIONEDETIKETTE = "auftragsetikette"; //$NON-NLS-1$
	/** deleting items */
	public static final String IMG_DELETE = "delete"; //$NON-NLS-1$
	/** a male */
	public static final String IMG_MANN = "mann"; //$NON-NLS-1$
	/** a female */
	public static final String IMG_FRAU = "frau"; //$NON-NLS-1$
	/** a Very Important Person/Patient */
	public static final String IMG_VIP = "vip"; //$NON-NLS-1$
	
	/** a printer */
	public static final String IMG_PRINTER = "printer"; //$NON-NLS-1$
	// public static final String IMG_PRINT="print"; //$NON-NLS-1$
	
	/** a filter */
	public static final String IMG_FILTER = "filter"; //$NON-NLS-1$
	/** creating a new Object */
	public static final String IMG_NEW = "new"; //$NON-NLS-1$
	/** importing items */
	public static final String IMG_IMPORT = "import"; //$NON-NLS-1$
	/** exporting items */
	public static final String IMG_EXPORT = "export"; //$NON-NLS-1$
	
	public static final String IMG_GOFURTHER = "gofurther"; //$NON-NLS-1$
	
	/** the 48x48 pixel version of the elexis(tm) logo */
	public static final String IMG_LOGO48 = "elexislogo48"; //$NON-NLS-1$
	/** editing an item */
	public static final String IMG_EDIT = "edit"; //$NON-NLS-1$
	/** warning */
	public static final String IMG_ACHTUNG = "achtung"; //$NON-NLS-1$
	/** ok */
	public static final String IMG_OK = "ok"; //$NON-NLS-1$
	/** tick */
	public static final String IMG_TICK = "tick"; //$NON-NLS-1$
	/** error */
	public static final String IMG_FEHLER = "fehler"; //$NON-NLS-1$
	/** refresh/reload */
	public static final String IMG_REFRESH = "refresh"; //$NON-NLS-1$
	/** wizard/doing things automagically */
	public static final String IMG_WIZARD = "wizard"; //$NON-NLS-1$
	/** add something to an existing object */
	public static final String IMG_ADDITEM = "add"; // $NON_NLS-1$ //$NON-NLS-1$
	/** remove something from an existing object */
	public static final String IMG_REMOVEITEM = "minus"; // $NON_NLS-1$ //$NON-NLS-1$
	/** excalamation mark red */
	public static final String IMG_AUSRUFEZ_ROT = "ausrufezeichen_rot"; // $NON_NLS-1$ //$NON-NLS-1$
	/** exclamantion mark */
	public static final String IMG_AUSRUFEZ = "ausrufezeichen"; // $NON_NLS-1$ //$NON-NLS-1$
	/** computer network */
	public static final String IMG_NETWORK = "netzwerk"; // $NON_NLS-1$ //$NON-NLS-1$
	/** a book */
	public static final String IMG_BOOK = "buch"; //$NON-NLS-1$
	/** a person */
	public static final String IMG_PERSON = "person"; // $NON_NLS-1$ //$NON-NLS-1$
	/** a person with an OK mark */
	public static final String IMG_PERSON_OK = "personOK"; // $NON_NLS-1$ //$NON-NLS-1$
	/** a diskette symbol */
	public static final String IMG_DISK = "diskette"; // $NON_NLS-1$ //$NON-NLS-1$
	/** a closed lock */
	public static final String IMG_LOCK_CLOSED = "schloss_zu"; // $NON_NLS-1$ //$NON-NLS-1$
	/** An opened lock */
	public static final String IMG_LOCK_OPEN = "schloss_offen"; // $NON_NLS-1$ //$NON-NLS-1$
	/** Clipboard symbol */
	public static final String IMG_CLIPBOARD = "clipboard"; // $NON_NLS-1$ //$NON-NLS-1$
	/** Arrow right */
	public static final String IMG_NEXT = "arrow_next"; // $NON_NLS-1$ //$NON-NLS-1$
	/** Arrow left */
	public static final String IMG_PREVIOUS = "arrow_prev"; // $NON_NLS-1$ //$NON-NLS-1$
	/** Arrow up */
	public static final String IMG_ARROWUP = "arrow_up"; // $NON_NLS-1$ //$NON-NLS-1$
	/** Arrow down */
	public static final String IMG_ARROWDOWN = "arrow_down"; // $NON_NLS-1$ //$NON-NLS-1$
	/** Move to upper list */
	public static final String IMG_MOVETOUPPERLIST = "movetoupperlist"; // $NON_NLS-1$ //$NON-NLS-1$
	/** Move to lower list */
	public static final String IMG_MOVETOLOWERLIST = "movetolowerlist"; // $NON_NLS-1$ //$NON-NLS-1$
	/** clear input field */
	public static final String IMG_CLEAR = "cross_small"; // $NON_NLS-1$ //$NON-NLS-1$
	
	public static final String CUR_HYPERLINK = "cursor_hyperlink"; //$NON-NLS-1$
	
	public Desk(){}
	
	@SuppressWarnings("unchecked")
	public Object start(IApplicationContext context) throws Exception{
		args = context.getArguments();
		if (args.containsKey("--clean-all")) { //$NON-NLS-1$
			String p = PreferenceInitializer.getDefaultDBPath();
			FileTool.deltree(p);
			Hub.localCfg.clear();
			Hub.localCfg.flush();
		}
		
		try {
			
			int returnCode =
				PlatformUI.createAndRunWorkbench(getDisplay(), new ApplicationWorkbenchAdvisor());
			// Die Funktion kehrt erst beim Programmende zurück.
			Hub.heart.suspend();
			System.out.println(Messages.Desk_37);
			Hub.localCfg.flush();
			if (Hub.globalCfg != null) {
				Hub.globalCfg.flush();
			}
			if (returnCode == PlatformUI.RETURN_RESTART) {
				return IApplication.EXIT_RESTART;
			}
			return IApplication.EXIT_OK;
		} catch (Exception ex) {
			ExHandler.handle(ex);
			ex.printStackTrace();
			return -1;
		} finally { // aufräumen
			if (theToolkit != null) {
				theToolkit.dispose();
			}
			if (theImageRegistry != null) {
				theImageRegistry.dispose();
			}
			/*
			 * if ((theDisplay != null) && (!theDisplay.isDisposed())) { theDisplay.dispose(); }
			 */
		}
		
	}
	
	public void stop(){
	// TODO Auto-generated method stub
	
	}
	
	/**
	 * return a command line argument/launch parameter
	 * 
	 * @param name
	 *            name of the argument
	 * @return
	 */
	public static String getCommandLineArgument(String name){
		if (args == null) {
			return null;
		}
		return args.get(name);
	}
	
	/**
	 * get the base directory for images. This is dependend from the plaf chosen. If no plaf was
	 * chosen, "modern" will be assumed.
	 * 
	 * @return a string denoting the directory with the images for the current plaf
	 */
	static String getImageBase(){
		String imageBase = Hub.localCfg.get(PreferenceConstants.USR_PLAF, null);
		if (imageBase == null) {
			imageBase = "/rsc/plaf/modern/icons/"; //$NON-NLS-1$
		} else {
			imageBase += "/icons/"; //$NON-NLS-1$
		}
		return imageBase;
	}
	
	public static ImageRegistry getImageRegistry(){
		if (theImageRegistry == null) {
			theImageRegistry = new ImageRegistry(getDisplay());
			synchronized (theImageRegistry) {
				theImageRegistry.put(IMG_HOME, getImageDescriptor(IMG_HOME));
				theImageRegistry.put(IMG_ADRESSETIKETTE, getImageDescriptor(IMG_ADRESSETIKETTE));
				theImageRegistry.put(IMG_PATIENTETIKETTE, getImageDescriptor(IMG_PATIENTETIKETTE));
				theImageRegistry.put(IMG_VERSIONEDETIKETTE,
					getImageDescriptor(IMG_VERSIONEDETIKETTE));
				theImageRegistry.put(IMG_DELETE, getImageDescriptor(IMG_DELETE));
				theImageRegistry.put(IMG_MANN, getImageDescriptor(IMG_MANN));
				theImageRegistry.put(IMG_FRAU, getImageDescriptor(IMG_FRAU));
				theImageRegistry.put(IMG_VIP, getImageDescriptor(IMG_VIP));
				theImageRegistry.put(IMG_PRINTER, getImageDescriptor(IMG_PRINTER));
				theImageRegistry.put(IMG_FILTER, getImageDescriptor(IMG_FILTER));
				theImageRegistry.put(IMG_NEW, getImageDescriptor(IMG_NEW));
				theImageRegistry.put(IMG_LOGO48, getImageDescriptor(IMG_LOGO48));
				theImageRegistry.put(IMG_IMPORT, getImageDescriptor(IMG_IMPORT));
				theImageRegistry.put(IMG_EDIT, getImageDescriptor(IMG_EDIT));
				theImageRegistry.put(IMG_ACHTUNG, getImageDescriptor(IMG_ACHTUNG));
				theImageRegistry.put(IMG_OK, getImageDescriptor(IMG_OK));
				theImageRegistry.put(IMG_TICK, getImageDescriptor(IMG_TICK));
				theImageRegistry.put(IMG_FEHLER, getImageDescriptor(IMG_FEHLER));
				theImageRegistry.put(IMG_REFRESH, getImageDescriptor(IMG_REFRESH));
				theImageRegistry.put(IMG_WIZARD, getImageDescriptor(IMG_WIZARD));
				theImageRegistry.put(IMG_ADDITEM, getImageDescriptor(IMG_ADDITEM));
				theImageRegistry.put(IMG_EXPORT, getImageDescriptor(IMG_EXPORT));
				theImageRegistry.put(IMG_GOFURTHER, getImageDescriptor(IMG_GOFURTHER));
				theImageRegistry.put(IMG_AUSRUFEZ, getImageDescriptor(IMG_AUSRUFEZ));
				theImageRegistry.put(IMG_AUSRUFEZ_ROT, getImageDescriptor(IMG_AUSRUFEZ_ROT));
				theImageRegistry.put(IMG_REMOVEITEM, getImageDescriptor(IMG_REMOVEITEM));
				theImageRegistry.put(IMG_NETWORK, getImageDescriptor(IMG_NETWORK));
				theImageRegistry.put(IMG_BOOK, getImageDescriptor(IMG_BOOK));
				theImageRegistry.put(IMG_PERSON, getImageDescriptor(IMG_PERSON));
				theImageRegistry.put(IMG_PERSON_OK, getImageDescriptor(IMG_PERSON_OK));
				theImageRegistry.put(IMG_DISK, getImageDescriptor(IMG_DISK));
				theImageRegistry.put(IMG_LOCK_CLOSED, getImageDescriptor(IMG_LOCK_CLOSED));
				theImageRegistry.put(IMG_LOCK_OPEN, getImageDescriptor(IMG_LOCK_OPEN));
				theImageRegistry.put(IMG_CLIPBOARD, getImageDescriptor(IMG_CLIPBOARD));
				theImageRegistry.put(IMG_NEXT, getImageDescriptor(IMG_NEXT));
				theImageRegistry.put(IMG_PREVIOUS, getImageDescriptor(IMG_PREVIOUS));
				theImageRegistry.put(IMG_ARROWUP, getImageDescriptor(IMG_ARROWUP));
				theImageRegistry.put(IMG_ARROWDOWN, getImageDescriptor(IMG_ARROWDOWN));
				theImageRegistry.put(IMG_MOVETOUPPERLIST, getImageDescriptor(IMG_MOVETOUPPERLIST));
				theImageRegistry.put(IMG_MOVETOLOWERLIST, getImageDescriptor(IMG_MOVETOLOWERLIST));
				theImageRegistry.put(IMG_CLEAR, getImageDescriptor(IMG_CLEAR));
			}
		}
		return theImageRegistry;
	}
	
	/**
	 * Return an ImageDescriptor. The Descriptor will be searched in the ImageRegistry first. If not
	 * found, it will be searched as image file in the directory denoted by the current plaf. Images
	 * with the extensions of png, gif and ico will be searched in this given order. If still no
	 * image is found, it will be searched in rsc/
	 * 
	 * @param imagename
	 *            the name of the image or the imagefile (without extension)
	 * @return
	 */
	public static ImageDescriptor getImageDescriptor(String imagename){
		ImageDescriptor ret = getImageRegistry().getDescriptor(imagename);
		if (ret == null) {
			ret = Hub.getImageDescriptor(getImageBase() + imagename + ".png"); //$NON-NLS-1$
			if (ret == null) {
				ret = Hub.getImageDescriptor(getImageBase() + imagename + Messages.Desk_32);
			}
			if (ret == null) {
				ret = Hub.getImageDescriptor(getImageBase() + imagename + Messages.Desk_33);
			}
			if (ret == null) {
				ret = Hub.getImageDescriptor("rsc/" + imagename); //$NON-NLS-1$
			}
			if (ret != null) {
				getImageRegistry().put(imagename, ret);
			}
		}
		return ret;
	}
	
	/**
	 * Return an image with a specified name.
	 * 
	 * @see getImageDescriptor(String)
	 * @param name
	 *            the name of the image to retrieve
	 * @return the Image or null if no such image was found
	 */
	public static Image getImage(String name){
		Image ret = getImageRegistry().get(name);
		if (ret == null) {
			ImageDescriptor id = getImageDescriptor(name);
			if (id != null) {
				ret = id.createImage();
				// getImageRegistry().remove(name);
				// getImageRegistry().put(name, ret);
			}
		}
		return ret;
	}
	
	/** shortcut for getColorRegistry().get(String col) */
	public static Color getColor(String desc){
		return getColorRegistry().get(desc);
	}
	
	public static ColorRegistry getColorRegistry(){
		
		if (theColorRegistry == null) {
			theColorRegistry = new ColorRegistry(getDisplay(), true);
		}
		return theColorRegistry;
	}
	
	public static FormToolkit getToolkit(){
		if (theToolkit == null) {
			theToolkit = new FormToolkit(getDisplay());
		}
		return theToolkit;
	}
	
	public static Display getDisplay(){
		if (theDisplay == null) {
			if (PlatformUI.isWorkbenchRunning()) {
				theDisplay = PlatformUI.getWorkbench().getDisplay();
				// theDisplay = PlatformUI.createDisplay();
			}
		}
		if (theDisplay == null) {
			theDisplay = PlatformUI.createDisplay();
		}
		return theDisplay;
	}
	
	public static void updateFont(String cfgName){
		FontRegistry fr = JFaceResources.getFontRegistry();
		FontData[] fd =
			PreferenceConverter.getFontDataArray(new SettingsPreferenceStore(Hub.userCfg), cfgName);
		fr.put(cfgName, fd);
	}
	
	public static Font getFont(String cfgName){
		FontRegistry fr = JFaceResources.getFontRegistry();
		if (!fr.hasValueFor(cfgName)) {
			FontData[] fd =
				PreferenceConverter.getFontDataArray(new SettingsPreferenceStore(Hub.userCfg),
					cfgName);
			fr.put(cfgName, fd);
		}
		return fr.get(cfgName);
	}
	
	public static Font getFont(String name, int height, int style){
		String key = name + ":" + Integer.toString(height) + ":" + Integer.toString(style); //$NON-NLS-1$ //$NON-NLS-2$
		FontRegistry fr = JFaceResources.getFontRegistry();
		if (!fr.hasValueFor(key)) {
			FontData[] fd = new FontData[] {
				new FontData(name, height, style)
			};
			fr.put(key, fd);
		}
		return fr.get(key);
	}
	
	public static Cursor getCursor(String name){
		if (cursors == null) {
			cursors = new HashMap<String, Cursor>();
		}
		Cursor ret = cursors.get(name);
		if (ret == null) {
			if (name.equals(CUR_HYPERLINK)) {
				ret = getDisplay().getSystemCursor(SWT.CURSOR_HAND);
				cursors.put(name, ret);
			}
		}
		return ret;
	}
	
	/**
	 * Eine Color aus einer RGB-Beschreibung als Hex-String herstellen
	 * 
	 * @param coldesc
	 *            Die Farbe als Beschreibung in Hex-Form
	 * @return die Farbe als Color, ist in Regisry gespeichert
	 */
	public static Color getColorFromRGB(final String coldesc){
		String col = StringTool.pad(StringTool.LEFT, '0', coldesc, 6);
		if (!getColorRegistry().hasValueFor(col)) {
			RGB rgb;
			try {
				rgb =
					new RGB(Integer.parseInt(col.substring(0, 2), 16), Integer.parseInt(col
						.substring(2, 4), 16), Integer.parseInt(col.substring(4, 6), 16));
			} catch (NumberFormatException nex) {
				ExHandler.handle(nex);
				rgb = new RGB(100, 100, 100);
			}
			getColorRegistry().put(col, rgb);
		}
		return getColorRegistry().get(col);
	}
	
	/**
	 * Eine Hex-String Beschreibung einer Farbe liefern
	 * 
	 * @param rgb
	 *            Die Farbe in RGB-Form
	 * @return
	 */
	public static String createColor(final RGB rgb){
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(StringTool.pad(StringTool.LEFT, '0', Integer.toHexString(rgb.red), 2))
				.append(StringTool.pad(StringTool.LEFT, '0', Integer.toHexString(rgb.green), 2))
				.append(StringTool.pad(StringTool.LEFT, '0', Integer.toHexString(rgb.blue), 2));
			String srgb = sb.toString();
			getColorRegistry().put(srgb, rgb);
			return srgb;
		} catch (NumberFormatException nex) {
			getColorRegistry().put("A0A0A0", new RGB(0xa0, 0xa0, 0xa0));
			return "A0A0A0";
		}
	}
	
	public static Shell getTopShell(){
		return getDisplay().getActiveShell();
	}
	
	/**
	 * Run a runnable asynchroneously in the UI Thread The method will immediately return (not wait
	 * for the runnable to exit)
	 */
	public static void asyncExec(Runnable runnable){
		getDisplay().asyncExec(runnable);
	}
	
	/**
	 * Run a runnable synchroneously in the UI Thread. The method will not return until the runnable
	 * exited
	 * 
	 * @param runnable
	 */
	public static void syncExec(Runnable runnable){
		getDisplay().syncExec(runnable);
		// BusyIndicator.showWhile(getDisplay(), runnable);
	}
}
