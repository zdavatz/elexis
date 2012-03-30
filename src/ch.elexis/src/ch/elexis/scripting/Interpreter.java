package ch.elexis.scripting;

import ch.elexis.ElexisException;

public interface Interpreter {
	
	public void setValue(String name, Object value) throws ElexisException;
	
	public Object run(String script, boolean showErrors) throws ElexisException;
}
