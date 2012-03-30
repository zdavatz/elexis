/*******************************************************************************
 * Copyright (c) 2005-2010, G. Weirich and Elexis
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

package ch.elexis.data;

import java.io.File;
import java.io.FileInputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.IFilter;

import ch.elexis.Desk;
import ch.elexis.util.IOptifier;
import ch.elexis.util.PlatformHelper;
import ch.elexis.util.SWTHelper;
import ch.elexis.views.TarmedDetailDialog;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;
import ch.rgw.tools.VersionInfo;
import ch.rgw.tools.JdbcLink.Stm;

/**
 * Implementation des Tarmed-Systems. Besteht aus den eigentlichen Leistungen, statischen Methoden
 * zum auslesen der Textformen der einzelnen Codes, einem Validator und einem Mandantenfilter.
 * 
 * @author gerry
 * 
 */
public class TarmedLeistung extends VerrechenbarAdapter {
	private static final String FLD_GUELTIG_BIS = "GueltigBis";
	private static final String FLD_GUELTIG_VON = "GueltigVon";
	private static final String FLD_TP_TL = "TP_TL";
	private static final String FLD_TP_AL = "TP_AL";
	private static final String FLD_SPARTE = "Sparte";
	private static final String FLD_DIGNI_QUANTI = "DigniQuanti";
	private static final String FLD_DIGNI_QUALI = "DigniQuali";
	public static final String FLD_TEXT = "Text";
	public static final String FLD_NICK = "Nick";
	public static final String XIDDOMAIN = "www.xid.ch/id/tarmedsuisse";
	Hashtable<String, String> ext;
	private static final String VERSION_000 = "0.0.0";
	private static final String VERSION_110 = "1.1.0";
	private static final String VERSION_111 = "1.1.1";
	public static final TarmedComparator tarmedComparator;
	public static final TarmedOptifier tarmedOptifier;
	public static final TimeTool INFINITE = new TimeTool("19991231");
	public static final String SIDE = "Seite";
	public static final String PFLICHTLEISTUNG = "obligation";
	private static final String upd110 = "ALTER TABLE TARMED ADD lastupdate BIGINT";
	
	private static final JdbcLink j = getConnection();
	static {
		TarmedLeistung version = load("Version");
		if (!version.exists()) {
			String filepath =
				PlatformHelper.getBasePath("ch.elexis.arzttarife_ch") + File.separator
					+ "createDB.script";
			Stm stm = j.getStatement();
			try {
				FileInputStream fis = new FileInputStream(filepath);
				stm.execScript(fis, true, true);
			} catch (Exception e) {
				ExHandler.handle(e);
				SWTHelper.showError("Kann Tarmed-Datenbank nicht erstellen",
					"create-Script nicht gefunden in " + filepath);
			} finally {
				j.releaseStatement(stm);
			}
			
		}
		addMapping("TARMED", "Parent", FLD_DIGNI_QUALI, FLD_DIGNI_QUANTI, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			FLD_SPARTE, "Text=tx255", "Name=tx255", "Nick=Nickname", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			"GueltigVon=S:D:GueltigVon", "GueltigBis=S:D:GueltigBis" //$NON-NLS-1$ //$NON-NLS-2$
		);
		TarmedLeistung tlv = TarmedLeistung.load("Version");
		if (!tlv.exists()) {
			tlv = new TarmedLeistung();
			tlv.create("Version");
		}
		VersionInfo vi = new VersionInfo(tlv.get(FLD_NICK));
		if (!tlv.exists() || vi.isOlder(VERSION_110)) {
			createOrModifyTable(upd110);
			tlv.set(FLD_NICK, VERSION_110);
		}
		if (vi.isOlder(VERSION_111)) {
			createOrModifyTable("Update TARMED set gueltigbis='20993112' where id='39.0305'");
			tlv.set(FLD_NICK, VERSION_111);
		}
		tarmedComparator = new TarmedComparator();
		tarmedOptifier = new TarmedOptifier();
		Xid.localRegisterXIDDomainIfNotExists(XIDDOMAIN, "Tarmed", Xid.ASSIGNMENT_LOCAL);
	}
	
	public String getXidDomain(){
		return XIDDOMAIN;
	}
	
	/** Text zu einem Code der qualitativen Dignität holen */
	public static String getTextForDigniQuali(final String dql){
		if (dql == null) {
			return ""; //$NON-NLS-1$
		}
		return checkNull(j
			.queryString("SELECT titel FROM TARMED_DEFINITIONEN WHERE SPALTE='DIGNI_QUALI' AND KUERZEL=" + JdbcLink.wrap(dql))); //$NON-NLS-1$
	}
	
