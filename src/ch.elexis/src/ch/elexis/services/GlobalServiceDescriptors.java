package ch.elexis.services;

/**
 * Descriptors of some standardized services.
 * 
 * @see ch.elexis.util.Extensions#findBestService(String)
 * @author Gerry Weirich
 * 
 */
public class GlobalServiceDescriptors {
	/** Scan Documents */
	public static final String SCANNING = "ScannerService";
	/** Scan Documetns directly do pdf */
	public static final String SCAN_TO_PDF = "ScanToPDFService";
	/** Document manager */
	public static final String DOCUMENT_MANAGEMENT = "DocumentManagement";
	/** IRangeHandlers */
	public static final String TEXT_CONTENTS_EXTENSION = "TextContentsExtension";
}
