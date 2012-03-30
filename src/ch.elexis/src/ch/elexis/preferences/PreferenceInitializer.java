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
 *  $Id: PreferenceInitializer.java 5755 2009-09-26 19:16:57Z rgw_ch $
 *******************************************************************************/
package ch.elexis.preferences;

import java.io.File;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import ch.elexis.Desk;
import ch.elexis.Hub;
import ch.elexis.StringConstants;
import ch.elexis.admin.AccessControlDefaults;
import ch.elexis.data.Brief;
import ch.elexis.data.PersistentObject;
import ch.elexis.util.Log;
import ch.rgw.tools.StringTool;

/**
 * Vorgabewerte setzen, wo nötig. Bitte in den drei Funktionen dieser Klasse alle notwendigen
 * Voreinstellungen eintragen.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {
	
	/**
	 * Diese Funktion wird automatisch beim Programmstart aufgerufen, und setzt alle hier
	 * definierten Einstellungswerte auf Voreinstellungen, sofern noch keine vom Anwender erstellten
	 * Werte vorhanden sind. Hier alle Benutzerspezifischen Voreinstellungen eintragen
	 */
	public void initializeDefaultPreferences(){
		IPreferenceStore localstore = new SettingsPreferenceStore(Hub.localCfg);
		
		// Datenbank
		/*
		 * localstore.setDefault(PreferenceConstants.DB_NAME,"hsql"); //$NON-NLS-1$
		 * localstore.setDefault(PreferenceConstants.DB_CLASS,"org.hsqldb.jdbcDriver");
		 * //$NON-NLS-1$ String base=getDefaultDBPath();
		 * 
		 * localstore.setDefault(PreferenceConstants.DB_CONNECT,"jdbc:hsqldb:"+base+"/db");
		 * //$NON-NLS-1$ //$NON-NLS-2$ localstore.setDefault(PreferenceConstants.DB_USERNAME,"sa");
		 * //$NON-NLS-1$ localstore.setDefault(PreferenceConstants.DB_PWD,""); //$NON-NLS-1$
		 * localstore.setDefault(PreferenceConstants.DB_TYP,"hsqldb"); //$NON-NLS-1$
		 */
		localstore.setDefault(PreferenceConstants.DB_NAME, "h2"); //$NON-NLS-1$
		//localstore.setDefault(PreferenceConstants.DB_CLASS,"org.h2.Driver"); //$NON-NLS-1$
		String base = getDefaultDBPath();
		
		localstore.setDefault(PreferenceConstants.DB_CONNECT, "jdbc:h2:" + base + "/db;MODE=MySQL"); //$NON-NLS-1$ //$NON-NLS-2$
		localstore.setDefault(PreferenceConstants.DB_USERNAME, "sa"); //$NON-NLS-1$
		localstore.setDefault(PreferenceConstants.DB_PWD, ""); //$NON-NLS-1$
		localstore.setDefault(PreferenceConstants.DB_TYP, "mysql"); //$NON-NLS-1$
		// Ablauf
		File userhome = new File(System.getProperty("user.home") + File.separator + "elexis"); //$NON-NLS-1$ //$NON-NLS-2$
		if (!userhome.exists()) {
			userhome.mkdirs();
		}
		localstore.setDefault(PreferenceConstants.ABL_LOGFILE, userhome.getAbsolutePath()
			+ File.separator + "elexis.log"); //$NON-NLS-1$
		localstore.setDefault(PreferenceConstants.ABL_LOGFILE_MAX_SIZE, new Integer(
			Log.DEFAULT_LOGFILE_MAX_SIZE).toString());
		localstore.setDefault(PreferenceConstants.ABL_LOGLEVEL, 2);
		localstore.setDefault(PreferenceConstants.ABL_LOGALERT, 1);
		localstore.setDefault(PreferenceConstants.ABL_TRACE, "none"); //$NON-NLS-1$
		localstore.setDefault(PreferenceConstants.ABL_BASEPATH, userhome.getAbsolutePath());
		localstore.setDefault(PreferenceConstants.ABL_CACHELIFETIME,
			PersistentObject.CACHE_DEFAULT_LIFETIME);
		localstore.setDefault(PreferenceConstants.ABL_HEARTRATE, 30);
		Hub.localCfg.set(PreferenceConstants.ABL_BASEPATH, userhome.getAbsolutePath());
		
		// Texterstellung
		if (System.getProperty("os.name").toLowerCase().startsWith("win")) { //$NON-NLS-1$ //$NON-NLS-2$
			localstore.setDefault(PreferenceConstants.P_TEXTMODUL, "NOA-Text"); //$NON-NLS-1$
			if (localstore.getString(PreferenceConstants.P_TEXTMODUL).equals(StringTool.leer)) {
				localstore.setValue(PreferenceConstants.P_TEXTMODUL, "NOA-Text"); //$NON-NLS-1$
			}
		} else {
			localstore.setDefault(PreferenceConstants.P_TEXTMODUL, "OpenOffice Wrapper"); //$NON-NLS-1$
			if (localstore.getString(PreferenceConstants.P_TEXTMODUL).equals("")) { //$NON-NLS-1$
				localstore.setValue(PreferenceConstants.P_TEXTMODUL, "OpenOffice Wrapper"); //$NON-NLS-1$
			}
		}
		File elexisbase = new File(Hub.getBasePath());
		File fDef = new File(elexisbase.getParentFile().getParent() + "/ooo"); //$NON-NLS-1$
		String defaultbase;
		if (fDef.exists()) {
			defaultbase = fDef.getAbsolutePath();
		} else {
			defaultbase = Hub.localCfg.get(PreferenceConstants.P_OOBASEDIR, "."); //$NON-NLS-1$
		}
		System.setProperty("openoffice.path.name", defaultbase); //$NON-NLS-1$
		localstore.setDefault(PreferenceConstants.P_OOBASEDIR, defaultbase);
		localstore.setValue(PreferenceConstants.P_OOBASEDIR, defaultbase);
		
		// Dokument
		StringBuilder sb = new StringBuilder();
		sb.append("Alle,").append(Brief.UNKNOWN).append(",").append(Brief.AUZ).append(",") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			.append(Brief.RP).append(",").append(Brief.LABOR); //$NON-NLS-1$
		
		localstore.setDefault(PreferenceConstants.DOC_CATEGORY, sb.toString());
		Hub.localCfg.flush();
	}
	
	public static String getDefaultDBPath(){
		String base;
		File f = new File(Hub.getBasePath() + "/rsc/demodata"); //$NON-NLS-1$
		if (f.exists() && f.canWrite()) {
			base = f.getAbsolutePath();
		} else {
			base = System.getenv("TEMP"); //$NON-NLS-1$
			if (base == null) {
				base = System.getenv("TMP"); //$NON-NLS-1$
				if (base == null) {
					base = System.getProperty("user.home"); //$NON-NLS-1$
				}
			}
			base += "/elexisdata"; //$NON-NLS-1$
			f = new File(base);
			if (!f.exists()) {
				f.mkdirs();
			}
		}
		return base;
	}
	
	/**
	 * Diese Funktion wird nach dem Erstellen des Display aufgerufen und dient zum Initialiseren
	 * früh benötigter Einstellungen, die bereits ein Display benötigen
	 * 
	 */
	public void initializeDisplayPreferences(Display display){
		Desk.getColorRegistry().put(Desk.COL_RED, new RGB(255, 0, 0));
		Desk.getColorRegistry().put(Desk.COL_GREEN, new RGB(0, 255, 0));
		Desk.getColorRegistry().put(Desk.COL_BLUE, new RGB(0, 0, 255));
		Desk.getColorRegistry().put(Desk.COL_SKYBLUE, new RGB(135, 206, 250));
		Desk.getColorRegistry().put(Desk.COL_LIGHTBLUE, new RGB(0, 191, 255));
		Desk.getColorRegistry().put(Desk.COL_BLACK, new RGB(0, 0, 0));
		Desk.getColorRegistry().put(Desk.COL_GREY, new RGB(0x60, 0x60, 0x60));
		Desk.getColorRegistry().put(Desk.COL_WHITE, new RGB(255, 255, 255));
		Desk.getColorRegistry().put(Desk.COL_DARKGREY, new RGB(50, 50, 50));
		Desk.getColorRegistry().put(Desk.COL_LIGHTGREY, new RGB(180, 180, 180));
		Desk.getColorRegistry().put(Desk.COL_GREY60, new RGB(153, 153, 153));
		Desk.getColorRegistry().put(Desk.COL_GREY20, new RGB(51, 51, 51));
		
		FontData[] small = new FontData[] {
			new FontData("Helvetica", 7, SWT.NORMAL)}; //$NON-NLS-1$
		Hub.userCfg
			.set(
				PreferenceConstants.USR_SMALLFONT + "_default", PreferenceConverter.getStoredRepresentation(small)); //$NON-NLS-1$
	}
	
	/**
	 * Diese Funktion wird nach erstem Erstellen der Datenbank (d.h. nur ein einziges Mal)
	 * aufgerufen und belegt globale Voreinstellungen. Hier alle im ganzen Netzwerk und für alle
	 * Benutzer gültigen Voreinstellungen eintragen
	 * 
	 */
	public void initializeGlobalPreferences(){
		IPreferenceStore global = new SettingsPreferenceStore(Hub.globalCfg);
		global.setDefault(PreferenceConstants.ABL_TRACE, "none"); //$NON-NLS-1$
		Hub.globalCfg.flush();
	}
	
	/**
	 * Diese Funktion wird ebenfalls nur beim ersten Mal nach dem Erstellen der Datenbank aufgerufen
	 * und erledigt die Vorkonfiguration der Zugriffsrechte Hier alle Zugriffsrechte voreinstellen
	 */
	public void initializeGrants(){
		Hub.globalCfg.set("groups", StringConstants.ROLES_DEFAULT); //$NON-NLS-1$
		Hub.acl.grant(StringConstants.ROLE_ALL, AccessControlDefaults.getAlle());
		Hub.acl.grant(StringConstants.ROLE_USERS, AccessControlDefaults.getAnwender());
		Hub.acl.flush();
	}
}
