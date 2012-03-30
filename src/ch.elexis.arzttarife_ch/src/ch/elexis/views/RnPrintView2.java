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
 * $Id$
 *******************************************************************************/
package ch.elexis.views;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;

import ch.elexis.Hub;
import ch.elexis.StringConstants;
import ch.elexis.TarmedRechnung.TarmedACL;
import ch.elexis.TarmedRechnung.XMLExporter;
import ch.elexis.actions.ElexisEventDispatcher;
import ch.elexis.arzttarife_schweiz.Messages;
import ch.elexis.banking.ESR;
import ch.elexis.data.Brief;
import ch.elexis.data.Fall;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.data.Rechnung;
import ch.elexis.data.Rechnungssteller;
import ch.elexis.data.RnStatus;
import ch.elexis.data.Zahlung;
import ch.elexis.tarmedprefs.TarmedRequirements;
import ch.elexis.text.ITextPlugin;
import ch.elexis.text.ReplaceCallback;
import ch.elexis.text.TextContainer;
import ch.elexis.util.IRnOutputter;
import ch.elexis.util.Log;
import ch.elexis.util.SWTHelper;
import ch.elexis.util.SortedList;
import ch.rgw.tools.Money;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;
import ch.rgw.tools.XMLTool;

/**
 * This is a pop-in replacement for RnPrintView. To avoid several problems around OpenOffice based
 * bills we keep things easier here. Thus this approach does not optimize printer access but rather
 * waits for each page to be printed before starting the next.
 * 
 * We also corrected several problems around the TrustCenter-system. Tokens are printed only on TG
 * bills and only if the mandator has a TC contract. Tokens are computed correctly now with the TC
 * number as identifier in TG bills and left as ESR in TP bills.
 * 
 * @author Gerry
 * 
 */
public class RnPrintView2 extends ViewPart {
	public static final String ID = "ch.elexis.arzttarife_ch.printview2";
	
	private double cmAvail = 21.4; // Verfügbare Druckhöhe in cm
	private static double cmPerLine = 0.67; // Höhe pro Zeile (0.65 plus
	// Toleranz)
	private static double cmFirstPage = 13.0; // Platz auf der ersten Seite
	private static double cmMiddlePage = 21.0; // Platz auf Folgeseiten
	private static double cmFooter = 4.5; // Platz für Endabrechnung
	private final Log log = Log.get("RnPrint");
	private String paymentMode;
	private Brief actBrief;
	TextContainer text;
	TarmedACL ta = TarmedACL.getInstance();
	
	public RnPrintView2(){

	}
	
	@Override
	public void createPartControl(final Composite parent){
		text = new TextContainer(getViewSite());
		text.getPlugin().createContainer(parent, new ITextPlugin.ICallback() {
			
			public void save(){
			// TODO Auto-generated method stub
			
			}
			
			public boolean saveAs(){
				// TODO Auto-generated method stub
				return false;
			}
		});
	}
	
	private void createBrief(final String template, final Kontakt adressat){
		actBrief =
			text.createFromTemplateName(null, template, Brief.RECHNUNG, adressat,
				Messages.RnPrintView_tarmedBill);
	}
	
	private boolean deleteBrief(){
		if (actBrief != null) {
			return actBrief.delete();
		}
		return true;
	}
	
	@Override
	public void setFocus(){
	// TODO Auto-generated method stub
	
	}
	
