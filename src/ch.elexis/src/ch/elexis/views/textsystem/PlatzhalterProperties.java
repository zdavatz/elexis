package ch.elexis.views.textsystem;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.elexis.util.Log;

public class PlatzhalterProperties extends AbstractProperties {
	private static final long serialVersionUID = -6366568655870957480L;
	
	private static Log log = Log.get("PlatzhalterProperties"); //$NON-NLS-1$
	
	private final static String PLATZHALTER_FILENAME = "Platzhalter.txt"; //$NON-NLS-1$
	
	protected String getFilename(){
		return PLATZHALTER_FILENAME;
	}
	
	/**
	 * Read contents. Every line is divided in <br>
	 * <p>
	 * <category>.[<key>]=<description>
	 * </p>
	 * 
	 * @return
	 */
	public List<PlatzhalterTreeData> getList(){
		PlatzhalterTreeData root = new PlatzhalterTreeData("root", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		PlatzhalterTreeData noKategorie =
			new PlatzhalterTreeData(Messages.PlatzhalterProperties_label_no_category, "", //$NON-NLS-2$ //$NON-NLS-1$
				Messages.PlatzhalterProperties_tooltip_no_category);
		
		KategorieProperties katProperties = new KategorieProperties();
		
		Map<String, PlatzhalterTreeData> catTreeMap = new HashMap<String, PlatzhalterTreeData>();
		catTreeMap.put(noKategorie.getName(), noKategorie);
		
		Enumeration<Object> keyEnumeration = keys();
		while (keyEnumeration.hasMoreElements()) {
			String keyString = (String) keyEnumeration.nextElement();
			String value = getProperty(keyString);
			String category = noKategorie.getName();
			String name = ""; //$NON-NLS-1$
			int openBracket = keyString.indexOf("["); //$NON-NLS-1$
			int closeBracket = keyString.lastIndexOf("]"); //$NON-NLS-1$
			int firstPoint = keyString.indexOf("."); //$NON-NLS-1$
			if (firstPoint < 0) {
				firstPoint = keyString.indexOf(":"); //$NON-NLS-1$
			}
			if (firstPoint == 0) {
				// starts with point. This is wrong
				keyString = keyString.substring(1);
			}
			if (openBracket < 0) {
				// no bracket -> no key
				if (closeBracket > 0) {
					keyString = keyString.substring(0, closeBracket);
				}
				name = keyString;
				if (firstPoint < 0) {
					// no point
					category = keyString;
				} else {
					category = keyString.substring(0, firstPoint);
				}
			} else {
				if (closeBracket < 0) {
					// Keine ]
					name = keyString.substring(openBracket + 1);
				} else {
					name = keyString.substring(openBracket + 1, closeBracket);
					category = keyString.substring(0, openBracket);
					if (category.endsWith(".") || category.endsWith(":")) { //$NON-NLS-1$ //$NON-NLS-2$
						category = category.substring(0, category.length() - 1);
					}
				}
			}
			if (name != null && name.length() > 0) {
				PlatzhalterTreeData categoryPtd = null;
				if (category == null || category.length() == 0) {
					categoryPtd = noKategorie;
				} else {
					categoryPtd = catTreeMap.get(category);
				}
				if (categoryPtd == null) {
					String description = katProperties.getDescription(category);
					categoryPtd = new PlatzhalterTreeData(category, "", //$NON-NLS-1$
						description);
					catTreeMap.put(category, categoryPtd);
					root.addChild(categoryPtd);
				}
				String displayName = name;
				boolean startsWithCat = displayName.startsWith(category + ".") //$NON-NLS-1$
					|| displayName.startsWith(category + ":"); //$NON-NLS-1$
				if (startsWithCat && displayName.length() > category.length()) {
					displayName = displayName.substring(category.length());
					if (displayName.startsWith(".") //$NON-NLS-1$
						|| displayName.startsWith(":")) { //$NON-NLS-1$
						displayName = displayName.substring(1);
					}
				}
				if (value == null || value.length() == 0) {
					value = displayName;
				}
				categoryPtd.addChild(new PlatzhalterTreeData(displayName, "[" //$NON-NLS-1$
					+ name + "]", value)); //$NON-NLS-1$
			} else {
				log.log(Messages.PlatzhalterProperties_message_empty, Log.INFOS);
			}
		}
		
		if (noKategorie.getChildren().size() > 0) {
			root.addChild(noKategorie);
		}
		return root.getChildren();
	}
}
