package ch.rgw.tools;

@SuppressWarnings("serial")
public class JdbcLinkSyntaxException extends JdbcLinkException {
	
	public JdbcLinkSyntaxException(Exception cause){
		super(cause);
	}
	
	public JdbcLinkSyntaxException(String message, Exception cause){
		super(message, cause);
	}
}
