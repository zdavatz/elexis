/*******************************************************************************
 * Copyright (c) 2005-2011, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *******************************************************************************/

package ch.elexis;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import ch.elexis.actions.ElexisEvent;
import ch.elexis.actions.ElexisEventDispatcher;
import ch.elexis.actions.ElexisEventListenerImpl;
import ch.elexis.actions.GlobalActions;
import ch.elexis.actions.Heartbeat;
import ch.elexis.actions.Heartbeat.HeartListener;
import ch.elexis.admin.AccessControl;
import ch.elexis.data.Anwender;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.PersistentObjectFactory;
import ch.elexis.data.Query;
import ch.elexis.data.Reminder;
import ch.elexis.preferences.PreferenceConstants;
import ch.elexis.preferences.PreferenceInitializer;
import ch.elexis.util.FileUtility;
import ch.elexis.util.Log;
import ch.elexis.util.PlatformHelper;
import ch.elexis.util.SWTHelper;
import ch.rgw.io.LockFile;
import ch.rgw.io.Settings;
import ch.rgw.io.SqlSettings;
import ch.rgw.io.SysSettings;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;
import ch.rgw.tools.VersionInfo;

/**
 * Diese Klasse ist der OSGi-Activator und steuert somit Start und Ende der Anwendung. Ganz früh
 * (vor dem Initialisieren der anwendung) und ganz spät (unmittelbar vor dem Entfernen der
 * Anwendung) notwendige Schritte müssen hier durchgeführt werden. Ausserdem werden hier globale
 * Variablen und Konstanten angelegt.
 */
public class Hub extends AbstractUIPlugin {
	// Globale Konstanten
	public final boolean DEBUGMODE;
	public static final String APPLICATION_NAME = "Elexis"; //$NON-NLS-1$
	public static final String PLUGIN_ID = "ch.elexis"; //$NON-NLS-1$
	public static final String COMMAND_PREFIX = PLUGIN_ID + ".commands."; //$NON-NLS-1$
	static final String neededJRE = "1.6.0"; //$NON-NLS-1$
	public static final String Version = "2.1.6.dev-qualifier"; //$NON-NLS-1$
	public static final String DBVersion = "1.8.11"; //$NON-NLS-1$
	public static final String SWTBOTTEST_KEY = "ch.elexis.swtbottest.key"; //$NON-NLS-1$
	static final String[] mine = {
		"ch.elexis", "ch.rgw"}; //$NON-NLS-1$ //$NON-NLS-2$
	private static List<ShutdownJob> shutdownJobs = new LinkedList<ShutdownJob>();
	
	// Globale Variable
	/**
	 * Suche externe Config - poor mans dependency -> see
	 * ch.elexis.ElexisConfigurationConstants.java
	 */
	public static boolean ecc = ElexisConfigurationConstants.init();
	
	/** Das Singleton-Objekt dieser Klasse */
	public static Hub plugin;
	
	/** Lokale Einstellungen (Werden in der Registry bzw. ~/.java gespeichert) */
	public static Settings localCfg;
	
	/** Globale Einstellungen (Werden in der Datenbank gespeichert) */
	public static Settings globalCfg;
	
	/** Anwenderspezifische Einstellungen (Werden in der Datenbank gespeichert) */
	public static Settings userCfg;
	
	/** Mandantspezifische EInstellungen (Werden in der Datenbank gespeichert) */
	public static Settings mandantCfg;
	
	/** Zentrale Logdatei */
	public static Log log;
	
	/** Globale Aktionen */
	public static GlobalActions mainActions;
	
	/** Der aktuell angemeldete Anwender */
	public static Anwender actUser;
	
	/** Der Mandant, auf dessen namen die aktuellen Handlungen gehen */
	public static Mandant actMandant;
	
	/** Die zentrale Zugriffskontrolle */
	public static final AccessControl acl = new AccessControl();
	
	/** Der Initialisierer für die Voreinstellungen */
	public static final PreferenceInitializer pin = new PreferenceInitializer();;
	
	/** Factory für interne PersistentObjects */
	public static final PersistentObjectFactory poFactory = new PersistentObjectFactory();
	
	/** Heartbeat */
	public static Heartbeat heart;
	
	/**
	 * Beschreibbares Verzeichnis für userspezifische Konfigurationsdaten etc. Achtung: "User" meint
	 * hier: den eingeloggten Betriebssystem-User, nicht den Elexis-User. In Windows wird userDir
	 * meist %USERPROFILE%\elexis sein, in Linux ~./elexis. Es kann mit getWritableUserDir() geholt
	 * werden.
	 * */
	private static File userDir;
	
