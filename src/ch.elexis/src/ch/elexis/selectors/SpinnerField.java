package ch.elexis.selectors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;

import ch.elexis.Desk;

public class SpinnerField extends ActiveControl {
	
	public SpinnerField(Composite parent, int displayBits, String displayName, int min, int max){
		super(parent, displayBits, displayName);
		final Spinner spinner = new Spinner(this, SWT.NONE);
		spinner.setMaximum(max);
		spinner.setMinimum(min);
		spinner.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e){
				int v = spinner.getSelection();
				textContents = Integer.toString(v);
				fireChangedEvent();
			}
			
		});
		setControl(spinner);
		
	}
	
	@Override
	protected void push(){
		Desk.syncExec(new Runnable() {
			public void run(){
				Spinner spinner = (Spinner) ctl;
				spinner.setSelection(Integer.parseInt(textContents));
			}
		});
	}
	
}
