package ch.rgw.tools;

@SuppressWarnings("serial")
public class JdbcLinkConcurrencyException extends JdbcLinkException {
	
	public JdbcLinkConcurrencyException(Exception cause){
		super(cause);
	}
	
	public JdbcLinkConcurrencyException(String message, Exception cause){
		super(message, cause);
	}
}
