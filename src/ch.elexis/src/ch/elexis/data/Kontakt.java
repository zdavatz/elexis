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
 *    $Id$
 *******************************************************************************/

package ch.elexis.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import ch.elexis.StringConstants;
import ch.elexis.util.MFUList;
import ch.rgw.tools.StringTool;

/**
 * Ein Kontakt ist der kleinste gemeinsame Nenner anller Arten von Menschen und Institutionen und
 * somit die Basisklasse für alle Kontakte. Ein Kontakt hat eine Anschrift und beliebig viele
 * zusätzliche Bezugsadressen, sowie Telefon, E-Mail und Website. Zu einem Kontakt können ausserdem
 * Reminders erstellt werden. Schliesslich hat jeder Kontakt noch einen "Infostore", einen im
 * Prinzip unbegrenzt grossen Speicher für beliebig viele parameter=wert - Paare, wo Informationen
 * aller Art abgelegt werden können. Jedem Element des Infostores können Zugriffsrechte zugeteilt
 * werden, die definieren, wer dieses Element lesen und Schreiben darf.
 * 
 * @author gerry
 * 
 */
public class Kontakt extends PersistentObject {
	// public static final String FLD_KUERZEL = "Kuerzel";
	public static final String FLD_E_MAIL = "E-Mail";
	public static final String FLD_WEBSITE = "Website";
	public static final String FLD_MOBILEPHONE = "NatelNr";
	public static final String FLD_FAX = "Fax";
	public static final String FLD_IS_LAB = "istLabor"; //$NON-NLS-1$
	public static final String FLD_IS_MANDATOR = "istMandant"; //$NON-NLS-1$
	public static final String FLD_IS_USER = "istAnwender"; //$NON-NLS-1$
	public static final String FLD_SHORT_LABEL = "Kuerzel"; //$NON-NLS-1$
	public static final String FLD_IS_ORGANIZATION = "istOrganisation"; //$NON-NLS-1$
	public static final String FLD_IS_PATIENT = "istPatient"; //$NON-NLS-1$
	public static final String FLD_IS_PERSON = "istPerson"; //$NON-NLS-1$
	public static final String FLD_ANSCHRIFT = "Anschrift"; //$NON-NLS-1$
	public static final String FLD_COUNTRY = "Land"; //$NON-NLS-1$
	public static final String FLD_PLACE = "Ort"; //$NON-NLS-1$
	public static final String FLD_ZIP = "Plz"; //$NON-NLS-1$
	public static final String FLD_STREET = "Strasse"; //$NON-NLS-1$
	public static final String FLD_PHONE2 = "Telefon2"; //$NON-NLS-1$
	public static final String FLD_PHONE1 = "Telefon1"; //$NON-NLS-1$
	public static final String FLD_REMARK = "Bemerkung"; //$NON-NLS-1$
	public static final String FLD_NAME3 = "Bezeichnung3"; //$NON-NLS-1$
	public static final String FLD_NAME2 = "Bezeichnung2"; //$NON-NLS-1$
	public static final String FLD_NAME1 = "Bezeichnung1"; //$NON-NLS-1$
	protected static final String TABLENAME = "KONTAKT"; //$NON-NLS-1$
	public static final String[] DEFAULT_SORT = {
		FLD_NAME1, FLD_NAME2, FLD_STREET, FLD_PLACE
	};
	volatile String Bezug;
	
	protected String getTableName(){
		return TABLENAME;
	}
	
	static {
		addMapping(
			TABLENAME,
			"BezugsKontakte = JOINT:myID:otherID:KONTAKT_ADRESS_JOINT", //$NON-NLS-1$
			"MyReminders		= LIST:IdentID:REMINDERS", //$NON-NLS-1$
			FLD_NAME1, FLD_NAME2,
			FLD_NAME3,
			"Kuerzel		= PatientNr", //$NON-NLS-1$
			FLD_REMARK, FLD_PHONE1, FLD_PHONE2, "E-Mail=EMail", FLD_WEBSITE, FLD_EXTINFO, //$NON-NLS-1$
			FLD_IS_ORGANIZATION, FLD_IS_PERSON, FLD_IS_PATIENT, FLD_IS_USER, FLD_IS_MANDATOR,
			FLD_IS_LAB, FLD_STREET, FLD_ZIP, FLD_PLACE, FLD_COUNTRY, FLD_FAX, FLD_ANSCHRIFT,
			FLD_MOBILEPHONE);
	}
	