	/** Kurz-Code für eine qualitative Dignität holen */
	public static String getCodeForDigniQuali(final String kurz){
		if (kurz == null) {
			return ""; //$NON-NLS-1$
		}
		return checkNull(j
			.queryString("SELECT KUERZEL FROM TARMED_DEFINITIONEN WHERE SPALTE='DIGNI_QUALI' AND TITEL=" + JdbcLink.wrap(kurz))); //$NON-NLS-1$
	}
	
	/** Text für einen Code für quantitative Dignität holen */
	public static String getTextForDigniQuanti(final String dqn){
		if (dqn == null) {
			return ""; //$NON-NLS-1$
		}
		return checkNull(j
			.queryString("SELECT titel FROM TARMED_DEFINITIONEN WHERE SPALTE='DIGNI_QUANTI' AND KUERZEL=" + JdbcLink.wrap(dqn))); //$NON-NLS-1$
	}
	
	/** Text für einen Sparten-Code holen */
	public static String getTextForSparte(final String sparte){
		if (sparte == null) {
			return ""; //$NON-NLS-1$
		}
		return checkNull(j
			.queryString("SELECT titel FROM TARMED_DEFINITIONEN WHERE SPALTE='SPARTE' AND KUERZEL=" + JdbcLink.wrap(sparte))); //$NON-NLS-1$
	}
	
	/** Text für eine Anästhesie-Risikoklasse holen */
	public static String getTextForRisikoKlasse(final String klasse){
		if (klasse == null) {
			return ""; //$NON-NLS-1$
		}
		return checkNull(j
			.queryString("SELECT titel FROM TARMED_DEFINITIONEN WHERE SPALTE='ANAESTHESIE' AND KUERZEL=" + JdbcLink.wrap(klasse))); //$NON-NLS-1$
	}
	
	/** Text für einen ZR_EINHEIT-Code holen (Sitzung, Monat usw.) */
	public static String getTextForZR_Einheit(final String einheit){
		if (einheit == null) {
			return ""; //$NON-NLS-1$
		}
		return checkNull(j
			.queryString("SELECT titel FROM TARMED_DEFINITIONEN WHERE SPALTE='ZR_EINHEIT' AND KUERZEL=" + JdbcLink.wrap(einheit))); //$NON-NLS-1$
	}
	
	/** Alle Codes für Quantitative Dignität holen */
	public static String[] getDigniQuantiCodes(){
		return null;
	}
	
