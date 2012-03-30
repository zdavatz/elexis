package ch.elexis.preferences.inputs;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class PasswordFieldEditor extends StringFieldEditor {
	
	Text textField;
	
	public PasswordFieldEditor(String name, String title, Composite parent){
		super(name, title, parent);
	}
	
	/**
	 * Returns this field editor's text control.
	 * <p>
	 * The control is created if it does not yet exist
	 * </p>
	 * 
	 * @param parent
	 *            the parent
	 * @return the text control
	 */
	public Text getTextControl(Composite parent){
		
		if (textField == null) {
			textField = new Text(parent, SWT.SINGLE | SWT.BORDER | SWT.PASSWORD);
			textField.setFont(parent.getFont());
			textField.addDisposeListener(new DisposeListener() {
				public void widgetDisposed(DisposeEvent event){
					textField = null;
				}
			});
			
		} else {
			checkParent(textField, parent);
		}
		return textField;
	}
	
}
