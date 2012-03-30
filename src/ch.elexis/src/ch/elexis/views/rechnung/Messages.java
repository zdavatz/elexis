package ch.elexis.views.rechnung;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import ch.rgw.tools.ExHandler;

public class Messages {
	private static final String BUNDLE_NAME = "ch.elexis.views.rechnung.messages"; //$NON-NLS-1$
	
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);
	
	private Messages(){}
	
	public static String getString(String key){
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
	
	public static String getString(String key, Object[] params){
		if (params == null) {
			return getString(key);
		}
		try {
			return java.text.MessageFormat.format(getString(key), params);
		} catch (Exception e) {
			ExHandler.handle(e);
			return "!" + key + "!";
		}
	}
}