	private final ElexisEventListenerImpl eeli_pat = new ElexisEventListenerImpl(Patient.class) {
		public void runInUi(ElexisEvent ev){
			setWindowText((Patient) ev.getObject());
		}
	};
	
	/**
	 * Constructor. No Eclipse dependend initializations here because the Platform has not been
	 * iniatialized fully yet
	 */
	public Hub(){
		if ("true".equals(System.getProperty("DEBUGMODE"))) {
			DEBUGMODE = true;
		} else {
			DEBUGMODE = false;
		}
		log = Log.get("Elexis startup"); //$NON-NLS-1$
		getWritableUserDir();
		localCfg = new SysSettings(SysSettings.USER_SETTINGS, Desk.class);
		setUserDir(userDir);
		
	}
	
	/*
	 * called in startup sequence after initialization of the platform but before initalization of
	 * the workbench
	 */
	private static void initializeLog(final Settings cfg){
		String logfileName = cfg.get(PreferenceConstants.ABL_LOGFILE, null);
		int maxLogfileSize = -1;
		String logPath;
		if (logfileName == null) {
			logPath = new File(userDir, "elexis.log").getAbsolutePath(); //$NON-NLS-1$
		} else if (logfileName.equalsIgnoreCase("none")) { //$NON-NLS-1$
			logPath = "none"; //$NON-NLS-1$
		} else {
			logPath = new File(logfileName).getAbsolutePath();
		}
		try {
			String defaultValue = new Integer(Log.DEFAULT_LOGFILE_MAX_SIZE).toString();
			String value = cfg.get(PreferenceConstants.ABL_LOGFILE_MAX_SIZE, defaultValue);
			maxLogfileSize = Integer.parseInt(value.trim());
		} catch (NumberFormatException ex) {
			// do nothing
		}
		Log.setLevel(cfg.get(PreferenceConstants.ABL_LOGLEVEL, Log.ERRORS));
		Log.setOutput(logPath, maxLogfileSize);
		Log.setAlertLevel(cfg.get(PreferenceConstants.ABL_LOGALERT, Log.FATALS));
		// Exception handler initialiseren, Output wie log, auf eigene Klassen
		// begrenzen
		ExHandler.setOutput(logPath);
		ExHandler.setClasses(mine);
		
	}
	
	private static void initializeLock(){
		final int timeoutSeconds = 600;
		try {
			final LockFile lockfile = new LockFile(userDir, "elexislock", 4, timeoutSeconds); //$NON-NLS-1$
			final int n = lockfile.lock();
			if (n == 0) {
				SWTHelper.alert(Messages.Hub_toomanyinstances, Messages.Hub_nomoreinstances);
				System.exit(2);
			} else {
				HeartListener lockListener = new HeartListener() {
					long timeSet;
					
					public void heartbeat(){
						long now = System.currentTimeMillis();
						if ((now - timeSet) > timeoutSeconds) {
							lockfile.updateLock(n);
							timeSet = now;
						}
					}
				};
				heart.addListener(lockListener, Heartbeat.FREQUENCY_LOW);
			}
		} catch (IOException ex) {
			log.log("Can not aquire lock file in " + userDir + "; " + ex.getMessage(), Log.ERRORS); //$NON-NLS-1$
		}
	}
	
	public static int getSystemLogLevel(){
		return localCfg.get(PreferenceConstants.ABL_LOGLEVEL, Log.ERRORS);
	}
	
	@Override
	public void start(final BundleContext context) throws Exception{
		super.start(context);
		plugin = this;
		startUpBundle();
		ElexisEventDispatcher.getInstance().addListeners(eeli_pat);
		// ElexisEventCascade.getInstance().start();
	}
	
	@Override
	public void stop(final BundleContext context) throws Exception{
		// ElexisEventCascade.getInstance().stop();
		ElexisEventDispatcher.getInstance().removeListeners(eeli_pat);
		ElexisEventDispatcher.getInstance().dump();
		heart.stop();
		plugin = null;
		PersistentObject.disconnect();
		globalCfg = null;
		super.stop(context);
	}
	
