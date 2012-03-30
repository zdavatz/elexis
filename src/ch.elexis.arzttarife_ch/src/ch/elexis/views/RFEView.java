package ch.elexis.views;

import java.util.HashMap;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;

import ch.elexis.Desk;
import ch.elexis.actions.ElexisEvent;
import ch.elexis.actions.ElexisEventDispatcher;
import ch.elexis.actions.ElexisEventListenerImpl;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Query;
import ch.elexis.data.RFE;
import ch.elexis.util.SWTHelper;

public class RFEView extends ViewPart {
	Table longTable, shortTable, mediumTable;
	CTabFolder tabs;
	Composite cCalc;
	boolean bDaempfung = false;
	HashMap<String, Integer> mapCodeToIndex = new HashMap<String, Integer>();
	HashMap<Integer, String> mapIndexToCode = new HashMap<Integer, String>();
	
	ElexisEventListenerImpl eeli_kons = new ElexisEventListenerImpl(Konsultation.class) {
		
		@Override
		public void runInUi(ElexisEvent ev){
			Konsultation k = (Konsultation) ev.getObject();
			adjustTable(k);
		}
		
	};
	
	private void adjustTable(Konsultation k){
		List<RFE> rfeForKOns = RFE.getRfeForKons(k.getId());
		CTabItem top = tabs.getSelection();
		if (top != null) {
			Control c = top.getControl();
			if (c instanceof Table) {
				Table table = (Table) c;
				table.deselectAll();
				for (TableItem it : table.getItems()) {
					// it.setBackground(null);
					// it.setForeground(null);
					it.setImage((Image) null);
				}
				for (RFE rfe : rfeForKOns) {
					int idx = mapCodeToIndex.get(rfe.getCode());
					TableItem item = table.getItem(idx);
					// item.setBackground(Desk.getColor(Desk.COL_SKYBLUE));
					// item.setForeground(Desk.getColor(Desk.COL_RED));
					item.setImage(Desk.getImage(Desk.IMG_TICK));
					// table.select(idx);
				}
			}
		}
	}
	
	@Override
	public void createPartControl(Composite parent){
		tabs = new CTabFolder(parent, SWT.BOTTOM);
		tabs.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		CTabItem ctLong = new CTabItem(tabs, SWT.NONE);
		ctLong.setText("lang");
		longTable = new Table(tabs, SWT.MULTI | SWT.FULL_SELECTION);
		ctLong.setControl(longTable);
		CTabItem ctMedium = new CTabItem(tabs, SWT.NONE);
		ctMedium.setText("kurz");
		mediumTable = new Table(tabs, SWT.MULTI | SWT.FULL_SELECTION);
		ctMedium.setControl(mediumTable);
		
		CTabItem ctStat = new CTabItem(tabs, SWT.NONE);
		ctStat.setText("Statistik");
		Composite cStat = new Composite(tabs, SWT.NONE);
		cStat.setLayout(new GridLayout());
		ctStat.setControl(cStat);
		Button bRecalc = new Button(cStat, SWT.PUSH);
		bRecalc.setText("Berechnen");
		bRecalc.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		cCalc = new Composite(cStat, SWT.NONE);
		cCalc.setLayout(new GridLayout());
		cCalc.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		bRecalc.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				for (Control c : cCalc.getChildren()) {
					c.dispose();
				}
				Query<RFE> qbe = new Query<RFE>(RFE.class);
				int[] result = new int[RFE.getRFETexts().length];
				int all = 0;
				for (RFE rfe : qbe.execute()) {
					String code = rfe.getCode();
					if (code.length() != 2) {
						continue;
					}
					int idx = mapCodeToIndex.get(code);
					result[idx]++;
					all++;
				}
				for (int rline = 0; rline < result.length; rline++) {
					String code = mapIndexToCode.get(rline);
					int num = result[rline];
					float percent = num * 100f / all;
					int pc = Math.round(percent);
					Label lbl = new Label(cCalc, SWT.NONE);
					lbl.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
					lbl.setText(code + ": " + num + " (=" + pc + "%)");
				}
				cCalc.layout(true);
			}
			
		});
		// table.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		int i = 0;
		for (String[] t : RFE.getRFEDescriptions()) {
			new TableItem(longTable, SWT.NONE).setText(t[1]);
			// new TableItem(shortTable, SWT.NONE).setText(t[0]);
			new TableItem(mediumTable, SWT.NONE).setText(t[2]);
			mapCodeToIndex.put(t[0], i);
			mapIndexToCode.put(i, t[0]);
			i++;
		}
		longTable.addSelectionListener(new ClickListener(longTable));
		// shortTable.addSelectionListener(new ClickListener(shortTable));
		mediumTable.addSelectionListener(new ClickListener(mediumTable));
		ElexisEventDispatcher.getInstance().addListeners(eeli_kons);
	}
	
	@Override
	public void dispose(){
		ElexisEventDispatcher.getInstance().removeListeners(eeli_kons);
	}
	
	@Override
	public void setFocus(){
	// TODO Auto-generated method stub
	
	}
	
	class ClickListener extends SelectionAdapter {
		Table table;
		
		ClickListener(Table table){
			this.table = table;
		}
		
		@Override
		public void widgetSelected(SelectionEvent e){
			Konsultation k = (Konsultation) ElexisEventDispatcher.getSelected(Konsultation.class);
			if (k != null) {
				int[] sel = table.getSelectionIndices();
				if (sel.length > 0) {
					RFE.clear(k);
					
					for (int s : sel) {
						String code = mapIndexToCode.get(s);
						new RFE(k.getId(), code);
					}
					adjustTable(k);
				}
			}
		}
		
	}
}
