package ch.elexis.commands;

import org.eclipse.core.commands.AbstractParameterValueConverter;
import org.eclipse.core.commands.ParameterValueConversionException;

import ch.elexis.Hub;
import ch.elexis.data.PersistentObject;

public class PersistentObjectConverter extends AbstractParameterValueConverter {
	
	@Override
	public Object convertToObject(String parameterValue) throws ParameterValueConversionException{
		return Hub.poFactory.createFromString(parameterValue);
	}
	
	@Override
	public String convertToString(Object parameterValue) throws ParameterValueConversionException{
		return ((PersistentObject)parameterValue).storeToString();
	}
	
}
