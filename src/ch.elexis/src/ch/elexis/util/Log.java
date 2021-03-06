/*******************************************************************************
 * Copyright (c) 2005-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 * $Id$
 *******************************************************************************/
package ch.elexis.util;

import java.util.logging.Level;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import ch.elexis.Desk;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.TimeTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Standardisiertes Log. Ein Programm kann das Log mit Log.get(präfix) anfordern
 * und fortan Ausgaben ins Log mittels Log(Text,level) machen. Die Ausgabe
 * erfolgt einstellbar nach stdout oder in eine Datei. Ob eine bestimmte Ausgabe
 * ins Log gelangt, hängt vom LogLevel und dem Text-Level ab. Wenn der Level
 * einer Meldung gleich oder niedriger ist, als der aktuell eingestellte
 * LogLevel, wird die Ausgabe gemacht, andernfalls wird sie verworfen. Ausserdem
 * kann festgelegt werden, ab welchem level eine Nachricht zu einer direkten
 * Benachrichtigung des Anwenders mittels MessageBox führt (setAlert und
 * setAlertLevel
 * 
 * @author G. Weirich
 */

public class Log {
	/** Experimentell */
	public static final int NOTHING = 0;
	/** Fatale Fehler, Programmabbruch */
	public static final int FATALS = 1;
	/** Nichtfatale Fehler, Programm kann weiterlaufen */
	public static final int ERRORS = 2;
	/**
	 * Warnung, Programm l�uft normal weiter, es k�nnten aber Probleme auftreten
	 */
	public static final int WARNINGS = 3;
	/** Reine Informationen, kein Einfluss aufs Programm */
	public static final int INFOS = 4;
	/** F�r Debugzwecke gedachte Meldungen */
	public static final int DEBUGMSG = 5;
	/** Immer auszugebende Meldungen, die aber keinem Fehler entsprechen */
	public static final int TRACE = 6;
	/** Immer auszugebende Meldungen, automatisch mit einem Timestamp versehen */
	public static final int SYNCMARK = -1;

	public static final int DEFAULT_LOGFILE_MAX_SIZE = 200000;

	private static String[] Levels = { "OK", "FATAL", "ERROR", "WARNING",
			"INFO", "DEBUG", "TRACE" };

	String prefix;
	private static int LogLevel;
	private static int alertLevel;
	private static String lastError;
	private static Shell doAlert;
	private static Logger l;

	static {
		LogLevel = 2;
		doAlert = null;
		alertLevel = Log.FATALS;
	}

	/**
	 * LogLevel einstellen
	 * 
	 * @param l
	 *            der gew�nschte Level. Ist immer global f�r alle Klassen des
	 *            aktuellen Programms.
	 */
	static public void setLevel(int l) {
		LogLevel = l;
	}

	/**
	 * AlertLevel einstellen. wenn der Level einer Nachricht unter diesem Level
	 * liegt, wird eine Alertbox zur Nazeige der Nachricht ge�ffnet (Anstatt nur
	 * zu loggen). Dies geht nur, wenn mit setAlert auch eine parent-Shell
	 * gesetzt worden ist.
	 */
	static public void setAlertLevel(int l) {
		alertLevel = l;
	}

	/**
	 * Alert inetellen oder l�schen. Wenn cmp nicht null ist, wird bei jeder
	 * Fehlermeldung>Log.Errors eine Alertbox mit der Fehlermeldung ausgegeben.
	 * 
	 * @param cmp
	 *            die Paent-Komponente f�r die Alertbox
	 */
	static public void setAlert(Shell cmp) {
		doAlert = cmp;
	}

	/**
	 * Das Log anfordern. Es gibt pro Programm nur ein Log.
	 * 
	 * @param prefix
	 *            Ein String, der allen Log-Ausgaben dieser Instanz
	 *            vorangestellt wird
	 * @return eine Log-Instanz
	 */
	static public Log get(String prefix) {
		l = LoggerFactory.getLogger(prefix);
		return new Log();
	}

	/**
	 * Eine Log-Nachricht ausgeben.
	 * 
	 * @param message
	 *            die Nachricht
	 * @param level
	 *            der level
	 */
	public void log(String message, int level) {
		if (level <= LogLevel) {
			if (level == FATALS || level == ERRORS)
				l.error(message);
			else if (level == WARNINGS)
				l.warn(message);
			else if (level == INFOS)
				l.info(message);
			else if (level == TRACE)
				l.trace(message);
			else
				l.debug(message);
			if (level <= alertLevel && PlatformUI.isWorkbenchRunning()) {
				if (level != SYNCMARK) {
					if (doAlert == null) {
						doAlert = Desk.getTopShell();
					}
					if (doAlert != null) {
						Desk.asyncExec(new Runnable() {
							public void run() {
								MessageBox msg = new MessageBox(doAlert,
										SWT.ICON_ERROR | SWT.OK);
								msg.setMessage(lastError);
								msg.open();
							}
						});

					}
				}
			}
		}
	}

	/**
	 * Eine Exception als Log-Nachricht ausgeben.
	 * 
	 * @param t
	 *            die Exception
	 * @param message
	 *            die Nachricht
	 * @param level
	 *            der level
	 */
	public void log(final Throwable t, String message, final int level) {
		if (message == null || message.length() == 0) {
			message = t.getMessage();
			if (message == null || message.length() == 0) {
				message = t.getClass().toString();
			}
		}
		log(message, level);
// TODO:	
		t.printStackTrace();
	}

	/**
	 * Eine Exception als Log-Nachricht ausgeben.
	 * 
	 * @param t
	 *            die Exception
	 * @param level
	 *            der level
	 */
	public void log(final Throwable t) {
		log(t, null, Log.ERRORS);
	}

	public static void trace(String msg) {
		l.trace(msg);
	}

	public boolean isDebug() {
		return DEBUGMSG <= LogLevel;
	}

	public boolean isInfo() {
		return INFOS <= LogLevel;
	}

	public boolean isWarn() {
		return WARNINGS <= LogLevel;
	}

	public boolean isError() {
		return ERRORS <= LogLevel;
	}

	private Log() { /* leer */
	}

	private Log(String p) {
		prefix = p;
	}

}