package ch.elexis.rs232;

import gnu.io.SerialPortEventListener;

public interface PortEventListener extends SerialPortEventListener {
	
	public static final String XON = "\013"; //$NON-NLS-1$
	public final static String XOFF = "\015"; //$NON-NLS-1$
	public final static String STX = "\002"; //$NON-NLS-1$
	public final static String ETX = "\003"; //$NON-NLS-1$
	public static final String NAK = "\025"; //$NON-NLS-1$
	public static final String CR = "\015"; //$NON-NLS-1$
	public static final String LF = "\012"; //$NON-NLS-1$
}
