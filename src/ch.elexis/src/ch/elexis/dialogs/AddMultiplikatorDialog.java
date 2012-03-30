package ch.elexis.dialogs;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ch.elexis.util.SWTHelper;
import ch.rgw.tools.TimeTool;

import com.tiff.common.ui.datepicker.DatePickerCombo;

public class AddMultiplikatorDialog extends TitleAreaDialog {
	DatePickerCombo dpc;
	Text multi;
	TimeTool begindate;
	String mult;

	public AddMultiplikatorDialog(final Shell shell) {
		super(shell);
	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		Composite ret = new Composite(parent, SWT.NONE);
		ret.setLayout(new GridLayout(2, false));
		ret.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		dpc = new DatePickerCombo(ret, SWT.BORDER);
		dpc.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		multi = new Text(ret, SWT.BORDER);
		multi.setTextLimit(4);
		multi.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));

		return ret;
	}

	@Override
	public void create() {
		super.create();
		setTitle(Messages.getString("MultiplikatorEditor.BegiNDate")); //$NON-NLS-1$
		setMessage(Messages
				.getString("MultiplikatorEditor.PleaseEnterBeginDate")); //$NON-NLS-1$
		getShell().setText(
				Messages.getString("MultiplikatorEditor.NewMultipilcator")); //$NON-NLS-1$
	}

	@Override
	protected void okPressed() {
		begindate = new TimeTool(dpc.getDate().getTime());
		mult = multi.getText();
		super.okPressed();
	}
	
	public TimeTool getBegindate() {
		return begindate;
	}
	
	public String getMult() {
		return mult;
	}
}
