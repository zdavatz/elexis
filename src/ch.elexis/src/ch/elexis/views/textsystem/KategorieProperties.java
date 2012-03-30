package ch.elexis.views.textsystem;

public class KategorieProperties extends AbstractProperties {
	private static final long serialVersionUID = 9181779544703607658L;
	
	private final static String KATEGORIE_FILENAME = "Kategorie.txt"; //$NON-NLS-1$
	
	protected String getFilename(){
		return KATEGORIE_FILENAME;
	}
	
	public String getDescription(final String kategorie){
		Object value = get(kategorie);
		if (value != null) {
			return value.toString();
		}
		return kategorie;
	}
	
}
