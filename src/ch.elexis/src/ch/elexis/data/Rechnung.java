/*******************************************************************************
 * Copyright (c) 2005-2011, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *  $Id$
 *******************************************************************************/

package ch.elexis.data;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.MessageDialog;

import ch.elexis.Desk;
import ch.elexis.Hub;
import ch.elexis.preferences.Leistungscodes;
import ch.elexis.preferences.PreferenceConstants;
import ch.elexis.util.Log;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.Money;
import ch.rgw.tools.Result;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;
import ch.rgw.tools.JdbcLink.Stm;

public class Rechnung extends PersistentObject {
	public static final String REMARK = "Bemerkung";
	public static final String BILL_STATE_DATE = "StatusDatum";
	public static final String BILL_DATE = "RnDatum";
	public static final String BILL_AMOUNT_CENTS = "Betragx100";
	public static final String BILL_DATE_UNTIL = "RnDatumBis";
	public static final String BILL_DATE_FROM = "RnDatumVon";
	public static final String BILL_STATE = "RnStatus";
	public static final String MANDATOR_ID = "MandantID";
	public static final String CASE_ID = "FallID";
	public static final String BILL_NUMBER = "RnNummer";
	static final String TABLENAME = "RECHNUNGEN";
	// public static final DecimalFormat geldFormat=new DecimalFormat("0.00");
	// Texte für Trace-Meldungen
	public static final String STATUS_CHANGED = "Statusänderung";
	public static final String PAYMENT = "Zahlung";
	public static final String CORRECTION = "Korrektur";
	public static final String REJECTED = "Zurückgewiesen";
	public static final String OUTPUT = "Ausgegeben";
	public static final String REMARKS = "Bemerkungen";
	
	static {
		addMapping(TABLENAME, BILL_NUMBER, CASE_ID, MANDATOR_ID, "RnDatum=S:D:RnDatum", BILL_STATE,
			"StatusDatum=S:D:StatusDatum", "RnDatumVon=S:D:RnDatumVon",
			"RnDatumBis=S:D:RnDatumBis", "Betragx100=Betrag", FLD_EXTINFO,
			"Zahlungen=LIST:RechnungsID:ZAHLUNGEN:Datum");
	}
	
	public Rechnung(final String nr, final Mandant m, final Fall f, final String von,
		final String bis, final Money Betrag, final int status){
		create(null);
		String Datum = new TimeTool().toString(TimeTool.DATE_GER);
		set(new String[] {
			BILL_NUMBER, MANDATOR_ID, CASE_ID, BILL_DATE_FROM, BILL_DATE_UNTIL, BILL_AMOUNT_CENTS,
			BILL_STATE, BILL_DATE
		}, nr, m.getId(), f.getId(), von, bis, Betrag.getCentsAsString(), Integer.toString(status),
			Datum);
		
		new AccountTransaction(f.getPatient(), this, Betrag.multiply(-1.0), Datum,
			"Rechnung erstellt");
	}
	
