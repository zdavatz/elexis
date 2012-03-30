package ch.elexis.eigenartikel;

import java.lang.reflect.Method;

import ch.elexis.data.PersistentObject;
import ch.elexis.data.PersistentObjectFactory;

public class EigenartikelPersistentObjectFactory extends PersistentObjectFactory {
	
	@Override
	public PersistentObject createFromString(String code){
		try {
			String[] ci = code.split("::"); //$NON-NLS-1$
			Class<?> clazz = null;
			if(ci[0].equals("ch.elexis.data.Eigenartikel")) {
				clazz = Class.forName("ch.elexis.eigenartikel.Eigenartikel");
			} else {
				clazz = Class.forName(ci[0]);
			}
			Method load = clazz.getMethod("load", new Class[] { String.class}); //$NON-NLS-1$
			return (PersistentObject) (load.invoke(null, new Object[] {
				ci[1]
			}));
		} catch (Exception ex) {
			return null;
		}
	}

	@Override
	public PersistentObject doCreateTemplate(Class<? extends PersistentObject> typ){
		try {
			return (PersistentObject) typ.newInstance();
		} catch (Exception ex) {
			return null;
		}
	}
	
	@Override
	public Class getClassforName(String fullyQualifiedClassName) {
		Class ret = null;
		try {
			if(fullyQualifiedClassName.equals("ch.elexis.data.Eigenartikel")) return Class.forName("ch.elexis.eigenartikel.Eigenartikel");
			ret = Class.forName(fullyQualifiedClassName);
			return ret;
		} catch ( ClassNotFoundException e ) {
			e.printStackTrace();
			return ret;
		}
	}
}
