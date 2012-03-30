package ch.elexis.views.textsystem;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "ch.elexis.views.textsystem.messages"; //$NON-NLS-1$
	public static String AbstractProperties_message_FileNotFound;
	public static String PlatzhalterProperties_label_no_category;
	public static String PlatzhalterProperties_message_empty;
	public static String PlatzhalterProperties_tooltip_no_category;
	public static String PlatzhalterView_menu_copy;
	public static String PlatzhalterView_message_Info;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
	private Messages(){}
}
