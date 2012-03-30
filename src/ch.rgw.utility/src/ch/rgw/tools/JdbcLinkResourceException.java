package ch.rgw.tools;

@SuppressWarnings("serial")
public class JdbcLinkResourceException extends JdbcLinkException {
	
	public JdbcLinkResourceException(Exception cause){
		super(cause);
	}
	
	public JdbcLinkResourceException(String message, Exception cause){
		super(message, cause);
	}
	
}
