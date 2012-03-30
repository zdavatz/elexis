package ch.elexis.scripting;

import java.util.HashMap;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import ch.elexis.Desk;
import ch.elexis.Hub;
import ch.elexis.data.Fall;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Patient;
import ch.elexis.data.Person;
import ch.elexis.data.Query;
import ch.elexis.util.SWTHelper;
import ch.rgw.tools.TimeTool;

import com.tiff.common.ui.datepicker.DatePicker;

public class Patientenzaehler extends TitleAreaDialog {
	DatePicker dpVon, dpBis;
	public int kons, cases, men, women;
	
	public String getResult(){
		StringBuilder sb = new StringBuilder();
		sb.append("Mandant ").append(Hub.actMandant.getLabel()).append(":\n").append("Total ")
			.append(men + women).append(" Patienten; ").append(women).append(" Frauen und ")
			.append(men).append(" Männer.\n").append("in ").append(kons).append(
				" Konsultationen zu ").append(cases).append(" Fällen.");
		return sb.toString();
	}
	
	public Patientenzaehler(){
		super(Desk.getTopShell());
	}
	
	@Override
	protected Control createDialogArea(Composite parent){
		Composite ret = (Composite) super.createDialogArea(parent);
		Composite inner = new Composite(ret, SWT.NONE);
		inner.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		inner.setLayout(new GridLayout(2, true));
		new Label(inner, SWT.NONE).setText("Startdatum");
		new Label(inner, SWT.NONE).setText("Enddatum");
		dpVon = new DatePicker(inner, SWT.NONE);
		dpBis = new DatePicker(inner, SWT.NONE);
		return ret;
	}
	
	@Override
	public void create(){
		super.create();
		setTitle("Patientenzähler");
		setMessage("Bitte start- und enddatum (inklusive) angeben");
	}
	
	@Override
	protected void okPressed(){
		TimeTool ttVon = new TimeTool(dpVon.getDate().getTime());
		TimeTool ttBis = new TimeTool(dpBis.getDate().getTime());
		Query<Konsultation> qbe = new Query<Konsultation>(Konsultation.class);
		qbe.add("Datum", ">=", ttVon.toString(TimeTool.DATE_COMPACT));
		qbe.add("Datum", "<=", ttBis.toString(TimeTool.DATE_COMPACT));
		qbe.add("MandantID", "=", Hub.actMandant.getId());
		HashMap<String, Patient> maenner = new HashMap<String, Patient>();
		HashMap<String, Patient> frauen = new HashMap<String, Patient>();
		HashMap<String, Fall> faelle = new HashMap<String, Fall>();
		
		for (Konsultation k : qbe.execute()) {
			Fall fall = k.getFall();
			faelle.put(fall.getId(), fall);
			Patient p = fall.getPatient();
			if (p.getGeschlecht().equals(Person.MALE)) {
				maenner.put(p.getId(), p);
			} else {
				frauen.put(p.getId(), p);
			}
			kons++;
		}
		men = maenner.size();
		women = frauen.size();
		cases = faelle.size();
		super.okPressed();
	}
	
}
