package ch.rgw.tools;

/**
 * Exception thrown if an exception was thrown from jdbc framework.
 */
@SuppressWarnings("serial")
public class JdbcLinkException extends RuntimeException {
	
	public JdbcLinkException(String string, Exception cause){
		super(string, cause);
	}
	
	public JdbcLinkException(String string){
		super(string);
	}
	
	public JdbcLinkException(Exception cause){
		super(cause);
	}
}
