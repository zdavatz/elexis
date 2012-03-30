package ch.elexis.eigenleistung;

import ch.elexis.actions.FlatDataLoader;
import ch.elexis.data.Eigenleistung;
import ch.elexis.data.Query;
import ch.elexis.util.viewers.CommonViewer;

public class EigenleistungLoader extends FlatDataLoader {
	
	public EigenleistungLoader(CommonViewer cv){
		super(cv, new Query<Eigenleistung>(Eigenleistung.class));
		setOrderFields(Eigenleistung.CODE);
	}
	
}
