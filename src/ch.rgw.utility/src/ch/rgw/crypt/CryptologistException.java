package ch.rgw.crypt;

public class CryptologistException extends Exception {
	
	private static final long serialVersionUID = -7838728866113260425L;
	public static final int ERR_BAD_PARAMETER = 1;
	public static final int ERR_SHORT_BLOCK = 2;
	public static final int ERR_DECRYPTION_FAILURE = 3;
	public static final int ERR_BAD_PROTOCOL = 4;
	public static final int ERR_TIMEOUT = 5;
	public static final int ERR_INTERNAL = 6;
	public static final int ERR_USER_UNKNOWN = 7;
	public static final int ERR_CERTIFICATE_ENCODING = 8;
	public static final int ERR_ENCRYPTION_FAILURE = 9;
	
	private int code = 0;
	
	public CryptologistException(String message, int code){
		super(message);
		this.code = code;
	}
	
	public int getCode(){
		return code;
	}
}
