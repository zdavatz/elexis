package ch.elexis.labortarif2009.ui;

import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;

import ch.elexis.Desk;
import ch.elexis.actions.ElexisEvent;
import ch.elexis.actions.ElexisEventDispatcher;
import ch.elexis.actions.ElexisEventListenerImpl;
import ch.elexis.data.Konsultation;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.elexis.util.viewers.CommonViewer;
import ch.elexis.util.viewers.ViewerConfigurer.ControlFieldListener;
import ch.elexis.util.viewers.ViewerConfigurer.ControlFieldProvider;
import ch.rgw.tools.IFilter;
import ch.rgw.tools.TimeTool;

public class Labor2009ControlFieldProvider implements ControlFieldProvider {
	
	private CommonViewer commonViewer;
	private StructuredViewer viewer;
	
	private Text txtFilter;
	
	private Labor2009CodeTextValidFilter filter;
	private FilterKonsultationListener konsFilter;
	
	public Labor2009ControlFieldProvider(final CommonViewer viewer){
		commonViewer = viewer;
		konsFilter = new FilterKonsultationListener(Konsultation.class);
	}

	public Composite createControl(Composite parent){
		Composite ret = new Composite(parent, SWT.NONE);
		ret.setLayout(new FormLayout());
		
		Label lblFilter = new Label(ret, SWT.NONE);
		lblFilter.setText("Filter: ");
		
		txtFilter = new Text(ret, SWT.BORDER | SWT.SEARCH);
		txtFilter.setText(""); //$NON-NLS-1$
		
		ToolBarManager tbManager = new ToolBarManager(SWT.FLAT | SWT.HORIZONTAL | SWT.WRAP);
		tbManager.add(new ControlContribution("") {
			
			@Override
			protected Control createControl(Composite parent){
				final Button button = new Button(parent, SWT.TOGGLE);
				button.setImage(Desk.getImage(Desk.IMG_FILTER));
				button.setToolTipText("Nach Datum der aktuellen Konsultation filtern");
				
				button.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e){
						if (button.getSelection()) {
							Konsultation selectedKons =
								(Konsultation) ElexisEventDispatcher
									.getSelected(Konsultation.class);
							// apply the filter
							if (selectedKons != null) {
								filter.setValidDate(new TimeTool(selectedKons.getDatum()));
								viewer.getControl().setRedraw(false);
								viewer.refresh();
								viewer.getControl().setRedraw(true);
							}
							ElexisEventDispatcher.getInstance().addListeners(konsFilter);
						} else {
							ElexisEventDispatcher.getInstance().removeListeners(konsFilter);
							filter.setValidDate(null);
							viewer.getControl().setRedraw(false);
							viewer.refresh();
							viewer.getControl().setRedraw(true);
						}
					}
				});

				return button;
			}
		});
		ToolBar toolbar = tbManager.createControl(ret);
		
		FormData fd = new FormData();
		fd.top = new FormAttachment(0, 5);
		fd.left = new FormAttachment(0, 5);
		lblFilter.setLayoutData(fd);
		
		fd = new FormData();
		fd.top = new FormAttachment(0, 5);
		fd.right = new FormAttachment(100, -5);
		toolbar.setLayoutData(fd);
		
		fd = new FormData();
		fd.top = new FormAttachment(0, 5);
		fd.left = new FormAttachment(lblFilter, 5);
		fd.right = new FormAttachment(toolbar, -5);
		txtFilter.setLayoutData(fd);
		
		return ret;
	}
	
	public void addChangeListener(ControlFieldListener cl){
		// TODO Auto-generated method stub
		
	}
	
	public void removeChangeListener(ControlFieldListener cl){
		// TODO Auto-generated method stub
		
	}
	
	public String[] getValues(){
		// TODO Auto-generated method stub
		return null;
	}
	
	public void clearValues(){
		// TODO Auto-generated method stub
		
	}
	
	public boolean isEmpty(){
		// TODO Auto-generated method stub
		return false;
	}
	
	public void setQuery(Query<? extends PersistentObject> q){
		// TODO Auto-generated method stub
		
	}
	
	public IFilter createFilter(){
		// TODO Auto-generated method stub
		return null;
	}
	
	public void fireChangedEvent(){
		// TODO Auto-generated method stub
		
	}
	
	public void fireSortEvent(String text){
		// TODO Auto-generated method stub
		
	}
	
	public void setFocus(){
		// apply filter to viewer on focus as the creation in common viewer is done
		// first filter then viewer -> viewer not ready on createControl.
		if (viewer == null) {
			viewer = commonViewer.getViewerWidget();
// viewer.setComparator(new KassenLeistungComparator());
			filter = new Labor2009CodeTextValidFilter();
			viewer.addFilter(filter);
			txtFilter.addKeyListener(new FilterKeyListener(txtFilter, viewer));
		}
	}
	
	private class FilterKonsultationListener extends ElexisEventListenerImpl {
		
		public FilterKonsultationListener(Class<?> clazz){
			super(clazz);
		}
		
		@Override
		public void runInUi(ElexisEvent ev){
			Konsultation selectedKons =
				(Konsultation) ElexisEventDispatcher.getSelected(Konsultation.class);
			// apply the filter
			if (selectedKons != null) {
				filter.setValidDate(new TimeTool(selectedKons.getDatum()));
				viewer.getControl().setRedraw(false);
				viewer.refresh();
				viewer.getControl().setRedraw(true);
			}
		}
	}

	private class FilterKeyListener extends KeyAdapter {
		private Text text;
		private StructuredViewer viewer;
		
		FilterKeyListener(Text filterTxt, StructuredViewer viewer){
			text = filterTxt;
			this.viewer = viewer;
		}
		
		public void keyReleased(KeyEvent ke){
			String txt = text.getText();
			if (txt.length() > 1) {
				filter.setSearchText(txt);
				viewer.getControl().setRedraw(false);
				viewer.refresh();
				viewer.getControl().setRedraw(true);
			} else {
				filter.setSearchText(null);
				viewer.getControl().setRedraw(false);
				viewer.refresh();
				viewer.getControl().setRedraw(true);
			}
		}
	}
}
