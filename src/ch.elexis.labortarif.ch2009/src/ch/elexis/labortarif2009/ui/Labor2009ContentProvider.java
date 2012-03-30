package ch.elexis.labortarif2009.ui;

import java.util.HashMap;

import org.eclipse.jface.viewers.Viewer;

import ch.elexis.data.Query;
import ch.elexis.labortarif2009.data.Labor2009Tarif;
import ch.elexis.util.viewers.ViewerConfigurer.ICommonViewerContentProvider;

public class Labor2009ContentProvider implements ICommonViewerContentProvider {
	
	public Object[] getElements(Object inputElement){
		Query<Labor2009Tarif> qbe = new Query<Labor2009Tarif>(Labor2009Tarif.class);
		return qbe.execute().toArray();
	}
	
	public void dispose(){
		// TODO Auto-generated method stub
		
	}
	
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput){
		// TODO Auto-generated method stub
		
	}
	
	public void changed(HashMap<String, String> values){
		// TODO Auto-generated method stub
		
	}
	
	public void reorder(String field){
		// TODO Auto-generated method stub
		
	}
	
	public void selected(){
		// TODO Auto-generated method stub
		
	}
	
	public void init(){
		// TODO Auto-generated method stub
		
	}
	
	public void startListening(){
		// TODO Auto-generated method stub
		
	}
	
	public void stopListening(){
		// TODO Auto-generated method stub
		
	}
}