	/**
	 * Returns a label describing this Kontakt.
	 * 
	 * The default implementation returns the short label, i. e. label(false) Sublcasses should
	 * overwrite getLabel(boolean short) for defining their own labels.
	 * 
	 * @return a string describing this Kontakt.
	 */
	public String getLabel(){
		// return the long label
		return getLabel(false);
	}
	
	/**
	 * Returns a label describing this Kontakt.
	 * 
	 * The default implementation returns "Bezeichnung1" for the short label, and "Bezeichnung1",
	 * "Bezeichnung2", "Strasse", "Plz" and "Ort", separated with a comma, for the long label.
	 * 
	 * Subclasses can overwrite this method and define their own label(s). If short is true, they
	 * should return a short label suitable for addresses. If short is false, they should return a
	 * long label describing all important properties of this Kontakt for unique identification by
	 * the user.
	 * 
	 * @param shortLabel
	 *            return a short label for true, and a long label otherwise
	 * @return a string describing this Kontakt.
	 */
	public String getLabel(boolean shortLabel){
		StringBuilder bld = new StringBuilder();
		
		if (shortLabel) {
			bld.append(get(FLD_NAME1));
			String bez3 = get(FLD_NAME3);
			if (!StringTool.isNothing(bez3)) {
				bld.append("(").append(bez3).append(")"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		} else {
			String[] ret = new String[6];
			get(new String[] {
				FLD_NAME1, FLD_NAME2, FLD_NAME3, FLD_STREET, FLD_ZIP, FLD_PLACE
			}, ret);
			bld.append(ret[0]).append(StringTool.space).append(checkNull(ret[1]));
			if (!StringTool.isNothing(ret[2])) {
				bld.append("(").append(ret[2]).append(")"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			bld.append(", ").append(checkNull(ret[3])).append(", ") //$NON-NLS-1$ //$NON-NLS-2$
				.append(checkNull(ret[4])).append(StringTool.space).append(checkNull(ret[5]));
		}
		
		return bld.toString();
	}
	
	public boolean isValid(){
		if (!super.isValid()) {
			return false;
		}
		return true;
	}
	
	/**
	 * Ein Array mit allen zu diesem Kontakt definierten Bezugskontakten holen
	 * 
	 * @return Ein Adress-Array, das auch die Länge null haben kann
	 */
	public List<BezugsKontakt> getBezugsKontakte(){
		Query<BezugsKontakt> qbe = new Query<BezugsKontakt>(BezugsKontakt.class);
		qbe.add("myID", StringTool.equals, getId()); //$NON-NLS-1$
		return qbe.execute();
	}
	
	/** Die Anschrift dieses Kontakts holen */
	public Anschrift getAnschrift(){
		return new Anschrift(this);
	}
	
	/** Die Anschrift dieses Kontakts setzen */
	public void setAnschrift(Anschrift adr){
		if (adr != null) {
			set(new String[] {
				FLD_STREET, FLD_ZIP, FLD_PLACE, FLD_COUNTRY
			}, adr.getStrasse(), adr.getPlz(), adr.getOrt(), adr.getLand());
		}
	}
	
	public String getPostAnschrift(boolean multiline){
		String an = get(FLD_ANSCHRIFT);
		if (StringTool.isNothing(an)) {
			an = createStdAnschrift();
		}
		an = an.replaceAll("[\\r\\n]\\n", StringTool.lf); //$NON-NLS-1$
		return multiline == true ? an : an.replaceAll("\\n", StringTool.space); //$NON-NLS-1$
	}
	
	public String createStdAnschrift(){
		Anschrift an = getAnschrift();
		String ret = StringTool.leer;
		StringBuilder sb = new StringBuilder();
		if (istPerson() == true) {
			Person p = Person.load(getId());
			
			// TODO default salutation should be configurable
			String salutation;
			if (p.getGeschlecht().equals(Person.MALE)) {
				salutation = Messages.getString("Kontakt.SalutationM"); //$NON-NLS-1$
			} else {
				salutation = Messages.getString("Kontakt.SalutationF"); //$NON-NLS-1$
			}
			sb.append(salutation);
			sb.append(StringTool.lf);
			
			String titel = p.get("Titel"); //$NON-NLS-1$
			if (!StringTool.isNothing(titel)) {
				sb.append(titel).append(StringTool.space);
			}
			sb.append(p.getVorname()).append(StringTool.space).append(p.getName())
				.append(StringTool.lf);
			sb.append(an.getEtikette(false, true));
			ret = sb.toString();
		} else {
			Organisation o = Organisation.load(getId());
			String[] rx = new String[2];
			o.get(new String[] {
				FLD_NAME1, FLD_NAME2
			}, rx);
			sb.append(rx[0]).append(StringTool.space).append(checkNull(rx[1]))
				.append(StringTool.lf);
			sb.append(an.getEtikette(false, true));
			ret = sb.toString();
		}/*
		 * else{ ret= an.getEtikette(true, true); }
		 */
		// create the postal if it does not exist yet
		String old = get(FLD_ANSCHRIFT);
		if (StringTool.isNothing(old)) {
			set(FLD_ANSCHRIFT, ret);
		}
		return ret;
	}
	
	/**
	 * Eine neue Zusatzadresse zu diesem Kontakt zufügen
	 * 
	 * @param adr
	 *            die Adresse
	 * @param sBezug
	 *            ein Text, der die Beziehung dieser Adresse zum Kontakt definiert (z.B.
	 *            "Geschäftlich" oder "Orthopäde" oder so)
	 */
	public BezugsKontakt addBezugsKontakt(Kontakt adr, String sBezug){
		if ((adr != null) && (sBezug != null)) {
			return new BezugsKontakt(this, adr, sBezug);
		}
		return null;
	}
	
	protected Kontakt(String id){
		super(id);
	}
	
	/** Kontakt mit gegebener Id aus der Datanbank einlesen */
	public static Kontakt load(String id){
		return new Kontakt(id);
	}
	
	protected Kontakt(){
		// System.out.println("Kontakt");
	}
	
	public String getMailAddress(){
		return checkNull(get("E-Mail")); //$NON-NLS-1$
	}
	
	/** Die Reminders zu diesem Kontakt holen */
	public Reminder[] getRelatedReminders(){
		List<String> l = getList("MyReminders", false); //$NON-NLS-1$
		Reminder[] ret = new Reminder[l.size()];
		int i = 0;
		for (String id : l) {
			ret[i++] = Reminder.load(id);
		}
		return ret;
	}
	
	@Override
	public boolean delete(){
		for (Reminder r : getRelatedReminders()) {
			r.delete();
		}
		for (BezugsKontakt bk : getBezugsKontakte()) {
			bk.delete();
		}
		return super.delete();
	}
	
	/**
	 * Ein Element aus dem Infostore auslesen Der Rückgabewert ist ein Object oder Null. Wenn die
	 * Rechte des aktuellen Anwenders zum Lesen dieses Elements nicht ausreichen, wird ebenfalls
	 * Null zurückgeliefert. 2.9.2007 We remove the checks. they are useless at this moment better
	 * check permissions on inout fields. gw
	 */
	public Object getInfoElement(String elem){
		
		// if(Hub.acl.request("Read"+elem)==true){
		return getInfoStore().get(elem);
		// }else{
		// log.log("Unzureichende Rechte zum Lesen von "+elem,Log.WARNINGS);
		// return null;
		// }
	}
	
	/**
	 * Convenience-Methode und einen String aus dem Infostore auszulesen.
	 * 
	 * @param elem
	 *            Name des Elements
	 * @return Wert oder "" wenn das Element nicht vorhanden ist oder die Rechte nicht zum Lesen
	 *         ausreichen
	 */
	public String getInfoString(String elem){
		return checkNull((String) getInfoElement(elem));
	}
	
	/**
	 * Ein Element in den Infostore schreiben. Wenn ein Element mit demselben Namen schon existiert,
	 * wird es überschrieben. Wenn die Rechte des angemeldeten Anwenders nicht für das Schreiben
	 * dieses Elements ausreichen, wird die Funktion still ignoriert.
	 * 
	 * @param elem
	 *            Name des Elements
	 * @param val
	 *            Inhalt des Elements 2.9.2007 emoved the checks g. weirich
	 */
	@SuppressWarnings("unchecked")
	public void setInfoElement(String elem, Object val){
		// if(Hub.acl.request("Write"+elem)==true){
		Map extinfos = getMap(FLD_EXTINFO);
		if (extinfos != null) {
			extinfos.put(elem, val);
			setMap(FLD_EXTINFO, extinfos);
		}
		/*
		 * }else{ log.log("Unzureichende Rechte zum Schreiben von "+elem,Log.WARNINGS); }
		 */
	}
	
	/**
	 * Den gesamten Infostore holen. Dies ist sinnvoll, wenn kurz nacheinander mehrere Werte gelesen
	 * oder geschrieben werden sollen, da damit das wiederholte entpacken/packen gespart wird. Nach
	 * Änderungen muss der Store mit flushInfoStore() explizit gesichert werden. ACHTUNG: Nicht
	 * Thread-Safe. Konkurriende Schreiboperationen, während ein Thread den store hält, werden
	 * verlorengehen.
	 * 
	 * @return eine Hashtable, die die parameter-wert-paare enthält.
	 */
	@SuppressWarnings("unchecked")
	public Map getInfoStore(){
		return getMap(FLD_EXTINFO);
		/*
		 * if(Hub.acl.request("LoadInfoStore")==true){ return getHashtable("ExtInfo"); } else{
		 * log.log("Unzureichende Rechte zum lesen des Infostore",Log.WARNINGS); return new
		 * Hashtable(); }
		 */
	}
	
	/**
	 * Den mit getInfoStore geholten Infostore wieder zurückschreiben. Dies muss immer dann
	 * geschehen, wenn nach getInfoStore() schreiboperationen durchgeführt wurden.
	 * 
	 * @param store
	 *            die zuvor mit getInfoStore() erhaltene Hashtable.
	 */
	@SuppressWarnings("unchecked")
	public void flushInfoStore(Map store){
		setMap(FLD_EXTINFO, store);
		/*
		 * if(Hub.acl.request("WriteInfoStore")==true){ setHashtable("ExtInfo",store); }else{
		 * log.log("Unzureichende Rechte zum Schreiben des Infostore" ,Log.WARNINGS); }
		 */
	}
	
	/**
	 * Einen Kontakt finden, der einen bestimmten Eintrag im Infostore enthält. Falls mehrere
	 * passende Kontakte vorhanden sind, wird nur der erste zurückgeliefert.
	 * 
	 * @param clazz
	 *            Unterklasse von Kontakt, nach der gesucht werden soll
	 * @param field
	 *            Name des gesuchten Infostore-Eintrags
	 * @param value
	 *            gesuchter Wert dieses Eintrags
	 * @return Ein Objekt der Klasse clazz, welches einen Infostore-Eintrag field mit dem Inhalt
	 *         value enthält, oder null wenn kein solches Objekt existiert.
	 */
	@SuppressWarnings("unchecked")
	public static Kontakt findKontaktfromInfoStore(Class clazz, String field, String value){
		Query qbe = new Query(clazz);
		List list = qbe.execute();
		for (Kontakt k : (List<Kontakt>) list) {
			String i = (String) k.getInfoElement(field);
			if (i != null && i.equals(value)) {
				return k;
			}
		}
		return null;
	}
	
	/**
	 * Statistik für einen bestimmten Objekttyp holen
	 * 
	 * @param typ
	 *            Der Typ (getClass().getName()) des Objekts.
	 * @return eine Liste mit Objektbezeichnern, die zwischen 0 und 30 nach Häufigkeit sortierte
	 *         Elemente enthält.
	 */
	@SuppressWarnings("unchecked")
	public List<String> getStatForItem(String typ){
		Map exi = getMap(FLD_EXTINFO);
		ArrayList<statL> al = (ArrayList<statL>) exi.get(typ);
		ArrayList<String> ret = new ArrayList<String>(al == null ? 1 : al.size());
		if (al != null) {
			for (statL sl : al) {
				ret.add(sl.v);
			}
		}
		return ret;
	}
	
	/**
	 * Eine Statistik für ein bestimmtes Objekt anlegen. Es wird gezählt, wie oft diese Funktion für
	 * dieses Objekt schon aufgerufen wurde, und Objekte desselben Typs aber unterschiedlicher
	 * Identität werden in einer Rangliste aufgelistet. Diese Rangliste kann mit getStatForItem()
	 * angerufen werden. Die Rangliste enthält maximal 40 Einträge.
	 * 
	 * @param lst
	 *            Das Objekt, das gezählt werden soll.
	 */
	@SuppressWarnings("unchecked")
	public void statForItem(PersistentObject lst){
		Map exi = getMap(FLD_EXTINFO);
		String typ = lst.getClass().getName();
		String ident = lst.storeToString();
		// Die Rangliste für diesen Objekttyp auslesen bzw. neu anlegen.
		ArrayList<statL> l = (ArrayList<statL>) exi.get(typ);
		if (l == null) {
			l = new ArrayList<statL>();
		}
		// Grösse der Rangliste limitieren. ggf. least frequently used entfernen
		while (l.size() > 40) {
			l.remove(l.size() - 1);
		}
		// Sehen, ob das übergebene Objekt schon in der Liste enthalten ist
		boolean found = false;
		for (statL c : l) {
			if (c.v.equals(ident)) {
				c.c++; // Gefunden, dann Zähler erhöhen
				found = true;
				break;
			}
		}
		if (found == false) {
			l.add(new statL(ident)); // Nicht gefunden, dann neu eintragen
		}
		Collections.sort(l); // Liste sortieren
		exi.put(typ, l);
		setMap(FLD_EXTINFO, exi);
	}
	
	public static class statL implements Comparable<statL>, Serializable {
		private static final long serialVersionUID = 10455663346456L;
		String v;
		int c;
		
		public statL(){}
		
		statL(String vv){
			v = vv;
			c = 1;
		}
		
		public int compareTo(statL ot){
			return ot.c - c;
		}
	}
	
	@SuppressWarnings("unchecked")
	public void statForString(String typ, String toStat){
		Map exi = getMap(FLD_EXTINFO);
		MFUList<String> l = (MFUList<String>) exi.get(typ);
		if (l == null) {
			l = new MFUList<String>(5, 15);
		}
		l.count(toStat);
		exi.put(typ, l);
		setMap(FLD_EXTINFO, exi);
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getStatForString(String typ){
		Map exi = getMap(FLD_EXTINFO);
		MFUList<String> al = (MFUList<String>) exi.get(typ);
		if (al == null) {
			al = new MFUList<String>(5, 15);
		}
		return al.getAll();
	}
	
	@SuppressWarnings("unchecked")
	public MFUList<String> getMFU(String typ){
		Map exi = getMap(FLD_EXTINFO);
		MFUList<String> l = (MFUList<String>) exi.get(typ);
		if (l == null) {
			l = new MFUList<String>(5, 15);
		}
		return l;
	}
	
	@SuppressWarnings("unchecked")
	public void setMFU(String typ, MFUList<String> mfu){
		Map exi = getMap(FLD_EXTINFO);
		exi.put(typ, mfu);
		setMap(FLD_EXTINFO, exi);
	}
	
	public String getKuerzel(){
		return get(FLD_SHORT_LABEL);
	}
	
	public String getBemerkung(){
		return get(FLD_REMARK);
	}
	
	public void setBemerkung(String b){
		set(FLD_REMARK, b);
	}
	
	public boolean istPerson(){
		return checkNull(get(FLD_IS_PERSON)).equals(StringConstants.ONE);
	}
	
	public boolean istPatient(){
		return checkNull(get(FLD_IS_PATIENT)).equals(StringConstants.ONE);
	}
	
	public boolean istOrganisation(){
		return checkNull(get(FLD_IS_ORGANIZATION)).equals(StringConstants.ONE);
	}
}
