package ch.elexis.util;

public abstract class RunnableWithParamAndReturnValue implements Runnable {
	private Object param;
	protected Object retval;
	
	public RunnableWithParamAndReturnValue(Object param){
		this.param = param;
	}
	
	public Object getValue(){
		return retval;
	}
	
	public void run(){
		try {
			retval = doRun(param);
		} catch (Exception ex) {
			retval = ex;
		}
	}
	
	public abstract Object doRun(Object param) throws Exception;
}
