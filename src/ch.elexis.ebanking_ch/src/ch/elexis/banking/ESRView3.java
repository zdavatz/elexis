package ch.elexis.banking;

import java.text.DecimalFormat;

import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import ch.elexis.Desk;
import ch.elexis.Hub;
import ch.elexis.actions.FlatDataLoader;
import ch.elexis.actions.GlobalEventDispatcher.IActivationListener;
import ch.elexis.actions.PersistentObjectLoader.QueryFilter;
import ch.elexis.admin.AccessControlDefaults;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.elexis.data.Rechnung;
import ch.elexis.util.viewers.CommonViewer;
import ch.elexis.util.viewers.DefaultControlFieldProvider;
import ch.elexis.util.viewers.SimpleWidgetProvider;
import ch.elexis.util.viewers.ViewerConfigurer;
import ch.elexis.util.viewers.ViewerConfigurer.ICommonViewerContentProvider;
import ch.elexis.util.viewers.ViewerConfigurer.ControlFieldProvider;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;

public class ESRView3 extends ViewPart implements IActivationListener {
	CommonViewer cv;
	ViewerConfigurer vc;
	FlatDataLoader loader;
	
	private static final int DATUM_INDEX = 0;
	private static final int RN_NUMMER_INDEX = 1;
	private static final int BETRAG_INDEX = 2;
	private static final int EINGELESEN_INDEX = 3;
	private static final int VERRECHNET_INDEX = 4;
	private static final int GUTGESCHRIEBEN_INDEX = 5;
	private static final int PATIENT_INDEX = 6;
	private static final int BUCHUNG_INDEX = 7;
	private static final int DATEI_INDEX = 8;
	
	private static final String[] COLUMN_TEXTS = {
		"Datum", // DATUM_INDEX
		"Rn-Nummer", // RN_NUMMER_INDEX
		"Betrag", // BETRAG
		"Eingelesen", // EINGELESEN_INDEX
		"Verrechnet", // VERRECHNET_INDEX
		"Gutgeschrieben", // GUTGESCHRIEBEN_INDEX
		"Patient", // PATIENT_INDEX
		"Buchung", // BUCHUNG_INDEX
		"Datei", // DATEI_INDEX
	};
	private static final int[] COLUMN_WIDTHS = {
		60, // DATUM_INDEX
		50, // RN_NUMMER_INDEX
		50, // BETRAG
		80, // EINGELESEN_INDEX
		80, // VERRECHNET_INDEX
		80, // GUTGESCHRIEBEN_INDEX
		150, // PATIENT_INDEX
		80, // BUCHUNG_INDEX
		80, // DATEI_INDEX
	};
	
	public ESRView3(){

	}
	