	/**
	 * Eine Rechnung aus einer Behandlungsserie erstellen. Es werde aus dieser Serie nur diejenigen
	 * Behandlungen verwendet, die zum selben Mandanten und zum selben Fall gehören. Falls der Fall
	 * einen Rechnungssteller hat, werden alle Konsultationen ungeachtet des Mandanten verrechnet
	 * 
	 * @return Ein Result mit ggf. der erstellten Rechnung als Inhalt
	 */
	public static Result<Rechnung> build(final List<Konsultation> behandlungen){
		Result<Rechnung> result = new Result<Rechnung>();
		if ((behandlungen == null) || (behandlungen.size() == 0)) {
			return result.add(Result.SEVERITY.WARNING, 1,
				"Die Rechnung enthält keine Behandlungen", null, true);
		}
		Rechnung ret = new Rechnung();
		ret.create(null);
		TimeTool startDate = new TimeTool("31.12.2999");
		TimeTool endDate = new TimeTool("01.01.2000");
		TimeTool actDate = new TimeTool();
		Mandant m = null;
		// Kontakt kostentraeger=null;
		List<IDiagnose> diagnosen = null;
		Fall f = null;
		// int summe=0;
		Money summe = new Money();
		for (Konsultation b : behandlungen) {
			Rechnung shouldntExist = b.getRechnung();
			if ((shouldntExist != null) && (shouldntExist.getStatus() != RnStatus.STORNIERT)) {
				log.log("Tried to create bill for already billed kons " + b.getLabel(),
					Log.WARNINGS);
				continue;
			}
			Mandant bm = b.getMandant();
			if ((bm == null) || (!bm.isValid())) {
				result =
					result.add(Result.SEVERITY.ERROR, 1, "Ungültiger Mandant bei Konsultation "
						+ b.getLabel(), ret, true);
				continue;
			}
			if (m == null) {
				m = bm;
				ret.set(MANDATOR_ID, m.getId());
			} else {
				if (!bm.getRechnungssteller().getId().equals(m.getRechnungssteller().getId())) {
					result =
						result.add(Result.SEVERITY.ERROR, 2,
							"Die Liste enthält unterschiedliche Rechnungssteller " + b.getLabel(), ret,
							true);
					continue;
				}
			}
			Fall bf = b.getFall();
			if (bf == null) {
				result =
					result.add(Result.SEVERITY.ERROR, 3, "Fehlender Fall bei Konsultation "
						+ b.getLabel(), ret, true);
				continue;
			}
			if (f == null) {
				f = bf;
				ret.set(CASE_ID, f.getId());
				f.setBillingDate(null); // ggf. Rechnungsvorschlag löschen
			} else {
				if (!f.getId().equals(bf.getId())) {
					result =
						result.add(Result.SEVERITY.ERROR, 4,
							"Die Liste enthält unterschiedliche Faelle " + b.getLabel(), ret, true);
					continue;
				}
			}
			
			if ((diagnosen == null) || (diagnosen.size() == 0)) {
				diagnosen = b.getDiagnosen();
			}
			if (actDate.set(b.getDatum()) == false) {
				result =
					result.add(Result.SEVERITY.ERROR, 5, "Ungültiges Datum bei Konsultation "
						+ b.getLabel(), ret, true);
				continue;
			}
			if (actDate.isBefore(startDate)) {
				startDate.set(actDate);
			}
			if (actDate.isAfter(endDate)) {
				endDate.set(actDate);
			}
			List<Verrechnet> lstg = b.getLeistungen();
			
			for (Verrechnet l : lstg) {
				Money sz = l.getNettoPreis().multiply(l.getZahl());
				summe.addMoney(sz);
			}
		}
		if (f == null) {
			result =
				result.add(Result.SEVERITY.ERROR, 8, "Die Rechnung hat keinen gültigen Fall ("
					+ getRnDesc(ret) + ")", ret, true);
			// garant=Hub.actMandant;
		} else {
			if (Hub.userCfg.get(Leistungscodes.BILLING_STRICT, true) && !f.isValid()) {
				result =
					result.add(Result.SEVERITY.ERROR, 8, "Die Rechnung hat keinen gültigen Fall ("
						+ getRnDesc(ret) + ")", ret, true);
			}
			// garant=f.getGarant();
			
		}
		
		// check if there are any Konsultationen
		if (Hub.userCfg.get(Leistungscodes.BILLING_STRICT, true)) {
			if ((diagnosen == null) || (diagnosen.size() == 0)) {
				result =
					result.add(Result.SEVERITY.ERROR, 6, "Die Rechnung enthält keine Diagnose ("
						+ getRnDesc(ret) + ")", ret, true);
			}
		}
		/*
		 * if(garant==null || !garant.isValid()){ result=result.add(Log.ERRORS,7,"Die Rechnung hat
		 * keinen Garanten ("+getRnDesc(ret)+")",ret,true); }
		 */

		String Datum = new TimeTool().toString(TimeTool.DATE_GER);
		ret.set(BILL_DATE_FROM, startDate.toString(TimeTool.DATE_GER));
		ret.set(BILL_DATE_UNTIL, endDate.toString(TimeTool.DATE_GER));
		ret.set(BILL_DATE, Datum);
		ret.setStatus(RnStatus.OFFEN);
		// summe.roundTo5();
		ret.set(BILL_AMOUNT_CENTS, summe.getCentsAsString());
		// ret.setExtInfo("Rundungsdifferenz", summe.getFracAsString());
		String nr = getNextRnNummer();
		ret.set(BILL_NUMBER, nr);
		if (!result.isOK()) {
			ret.delete();
			return result;
		}
		
		for (Konsultation b : behandlungen) {
			b.setRechnung(ret);
		}
		if (ret.getOffenerBetrag().isZero()) {
			ret.setStatus(RnStatus.BEZAHLT);
		} else {
			new AccountTransaction(f.getPatient(), ret, summe.negate(), Datum, "Rn " + nr
				+ " erstellt.");
		}
		
		return result.add(Result.SEVERITY.OK, 0, "OK", ret, false);
	}
	
