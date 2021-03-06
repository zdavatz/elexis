package ch.elexis.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import ch.elexis.Hub;
import ch.elexis.data.Brief;
import ch.elexis.text.TextContainer;
import ch.elexis.text.ITextPlugin.ICallback;
import ch.elexis.util.SWTHelper;

/**
 * A Dialog to display data to print
 * 
 * @author Gerry Weirich
 * 
 */
public class GenericPrintDialog extends Dialog implements ICallback {
	String title;
	String subject;
	TextContainer text;
	Brief brief;
	
	public GenericPrintDialog(Shell shell, String title, String subject){
		super(shell);
		this.title = title;
		this.subject = subject;
	}
	
	@Override
	public void create(){
		super.create();
		getShell().setText(title);
		getShell().setSize(900, 700);
		SWTHelper.center(Hub.plugin.getWorkbench().getActiveWorkbenchWindow().getShell(),
			getShell());
	}
	
	@Override
	protected Control createDialogArea(Composite parent){
		Composite ret = (Composite) super.createDialogArea(parent);
		ret.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		ret.setLayout(new FillLayout());
		text = new TextContainer(getShell());
		text.getPlugin().createContainer(ret, this);
		text.getPlugin().showMenu(true);
		text.getPlugin().showToolbar(true);
		brief = text.createFromTemplateName(null, "Liste", Brief.UNKNOWN, Hub.actUser, subject); //$NON-NLS-1$ 
		text.getPlugin().insertText("[Titel]", subject, SWT.LEFT);
		return ret;
	}
	
	public void insertText(String pattern, String t){
		text.getPlugin().insertText(pattern, t, SWT.LEFT);
	}
	
	public void insertTable(String place, String[][] table, int[] cellWidths){
		text.getPlugin().insertTable(place, 0, table, cellWidths);
	}
	
	@Override
	public void save(){
		text.saveBrief(brief, Brief.UNKNOWN);
	}
	
	@Override
	public boolean saveAs(){
		// TODO Auto-generated method stub
		return false;
	}
}