	@Override
	public void createPartControl(Composite parent){
		parent.setLayout(new FillLayout());
		cv = new CommonViewer();
		loader = new FlatDataLoader(cv, new Query<ESRRecord>(ESRRecord.class));
		loader.addQueryFilter(new QueryFilter() {
			
			public void apply(Query<? extends PersistentObject> qbe){
				if (Hub.acl.request(AccessControlDefaults.ACCOUNTING_GLOBAL) == false) {
					if (Hub.actMandant != null) {
						qbe.startGroup();
						qbe.add("MandantID", "=", Hub.actMandant.getId()); //$NON-NLS-1$ //$NON-NLS-2$
						qbe.or();
						qbe.add("MandantID", "", null); //$NON-NLS-1$ //$NON-NLS-2$
						qbe.add("RejectCode", "<>", "0"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						qbe.endGroup();
						qbe.and();
					}
				}
				
			}
		});
		
		vc =
			new ViewerConfigurer(loader, new ESRLabelProvider(),
				(ControlFieldProvider) new DefaultControlFieldProvider(cv, new String[] {
					"Datum"
				}), new ViewerConfigurer.DefaultButtonProvider(), new SimpleWidgetProvider(
					SimpleWidgetProvider.TYPE_LAZYLIST, SWT.NONE, null));
		cv.create(vc, parent, SWT.NONE, getViewSite());
		
		/*
		 * makeActions(); cv.setObjectCreateAction(getViewSite(), createKontakt); menu = new
		 * ViewMenus(getViewSite()); menu.createViewerContextMenu(cv.getViewerWidget(), delKontakt,
		 * dupKontakt); menu.createMenu(GlobalActions.printKontaktEtikette);
		 * menu.createToolbar(GlobalActions.printKontaktEtikette);
		 * vc.getContentProvider().startListening();
		 * vc.getControlFieldProvider().addChangeListener(this); cv.addDoubleClickListener(new
		 * CommonViewer.DoubleClickListener() { public void doubleClicked(PersistentObject obj,
		 * CommonViewer cv){ try { KontaktDetailView kdv = (KontaktDetailView)
		 * getSite().getPage().showView(KontaktDetailView.ID); kdv.kb.catchElexisEvent(new
		 * ElexisEvent(obj, obj.getClass(), ElexisEvent.EVENT_SELECTED)); } catch (PartInitException
		 * e) { ExHandler.handle(e); }
		 * 
		 * } });
		 */
	}
	
	@Override
	public void setFocus(){
	// TODO Auto-generated method stub
	
	}
	
	public void activation(boolean mode){
	// TODO Auto-generated method stub
	
	}
	
	public void visible(boolean mode){
	// TODO Auto-generated method stub
	
	}
	
	class ESRLabelProvider extends LabelProvider implements ITableLabelProvider,
			ITableColorProvider {
		DecimalFormat df = new DecimalFormat("###0.00");
		
		public Image getColumnImage(Object element, int columnIndex){
			// TODO Auto-generated method stub
			return null;
		}
		
		public String getColumnText(Object element, int columnIndex){
			String text = "";
			
			if (element instanceof ESRRecord) {
				ESRRecord rec = (ESRRecord) element;
				
				if (rec.getTyp().equals(ESRRecord.MODE.Summenrecord)) {
					switch (columnIndex) {
					case DATUM_INDEX:
						text = rec.get("Datum");
						break;
					case RN_NUMMER_INDEX:
						text = "Summe";
						break;
					case BETRAG_INDEX:
						text = rec.getBetrag().getAmountAsString();
						break;
					case DATEI_INDEX:
						text = rec.getFile();
						break;
					}
				} else {
					switch (columnIndex) {
					case DATUM_INDEX:
						text = rec.get("Datum");
						break;
					case RN_NUMMER_INDEX:
						Rechnung rn = rec.getRechnung();
						if (rn != null) {
							text = rn.getNr();
						}
						break;
					case BETRAG_INDEX:
						text = rec.getBetrag().getAmountAsString();
						break;
					case EINGELESEN_INDEX:
						text = rec.getEinlesedatatum();
						break;
					case VERRECHNET_INDEX:
						text = rec.getVerarbeitungsdatum();
						break;
					case GUTGESCHRIEBEN_INDEX:
						text = rec.getValuta();
						break;
					case PATIENT_INDEX:
						text = rec.getPatient().getLabel();
						break;
					case BUCHUNG_INDEX:
						String dat = rec.getGebucht();
						if (StringTool.isNothing(dat)) {
							text = "Nicht verbucht!";
						} else {
							text = new TimeTool(dat).toString(TimeTool.DATE_GER);
						}
						break;
					case DATEI_INDEX:
						text = rec.getFile();
						break;
					}
				}
			}
			
			return text;
		}
		
		public Color getForeground(Object element, int columnIndex){
			return Desk.getColor(Desk.COL_BLACK);
		}
		
		public Color getBackground(Object element, int columnIndex){
			if (element instanceof ESRRecord) {
				ESRRecord rec = (ESRRecord) element;
				if (rec.getTyp().equals(ESRRecord.MODE.Summenrecord)) {
					return Desk.getColor(Desk.COL_GREEN);
				}
				String buch = rec.getGebucht();
				if (rec.getRejectCode().equals(ESRRecord.REJECT.OK)) {
					if (StringTool.isNothing(buch)) {
						return Desk.getColor(Desk.COL_GREY);
					}
					return Desk.getColor(Desk.COL_WHITE);
				}
				return Desk.getColor(Desk.COL_RED);
			}
			return Desk.getColor(Desk.COL_SKYBLUE);
		}
	}
}