	private static String getRnDesc(final Rechnung rn){
		StringBuilder sb = new StringBuilder();
		if (rn == null) {
			sb.append("Keine Rechnungsnummer");
		} else {
			Fall fall = rn.getFall();
			sb.append("Rechnung: " + rn.getNr()).append(" / ");
			if (fall == null) {
				sb.append("Kein Fall");
			} else {
				sb.append("Fall: " + fall.getLabel()).append(" / ");
				Patient pat = fall.getPatient();
				if (pat == null) {
					sb.append("Kein Patient");
				} else {
					sb.append(pat.getLabel());
				}
			}
		}
		return sb.toString();
	}
	
	/** Die Rechnungsnummer holen */
	public String getNr(){
		return get(BILL_NUMBER);
	}
	
	/** Den Fall dieser Rechnung holen */
	public Fall getFall(){
		return Fall.load(get(CASE_ID));
	}
	
	/** Den Mandanten zu dieser Rechnung holen */
	public Mandant getMandant(){
		return Mandant.load(get(MANDATOR_ID));
	}
	
	/** Eine Liste aller Konsultationen dieser Rechnung holen */
	public List<Konsultation> getKonsultationen(){
		Query<Konsultation> qbe = new Query<Konsultation>(Konsultation.class);
		qbe.add("RechnungsID", "=", getId());
		qbe.orderBy(false, new String[] {
			"Datum"
		});
		return qbe.execute();
	}
	
	/**
	 * Rechnung stornieren. Allenfalls bereits erfolgte Zahlungen für diese Rechnungen bleiben
	 * verbucht (das Konto weist dann einen Plus-Saldo auf). Der Rechnungsbetrag wird per
	 * Stornobuchung gutgeschrieben.
	 * 
	 * @param reopen
	 *            wenn True werden die in dieser Rechnung enthaltenen Behandlungen wieder
	 *            freigegeben, andernfalls bleiben sie abgeschlossen.
	 */
	public void storno(final boolean reopen){
		Money betrag = getBetrag();
		new Zahlung(this, betrag, "Storno", null);
		if (reopen == true) {
			Query<Konsultation> qbe = new Query<Konsultation>(Konsultation.class);
			qbe.add(Konsultation.FLD_BILL_ID, Query.EQUALS, getId());
			for (Konsultation k : qbe.execute()) {
				k.set(Konsultation.FLD_BILL_ID, null);
			}
			/*
			 * getConnection().exec( "UPDATE BEHANDLUNGEN SET RECHNUNGSID=NULL WHERE RECHNUNGSID=" +
			 * getWrappedId());
			 */
			setStatus(RnStatus.STORNIERT);
		} else {
			
			setStatus(RnStatus.ABGESCHRIEBEN);
		}
	}
	
	/** Datum der Rechnung holen */
	public String getDatumRn(){
		return get(BILL_DATE);
	}
	
	/** Datum der ersten Konsultation dieser Rechnung holen */
	public String getDatumVon(){
		return get(BILL_DATE_FROM);
	}
	
	/** Datum der letzten Konsultation dieser Rechnung holen */
	public String getDatumBis(){
		String raw = get(BILL_DATE_UNTIL);
		return raw == null ? StringTool.leer : raw.trim();
	}
	
	/** Totalen Rechnungsbetrag holen */
	public Money getBetrag(){
		int raw = checkZero(get(BILL_AMOUNT_CENTS));
		return new Money(raw);
	}
	
