package ch.elexis.scripting;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "ch.elexis.scripting.messages"; //$NON-NLS-1$
	public static String ScriptEditor_editScript;
	public static String ScriptEditor_ScriptTitle;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
	private Messages(){}
}