	/**
	 * Druckt die Rechnung auf eine Vorlage, deren Ränder alle auf 0.5cm eingestellt sein müssen,
	 * und die unterhalb von 170 mm leer ist. (Papier mit EZ-Schein wird erwartet) Zweite und
	 * Folgeseiten müssen gem Tarmedrechnung formatiert sein.
	 * 
	 * @param rn
	 *            die Rechnung
	 * @param saveFile
	 *            Filename für eine XML-Kopie der Rechnung oder null: Keine Kopie
	 * @param withForms
	 * @param monitor
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean doPrint(final Rechnung rn, final IRnOutputter.TYPE rnType,
		final String saveFile, final boolean withESR, final boolean withForms,
		final boolean doVerify, final IProgressMonitor monitor){
		Mandant mSave = Hub.actMandant;
		monitor.subTask(rn.getLabel());
		Fall fall = rn.getFall();
		Mandant mnd = rn.getMandant();
		if (fall == null || mnd == null) {
			return false;
		}
		Patient pat = fall.getPatient();
		Hub.setMandant(mnd);
		Rechnungssteller rs = mnd.getRechnungssteller();
		if (pat == null || rs == null) {
			return false;
		}
		
		String printer = null;
		XMLExporter xmlex = new XMLExporter();
		DecimalFormat df = new DecimalFormat(StringConstants.DOUBLE_ZERO);
		Document xmlRn = xmlex.doExport(rn, saveFile, rnType, doVerify);
		if (rn.getStatus() == RnStatus.FEHLERHAFT) {
			return false;
		}
		Element invoice =
			xmlRn.getRootElement().getChild(XMLExporter.ELEMENT_INVOICE, XMLExporter.ns);
		Element balance = invoice.getChild(XMLExporter.ELEMENT_BALANCE, XMLExporter.ns);
		paymentMode = XMLExporter.TIERS_GARANT; // fall.getPaymentMode();
		Element eTiers = invoice.getChild(XMLExporter.ELEMENT_TIERS_GARANT, XMLExporter.ns);
		if (eTiers == null) {
			eTiers = invoice.getChild(XMLExporter.ELEMENT_TIERS_PAYANT, XMLExporter.ns);
			paymentMode = XMLExporter.TIERS_PAYANT;
		}
		
		String tcCode = null;
		if (TarmedRequirements.hasTCContract(rs) && paymentMode.equals(XMLExporter.TIERS_GARANT)) {
			tcCode = TarmedRequirements.getTCCode(rs);
		} else if (paymentMode.equals(XMLExporter.TIERS_PAYANT)) {
			tcCode = "01";
		}
		ElexisEventDispatcher.fireSelectionEvents(rn, fall, pat, rs);
		
		// make sure the Textplugin can replace all fields
		fall.setInfoString("payment", paymentMode);
		fall.setInfoString("Gesetz", TarmedRequirements.getGesetz(fall));
		mnd.setInfoElement("EAN", TarmedRequirements.getEAN(mnd));
		rs.setInfoElement("EAN", TarmedRequirements.getEAN(rs));
		mnd.setInfoElement("KSK", TarmedRequirements.getKSK(mnd));
		mnd.setInfoElement("NIF", TarmedRequirements.getNIF(mnd));
		if (!mnd.equals(rs)) {
			rs.setInfoElement("EAN", TarmedRequirements.getEAN(rs));
			rs.setInfoElement("KSK", TarmedRequirements.getKSK(rs));
			rs.setInfoElement("NIF", TarmedRequirements.getNIF(rs));
		}
		
		Kontakt adressat;
		
		if (paymentMode.equals(XMLExporter.TIERS_PAYANT)) {
			adressat = fall.getRequiredContact(TarmedRequirements.INSURANCE);
		} else {
			adressat = fall.getGarant();
		}
		if ((adressat == null) || (!adressat.exists())) {
			adressat = pat;
		}
		adressat.getPostAnschrift(true); // damit sicher eine existiert
		String userdata = rn.getRnId();
		ESR esr =
			new ESR(rs.getInfoString(ta.ESRNUMBER), rs.getInfoString(ta.ESRSUB), userdata,
				ESR.ESR27);
		Money mDue =
			XMLTool.xmlDoubleToMoney(balance.getAttributeValue(XMLExporter.ATTR_AMOUNT_DUE));
		Money mPaid =
			XMLTool.xmlDoubleToMoney(balance.getAttributeValue(XMLExporter.ATTR_AMOUNT_PREPAID));
		String offenRp = mDue.getCentsAsString();
		// Money mEZDue=new Money(xmlex.mTotal);
		Money mEZDue = new Money(mDue); // XMLTool.xmlDoubleToMoney(balance.getAttributeValue("amount_obligations"));
		Money mEZBrutto = new Money(mDue);
		mEZDue.addMoney(mPaid);
		if (withESR == true) {
			String tmpl = "Tarmedrechnung_EZ"; //$NON-NLS-1$
			if ((rn.getStatus() == RnStatus.MAHNUNG_1)
				|| (rn.getStatus() == RnStatus.MAHNUNG_1_GEDRUCKT)) {
				tmpl = "Tarmedrechnung_M1"; //$NON-NLS-1$
			} else if ((rn.getStatus() == RnStatus.MAHNUNG_2)
				|| (rn.getStatus() == RnStatus.MAHNUNG_2_GEDRUCKT)) {
				tmpl = "Tarmedrechnung_M2"; //$NON-NLS-1$
			} else if ((rn.getStatus() == RnStatus.MAHNUNG_3)
				|| (rn.getStatus() == RnStatus.MAHNUNG_3_GEDRUCKT)) {
				tmpl = "Tarmedrechnung_M3"; //$NON-NLS-1$
			}
			createBrief(tmpl, adressat);
			
			List<Zahlung> extra = rn.getZahlungen();
			Kontakt bank = Kontakt.load(rs.getInfoString(ta.RNBANK));
			final StringBuilder sb = new StringBuilder();
			String sTarmed = balance.getAttributeValue(XMLExporter.ATTR_AMOUNT_TARMED);
			String sMedikament = balance.getAttributeValue(XMLExporter.ATTR_AMOUNT_DRUG);
			String sAnalysen = balance.getAttributeValue(XMLExporter.ATTR_AMOUNT_LAB);
			String sMigel = balance.getAttributeValue(XMLExporter.ATTR_AMOUNT_MIGEL);
			String sPhysio = balance.getAttributeValue(XMLExporter.ATTR_AMOUNT_PHYSIO);
			String sOther = balance.getAttributeValue(XMLExporter.ATTR_AMOUNT_UNCLASSIFIED);
			sb.append(Messages.RnPrintView_tarmedPoints).append(sTarmed).append(StringConstants.LF);
			sb.append(Messages.RnPrintView_medicaments).append(sMedikament).append(
				StringConstants.LF);
			sb.append(Messages.RnPrintView_labpoints).append(sAnalysen).append(StringConstants.LF);
			sb.append(Messages.RnPrintView_migelpoints).append(sMigel).append(StringConstants.LF);
			sb.append(Messages.RnPrintView_physiopoints).append(sPhysio).append(StringConstants.LF);
			sb.append(Messages.RnPrintView_otherpoints).append(sOther).append(StringConstants.LF);
			
			for (Zahlung z : extra) {
				Money betrag = new Money(z.getBetrag()).multiply(-1.0);
				if (!betrag.isNegative()) {
					sb
						.append(z.getBemerkung())
						.append(":\t").append(betrag.getAmountAsString()).append(StringConstants.LF); //$NON-NLS-1$ 
					mEZDue.addMoney(betrag);
				}
			}
			sb.append("--------------------------------------").append(StringConstants.LF); //$NON-NLS-1$ 
			
			sb.append(Messages.RnPrintView_sum).append(mEZDue);
			
			if (!mPaid.isZero()) {
				sb.append(Messages.RnPrintView_prepaid).append(mPaid.getAmountAsString()).append(
					StringConstants.LF);
				// sb.append("Noch zu zahlen:\t").append(xmlex.mDue.getAmountAsString()).append("\n");
				sb.append(Messages.RnPrintView_topay).append(
					mEZDue.subtractMoney(mPaid).roundTo5().getAmountAsString()).append(
					StringConstants.LF);
			}
			
			text.getPlugin().setFont("Serif", SWT.NORMAL, 9); //$NON-NLS-1$
			text.replace("\\[Leistungen\\]", sb.toString());
			
			if (esr.printBESR(bank, adressat, rs, mEZDue.roundTo5().getCentsAsString(), text) == false) {
				// avoid dead letters
				deleteBrief();
				;
				Hub.setMandant(mSave);
				return false;
			}
			printer = Hub.localCfg.get("Drucker/A4ESR/Name", null); //$NON-NLS-1$
			String esrTray = Hub.localCfg.get("Drucker/A4ESR/Schacht", null); //$NON-NLS-1$
			if (StringTool.isNothing(esrTray)) {
				esrTray = null;
			}
			// Das mit der Tray- Einstellung funktioniert sowieso nicht richtig.
			// OOo nimmt den Tray aus der Druckformatvorlage. Besser wir setzen
			// ihn hier auf
			// null vorläufig.
			// Alternative: Wir verwenden ihn, falls er eingestellt ist, sonst
			// nicht.
			// Dies scheint je nach Druckertreiber unterschiedlich zu
			// funktionieren.
			if (text.getPlugin().print(printer, esrTray, false) == false) {
				SWTHelper.showError("Fehler beim Drucken", "Konnte den Drucker nicht starten");
				rn.addTrace(Rechnung.REJECTED, "Druckerfehler");
				// avoid dead letters
				deleteBrief();
				;
				Hub.setMandant(mSave);
				return false;
			}
			
			monitor.worked(2);
		}
		if (withForms == false) {
			// avoid dead letters
			deleteBrief();
			;
			Hub.setMandant(mSave);
			return true;
		}
		printer = Hub.localCfg.get("Drucker/A4/Name", null); //$NON-NLS-1$
		String tarmedTray = Hub.localCfg.get("Drucker/A4/Schacht", null); //$NON-NLS-1$
		if (StringTool.isNothing(tarmedTray)) {
			tarmedTray = null;
		}
		createBrief("Tarmedrechnung_S1", adressat);
		
		StringBuilder sb = new StringBuilder();
		Element root = xmlRn.getRootElement();
		Namespace ns = root.getNamespace();
		//Element invoice=root.getChild("invoice",ns); //$NON-NLS-1$
		if (invoice.getAttributeValue("resend").equalsIgnoreCase("true")) { //$NON-NLS-1$ //$NON-NLS-2$
			text.replace("\\[F5\\]", Messages.RnPrintView_yes); //$NON-NLS-1$
		} else {
			text.replace("\\[F5\\]", Messages.RnPrintView_no); //$NON-NLS-1$
		}
		
		// Vergütungsart F17
		// replaced with Fall.payment
		
		if (fall.getAbrechnungsSystem().equals("UVG")) { //$NON-NLS-1$
			text.replace("\\[F58\\]", fall.getBeginnDatum()); //$NON-NLS-1$
		} else {
			text.replace("\\[F58\\]", ""); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		Element detail = invoice.getChild("detail", ns); //$NON-NLS-1$
		Element diagnosis = detail.getChild("diagnosis", ns); //$NON-NLS-1$
		String type = diagnosis.getAttributeValue(Messages.RnPrintView_62);
		
		// TODO Cheap workaround, fix
		if (type.equals("by_contract")) { //$NON-NLS-1$
			type = "TI-Code"; //$NON-NLS-1$
		}
		text.replace("\\[F51\\]", type); //$NON-NLS-1$
		if (type.equals("freetext")) { //$NON-NLS-1$
			text.replace("\\[F52\\]", ""); //$NON-NLS-1$ //$NON-NLS-2$
			text.replace("\\[F53\\]", diagnosis.getText()); //$NON-NLS-1$
		} else {
			text.replace("\\[F52\\]", diagnosis.getAttributeValue("code")); //$NON-NLS-1$ //$NON-NLS-2$
			text.replace("\\[F53\\]", ""); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		Kontakt zuweiser = fall.getRequiredContact("Zuweiser");
		if (zuweiser != null) {
			String ean = TarmedRequirements.getEAN(zuweiser);
			if (!ean.equals(TarmedRequirements.EAN_PSEUDO)) {
				text.replace("\\[F23\\]", ean);
			}
		}
		
		Element services = detail.getChild(XMLExporter.ELEMENT_SERVICES, ns); //$NON-NLS-1$
		SortedList<Element> ls = new SortedList(services.getChildren(), new RnComparator());
		
		Element remark = invoice.getChild(XMLExporter.ELEMENT_REMARK); //$NON-NLS-1$
		if (remark != null) {
			final String rem = remark.getText();
			text.getPlugin().findOrReplace(Messages.RnPrintView_remark, new ReplaceCallback() {
				public String replace(final String in){
					return Messages.RnPrintView_remarksp + rem;
				}
			});
		}
		replaceHeaderFields(text, rn);
		text.replace("\\[F.+\\]", ""); //$NON-NLS-1$ //$NON-NLS-2$
		Object cursor = text.getPlugin().insertText("[Rechnungszeilen]", "\n", SWT.LEFT); //$NON-NLS-1$ //$NON-NLS-2$
		TimeTool r = new TimeTool();
		int page = 1;
		double seitentotal = 0.0;
		double sumPfl = 0.0;
		double sumNpfl = 0.0;
		double sumTotal = 0.0;
		ITextPlugin tp = text.getPlugin();
		cmAvail = cmFirstPage;
		monitor.worked(2);
		for (Element s : ls) {
			tp.setFont("Helvetica", SWT.BOLD, 7); //$NON-NLS-1$
			cursor = tp.insertText(cursor, "\t" + s.getText() + "\n", SWT.LEFT); //$NON-NLS-1$ //$NON-NLS-2$
			tp.setFont("Helvetica", SWT.NORMAL, 8); //$NON-NLS-1$
			sb.setLength(0);
			if (r.set(s.getAttributeValue("date_begin")) == false) { //$NON-NLS-1$
				continue;
			}
			sb.append("■ "); //$NON-NLS-1$
			sb.append(r.toString(TimeTool.DATE_GER)).append("\t"); //$NON-NLS-1$
			sb.append(getValue(s, "tariff_type")).append("\t"); //$NON-NLS-1$ //$NON-NLS-2$
			sb.append(getValue(s, "code")).append("\t"); //$NON-NLS-1$ //$NON-NLS-2$
			sb.append(getValue(s, "ref_code")).append("\t"); //$NON-NLS-1$ //$NON-NLS-2$
			sb.append(getValue(s, "number")).append("\t"); //$NON-NLS-1$ //$NON-NLS-2$
			sb.append(getValue(s, "side")).append("\t"); //$NON-NLS-1$ //$NON-NLS-2$
			sb.append(getValue(s, "quantity")).append("\t"); //$NON-NLS-1$ //$NON-NLS-2$
			String val = s.getAttributeValue("unit.mt"); //$NON-NLS-1$
			if (StringTool.isNothing(val)) {
				val = s.getAttributeValue("unit"); //$NON-NLS-1$
				if (StringTool.isNothing(val)) {
					val = "\t"; //$NON-NLS-1$
				}
			}
			sb.append(val).append("\t"); //$NON-NLS-1$
			sb.append(getValue(s, "scale_factor.mt")).append("\t"); //$NON-NLS-1$ //$NON-NLS-2$
			val = s.getAttributeValue("unit_factor.mt"); //$NON-NLS-1$
			if (StringTool.isNothing(val)) {
				val = s.getAttributeValue("unit_factor"); //$NON-NLS-1$
				if (StringTool.isNothing(val)) {
					val = "\t"; //$NON-NLS-1$
				}
			}
			sb.append(val).append("\t"); //$NON-NLS-1$
			sb.append(getValue(s, "unit.tt")).append("\t\t"); //$NON-NLS-1$ //$NON-NLS-2$
			sb.append(getValue(s, "unit_factor.tt")).append("\t"); //$NON-NLS-1$ //$NON-NLS-2$
			sb.append("1\t1\t"); //$NON-NLS-1$
			String pfl = s.getAttributeValue("obligation"); //$NON-NLS-1$
			String am = s.getAttributeValue(XMLExporter.ATTR_AMOUNT); //$NON-NLS-1$
			// double dLine=Double.parseDouble(am);
			double dLine;
			try {
				dLine = XMLTool.xmlDoubleToMoney(am).getAmount();
			} catch (NumberFormatException ex) {
				// avoid dead letters
				deleteBrief();
				;
				log.log("Fehlerhaftes Format für amount bei " + sb.toString(), Log.ERRORS);
				Hub.setMandant(mSave);
				return false;
			}
			sumTotal += dLine;
			if (pfl.equalsIgnoreCase("true")) { //$NON-NLS-1$
				sb.append("0\t"); //$NON-NLS-1$
				sumPfl += dLine;
			} else {
				sb.append("1\t"); //$NON-NLS-1$
				sumNpfl += dLine;
			}
			sb.append(getValue(s, "vat_rate")).append("\t"); //$NON-NLS-1$
			
			sb.append(am);
			seitentotal += dLine;
			sb.append("\n"); //$NON-NLS-1$
			cursor = tp.insertText(cursor, sb.toString(), SWT.LEFT);
			cmAvail -= cmPerLine;
			if (cmAvail <= 0) {
				StringBuilder footer = new StringBuilder();
				cursor = tp.insertText(cursor, "\n\n", SWT.LEFT); //$NON-NLS-1$
				footer
					.append("■ Zwischentotal\t\tCHF\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t").append(df.format(seitentotal)); //$NON-NLS-1$
				tp.setFont("Helvetica", SWT.BOLD, 7); //$NON-NLS-1$
				cursor = tp.insertText(cursor, footer.toString(), SWT.LEFT);
				seitentotal = 0.0;
				if (tcCode != null) {
					esr.printESRCodeLine(text.getPlugin(), offenRp, tcCode);
				}
				
				if (text.getPlugin().print(printer, tarmedTray, false) == false) {
					// avoid dead letters
					deleteBrief();
					;
					Hub.setMandant(mSave);
					return false;
				}
				
				insertPage(++page, adressat, rn);
				cursor = text.getPlugin().insertText("[Rechnungszeilen]", "\n", SWT.LEFT); //$NON-NLS-1$ //$NON-NLS-2$
				cmAvail = cmMiddlePage;
				monitor.worked(2);
			}
			
		}
		cursor = tp.insertText(cursor, "\n", SWT.LEFT); //$NON-NLS-1$
		if (cmAvail < cmFooter) {
			if (tcCode != null) {
				esr.printESRCodeLine(text.getPlugin(), offenRp, tcCode);
			}
			if (text.getPlugin().print(printer, tarmedTray, false) == false) {
				// avoid dead letters
				deleteBrief();
				;
				Hub.setMandant(mSave);
				return false;
			}
			insertPage(++page, adressat, rn);
			cursor = text.getPlugin().insertText("[Rechnungszeilen]", "\n", SWT.LEFT); //$NON-NLS-1$ //$NON-NLS-2$
			monitor.worked(2);
		}
		StringBuilder footer = new StringBuilder(100);
		//Element balance=invoice.getChild("balance",ns); //$NON-NLS-1$
		
		cursor = text.getPlugin().insertTextAt(0, 220, 190, 45, " ", SWT.LEFT); //$NON-NLS-1$
		cursor = print(cursor, tp, true, "\tTARMED AL \t"); //$NON-NLS-1$
		footer.append(balance.getAttributeValue("amount_tarmed.mt")) //$NON-NLS-1$
			.append("  (").append(balance.getAttributeValue("unit_tarmed.mt")).append(")\t"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		cursor = print(cursor, tp, false, footer.toString());
		cursor = print(cursor, tp, true, "Physio \t"); //$NON-NLS-1$
		cursor = print(cursor, tp, false, getValue(balance, "amount_physio")); //$NON-NLS-1$
		cursor = print(cursor, tp, true, "\tMiGeL \t"); //$NON-NLS-1$
		cursor = print(cursor, tp, false, getValue(balance, "amount_migel")); //$NON-NLS-1$
		cursor = print(cursor, tp, true, "\tÜbrige \t"); //$NON-NLS-1$
		cursor = print(cursor, tp, false, getValue(balance, "amount_unclassified")); //$NON-NLS-1$
		cursor = print(cursor, tp, true, "\n\tTARMED TL \t"); //$NON-NLS-1$
		footer.setLength(0);
		footer.append(balance.getAttributeValue("amount_tarmed.tt")) //$NON-NLS-1$
			.append("  (").append(balance.getAttributeValue("unit_tarmed.tt")).append(")\t"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		cursor = print(cursor, tp, false, footer.toString());
		cursor = print(cursor, tp, true, "Labor \t"); //$NON-NLS-1$
		cursor = print(cursor, tp, false, getValue(balance, "amount_lab")); //$NON-NLS-1$
		cursor = print(cursor, tp, true, "\tMedi \t"); //$NON-NLS-1$
		cursor = print(cursor, tp, false, getValue(balance, "amount_drug")); //$NON-NLS-1$
		cursor = print(cursor, tp, true, "\tKantonal \t"); //$NON-NLS-1$
		cursor = print(cursor, tp, false, getValue(balance, "amount_cantonal")); //$NON-NLS-1$
		
		footer.setLength(0);
		footer.append("\n\n").append("■ Gesamtbetrag\t\tCHF\t\t").append(df.format(sumTotal)) //$NON-NLS-1$ //$NON-NLS-2$
			.append("\tdavon PFL \t").append(df.format(sumPfl)).append("\tAnzahlung \t") //$NON-NLS-1$ //$NON-NLS-2$
			.append(mPaid.getAmountAsString())
			.append("\tFälliger Betrag \t").append(mDue.getAmountAsString()); //$NON-NLS-1$

		Element vat = balance.getChild("vat", ns);
		String vatNumber = getValue(vat, "vat_number");
		if(vatNumber.equals(" "))
			vatNumber = "keine";
			
		footer.append("\n\n■ MwSt.Nr. \t\t"); //$NON-NLS-1$
		cursor = print(cursor, tp, true, footer.toString());
		cursor = print(cursor, tp, false, vatNumber + "\n\n"); //$NON-NLS-1$
		cursor = print(cursor, tp, true, "  Satz\t\tBetrag\tMwSt\n"); //$NON-NLS-1$
		tp.setFont("Helvetica", SWT.NORMAL, 9); //$NON-NLS-1$
		footer.setLength(0);
		
		List<Element>rates = vat.getChildren();
		
		for(Element rate : rates) {
			footer.append("■ ").append(getValue(rate, "vat_rate")).append("\t\t")
				.append(getValue(rate, "amount")).append("\t")
				.append(getValue(rate, "vat")).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		
		cursor = print(cursor, tp, false, footer.toString());
		cursor = print(cursor, tp, true, "\n Total\t\t"); //$NON-NLS-1$
		footer.setLength(0);
		footer.append(mDue.getAmountAsString()).append("\t").append(getValue(vat, "vat")); //$NON-NLS-1$
		tp.setFont("Helvetica", SWT.BOLD, 9); //$NON-NLS-1$
		tp.insertText(cursor, footer.toString(), SWT.LEFT);
		if (tcCode != null) {
			esr.printESRCodeLine(text.getPlugin(), offenRp, tcCode);
		}
		
		if (text.getPlugin().print(printer, tarmedTray, false) == false) {
			// avoid dead letters
			deleteBrief();
			;
			Hub.setMandant(mSave);
			return false;
		}
		monitor.worked(2);
		// avoid dead letters
		deleteBrief();
		;
		Hub.setMandant(mSave);
		try {
			Thread.sleep(5);
		} catch (InterruptedException e) {
			// never mind
		}
		return true;
	}
	
	private void insertPage(final int page, final Kontakt adressat, final Rechnung rn){
		createBrief("Tarmedrechnung_S2", adressat);
		replaceHeaderFields(text, rn);
		text
			.replace("\\[Seite\\]", StringTool.pad(StringTool.LEFT, '0', Integer.toString(page), 2)); //$NON-NLS-1$
	}
	
	/*
	 * private TextContainer insertPage(final int page, final Kontakt adressat, TextContainer text,
	 * final Rechnung rn){
	 * 
	 * if(--existing<0){ ctF=addItem("Tarmedrechnung_S2",Messages.RnPrintView_page+page,adressat);
	 * //$NON-NLS-1$ }else{ ctF=ctab.getItem(page); useItem(page,"Tarmedrechnung_S2", adressat);
	 * //$NON-NLS-1$ } text=(TextContainer) ctF.getData("text"); //$NON-NLS-1$
	 * replaceHeaderFields(text, rn); text.replace("\\[Seite\\]",StringTool.pad(SWT
	 * .LEFT,'0',Integer.toString(page),2)); //$NON-NLS-1$ return text;
	 * 
	 * }
	 */
	private Object print(final Object cur, final ITextPlugin p, final boolean small,
		final String text){
		if (small) {
			p.setFont("Helvetica", SWT.BOLD, 7); //$NON-NLS-1$
		} else {
			p.setFont("Helvetica", SWT.NORMAL, 9); //$NON-NLS-1$
		}
		return p.insertText(cur, text, SWT.LEFT);
	}
	
