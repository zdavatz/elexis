package ch.elexis.exchange;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "ch.elexis.exchange.messages"; //$NON-NLS-1$
	public static String BlockContainer_Blockbeschreibung;
	public static String BlockContainer_xchangefiles;
	public static String KontaktMatcher_noauto1;
	public static String KontaktMatcher_noauto2;
	public static String KontaktMatcher_noauto3;
	public static String KontaktMatcher_noauto4;
	public static String KontaktMatcher_noauto5;
	public static String KontaktMatcher_OrganizationNotFound;
	public static String KontaktMatcher_OrganizationNotUnique;
	public static String KontaktMatcher_PersonNotFound;
	public static String KontaktMatcher_PersonNotUnique;
	public static String XChangeContainer_kg;
	public static String XChangeContainer_kontakte;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
	private Messages(){}
}