	/**
	 * Hier stehen Aktionen, die ganz früh, noch vor dem Starten der Workbench, durchgeführt werden
	 * sollen.
	 */
	public void startUpBundle(){
		String[] args = Platform.getApplicationArgs();
		String config = "default"; //$NON-NLS-1$
		for (String s : args) {
			if (s.startsWith("--use-config=")) { //$NON-NLS-1$
				String[] c = s.split("="); //$NON-NLS-1$
				config = c[1];
				localCfg = localCfg.getBranch(config, true);
			} else if (s.startsWith("--plaf=")) { //$NON-NLS-1$
				String[] c = s.split("="); //$NON-NLS-1$
				String plaf = c[1];
				localCfg.set(PreferenceConstants.USR_PLAF, plaf);
				localCfg.flush();
			}
		}
		initializeLog(localCfg);
		log.log(Messages.Hub_12 + config, Log.INFOS);
		// Damit Anfragen auf userCfg und mandantCfg bei nicht eingeloggtem User
		// keine NPE werfen
		userCfg = localCfg;
		mandantCfg = localCfg;
		
		String basePath = FileUtility.getFilepath(PlatformHelper.getBasePath("ch.elexis")); //$NON-NLS-1$
		localCfg.set("elexis-basepath", FileUtility.getFilepath(basePath)); //$NON-NLS-1$
		
		// Java Version prüfen
		VersionInfo vI = new VersionInfo(System.getProperty("java.version", "0.0.0")); //$NON-NLS-1$ //$NON-NLS-2$
		log.log(
			getId() + "; Java: " + vI.version() + "\nencoding: "
				+ System.getProperty("file.encoding"), Log.SYNCMARK);
		
		if (vI.isOlder(neededJRE)) {
			String msg = Messages.Hub_21 + neededJRE;
			getLog().log(new Status(Status.ERROR, "ch.elexis", //$NON-NLS-1$
				-1, msg, new Exception(msg)));
			SWTHelper.alert(Messages.Hub_23, msg);
			log.log(msg, Log.FATALS);
		}
		log.log(Messages.Hub_24 + getBasePath(), Log.INFOS);
		pin.initializeDefaultPreferences();
		
		heart = Heartbeat.getInstance();
		initializeLock();
	}
	
	/**
	 * Programmende
	 */
	public static void postShutdown(){
		// heart.stop();
		// JobPool.getJobPool().dispose();
		if (Hub.actUser != null) {
			Anwender.logoff();
		}
		if (globalCfg != null) {
			// We should not flush at this point, since this might
			// overwrite other client's
			// settings
			// acl.flush();
			// globalCfg.flush();
		}
		
		// shutdownjobs are executed after the workbench has been shut down.
		// So those jobs must not use any of the workbench's resources.
		if ((shutdownJobs != null) && (shutdownJobs.size() > 0)) {
			Shell shell = new Shell(Display.getDefault());
			MessageDialog dlg =
				new MessageDialog(shell, Messages.Hub_title_configuration,
					Dialog.getDefaultImage(), Messages.Hub_message_configuration,
					SWT.ICON_INFORMATION, new String[] {}, 0);
			dlg.setBlockOnOpen(false);
			dlg.open();
			for (ShutdownJob job : shutdownJobs) {
				try {
					job.doit();
				} catch (Exception e) {
					log.log("Error starting job: " + e.getMessage(), Log.ERRORS);
				}
			}
			dlg.close();
		}
	}
	
