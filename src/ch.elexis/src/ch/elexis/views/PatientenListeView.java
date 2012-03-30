/*******************************************************************************
 * Copyright (c) 2005-2011, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    M. Descher - Declarative access to the contextMenu
 *******************************************************************************/

package ch.elexis.views;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.window.SameShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISaveablePart2;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PropertyDialogAction;
import org.eclipse.ui.part.ViewPart;

import ch.elexis.Desk;
import ch.elexis.Hub;
import ch.elexis.actions.ElexisEvent;
import ch.elexis.actions.ElexisEventDispatcher;
import ch.elexis.actions.ElexisEventListenerImpl;
import ch.elexis.actions.GlobalActions;
import ch.elexis.actions.GlobalEventDispatcher;
import ch.elexis.actions.GlobalEventDispatcher.IActivationListener;
import ch.elexis.actions.Heartbeat.HeartListener;
import ch.elexis.admin.AccessControlDefaults;
import ch.elexis.core.data.ISticker;
import ch.elexis.data.Anwender;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Person;
import ch.elexis.data.Query;
import ch.elexis.data.Reminder;
import ch.elexis.data.Sticker;
import ch.elexis.dialogs.PatientErfassenDialog;
import ch.elexis.preferences.PreferenceConstants;
import ch.elexis.util.SWTHelper;
import ch.elexis.util.ViewMenus;
import ch.elexis.util.viewers.CommonViewer;
import ch.elexis.util.viewers.DefaultControlFieldProvider;
import ch.elexis.util.viewers.DefaultLabelProvider;
import ch.elexis.util.viewers.SimpleWidgetProvider;
import ch.elexis.util.viewers.ViewerConfigurer;
import ch.elexis.util.viewers.ViewerConfigurer.ControlFieldListener;

/**
 * Display of Patients
 * 
 * @author gerry
 * 
 */
