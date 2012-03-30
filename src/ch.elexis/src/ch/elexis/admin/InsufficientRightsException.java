package ch.elexis.admin;

public class InsufficientRightsException extends Exception {
	private static final long serialVersionUID = 7507842875772322981L;
	
	public InsufficientRightsException(String message){
		super(message);
	}
}
