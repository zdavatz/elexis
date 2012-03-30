package ch.rgw.crypt;

public class BadParameterException extends CryptologistException {
	
	private static final long serialVersionUID = -5502719232422683351L;
	
	public BadParameterException(String message, int code){
		super(message, code);
	}
}
