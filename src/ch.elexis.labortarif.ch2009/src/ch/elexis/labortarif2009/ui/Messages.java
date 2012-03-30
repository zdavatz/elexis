package ch.elexis.labortarif2009.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "ch.elexis.labortarif2009.ui.messages"; //$NON-NLS-1$
	public static String DetailDisplay_chapter;
	public static String DetailDisplay_code;
	public static String DetailDisplay_fachbereich;
	public static String DetailDisplay_limitation;
	public static String DetailDisplay_name;
	public static String DetailDisplay_taxpoints;
	public static String Labor2009Selector_code;
	public static String Labor2009Selector_text;
	public static String Preferences_automaticallyCalculatioAdditions;
	public static String Preferences_pleaseEnterMultiplier;
	public static String Preferences_specialities;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
	private Messages(){}
}