	/**
	 * Since different ouputters can use different rules for rounding, the sum of the bill that an
	 * outputter created might be different from the sum, the Rechnung#build method calculated. So
	 * an outputter should always use setBetrag to correct the final amount. If the difference
	 * between the internal calculated amount and the outputter's result is more than 5 currency
	 * units or more than 2% of the sum, this method will return false an will not set the new
	 * value. Otherwise, the new value will be set, the account will be adjusted and the method
	 * returns true
	 * 
	 * @param betrag
	 *            new new sum
	 * @return true on success
	 */
	public boolean setBetrag(final Money betrag){
		// use absolute value to fix earlier bug
		
		int oldVal = Math.abs(checkZero(get(BILL_AMOUNT_CENTS)));
		if (oldVal != 0) {
			int newVal = betrag.getCents();
			int diff = Math.abs(oldVal - newVal);
			
			if ((diff > 500) || ((diff * 50) > oldVal)) {
				Money old = new Money(oldVal);
				String nr = checkNull(get(BILL_NUMBER));
				String message =
					"Der errechnete Rechnungsbetrag (" + betrag.getAmountAsString()
						+ ") weicht vom Rechnungsbetrag (" + old.getAmountAsString()
						+ ") ab. Trotzdem weriterfahren?";
				if (!MessageDialog.openConfirm(Desk.getTopShell(), "Differenz bei der Rechnung "
					+ nr, message)) {
					return false;
				}
			}
			Query<AccountTransaction> qa = new Query<AccountTransaction>(AccountTransaction.class);
			qa.add(AccountTransaction.FLD_BILL_ID, Query.EQUALS, getId());
			qa.add(AccountTransaction.FLD_PAYMENT_ID, StringTool.leer, null);
			List<AccountTransaction> as = qa.execute();
			if ((as != null) && (as.size() == 1)) {
				AccountTransaction at = as.get(0);
				if (at.exists()) {
					Money negBetrag = new Money(betrag);
					negBetrag.negate();
					at.set(AccountTransaction.FLD_AMOUNT, negBetrag.getCentsAsString());
				}
			}
		}
		set(BILL_AMOUNT_CENTS, betrag.getCentsAsString());
		
		return true;
	}
	
	/** Offenen Betrag in Rappen/cents holen */
	public Money getOffenerBetrag(){
		List<Zahlung> lz = getZahlungen();
		// String betr=getBetrag();
		Money total = getBetrag();
		for (Zahlung z : lz) {
			Money abzahlung = z.getBetrag();
			total.subtractMoney(abzahlung);
		}
		return new Money(total);
	}
	
	/**
	 * Bereits bezahlten Betrag holen. Es werden nur positive Wert (Zahlungen) addiert, negative
	 * Werte (Gebühren) werden übergangen.
	 */
	public Money getAnzahlung(){
		List<Zahlung> lz = getZahlungen();
		Money total = new Money();
		for (Zahlung z : lz) {
			Money abzahlung = z.getBetrag();
			if (!abzahlung.isNegative()) {
				total.addMoney(abzahlung);
			}
		}
		return total;
	}
	
	/** Rechnungsstatus holen */
	public int getStatus(){
		try {
			int i = Integer.parseInt(checkNull(get(BILL_STATE)));
			if ((i < 0) || (i >= RnStatus.getStatusTexts().length)) {
				return RnStatus.UNBEKANNT;
			}
			return i;
		} catch (NumberFormatException e) {
			return RnStatus.UNBEKANNT;
		}
	}
	
	/** Rechnungsstatus setzen */
	public void setStatus(final int stat){
		set(BILL_STATE, Integer.toString(stat));
		set(BILL_STATE_DATE, new TimeTool().toString(TimeTool.DATE_GER));
		addTrace(STATUS_CHANGED, Integer.toString(stat));
	}
	
	/** Eine Zahlung zufügen */
	public void addZahlung(final Money betrag, final String text, TimeTool date){
		if (betrag.isZero()) {
			return;
		}
		Money oldOffen = getOffenerBetrag();
		int oldOffenCents = oldOffen.getCents();
		Money newOffen = new Money(oldOffen);
		newOffen.subtractMoney(betrag);
		if (newOffen.isNeglectable()) {
			setStatus(RnStatus.BEZAHLT);
		} else if (newOffen.isNegative()) {
			setStatus(RnStatus.ZUVIEL_BEZAHLT);
		} else if (newOffen.equals(getBetrag())) {
			// if the remainder is equal to the total, it was probably a negative payment -> storno
			// So what might be the state of the bill after this payment?
			// It cannot simply be "OFFEN", because the bill was (almost sure) printed already
			// it cannot simply be OFFEN UND GEDRUCKT, beacuse it might have been 3. MAHNUNG
			// GEDRUCKT already.
			// So probably it's best to use the same state as it was before the last positive
			// payment ?
			// thus let's check the last few states.
			List<String> zahlungen = getTrace(STATUS_CHANGED);
			if (zahlungen.size() < 2) {
				setStatus(RnStatus.OFFEN_UND_GEDRUCKT);
			} else {
				// status description is of the form "11.01.2008, 09:16:42: 15"
				String statusDescription = zahlungen.get(zahlungen.size() - 2);
				Matcher matcher = Pattern.compile(".*:\\s*(\\d+)").matcher(statusDescription);
				if (matcher.matches()) {
					String prevStatus = matcher.group(1);
					int newStatus = Integer.parseInt(prevStatus);
					setStatus(newStatus);
				} else {
					// we couldn't find the previous status, do nothing
				}
			}
		} else if (newOffen.getCents() < oldOffenCents) {
			setStatus(RnStatus.TEILZAHLUNG);
		}
		new Zahlung(this, betrag, text, date);
	}
	