	private String getValue(final Element s, final String field){
		String ret = s.getAttributeValue(field);
		if (StringTool.isNothing(ret)) {
			return " "; //$NON-NLS-1$
		}
		return ret;
	}
	
	private void replaceHeaderFields(final TextContainer text, final Rechnung rn){
		Fall fall = rn.getFall();
		Mandant m = rn.getMandant();
		text.replace("\\[F1\\]", rn.getRnId()); //$NON-NLS-1$
		
		String titel;
		String titelMahnung;
		
		if (paymentMode.equals(XMLExporter.TIERS_PAYANT)) { //$NON-NLS-1$
			titel = Messages.RnPrintView_tbBill;
			
			switch (rn.getStatus()) {
			case RnStatus.MAHNUNG_1_GEDRUCKT:
			case RnStatus.MAHNUNG_1:
				titelMahnung = Messages.RnPrintView_firstM;
				break;
			case RnStatus.MAHNUNG_2:
			case RnStatus.MAHNUNG_2_GEDRUCKT:
				titelMahnung = Messages.RnPrintView_secondM;
				break;
			case RnStatus.IN_BETREIBUNG:
			case RnStatus.TEILVERLUST:
			case RnStatus.TOTALVERLUST:
			case RnStatus.MAHNUNG_3:
			case RnStatus.MAHNUNG_3_GEDRUCKT:
				titelMahnung = Messages.RnPrintView_thirdM;
				break;
			default:
				titelMahnung = ""; //$NON-NLS-1$
			}
			;
		} else {
			titel = Messages.RnPrintView_getback;
			titelMahnung = ""; //$NON-NLS-1$
		}
		
		text.replace("\\[Titel\\]", titel); //$NON-NLS-1$
		text.replace("\\[TitelMahnung\\]", titelMahnung); //$NON-NLS-1$
		
		if (fall.getAbrechnungsSystem().equals("IV")) { //$NON-NLS-1$
			text.replace("\\[NIF\\]", TarmedRequirements.getNIF(m)); //$NON-NLS-1$
			String ahv = TarmedRequirements.getAHV(fall.getPatient());
			if (StringTool.isNothing(ahv)) {
				ahv = fall.getRequiredString("AHV-Nummer");
			}
			text.replace("\\[F60\\]", ahv); //$NON-NLS-1$
		} else {
			text.replace("\\[NIF\\]", TarmedRequirements.getKSK(m)); //$NON-NLS-1$
			text.replace("\\[F60\\]", ""); //$NON-NLS-1$ //$NON-NLS-2$
		}
		text.replace("\\?\\?\\??[a-zA-Z0-9 \\.]+\\?\\?\\??", "");
		
	}
	
