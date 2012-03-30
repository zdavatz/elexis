package ch.elexis.rs232;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "ch.elexis.rs232.messages"; //$NON-NLS-1$
	public static String AbstractConnection_ComPortInUse;
	public static String AbstractConnection_PleaseWait;
	public static String SerialParameters_4;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
	private Messages(){}
}
