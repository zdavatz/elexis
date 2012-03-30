package ch.elexis.commands;

import java.util.HashMap;

import org.eclipse.core.commands.AbstractParameterValueConverter;
import org.eclipse.core.commands.ParameterValueConversionException;

import ch.rgw.tools.StringTool;
import ch.rgw.tools.Tree;

public class TreeToStringConverter extends AbstractParameterValueConverter {
	static final HashMap<String, Tree<?>> map = new HashMap<String, Tree<?>>();
	
	@Override
	public Object convertToObject(String parameterValue) throws ParameterValueConversionException{
		Tree<?> ret = map.get(parameterValue);
		return ret;
	}
	
	@Override
	public String convertToString(Object parameterValue) throws ParameterValueConversionException{
		if (parameterValue instanceof Tree) {
			String ret = StringTool.unique(getClass().getName());
			map.put(ret, (Tree<?>) parameterValue);
			return ret;
		}
		throw new ParameterValueConversionException("Parameter was not instance of Tree");
	}
	
}