	private class RnComparator implements Comparator<Element> {
		TimeTool tt0 = new TimeTool();
		TimeTool tt1 = new TimeTool();
		
		public int compare(Element e0, Element e1){
			if (!tt0.set(e0.getAttributeValue("date_begin"))) {
				return 1;
			}
			if (!tt1.set(e1.getAttributeValue("date_begin"))) {
				return -1;
			}
			int dat = tt0.compareTo(tt1);
			if (dat != 0) {
				return dat;
			}
			String t0 = e0.getAttributeValue(XMLExporter.ATTR_TARIFF_TYPE);
			String t1 = e1.getAttributeValue(XMLExporter.ATTR_TARIFF_TYPE);
			if (t0.equals("001")) { // tarmed-tarmed: nach code sortieren
				if (t1.equals("001")) {
					String c0 = e0.getAttributeValue(XMLExporter.ATTR_CODE);
					String c1 = e1.getAttributeValue(XMLExporter.ATTR_CODE);
					return c0.compareTo(c1);
				} else {
					return -1; // tarmed immer oberhab nicht-tarmed
				}
			} else if (t1.equals("001")) {
				return 1; // nicht-tarmed immer unterhalb tarmed
			} else { // nicht-tarmed - nicht-tarmed: alphabetisch
				int diffc = t0.compareTo(t1);
				if (diffc == 0) {
					diffc = e0.getText().compareToIgnoreCase(e1.getText());
				}
				return diffc;
			}
		}
	}
	
}