public class PatientenListeView extends ViewPart implements IActivationListener, ISaveablePart2,
		HeartListener {
	public static final String ID = "ch.elexis.PatListView"; //$NON-NLS-1$
	private CommonViewer cv;
	private ViewerConfigurer vc;
	private ViewMenus menus;
	private IAction filterAction, newPatAction;
	private Patient actPatient;
	private boolean initiated = false;
	PatListFilterBox plfb;
	PatListeContentProvider plcp;
	Composite parent;
	
	ElexisEventListenerImpl eeli_user =
		new ElexisEventListenerImpl(Anwender.class, ElexisEvent.EVENT_USER_CHANGED) {
			
			public void runInUi(ElexisEvent ev){
				UserChanged();
			}
		};
	
	@Override
	public void dispose(){
		plcp.stopListening();
		GlobalEventDispatcher.removeActivationListener(this, this);
		ElexisEventDispatcher.getInstance().removeListeners(eeli_user);
		super.dispose();
	}
	
	/**
	 * retrieve the patient that is currently selected in the list
	 * 
	 * @return the selected patient or null if none was selected
	 */
	public Patient getSelectedPatient(){
		Object[] sel = cv.getSelection();
		if (sel != null) {
			return (Patient) sel[0];
		}
		return null;
	}
	
	/**
	 * Refresh the contents of the list.
	 */
	public void reload(){
		cv.notify(CommonViewer.Message.update);
	}
	
	@Override
	public void createPartControl(final Composite parent){
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		
		this.parent = parent;
		this.parent.setLayout(layout);
		
		cv = new CommonViewer();
		ArrayList<String> fields = new ArrayList<String>();
		initiated = !("".equals(Hub.userCfg.get(PreferenceConstants.USR_PATLIST_SHOWPATNR, "")));
		if (Hub.userCfg.get(PreferenceConstants.USR_PATLIST_SHOWPATNR, false)) {
			fields.add(Patient.FLD_PATID + Query.EQUALS
				+ Messages.getString("PatientenListeView.PatientNr")); //$NON-NLS-1$
		}
		if (Hub.userCfg.get(PreferenceConstants.USR_PATLIST_SHOWNAME, true)) {
			fields.add(Patient.FLD_NAME + Query.EQUALS
				+ Messages.getString("PatientenListeView.PatientName")); //$NON-NLS-1$
		}
		if (Hub.userCfg.get(PreferenceConstants.USR_PATLIST_SHOWFIRSTNAME, true)) {
			fields.add(Patient.FLD_FIRSTNAME + Query.EQUALS
				+ Messages.getString("PatientenListeView.PantientFirstName")); //$NON-NLS-1$
		}
		if (Hub.userCfg.get(PreferenceConstants.USR_PATLIST_SHOWDOB, true)) {
			fields.add(Patient.BIRTHDATE + Query.EQUALS
				+ Messages.getString("PatientenListeView.PatientBirthdate")); //$NON-NLS-1$
		}
		plcp = new PatListeContentProvider(cv, fields.toArray(new String[0]), this);
		makeActions();
		plfb = new PatListFilterBox(parent);
		plfb.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		((GridData) plfb.getLayoutData()).heightHint = 0;
		
		vc =
			new ViewerConfigurer(
			// new LazyContentProvider(cv,loader,
				// AccessControlDefaults.PATIENT_DISPLAY),
				plcp, new PatLabelProvider(), new DefaultControlFieldProvider(cv, fields
					.toArray(new String[0])), new ViewerConfigurer.DefaultButtonProvider(), // cv,Patient.class),
				new SimpleWidgetProvider(SimpleWidgetProvider.TYPE_LAZYLIST, SWT.SINGLE, cv));
		cv.create(vc, parent, SWT.NONE, getViewSite());
		// let user select patient by pressing ENTER in the control fields
		cv.getConfigurer().getControlFieldProvider().addChangeListener(
			new ControlFieldSelectionListener());
		cv.getViewerWidget().getControl()
			.setFont(Desk.getFont(PreferenceConstants.USR_DEFAULTFONT));
		
		menus = new ViewMenus(getViewSite());
		menus.createToolbar(newPatAction, filterAction);
		menus.createControlContextMenu(cv.getViewerWidget().getControl(), new PatientMenuPopulator(
			this));
		plcp.startListening();
		ElexisEventDispatcher.getInstance().addListeners(eeli_user);
		GlobalEventDispatcher.addActivationListener(this, this);
		
		StructuredViewer viewer = cv.getViewerWidget();
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			
			@Override
			public void doubleClick(DoubleClickEvent event){
				PropertyDialogAction pdAction = new PropertyDialogAction(new SameShellProvider(parent),
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart().getSite().getSelectionProvider());

				if (pdAction.isApplicableForSelection())
				    pdAction.run();
			}
		});
		getSite().registerContextMenu(menus.getContextMenu(), viewer);
		getSite().setSelectionProvider(viewer);
	}
	
	public PatListeContentProvider getContentProvider(){
		return plcp;
	}
	
	@Override
	public void setFocus(){
		vc.getControlFieldProvider().setFocus();
	}
	
	class PatLabelProvider extends DefaultLabelProvider implements ITableColorProvider {
		
		@Override
		public Image getColumnImage(final Object element, final int columnIndex){
			if (element instanceof Patient) {
				Patient pat = (Patient) element;
				
				if (Reminder.findRemindersDueFor(pat, Hub.actUser, false).size() > 0) {
					return Desk.getImage(Desk.IMG_AUSRUFEZ);
				}
				ISticker et = pat.getSticker();
				Image im = null;
				if (et != null && (im = ((Sticker)et).getImage()) != null) {
					return im;
				} else {
					if (pat.getGeschlecht().equals(Person.MALE)) {
						return Desk.getImage(Desk.IMG_MANN);
					} else {
						return Desk.getImage(Desk.IMG_FRAU);
					}
				}
			} else {
				return super.getColumnImage(element, columnIndex);
			}
		}
		
		public Color getBackground(final Object element, final int columnIndex){
			if (element instanceof Patient) {
				Patient pat = (Patient) element;
				ISticker et = pat.getSticker();
				if (et != null) {
					return ((Sticker)et).getBackground();
				}
			}
			return null;
		}
		
		public Color getForeground(final Object element, final int columnIndex){
			if (element instanceof Patient) {
				Patient pat = (Patient) element;
				ISticker et = pat.getSticker();
				if (et != null) {
					return ((Sticker)et).getForeground();
				}
			}
			
			return null;
		}
		
	}
	
	public void reset(){
		vc.getControlFieldProvider().clearValues();
	}
	
	private void makeActions(){
		
		filterAction =
			new Action(Messages.getString("PatientenListeView.FilteList"), Action.AS_CHECK_BOX) { //$NON-NLS-1$
				{
					setImageDescriptor(Desk.getImageDescriptor(Desk.IMG_FILTER));
					setToolTipText(Messages.getString("PatientenListeView.FilterList")); //$NON-NLS-1$
				}
				
				@Override
				public void run(){
					GridData gd = (GridData) plfb.getLayoutData();
					if (filterAction.isChecked()) {
						gd.heightHint = 80;
						plfb.reset();
						plcp.setFilter(plfb);
						
					} else {
						gd.heightHint = 0;
						plcp.removeFilter(plfb);
					}
					parent.layout(true);
					
				}
				
			};
		
		newPatAction = new Action(Messages.getString("PatientenListeView.NewPatientAction")) { //$NON-NLS-1$
				{
					setImageDescriptor(Desk.getImageDescriptor(Desk.IMG_NEW));
					setToolTipText(Messages.getString("PatientenListeView.NewPationtToolTip")); //$NON-NLS-1$
				}
				
				@Override
				public void run(){
					// access rights guard
					if (!Hub.acl.request(AccessControlDefaults.PATIENT_INSERT)) {
						SWTHelper
							.alert(
								Messages.getString("PatientenListeView.MissingRights"), Messages.getString("PatientenListeView.YouMayNotCreatePatient")); //$NON-NLS-1$ //$NON-NLS-2$
						return;
					}
					HashMap<String, String> ctlFields = new HashMap<String, String>();
					String[] fx = vc.getControlFieldProvider().getValues();
					int i = 0;
					if (Hub.userCfg.get(PreferenceConstants.USR_PATLIST_SHOWPATNR, false)) {
						if (i < fx.length) {
							ctlFields.put(Patient.FLD_PATID, fx[i++]);
						}
					}
					if (Hub.userCfg.get(PreferenceConstants.USR_PATLIST_SHOWNAME, true)) {
						if (i < fx.length) {
							ctlFields.put(Patient.FLD_NAME, fx[i++]);
						}
					}
					if (Hub.userCfg.get(PreferenceConstants.USR_PATLIST_SHOWFIRSTNAME, true)) {
						if (i < fx.length) {
							ctlFields.put(Patient.FLD_FIRSTNAME, fx[i++]);
						}
					}
					if (Hub.userCfg.get(PreferenceConstants.USR_PATLIST_SHOWDOB, true)) {
						if (i < fx.length) {
							ctlFields.put(Patient.FLD_DOB, fx[i++]);
						}
					}
					PatientErfassenDialog ped =
						new PatientErfassenDialog(getViewSite().getShell(), ctlFields);
					if (ped.open() == Dialog.OK) {
						vc.getControlFieldProvider().clearValues();
						actPatient = ped.getResult();
						plcp.invalidate();
						cv.notify(CommonViewer.Message.update);
						cv.setSelection(actPatient, true);
					}
				}
			};
	}
	
	public void activation(final boolean mode){
		if (mode == true) {
			newPatAction.setEnabled(Hub.acl.request(AccessControlDefaults.PATIENT_INSERT));
			heartbeat();
			Hub.heart.addListener(this);
		} else {
			Hub.heart.removeListener(this);
			
		}
		
	}
	
	public void visible(final boolean mode){
	// TODO Auto-generated method stub
	
	}
	
	/*
	 * Die folgenden 6 Methoden implementieren das Interface ISaveablePart2 Wir benötigen das
	 * Interface nur, um das Schliessen einer View zu verhindern, wenn die Perspektive fixiert ist.
	 * Gibt es da keine einfachere Methode?
	 */
	public int promptToSaveOnClose(){
		return GlobalActions.fixLayoutAction.isChecked() ? ISaveablePart2.CANCEL
				: ISaveablePart2.NO;
	}
	
	public void doSave(final IProgressMonitor monitor){ /* leer */
	}
	
	public void doSaveAs(){ /* leer */
	}
	
	public boolean isDirty(){
		return GlobalActions.fixLayoutAction.isChecked();
	}
	
	public boolean isSaveAsAllowed(){
		return false;
	}
	
	public boolean isSaveOnCloseNeeded(){
		return true;
	}
	
	public void heartbeat(){
		cv.notify(CommonViewer.Message.update);
	}
	
	/**
	 * Select Patient when user presses ENTER in the control fields. If mor than one Patients are
	 * listed, the first one is selected. (This listener only implements selected().)
	 */
	class ControlFieldSelectionListener implements ControlFieldListener {
		public void changed(HashMap<String, String> values){
		// nothing to do (handled by LazyContentProvider)
		}
		
		public void reorder(final String field){
		// nothing to do (handled by LazyContentProvider)
		}
		
		/**
		 * ENTER has been pressed in the control fields, select the first listed patient
		 */
		// this is also implemented in KontakteView
		public void selected(){
			StructuredViewer viewer = cv.getViewerWidget();
			Object[] elements =
				cv.getConfigurer().getContentProvider().getElements(viewer.getInput());
			if ((elements != null) && (elements.length > 0)) {
				Object element = elements[0];
				/*
				 * just selecting the element in the viewer doesn't work if the control fields are
				 * not empty (i. e. the size of items changes): cv.setSelection(element, true); bug
				 * in TableViewer with style VIRTUAL? work-arount: just globally select the element
				 * without visual representation in the viewer
				 */
				if (element instanceof PersistentObject) {
					// globally select this object
					ElexisEventDispatcher.fireSelectionEvent((PersistentObject) element);
				}
			}
		}
	}
	
	public void UserChanged(){
		if (!initiated)
			SWTHelper.reloadViewPart(PatientenListeView.ID);
		if (!cv.getViewerWidget().getControl().isDisposed()) {
			cv.getViewerWidget().getControl().setFont(
				Desk.getFont(PreferenceConstants.USR_DEFAULTFONT));
			cv.notify(CommonViewer.Message.update);
		}
	}
	
}
