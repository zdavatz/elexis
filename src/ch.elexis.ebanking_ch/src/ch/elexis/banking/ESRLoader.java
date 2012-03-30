package ch.elexis.banking;

import ch.elexis.actions.FlatDataLoader;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.elexis.util.viewers.CommonViewer;

public class ESRLoader extends FlatDataLoader {
	
	public ESRLoader(CommonViewer cv, Query<? extends PersistentObject> qbe){
		super(cv, qbe);
		
	}
	
}
