package ch.elexis.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import ch.elexis.actions.ElexisEventDispatcher;
import ch.elexis.data.Leistungsblock;
import ch.elexis.dialogs.EigenLeistungDialog;

public class CreateEigenleistungUi extends AbstractHandler {
	public static final String COMMANDID = "ch.elexis.eigenleistung.create"; //$NON-NLS-1$
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		try {
			// create and open the dialog
			Shell parent = HandlerUtil.getActiveWorkbenchWindow(event).getShell();
			EigenLeistungDialog dialog = new EigenLeistungDialog(parent, null);
			// open dialog and add created IVerrechenbar to the selected Leistungsblock
			if (dialog.open() == Dialog.OK) {
				Leistungsblock lb =
					(Leistungsblock) ElexisEventDispatcher.getSelected(Leistungsblock.class);
				if (lb != null) {
					lb.addElement(dialog.getResult());
				}
			}
		} catch (Exception ex) {
			throw new RuntimeException(COMMANDID, ex);
		}
		return null;
	}
}
