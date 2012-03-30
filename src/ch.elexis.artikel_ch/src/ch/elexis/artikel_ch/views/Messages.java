package ch.elexis.artikel_ch.views;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "ch.elexis.artikel_ch.views.messages"; //$NON-NLS-1$
	public static String MedikamentDetailDisplay_Title;
	public static String MiGelDetailDisplay_PriceUnit;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
	private Messages(){}
}
