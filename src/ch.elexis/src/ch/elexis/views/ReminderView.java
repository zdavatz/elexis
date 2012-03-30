/*******************************************************************************
 * Copyright (c) 2006-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 * $Id: ReminderView.java 5970 2010-01-27 16:43:04Z rgw_ch $
 *******************************************************************************/
package ch.elexis.views;

import java.util.List;
import java.util.SortedSet;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import ch.elexis.Desk;
import ch.elexis.Hub;
import ch.elexis.actions.ElexisEvent;
import ch.elexis.actions.ElexisEventDispatcher;
import ch.elexis.actions.ElexisEventListenerImpl;
import ch.elexis.actions.GlobalEventDispatcher;
import ch.elexis.actions.RestrictedAction;
import ch.elexis.actions.GlobalEventDispatcher.IActivationListener;
import ch.elexis.actions.Heartbeat.HeartListener;
import ch.elexis.admin.AccessControlDefaults;
import ch.elexis.data.Anwender;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.elexis.data.Reminder;
import ch.elexis.dialogs.EditReminderDialog;
import ch.elexis.preferences.PreferenceConstants;
import ch.elexis.util.SWTHelper;
import ch.elexis.util.ViewMenus;
import ch.elexis.util.viewers.CommonContentProviderAdapter;
import ch.elexis.util.viewers.CommonViewer;
import ch.elexis.util.viewers.DefaultLabelProvider;
import ch.elexis.util.viewers.SimpleWidgetProvider;
import ch.elexis.util.viewers.ViewerConfigurer;
import ch.rgw.io.Settings;
import ch.rgw.tools.TimeTool;

public class ReminderView extends ViewPart implements IActivationListener, HeartListener {
	public static final String ID = "ch.elexis.reminderview"; //$NON-NLS-1$
	private IAction newReminderAction, deleteReminderAction, onlyOpenReminderAction,
			ownReminderAction;
	private RestrictedAction othersReminderAction;
	private RestrictedAction selectPatientAction;
	private boolean bVisible;
	
	private ElexisEventListenerImpl eeli_pat = new ElexisEventListenerImpl(Patient.class) {
		
		public void runInUi(final ElexisEvent ev){
			if (((Patient) ev.getObject()).equals(actPatient)) {
				return;
			}
			actPatient = (Patient) ev.getObject();
			if (bVisible) {
				cv.notify(CommonViewer.Message.update);
			}
			Desk.asyncExec(new Runnable() {
				
				public void run(){
					List<Reminder> list =
						Reminder.findRemindersDueFor((Patient) ev.getObject(), Hub.actUser, true);
					if (list.size() != 0) {
						StringBuilder sb = new StringBuilder();
						for (Reminder r : list) {
							sb.append(r.getMessage()).append("\n\n"); //$NON-NLS-1$
						}
						SWTHelper.alert(Messages
							.getString("ReminderView.importantRemindersCaption"), sb.toString()); //$NON-NLS-1$
					}
				}
				
			});
			
		}
	};
	
	private ElexisEventListenerImpl eeli_user =
		new ElexisEventListenerImpl(Anwender.class, ElexisEvent.EVENT_USER_CHANGED) {
			
			public void runInUi(ElexisEvent ev){
				boolean bChecked = Hub.userCfg.get(PreferenceConstants.USR_REMINDERSOPEN, true);
				onlyOpenReminderAction.setChecked(bChecked);
				ownReminderAction.setChecked(Hub.userCfg.get(PreferenceConstants.USR_REMINDEROWN,
					false));
				
				// get state from user's configuration
				othersReminderAction.setChecked(Hub.userCfg.get(
					PreferenceConstants.USR_REMINDEROTHERS, false));
				
				// update action's access rights
				othersReminderAction.reflectRight();
				
				if (bVisible) {
					cv.notify(CommonViewer.Message.update);
				}
				
			}
		};
	
	private ElexisEventListenerImpl eeli_reminder =
		new ElexisEventListenerImpl(Reminder.class, ElexisEvent.EVENT_RELOAD
			| ElexisEvent.EVENT_CREATE | ElexisEvent.EVENT_UPDATE) {
			public void catchElexisEvent(ElexisEvent ev){
				cv.notify(CommonViewer.Message.update);
			}
		};
	CommonViewer cv;
	ViewerConfigurer vc;
	// Patient pat;
	// String dateDue;
	// Reminder.Status status;
	// Reminder.Typ typ;
	Query<Reminder> qbe;
	Settings cfg;
	ReminderFilter filter;
	private Patient actPatient;
	
	public ReminderView(){
		qbe = new Query<Reminder>(Reminder.class);
		
	}
	
