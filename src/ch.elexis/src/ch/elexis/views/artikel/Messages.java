package ch.elexis.views.artikel;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "ch.elexis.views.artikel.messages"; //$NON-NLS-1$
	public static String ArtikelContextMenu_create;
	public static String ArtikelContextMenu_deleteAction;
	public static String ArtikelContextMenu_deleteActionConfirmCaption;
	public static String ArtikelContextMenu_deleteActionToolTip;
	public static String ArtikelContextMenu_deleteConfirmBody;
	public static String ArtikelContextMenu_newAction;
	public static String ArtikelContextMenu_newActionTooltip;
	public static String ArtikelContextMenu_pleaseEnterNameForArticle;
	public static String ArtikelContextMenu_propertiesAction;
	public static String ArtikelContextMenu_propertiesTooltip;
	public static String Artikeldetail_ATC_code;
	public static String Artikeldetail_EAN;
	public static String Artikeldetail_Einkaufspreis;
	public static String Artikeldetail_istbestand;
	public static String Artikeldetail_istbestandAnbruch;
	public static String Artikeldetail_lieferant;
	public static String Artikeldetail_LieferantWaehlen;
	public static String Artikeldetail_maxbestand;
	public static String Artikeldetail_minbestand;
	public static String Artikeldetail_Pharmacode;
	public static String Artikeldetail_stueckProAbgabe;
	public static String Artikeldetail_typ;
	public static String Artikeldetail_Verkaufspreis;
	public static String Artikeldetail_verpackungseinheit;
	public static String ArtikelView_errorCaption;
	public static String ArtikelView_errorText;
	public static String ArtikelView_importAction;
	public static String ArtikelView_importCaption;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
	private Messages(){}
}
