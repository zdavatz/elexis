package ch.elexis.preferences.inputs;

import org.eclipse.swt.layout.GridLayout;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;

public class FileSelectorField extends Composite {
	String result;
	
	public FileSelectorField(String title, Composite parent, int flags){
		super(parent, flags);
		setLayout(new GridLayout(3, false));
		
	}
	
}