	@Override
	public void createPartControl(final Composite parent){
		cv = new CommonViewer();
		filter = new ReminderFilter();
		vc = new ViewerConfigurer(new CommonContentProviderAdapter() {
			@Override
			public Object[] getElements(final Object inputElement){
				// Display reminders only if one is logged in
				if (Hub.actUser == null) {
					return new Object[0];
				}
				SortedSet<Reminder> allReminders = Hub.actUser.getReminders(null);
				if (othersReminderAction.isChecked()
					&& Hub.acl.request(AccessControlDefaults.ADMIN_VIEW_ALL_REMINDERS)) {
					qbe.clear();
					allReminders.addAll(qbe.execute());
				} else {
					if (ownReminderAction.isChecked()) {
						qbe.clear();
						qbe.add(Reminder.CREATOR, Query.EQUALS, Hub.actUser.getId());
						allReminders.addAll(qbe.execute());
					}
					// compatibility to old reminders where responsible
					// was given instead of n:m
					qbe.clear();
					qbe.add(Reminder.RESPONSIBLE, Query.EQUALS, Hub.actUser.getId());
					allReminders.addAll(qbe.execute());
					// ..to be removed later
				}
				return allReminders.toArray();
				
			}
		}, new ReminderLabelProvider(), null, // new DefaultControlFieldProvider(cv,new
			// String[]{"Fällig"}),
			new ViewerConfigurer.DefaultButtonProvider(), new SimpleWidgetProvider(
				SimpleWidgetProvider.TYPE_TABLE, SWT.SINGLE, cv));
		makeActions();
		ViewMenus menu = new ViewMenus(getViewSite());
		menu.createToolbar(newReminderAction);
		menu.createMenu(newReminderAction, deleteReminderAction, onlyOpenReminderAction,
			ownReminderAction, othersReminderAction, selectPatientAction);
		
		if (Hub.acl.request(AccessControlDefaults.ADMIN_VIEW_ALL_REMINDERS)) {
			othersReminderAction.setEnabled(true);
			othersReminderAction.setChecked(Hub.userCfg.get(PreferenceConstants.USR_REMINDEROTHERS,
				false));
		} else {
			othersReminderAction.setEnabled(false);
		}
		cv.create(vc, parent, SWT.NONE, getViewSite());
		cv.addDoubleClickListener(new CommonViewer.DoubleClickListener() {
			public void doubleClicked(final PersistentObject obj, final CommonViewer cv){
				new EditReminderDialog(getViewSite().getShell(), (Reminder) obj).open();
				cv.notify(CommonViewer.Message.update);
			}
		});
		menu.createViewerContextMenu(cv.getViewerWidget(), selectPatientAction,
			deleteReminderAction);
		cv.getViewerWidget().addFilter(filter);
		GlobalEventDispatcher.addActivationListener(this, getViewSite().getPart());
		
	}
	
	@Override
	public void setFocus(){
	// TODO Auto-generated method stub
	
	}
	
	@Override
	public void dispose(){
		GlobalEventDispatcher.removeActivationListener(this, getViewSite().getPart());
		Hub.userCfg.set(PreferenceConstants.USR_REMINDERSOPEN, onlyOpenReminderAction.isChecked());
	}
	
	class ReminderLabelProvider extends DefaultLabelProvider implements IColorProvider {
		
		public Color getBackground(final Object element){
			if (element instanceof Reminder) {
				Reminder.Status stat = ((Reminder) element).getStatus();
				cfg = Hub.userCfg.getBranch(PreferenceConstants.USR_REMINDERCOLORS, true);
				if (stat == Reminder.Status.STATE_DUE) {
					return Desk.getColorFromRGB(cfg.get("fällig", "FFFFFF")); //$NON-NLS-1$ //$NON-NLS-2$
				} else if (stat == Reminder.Status.STATE_OVERDUE) {
					return Desk.getColorFromRGB(cfg.get("überfällig", "FF0000")); //$NON-NLS-1$ //$NON-NLS-2$
				} else if (stat == Reminder.Status.STATE_PLANNED) {
					return Desk.getColorFromRGB(cfg.get("geplant", "00FF00")); //$NON-NLS-1$ //$NON-NLS-2$
				} else {
					return null;
				}
			}
			return null;
		}
		
		public Color getForeground(final Object element){
			return null;
		}
		
	}
	
