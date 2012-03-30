package ch.elexis.eigenartikel;

import ch.elexis.actions.FlatDataLoader;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.elexis.util.viewers.CommonViewer;

public class EigenartikelLoader extends FlatDataLoader {

	public EigenartikelLoader(CommonViewer cv) {
		super(cv, new Query<Eigenartikel>(Eigenartikel.class));
		setOrderFields(Eigenartikel.FLD_NAME);
		addQueryFilter(new EigenartikelLabelFilter());
	}
	
	static class EigenartikelLabelFilter implements QueryFilter {

		@Override
		public void apply(Query<? extends PersistentObject> qbe){
			qbe.add(Eigenartikel.FLD_TYP, Query.EQUALS, Eigenartikel.TYPNAME);	
		}
	}
	
}
