package ch.elexis.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ch.elexis.util.SWTHelper;

public class BrowserInputDialog extends Dialog {
	String url;
	String title;
	String text;
	Text input;
	String result;
	
	public BrowserInputDialog(Shell shell, String url, String title, String text){
		super(shell);
		this.url = url;
		this.title = title;
		this.text = text;
	}
	
	@Override
	protected Control createDialogArea(Composite parent){
		Composite ret = (Composite) super.createDialogArea(parent);
		Browser browser = new Browser(ret, SWT.NONE);
		browser.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		browser.setUrl(url);
		Label lbtext = new Label(ret, SWT.NONE);
		lbtext.setText(text);
		input = new Text(ret, SWT.BORDER);
		input.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		return ret;
	}
	
	@Override
	protected void okPressed(){
		result = input.getText();
		super.okPressed();
	}
	
	@Override
	public void create(){
		super.create();
		getShell().setText(title);
	}
	
	public String getValue(){
		return result;
	}
	
}