	/** Konstruktor wird nur vom Importer gebraucht */
	public TarmedLeistung(final String code, final String parent, final String DigniQuali,
		final String DigniQuanti, final String sparte){
		create(code);
		j.exec("INSERT INTO TARMED_EXTENSION (CODE) VALUES (" + getWrappedId() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
		set(
			new String[] {
				"Parent", FLD_DIGNI_QUALI, FLD_DIGNI_QUANTI, FLD_SPARTE}, parent, DigniQuali, DigniQuanti, sparte); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}
	
	/*
	 * public String[] getDisplayedFields(){ return new String[] { "ID", "Text"}; //$NON-NLS-1$
	 * //$NON-NLS-2$ }
	 */

	@Override
	public String getLabel(){
		return getId() + " " + getText(); //$NON-NLS-1$
	}
	
	@Override
	protected String getTableName(){
		return "TARMED"; //$NON-NLS-1$
	}
	
	/** Code liefern */
	@Override
	public String getCode(){
		return getId();
	}
	
	/** Text liefern */
	@Override
	public String getText(){
		return get(FLD_TEXT); //$NON-NLS-1$
	}
	
	/** Text setzen (wird nur vom Importer gebraucht */
	public void setText(final String tx){
		set(FLD_TEXT, tx); //$NON-NLS-1$
	}
	
	/** Erweiterte Informationen laden */
	@SuppressWarnings("unchecked")
	public Hashtable<String, String> loadExtension(){
		Stm stm = j.getStatement();
		ResultSet res =
			stm.query("SELECT limits FROM TARMED_EXTENSION WHERE CODE=" + getWrappedId()); //$NON-NLS-1$
		try {
			if (res.next()) {
				byte[] in = res.getBytes(1);
				if ((in == null) || (in.length == 0)) {
					ext = new Hashtable<String, String>();
				} else {
					ext = StringTool.fold(in, StringTool.GUESS, null);
				}
			}
		} catch (Exception ex) {
			ExHandler.handle(ex);
			ext = new Hashtable<String, String>();
		} finally {
			j.releaseStatement(stm);
			
		}
		return ext;
	}
	
	/** Erweiterte Informationen rückspeichern */
	public void flushExtension(){
		if (ext != null) {
			byte[] flat = StringTool.flatten(ext, StringTool.ZIP, null);
			PreparedStatement preps =
				j
					.prepareStatement("UPDATE TARMED_EXTENSION SET limits=? WHERE CODE=" + getWrappedId()); //$NON-NLS-1$
			try {
				preps.setBytes(1, flat);
				preps.execute();
			} catch (Exception ex) {
				ExHandler.handle(ex);
			}
		}
	}
	
	/** Medizinische Interpretation auslesen */
	public String getMedInterpretation(){
		return checkNull(j
			.queryString("SELECT med_interpret FROM TARMED_EXTENSION WHERE CODE=" + getWrappedId())); //$NON-NLS-1$
	}
	
	/** Medizinische Interpretation setzen (Wird nur vom Importer gebraucht) */
	public void setMedInterpretation(final String text){
		j
			.exec("UPDATE TARMED_EXTENSION SET med_interpret=" + JdbcLink.wrap(text) + " WHERE CODE=" + getWrappedId()); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/** Technische Interpretation auslesen */
	public String getTechInterpretation(){
		return checkNull(j
			.queryString("SELECT tech_interpret FROM TARMED_EXTENSION WHERE CODE=" + getWrappedId())); //$NON-NLS-1$
	}
	
	/** Technische Intepretation setzen (Wird nur vom Importer gebraucht */
	public void setTechInterpretation(final String text){
		j
			.exec("UPDATE TARMED_EXTENSION SET tech_interpret=" + JdbcLink.wrap(text) + " WHERE CODE=" + getWrappedId()); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/** Qualitative Dignität holen (als code) */
	public String getDigniQuali(){
		return checkNull(get(FLD_DIGNI_QUALI)); //$NON-NLS-1$
	}
	
	/** Qualitative Dignität als Text holen */
	public String getDigniQualiAsText(){
		return checkNull(getTextForDigniQuali(get(FLD_DIGNI_QUALI))); //$NON-NLS-1$
	}
	
	/** Qualitative Dinität setzen (Wird nur vom Importer gebraucht) */
	public void setDigniQuali(final String dql){
		set(FLD_DIGNI_QUALI, dql); //$NON-NLS-1$
	}
	
	/** Quantitative Dignität als code holen */
	public String getDigniQuanti(){
		return checkNull(get(FLD_DIGNI_QUANTI)); //$NON-NLS-1$
	}
	
	/** Quantitative Dignität als Text holen */
	public String getDigniQuantiAsText(){
		return checkNull(getTextForDigniQuanti(get(FLD_DIGNI_QUANTI))); //$NON-NLS-1$
	}
	
	/** Sparte holen (als Code) */
	public String getSparte(){
		return checkNull(get(FLD_SPARTE)); //$NON-NLS-1$
	}
	
	/** Sparte als Text holen */
	public String getSparteAsText(){
		return checkNull(getTextForSparte(get(FLD_SPARTE))); //$NON-NLS-1$
	}
	
	/** Name des verwendeten Codesystems holen (liefert immer "Tarmed") */
	@Override
	public String getCodeSystemName(){
		return "Tarmed"; //$NON-NLS-1$
	}
	
	protected TarmedLeistung(final String id){
		super(id);
	}
	
	public TarmedLeistung(){/* leer */
	}
	
	/** Eine Position einlesen */
	public static TarmedLeistung load(final String id){
		return new TarmedLeistung(id);
	}
	
	/** Eine Position vom code einlesen */
	public static IVerrechenbar getFromCode(final String code){
		return new TarmedLeistung(code);
	}
	
	/**
	 * Konfigurierbarer Filter für die Anzeige des Tarmed-Codebaums in Abhängigkeit vom gewählten
	 * Mandanten (Nur zur Dignität passende Einträge anzeigen)
	 * 
	 * @author gerry
	 */
	
	public static class MandantFilter implements IFilter {
		
		MandantFilter(final Mandant m){

		}
		
		public boolean select(final Object object){
			if (object instanceof TarmedLeistung) {
				/* TarmedLeistung tl = (TarmedLeistung) object; */
				return true;
			}
			return false;
		}
		
	}
	
	/**
	 * Komparator zum Sortieren der Codes. Es wird einfach nach Codeziffer sortiert. Wirft eine
	 * ClassCastException, wenn die Objekte nicht TarmedLeistungen sind.
	 * 
	 * @author gerry
	 */
	static class TarmedComparator implements Comparator {
		
		public int compare(final Object o1, final Object o2){
			TarmedLeistung tl1 = (TarmedLeistung) o1;
			TarmedLeistung tl2 = (TarmedLeistung) o2;
			return tl1.getCode().compareTo(tl2.getCode());
		}
		
	}
	
	@Override
	public IOptifier getOptifier(){
		return tarmedOptifier;
	}
	
	@Override
	public Comparator getComparator(){
		return tarmedComparator;
	}
	
	@Override
	public IFilter getFilter(final Mandant m){
		return new MandantFilter(m);
	}
	
	@Override
	public boolean isDragOK(){
		return (!StringTool.isNothing(getDigniQuali().trim()));
	}
	
	public int getAL(){
		loadExtension();
		return (int) Math.round(checkZeroDouble(ext.get(FLD_TP_AL)) * 100); //$NON-NLS-1$
	}
	
	public int getTL(){
		loadExtension();
		return (int) Math.round(checkZeroDouble(ext.get(FLD_TP_TL)) * 100); //$NON-NLS-1$
	}
	
	/**
	 * Preis der Leistung in Rappen public int getPreis(TimeTool date, String subgroup) {
	 * loadExtension(); String t=ext.get("TP_TL"); String a=ext.get("TP_AL"); double tl=0.0; double
	 * al=0.0; try{ tl= (t==null) ? 0.0 : Double.parseDouble(t); }catch(NumberFormatException ex){
	 * tl=0.0; } try{ al= (a==null) ? 0.0 : Double.parseDouble(a); }catch(NumberFormatException ex){
	 * al=0.0; } double tp=getVKMultiplikator(date, subgroup)*100; return
	 * (int)Math.round((tl+al)*tp); }
	 */
	@Override
	public int getMinutes(){
		loadExtension();
		double min = checkZeroDouble(ext.get("LSTGIMES_MIN")); //$NON-NLS-1$
		min += checkZeroDouble(ext.get("VBNB_MIN")); //$NON-NLS-1$
		min += checkZeroDouble(ext.get("BEFUND_MIN")); //$NON-NLS-1$
		min += checkZeroDouble(ext.get("WECHSEL_MIN")); //$NON-NLS-1$
		return (int) Math.round(min);
	}
	
	public String getExclusion(){
		loadExtension();
		return checkNull(ext.get("exclusion")); //$NON-NLS-1$
	}
	
	public int getTP(final TimeTool date, final Fall fall){
		loadExtension();
		String t = ext.get(FLD_TP_TL); //$NON-NLS-1$
		String a = ext.get(FLD_TP_AL); //$NON-NLS-1$
		double tl = 0.0;
		double al = 0.0;
		try {
			tl = (t == null) ? 0.0 : Double.parseDouble(t);
		} catch (NumberFormatException ex) {
			tl = 0.0;
		}
		try {
			al = (a == null) ? 0.0 : Double.parseDouble(a);
		} catch (NumberFormatException ex) {
			al = 0.0;
		}
		return (int) Math.round((tl + al) * 100.0);
	}
	
	public double getFactor(final TimeTool date, final Fall fall){
		return getVKMultiplikator(date, fall);
	}
	
	/**
	 * Returns the GueltigVon value
	 * 
	 * @return the GueltigVon value as a TimeTool object, or null if the value is not defined
	 */
	public TimeTool getGueltigVon(){
		String value = get(FLD_GUELTIG_VON);
		if (!StringTool.isNothing(value)) {
			return new TimeTool(value);
		} else {
			return null;
		}
	}
	
	/**
	 * Returns the GueltigBis value
	 * 
	 * @return the GueltigBis value as a TimeTool object, or null if the value is not defined
	 */
	public TimeTool getGueltigBis(){
		String value = get(FLD_GUELTIG_BIS);
		if (!StringTool.isNothing(value)) {
			return new TimeTool(value);
		} else {
			return null;
		}
	}
	
	@Override
	public List<IAction> getActions(final Verrechnet kontext){
		List<IAction> ret = super.getActions(kontext);
		if (kontext != null) {
			ret.add(new Action("Details") {
				@Override
				public void run(){
					new TarmedDetailDialog(Desk.getTopShell(), kontext).open();
				}
			});
		}
		return ret;
	}
	
	public static boolean isObligation(Verrechnet v){
		IVerrechenbar vv = v.getVerrechenbar();
		if (vv instanceof TarmedLeistung) {
			String obli = v.getDetail(PFLICHTLEISTUNG);
			if ((obli == null) || (Boolean.parseBoolean(obli))) {
				return true;
			}
		}
		return false;
	}
	
	public static String getSide(Verrechnet v){
		IVerrechenbar vv = v.getVerrechenbar();
		if (vv instanceof TarmedLeistung) {
			String side = v.getDetail(SIDE);
			if ("l".equalsIgnoreCase(side)) {
				return "left";
			} else if ("r".equalsIgnoreCase(side)) {
				return "right";
			}
		}
		return "none";
	}

	@Override
	public VatInfo getVatInfo(){
		// TarmedLeistung is a treatment per default
		return VatInfo.VAT_CH_ISTREATMENT;
	}
}
