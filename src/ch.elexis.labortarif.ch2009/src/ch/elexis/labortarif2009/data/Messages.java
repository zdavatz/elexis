package ch.elexis.labortarif2009.data;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "ch.elexis.labortarif2009.data.messages"; //$NON-NLS-1$
	public static String Importer_importEAL;
	public static String Importer_selectFile;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
	private Messages(){}
}
