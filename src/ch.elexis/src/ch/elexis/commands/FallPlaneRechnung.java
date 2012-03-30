package ch.elexis.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;

import ch.elexis.Desk;

public class FallPlaneRechnung extends AbstractHandler {
	
	public Object execute(ExecutionEvent arg0) throws ExecutionException{
		InputDialog dlg =
			new InputDialog(Desk.getTopShell(), Messages.FallPlaneRechnung_PlanBillingHeading,
				Messages.FallPlaneRechnung_PlanBillingAfterDays, "30", new IInputValidator() { //$NON-NLS-1$
				
					public String isValid(String newText){
						if (newText.matches("[0-9]*")) { //$NON-NLS-1$
							return null;
						}
						return Messages.FallPlaneRechnung_PlanBillingPleaseEnterPositiveInteger;
					}
				});
		if (dlg.open() == Dialog.OK) {
			return dlg.getValue();
		}
		return null;
	}
	
}