	public static void setMandant(final Mandant m){
		if (actMandant != null) {
			// Hub.mandantCfg.dump(null);
			mandantCfg.flush();
		}
		if (m == null) {
			if ((mainActions != null) && (mainActions.mainWindow != null)
				&& (mainActions.mainWindow.getShell() != null)) {
				mandantCfg = userCfg;
			}
		} else {
			mandantCfg =
				new SqlSettings(PersistentObject.getConnection(),
					"USERCONFIG", "Param", "Value", "UserID=" + m.getWrappedId()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		}
		actMandant = m;
		setWindowText(null);
		ElexisEventDispatcher.getInstance().fire(
			new ElexisEvent(Hub.actMandant, Mandant.class, ElexisEvent.EVENT_MANDATOR_CHANGED));
	}
	
	public static void setWindowText(Patient pat){
		StringBuilder sb = new StringBuilder();
		sb.append("Elexis ").append(Version).append(" - "); //$NON-NLS-1$ //$NON-NLS-2$
		if (Hub.actUser == null) {
			sb.append(Messages.Hub_nouserloggedin);
		} else {
			sb.append(" ").append(Hub.actUser.getLabel()); //$NON-NLS-1$
		}
		if (Hub.actMandant == null) {
			sb.append(Messages.Hub_nomandantor);
			
		} else {
			sb.append(" / ").append(Hub.actMandant.getLabel()); //$NON-NLS-1$
		}
		if (pat == null) {
			pat = (Patient) ElexisEventDispatcher.getSelected(Patient.class);
		}
		if (pat == null) {
			sb.append(Messages.Hub_nopatientselected);
		} else {
			String nr = pat.getPatCode();
			String alter = pat.getAlter();
			sb.append("  / ").append(pat.getLabel()).append("(").append(alter).append(") - ") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				.append("[").append(nr).append("]"); //$NON-NLS-1$ //$NON-NLS-2$
			
			if (Reminder.findForPatient(pat, Hub.actUser).size() != 0) {
				sb.append(Messages.Hub_message_reminders);
			}
			String act = new TimeTool().toString(TimeTool.DATE_COMPACT);
			TimeTool ttPatg = new TimeTool();
			if (ttPatg.set(pat.getGeburtsdatum())) {
				String patg = ttPatg.toString(TimeTool.DATE_COMPACT);
				if (act.substring(4).equals(patg.substring(4))) {
					sb.append(Messages.Hub_message_birthday);
				}
			}
		}
		if (mainActions.mainWindow != null) {
			Shell shell = mainActions.mainWindow.getShell();
			if ((shell != null) && (!shell.isDisposed())) {
				mainActions.mainWindow.getShell().setText(sb.toString());
			}
		}
	}
	
	/**
	 * Returns an image descriptor for the image file at the given plug-in relative path.
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(final String path){
		return AbstractUIPlugin.imageDescriptorFromPlugin("ch.elexis", path); //$NON-NLS-1$
	}
	
	public static String getId(){
		StringBuilder sb = new StringBuilder();
		sb.append(APPLICATION_NAME).append(" v.").append(Version).append("\n")
			.append(getRevision(true)).append("\n").append(System.getProperty("os.name"))
			.append(StringConstants.SLASH).append(System.getProperty("os.version"))
			.append(StringConstants.SLASH).append(System.getProperty("os.arch")); //$NON-NLS-1$
		return sb.toString();
	}
	
	/**
	 * Revisionsnummer und Erstellungsdatum dieser Instanz ermitteln. Dazu wird die beim letzten
	 * Commit von Subversion geänderte Variable LastChangedRevision untersucht, und fürs Datum das
	 * von ANT beim build eingetragene Datum gesucht. Wenn diese Instanz nicht von ANT erstellt
	 * wurde, handelt es sich um eine Entwicklerversion, welche unter Eclipse-Kontrolle abläuft.
	 * 
	 * Note: Obsoleted with change to mercurial
	 */
	/*
	 * public static String getRevision(final boolean withdate){ String SVNREV =
	 * "$LastChangedRevision: 6387 $"; //$NON-NLS-1$ String res =
	 * SVNREV.replaceFirst("\\$LastChangedRevision:\\s*([0-9]+)\\s*\\$", "$1"); //$NON-NLS-1$
	 * //$NON-NLS-2$ if (withdate == true) { File base = new File(getBasePath() +
	 * "/rsc/compiletime.txt"); //$NON-NLS-1$ if (base.canRead()) { String dat = null; try { dat =
	 * FileTool.readTextFile(base); } catch(IOException e) { ExHandler.handle(e); }
	 * 
	 * if (dat.equals("@TODAY@")) { //$NON-NLS-1$ res += Messages.Hub_38; } else { res += ", " + new
	 * TimeTool(dat + "00").toString(TimeTool.FULL_GER); //$NON-NLS-1$ //$NON-NLS-2$ } } else { res
	 * += Messages.Hub_compiletimenotknown; } } Hub.plugin.getBundle(). return res; }
	 */
	
	public static String getRevision(final boolean withDate){
		StringBuilder sb = new StringBuilder();
		Bundle bundle = plugin.getBundle();
		org.osgi.framework.Version v = bundle.getVersion();
		sb.append("[Bundle info: ").append(v.toString());
		String check = System.getProperty("inEclipse"); //$NON-NLS-1$
		if (check != null && check.equals("true")) { //$NON-NLS-1$
			sb.append(" (developer version)");
		}
		if (withDate) {
			long lastModify = bundle.getLastModified();
			TimeTool tt = new TimeTool(lastModify);
			sb.append("; ").append(tt.toString(TimeTool.DATE_ISO));
		}
		sb.append("]");
		return sb.toString();
	}
	
	/**
	 * get the base directory of this currently running elexis application
	 * 
	 * @return the topmost directory of this application or null if this information could not be
	 *         retrieved
	 */
	public static String getBasePath(){
		return PlatformHelper.getBasePath(PLUGIN_ID);
	}
	
	/**
	 * get a list af all users known to this system
	 */
	public static List<Anwender> getUserList(){
		Query<Anwender> qbe = new Query<Anwender>(Anwender.class);
		return qbe.execute();
	}
	
	/**
	 * get a list of all mandators known to this system
	 */
	public static List<Mandant> getMandantenList(){
		Query<Mandant> qbe = new Query<Mandant>(Mandant.class);
		return qbe.execute();
	}
	
	/**
	 * get the currently active Shell. If no such Shell exists, it will be created using dhe default
	 * Display.
	 * 
	 * @return always a valid shell. Never null
	 */
	public static Shell getActiveShell(){
		if (plugin != null) {
			IWorkbench wb = plugin.getWorkbench();
			if (wb != null) {
				IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
				if (win != null) {
					return win.getShell();
				}
			}
		}
		Display dis = Desk.getDisplay();
		if (dis == null) {
			dis = PlatformUI.createDisplay();
		}
		return new Shell(dis);
	}
	
	/**
	 * A job that executes during stop() of the plugin (that means after the workbench is shut down
	 * 
	 * @author gerry
	 * 
	 */
	public interface ShutdownJob {
		/**
		 * do whatever you like
		 */
		public void doit() throws Exception;
	}
	
	/**
	 * Add a ShutdownJob to the list of jobs that has to be done after the Elexis workbench was shut
	 * down.
	 * 
	 * @param job
	 */
	public static void addShutdownJob(final ShutdownJob job){
		if (!shutdownJobs.contains(job)) {
			shutdownJobs.add(job);
		}
	}
	
	public void setUserDir(File dir){
		userDir = dir;
		localCfg.set("elexis-userDir", dir.getAbsolutePath()); //$NON-NLS-1$
	}
	
	/**
	 * return a directory suitable for plugin specific configuration data. If no such dir exists, it
	 * will be created. If it could not be created, the application will refuse to start.
	 * 
	 * @return a directory that exists always and is always writable and readable for plugins of the
	 *         currently running elexis instance. Caution: this directory is not necessarily shared
	 *         among different OS-Users. In Windows it is normally %USERPROFILE%\elexis, in Linux
	 *         ~./elexis
	 */
	public static File getWritableUserDir(){
		if (userDir == null) {
			String userhome = null;
			
			if (localCfg != null) {
				userhome = localCfg.get("elexis-userDir", null); //$NON-NLS-1$
			}
			if (userhome == null) {
				userhome = System.getProperty("user.home"); //$NON-NLS-1$
			}
			if (StringTool.isNothing(userhome)) {
				userhome = System.getProperty("java.io.tempdir"); //$NON-NLS-1$
			}
			userDir = new File(userhome, "elexis"); //$NON-NLS-1$
		}
		if (!userDir.exists()) {
			if (!userDir.mkdirs()) {
				System.err.print("fatal: could not create Userdir"); //$NON-NLS-1$
				SWTHelper.alert("Panic exit", "could not create userdir " //$NON-NLS-1$ //$NON-NLS-2$
					+ userDir.getAbsolutePath());
				System.exit(-5);
			}
		}
		return userDir;
	}
	
	/**
	 * Return a directory suitable for temporary files. Most probably this will be a default tempdir
	 * provided by the os. If none such exists, it will be the user dir.
	 * 
	 * @return always a valid and writable directory.
	 */
	public static File getTempDir(){
		File ret = null;
		String temp = System.getProperty("java.io.tmpdir"); //$NON-NLS-1$
		if (!StringTool.isNothing(temp)) {
			ret = new File(temp);
			if (ret.exists() && ret.isDirectory()) {
				return ret;
			} else {
				if (ret.mkdirs()) {
					return ret;
				}
			}
		}
		return getWritableUserDir();
	}
	
	/**
	 * Return the name of a config instance, the user chose. This is just the valuie of the
	 * -Dconfig=xx runtime value or "default" if no -Dconfig was set
	 */
	public static String getCfgVariant(){
		String config = System.getProperty("config");
		return config == null ? "default" : config;
	}
}
