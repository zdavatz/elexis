package ch.elexis.selectors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class BooleanField extends ActiveControl {
	
	public BooleanField(Composite parent, int displayBits, String displayName){
		super(parent, displayBits | ActiveControl.HIDE_LABEL, displayName);
		final Button b = new Button(this, SWT.CHECK);
		b.setText(displayName);
		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				textContents = b.getSelection() ? "1" : "0";
				fireChangedEvent();
			}
		});
		setControl(b);
	}
	
	@Override
	protected void push(){
		if (textContents.equals("true")) {
			((Button) ctl).setSelection(true);
		} else {
			((Button) ctl).setSelection(false);
		}
		
	}
	
}
