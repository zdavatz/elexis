package ch.elexis.preferences;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.data.LabItem;
import ch.elexis.data.Query;

public class LaborPrefs2 extends PreferencePage implements IWorkbenchPreferencePage {
	private final HashMap<String, List<LabItem>> groups = new HashMap<String, List<LabItem>>();
	
	public LaborPrefs2(){
		super(Messages.LaborPrefs2_LabItemsAndGroups);
	}
	
	@Override
	protected Control createContents(Composite parent){
		
		for (LabItem item : new Query<LabItem>(LabItem.class).execute()) {
			String groupname = item.getGroup();
			List<LabItem> group = groups.get(groupname);
			if (group == null) {
				group = new LinkedList<LabItem>();
			}
			group.add(item);
		}
		return null;
	}
	
	public void init(IWorkbench workbench){
	// TODO Auto-generated method stub
	
	}
	
}
