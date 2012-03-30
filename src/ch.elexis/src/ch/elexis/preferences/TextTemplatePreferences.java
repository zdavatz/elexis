package ch.elexis.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.Hub;
import ch.elexis.util.SWTHelper;

public class TextTemplatePreferences extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {
	
	public static final String SUFFIX_FOR_THIS_STATION =
		Messages.TextTemplatePreferences_suffixForStation;
	public static final String BRANCH = "document_templates/"; //$NON-NLS-1$
	public static final String SUFFIX_STATION = BRANCH + "suffix_station"; //$NON-NLS-1$
	
	public TextTemplatePreferences(){
		super(GRID);
		setPreferenceStore(new SettingsPreferenceStore(Hub.localCfg));
	}
	
	@Override
	protected void createFieldEditors(){
		Label expl = new Label(getFieldEditorParent(), SWT.WRAP);
		expl.setText(Messages.TextTemplatePreferences_ExplanationLine1
			+ Messages.TextTemplatePreferences_ExplanationLine2
			+ Messages.TextTemplatePreferences_ExplanationLine3);
		expl.setLayoutData(SWTHelper.getFillGridData(2, true, 1, false));
		addField(new StringFieldEditor(SUFFIX_STATION, SUFFIX_FOR_THIS_STATION,
			getFieldEditorParent()));
		/*
		 * IExtensionRegistry exr = Platform.getExtensionRegistry(); IExtensionPoint exp =
		 * exr.getExtensionPoint("ch.elexis.documentTemplates"); if (exp != null) { IExtension[]
		 * extensions = exp.getExtensions(); for (IExtension ex : extensions) {
		 * IConfigurationElement[] elems = ex.getConfigurationElements(); for (IConfigurationElement
		 * el : elems) { String n=el.getAttribute("name"); addField(new StringFieldEditor(BRANCH+n,
		 * n, getFieldEditorParent())); } }
		 * 
		 * }
		 */
	}
	
	@Override
	protected void performApply(){
		Hub.localCfg.flush();
	}
	
	@Override
	public void init(IWorkbench workbench){
	// TODO Auto-generated method stub
	
	}
	
}
