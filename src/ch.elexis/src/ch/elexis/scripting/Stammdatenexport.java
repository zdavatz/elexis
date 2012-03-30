package ch.elexis.scripting;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;

import ch.elexis.Hub;
import ch.elexis.data.Fall;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.elexis.status.ElexisStatus;
import ch.rgw.tools.ExHandler;

public class Stammdatenexport {
	
	public String doExport(String startDate){
		FileDialog out = new FileDialog(Hub.getActiveShell(), SWT.SAVE);
		out.setFilterExtensions(new String[] {
			"*.csv"
		});
		out.setFilterNames(new String[] {
			"Comma Separated Values (CVS)"
		});
		out.setOverwrite(true);
		String file = out.open();
		if (file != null) {
			try {
				FileWriter writer = new FileWriter(new File(file));
				CSVWriter csv = new CSVWriter(writer);
				String[] header =
					new String[] {
						"UUID", "Nr", "Titel", "Name", "Vorname", "Geschlecht", "Geburtsdatum",
						"Strasse", "Plz", "Ort", "Postanschrift", "Telefon 1", "Telefon 2",
						"Telefon Mobil", "Bemerkung"
					};
				String[] fields =
					new String[] {
						"ID", Patient.FLD_PATID, "Titel", Patient.NAME, Patient.FIRSTNAME,
						Patient.SEX, Patient.FLD_DOB, Patient.FLD_STREET, Patient.FLD_ZIP,
						Patient.FLD_PLACE, "Anschrift", Patient.FLD_PHONE1, "Telefon2", "Natel",
						"Bemerkung"
					};
				csv.writeNext(header);
				if (startDate == null || startDate.length() == 0) {
					for (Patient pat : new Query<Patient>(Patient.class).execute()) {
						String[] line = new String[header.length];
						for (int i = 0; i < header.length; i++) {
							line[i] = pat.get(fields[i]);
						}
						csv.writeNext(line);
					}
				} else {
					HashMap<Patient, String> patienten = new HashMap<Patient, String>();
					Query<Konsultation> qbe = new Query<Konsultation>(Konsultation.class);
					qbe.add("Datum", ">", startDate);
					List<Konsultation> lKons = qbe.execute();
					
					for (Konsultation k : lKons) {
						Fall fall = k.getFall();
						Patient p = fall.getPatient();
						patienten.put(p, "1");
					}
					for (Patient p : patienten.keySet()) {
						String[] line = new String[header.length];
						for (int i = 0; i < header.length; i++) {
							line[i] = p.get(fields[i]);
						}
						csv.writeNext(line);
					}
				}
				csv.close();
				return "Der Export wurde efrolgreich abgeschlossen";
			} catch (Exception ex) {
				ElexisStatus status =
					new ElexisStatus(ElexisStatus.ERROR, Hub.PLUGIN_ID, ElexisStatus.CODE_NONE,
						"Fehler beim Export: " + ex.getMessage(), ex);
				throw new ScriptingException(status);
			}
		}
		return "Abbruch durch den Benutzer";
	}
	
}