	/** EIne Liste aller Zahlungen holen */
	public List<Zahlung> getZahlungen(){
		List<String> ids = getList("Zahlungen", false);
		ArrayList<Zahlung> ret = new ArrayList<Zahlung>();
		for (String id : ids) {
			Zahlung z = Zahlung.load(id);
			ret.add(z);
		}
		return ret;
	}
	
	public String getBemerkung(){
		return getExtInfo(REMARK);
	}
	
	public void setBemerkung(final String bem){
		setExtInfo(REMARK, bem);
	}
	
	public String getExtInfo(final String key){
		Hashtable<String, String> ext = loadExtension();
		String ret = ext.get(key);
		return checkNull(ret);
	}
	
	public void setExtInfo(final String key, final String value){
		Hashtable<String, String> ext = loadExtension();
		ext.put(key, value);
		flushExtension(ext);
	}
	
	/**
	 * EIn Trace-Eintrag ist eine Notiz über den Verlauf. (Z.B. Statusänderungen, Zahlungen,
	 * Rückbuchungen etc.)
	 * 
	 * @param name
	 *            Name des Eintragstyps
	 * @param text
	 *            Text zum Eintrag
	 */
	@SuppressWarnings("unchecked")
	public void addTrace(final String name, final String text){
		Hashtable hash = loadExtension();
		byte[] raw = (byte[]) hash.get(name);
		List<String> trace = null;
		if (raw != null) {
			trace = StringTool.unpack(raw);
		}
		if (trace == null) {
			trace = new ArrayList<String>();
		}
		trace.add(new TimeTool().toString(TimeTool.FULL_GER) + ": " + text);
		hash.put(name, StringTool.pack(trace));
		flushExtension(hash);
	}
	
	/**
	 * ALle Einträge zu einem bestimmten Eintragstyp holen
	 * 
	 * @param name
	 *            Name des Eintragstyps (z.B. "Zahlungen")
	 * @return eine List<String>, welche leer sein kann
	 */
	@SuppressWarnings("unchecked")
	public List<String> getTrace(final String name){
		Hashtable hash = loadExtension();
		byte[] raw = (byte[]) hash.get(name);
		List<String> trace = null;
		if (raw != null) {
			trace = StringTool.unpack(raw);
		}
		if (trace == null) {
			trace = new ArrayList<String>();
		}
		return trace;
	}
	
	public String getRnDatumFrist(){
		String stat = get(BILL_STATE_DATE);
		int frist = 0;
		switch (getStatus()) {
		case RnStatus.OFFEN_UND_GEDRUCKT:
			frist = Hub.mandantCfg.get(PreferenceConstants.RNN_DAYSUNTIL1ST, 30);
			break;
		case RnStatus.MAHNUNG_1_GEDRUCKT:
			frist = Hub.mandantCfg.get(PreferenceConstants.RNN_DAYSUNTIL2ND, 10);
			break;
		case RnStatus.MAHNUNG_2_GEDRUCKT:
			frist = Hub.mandantCfg.get(PreferenceConstants.RNN_DAYSUNTIL3RD, 10);
			break;
		}
		TimeTool tm = new TimeTool(stat);
		tm.add(TimeTool.DAY_OF_MONTH, frist);
		return tm.toString(TimeTool.DATE_GER);
	}
	
	/**
	 * Mark bill as rejected
	 */
	public void reject(final RnStatus.REJECTCODE reason, final String text){
		setStatus(RnStatus.FEHLERHAFT);
		addTrace(REJECTED, reason.toString() + ", " + text);
	}
	
	@SuppressWarnings("unchecked")
	// TODO weird
	public Hashtable<String, String> loadExtension(){
		return (Hashtable<String,String>)getMap(FLD_EXTINFO);
	}
	
	@SuppressWarnings("unchecked")
	public void flushExtension(final Hashtable ext){
		setMap(FLD_EXTINFO, ext);
	}
	