	private void makeActions(){
		newReminderAction = new Action(Messages.getString("ReminderView.newReminderAction")) { //$NON-NLS-1$
				{
					setImageDescriptor(Desk.getImageDescriptor(Desk.IMG_NEW));
					setToolTipText(Messages.getString("ReminderView.newReminderToolTip")); //$NON-NLS-1$
				}
				
				@Override
				public void run(){
					EditReminderDialog erd = new EditReminderDialog(getViewSite().getShell(), null);
					erd.open();
					cv.notify(CommonViewer.Message.update_keeplabels);
				}
			};
		deleteReminderAction = new Action(Messages.getString("ReminderView.deleteAction")) { //$NON-NLS-1$
				{
					setImageDescriptor(Desk.getImageDescriptor(Desk.IMG_DELETE));
					setToolTipText(Messages.getString("ReminderView.deleteToolTip")); //$NON-NLS-1$
				}
				
				@Override
				public void run(){
					Object[] sel = cv.getSelection();
					if ((sel != null) && (sel.length > 0)) {
						Reminder r = (Reminder) sel[0];
						r.delete();
						cv.notify(CommonViewer.Message.update_keeplabels);
					}
				}
			};
		onlyOpenReminderAction =
			new Action(Messages.getString("ReminderView.onlyDueAction"), Action.AS_CHECK_BOX) { //$NON-NLS-1$
				{
					setToolTipText(Messages.getString("ReminderView.onlyDueToolTip")); //$NON-NLS-1$
				}
				
				@Override
				public void run(){
					boolean bChecked = onlyOpenReminderAction.isChecked();
					Hub.userCfg.set(PreferenceConstants.USR_REMINDERSOPEN, bChecked);
					cv.notify(CommonViewer.Message.update_keeplabels);
				}
			};
		ownReminderAction =
			new Action(Messages.getString("ReminderView.myRemindersAction"), Action.AS_CHECK_BOX) { //$NON-NLS-1$
				{
					setToolTipText(Messages.getString("ReminderView.myRemindersToolTip")); //$NON-NLS-1$
				}
				
				@Override
				public void run(){
					boolean bChecked = ownReminderAction.isChecked();
					Hub.userCfg.set(PreferenceConstants.USR_REMINDEROWN, bChecked);
					cv.notify(CommonViewer.Message.update_keeplabels);
				}
			};
		othersReminderAction =
			new RestrictedAction(AccessControlDefaults.ADMIN_VIEW_ALL_REMINDERS, Messages
				.getString("ReminderView.foreignAction"), //$NON-NLS-1$
				Action.AS_CHECK_BOX) {
				{
					setToolTipText(Messages.getString("ReminderView.foreignTooltip")); //$NON-NLS-1$
				}
				
				@Override
				public void doRun(){
					Hub.userCfg.set(PreferenceConstants.USR_REMINDEROTHERS, othersReminderAction
						.isChecked());
					cv.notify(CommonViewer.Message.update_keeplabels);
				}
			};
		
		selectPatientAction =
			new RestrictedAction(AccessControlDefaults.PATIENT_DISPLAY, Messages
				.getString("ReminderView.activatePatientAction"), //$NON-NLS-1$
				Action.AS_UNSPECIFIED) {
				{
					setImageDescriptor(Desk.getImageDescriptor(Desk.IMG_PERSON));
					setToolTipText(Messages.getString("ReminderView.activatePatientTooltip")); //$NON-NLS-1$
				}
				
				public void doRun(){
					Object[] sel = cv.getSelection();
					if (sel != null && sel.length > 0) {
						Reminder reminder = (Reminder) sel[0];
						Patient patient = reminder.getKontakt();
						if (patient != null) {
							ElexisEventDispatcher.fireSelectionEvent(patient);
						}
					}
				}
			};
		
	}
	
	public void activation(final boolean mode){
	/* egal */
	}
	
	public void visible(final boolean mode){
		bVisible = mode;
		if (mode) {
			ElexisEventDispatcher.getInstance().addListeners(eeli_pat, eeli_user, eeli_reminder);
			Hub.heart.addListener(this);
			cv.notify(CommonViewer.Message.update);
			heartbeat();
		} else {
			ElexisEventDispatcher.getInstance().removeListeners(eeli_pat, eeli_user, eeli_reminder);
			Hub.heart.removeListener(this);
		}
		
	}
	
	public void heartbeat(){
		cv.notify(CommonViewer.Message.update_keeplabels);
	}
	
	class ReminderFilter extends ViewerFilter {
		@Override
		public boolean select(final Viewer viewer, final Object parentElement, final Object element){
			if (element instanceof Reminder) {
				Reminder check = (Reminder) element;
				if (onlyOpenReminderAction.isChecked()) {
					if (check.getDateDue().isAfter(new TimeTool())) {
						return false;
					}
					if (check.getStatus().ordinal() > 2) {
						return false;
					}
				}
				Patient act = ElexisEventDispatcher.getSelectedPatient();
				if (act != null) {
					if (!check.get("IdentID").equals(act.getId())) { //$NON-NLS-1$
						if (check.getTyp() != Reminder.Typ.anzeigeTodoAll) {
							return false;
						}
					}
				}
				
			}
			return true;
		}
		
	}
}
