package ch.elexis.scripting;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import ch.elexis.data.Fall;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.elexis.data.Sticker;
import ch.elexis.text.model.Samdas;
import ch.elexis.views.PatFilterImpl;
import ch.elexis.views.PatListFilterBox.IPatFilter;
import ch.rgw.tools.IFilter;

public class ConsultationExport {

	public String doExport(String dir, String stickerName) {
		try {
			Query<Patient> qbe = new Query<Patient>(Patient.class);
			if (stickerName != null) {
				List<Sticker> ls = new Query<Sticker>(Sticker.class,
						Sticker.NAME, stickerName).execute();
				if (ls != null && ls.size() > 0) {
					final Sticker sticker = ls.get(0);

					final PatFilterImpl pf = new PatFilterImpl();
					IFilter flt = new IFilter() {
						@Override
						public boolean select(Object element) {
							return pf.accept((Patient) element, sticker) == IPatFilter.ACCEPT;
						}

					};
					qbe.addPostQueryFilter(flt);
				}else{
					return "Sticker "+stickerName+" nicht gefunden.";
				}
			}
			for (Patient pat : qbe.execute()) {
				Element e = new Element("Patient");
				e.setAttribute("ID", pat.getId());
				e.setAttribute("Name",pat.get(Patient.FLD_NAME));
				e.setAttribute("Vorname",pat.get(Patient.FLD_FIRSTNAME));
				e.setAttribute("GebDat",pat.get(Patient.FLD_DOB));
				for (Fall fall : pat.getFaelle()) {
					Element f = new Element("Fall");
					e.addContent(f);
					f.setAttribute("ID", fall.getId());
					f.setAttribute("Bezeichnung", fall.getBezeichnung());
					f.setAttribute("BeginnDatum", fall.getBeginnDatum());
					f.setAttribute("EndDatum", fall.getEndDatum());
					f.setAttribute("Gesetz", fall.getRequiredString("Gesetz"));
					f.setAttribute("Abrechnungssystem",fall.getAbrechnungsSystem());
					Kontakt k = fall.getGarant();
					if (k != null) {
						f.setAttribute("Garant", fall.getGarant().getLabel());
					}
					Kontakt vers = fall.getRequiredContact("Kostenträger");
					if(vers==null){
						vers=fall.getRequiredContact("Kostentraeger");
					}
					if (vers != null) {
						f.setAttribute("Kostentraeger", vers.getLabel());
						f.setAttribute("Versicherungsnummer",
								fall.getRequiredString("Versicherungsnummer"));
					}
					for (Konsultation kons : fall.getBehandlungen(false)) {
						Element kel = new Element("Konsultation");
						f.addContent(kel);
						kel.setAttribute("ID", kons.getId());
						kel.setAttribute("Datum", kons.getDatum());
						kel.setAttribute("Label", kons.getVerboseLabel());
						Samdas samdas=new Samdas(kons.getEintrag().getHead());
						kel.setText(samdas.getRecordText());
					}
				}
				Document doc = new Document();
				doc.setRootElement(e);
				FileOutputStream fout = new FileOutputStream(new File(dir,
						pat.getId() + ".xml"));
				OutputStreamWriter cout = new OutputStreamWriter(fout, "UTF-8"); //$NON-NLS-1$
				XMLOutputter xout = new XMLOutputter(Format.getPrettyFormat());
				xout.output(doc, cout);
				cout.close();
				fout.close();
			}
			return "ok";
		} catch (Exception ex) {
			return ex.getClass().getName() + ":" + ex.getMessage();
		}
	}
}