	public static Rechnung load(final String id){
		Rechnung ret = new Rechnung(id);
		if (ret.exists()) {
			return ret;
		}
		return null;
	}
	
	/**
	 * Eien Rechnung anhand ihrer Nummer holen
	 * 
	 * @param Rnnr
	 *            die Rechnungsnummer
	 * @return die Rechnung mit dieser Nummer oder Null, wenn keine Rechnung mit dieser Nummer
	 *         existiert.
	 */
	public static Rechnung getFromNr(final String Rnnr){
		String id = new Query<Rechnung>(Rechnung.class).findSingle(BILL_NUMBER, Query.EQUALS, Rnnr);
		Rechnung ret = load(id);
		if (ret.isValid()) {
			return ret;
		} else {
			return null;
		}
	}
	
	/** Die nächste Rechnungsnummer holen. */
	public static String getNextRnNummer(){
		JdbcLink j = getConnection();
		Stm stm = j.getStatement();
		String nr = null;
		while (true) {
			// Zugriff sperren
			String lockid = PersistentObject.lock("RechnungsNummer", true);
			// letzte Nummer holen
			String pid = j.queryString("SELECT WERT FROM CONFIG WHERE PARAM='RechnungsNr'");
			// ggf. Initialisieren
			if (StringTool.isNothing(pid)) {
				pid = "0";
				j.exec("INSERT INTO CONFIG (PARAM,WERT) VALUES ('RechnungsNr','0')");
			}
			// hochzählen
			int lastNum = Integer.parseInt(pid) + 1;
			nr = Integer.toString(lastNum);
			// neue Höchstzahl speichern
			j.exec("UPDATE CONFIG SET WERT='" + nr + "' WHERE PARAM='RechnungsNr'");
			// Sperre lösen
			PersistentObject.unlock("RechnungsNummer", lockid);
			// Nochmal vergewissern, dass diese Nummer wirklich noch nicht existiert, sonst nächste
			// Nummer holen
			String exists =
				j.queryString("SELECT ID FROM RECHNUNGEN WHERE RnNummer=" + JdbcLink.wrap(nr));
			if (exists == null) {
				break;
			}
		}
		j.releaseStatement(stm);
		return nr;
	}
	
	protected Rechnung(){ /* leer */}
	
	protected Rechnung(final String id){
		super(id);
	}
	
	@Override
	public boolean delete(){
		for (Zahlung z : getZahlungen()) {
			z.set(Zahlung.BILL_ID, StringTool.leer); // avoid log entries
			z.delete();
			z.set(Zahlung.BILL_ID, getId());
		}
		return super.delete();
	}
	
	@Override
	public String getLabel(){
		StringBuilder sb = new StringBuilder();
		sb.append(getNr()).append(" ").append(getDatumRn());
		Fall fall = getFall();
		if ((fall != null) && fall.exists()) {
			sb.append(": ").append(fall.getPatient().getLabel()).append(" ");
		}
		sb.append(getBetrag());
		return sb.toString();
	}
	
	/**
	 * Eine einfache eindeutige ID für die Rechnung liefern (Aus PatNr. und RnNr)
	 * 
	 * @return
	 */
	public String getRnId(){
		Patient p = getFall().getPatient();
		String pid;
		if (Hub.globalCfg.get("PatIDMode", "number").equals("number")) {
			pid = StringTool.pad(StringTool.LEFT, '0', p.getPatCode(), 6);
		} else {
			pid = new TimeTool(p.getGeburtsdatum()).toString(TimeTool.DATE_COMPACT);
		}
		String nr = StringTool.pad(StringTool.LEFT, '0', getNr(), 6);
		return pid + nr;
	}
	
	/**
	 * Retrieve the state a bill had at a given moment
	 * 
	 * @param date
	 *            the time to consider
	 * @return the Status the bill had at this moment
	 */
	public int getStatusAtDate(TimeTool date){
		List<String> trace = getTrace(Rechnung.STATUS_CHANGED);
		int ret = getStatus();
		TimeTool tt = new TimeTool();
		for (String s : trace) {
			String[] stm = s.split("\\s*:\\s");
			if (tt.set(stm[0])) {
				if (tt.isBefore(date)) {
					ret = Integer.parseInt(stm[1]);
				}
			}
		}
		return ret;
	}
	
	@Override
	protected String getTableName(){
		return TABLENAME;
	}
	
}
