package ch.rgw.tools;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Log {
	
	/** Experimentell */
	public static final int NOTHING = 0;
	/** Fatale Fehler, Programmabbruch */
	public static final int FATALS = 1;
	/** Nichtfatale Fehler, Programm kann weiterlaufen */
	public static final int ERRORS = 2;
	/** Warnung, Programm l�uft normal weiter, es k�nnten aber Probleme auftreten */
	public static final int WARNINGS = 3;
	/** Reine Informationen, kein Einfluss aufs Programm */
	public static final int INFOS = 4;
	/** F�r Debugzwecke gedachte Meldungen */
	public static final int DEBUGMSG = 5;
	/** Immer auszugebende Meldungen, die aber keinem Fehler entsprechen */
	public static final int TRACE = 6;
	/** Immer auszugebende Meldungen, automatisch mit einem Timestamp versehen */
	public static final int SYNCMARK = -1;
	private static Logger l;
	
	public static Log get(String name){
		l = Logger.getLogger(name);
		return new Log();
	}
	
	public void log(String message, int level){
		l.log(translate(level), message);
	}
	
	public void log(Level level, String message){
		l.log(level, message);
	}
	
	private Level translate(int logLevel){
		switch (logLevel) {
		case NOTHING:
			return Level.FINEST;
		case FATALS:
			return Level.SEVERE;
		case ERRORS:
			return Level.SEVERE;
		case WARNINGS:
			return Level.WARNING;
		case INFOS:
			return Level.INFO;
		case DEBUGMSG:
			return Level.FINE;
		case TRACE:
			return Level.ALL;
		case SYNCMARK:
			return Level.ALL;
		default:
			return Level.ALL;
		}
	}
}
