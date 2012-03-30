/*******************************************************************************
 * Copyright (c) 2007-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 * $Id: KontaktMatcher.java 6137 2010-02-14 09:45:36Z rgw_ch $
 *******************************************************************************/

package ch.elexis.matchers;

import static ch.elexis.dialogs.KontaktSelektor.HINTSIZE;
import static ch.elexis.dialogs.KontaktSelektor.HINT_BIRTHDATE;
import static ch.elexis.dialogs.KontaktSelektor.HINT_FIRSTNAME;
import static ch.elexis.dialogs.KontaktSelektor.HINT_NAME;
import static ch.elexis.dialogs.KontaktSelektor.HINT_PLACE;
import static ch.elexis.dialogs.KontaktSelektor.HINT_SEX;
import static ch.elexis.dialogs.KontaktSelektor.HINT_STREET;
import static ch.elexis.dialogs.KontaktSelektor.HINT_ZIP;

import java.util.List;

import ch.elexis.data.Anschrift;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Organisation;
import ch.elexis.data.Person;
import ch.elexis.data.Query;
import ch.elexis.dialogs.KontaktSelektor;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;

/**
 * Class to match personal data to contacts
 * 
 * @deprecated use ch.elexis.exchange.KontaktMatcher
 * @author gerry
 */

@Deprecated
public class KontaktMatcher {
	public enum CreateMode {
		FAIL, CREATE, ASK
	};
	
	public static Kontakt findKontakt(final String name, final String strasse, final String plz,
		final String ort){
		Organisation o = findOrganisation(name, strasse, plz, ort, CreateMode.FAIL);
		if (o == null) {
			Person p = findPerson(name, "", "", "", strasse, plz, ort, "", CreateMode.FAIL);
			return p;
		} else {
			return o;
		}
	}
	
	/**
	 * Find the organization mathcing the given parameters
	 * 
	 * @param name
	 * @param strasse
	 * @param plz
	 * @param ort
	 * @param createIfNotExists
	 * @return the organization that matches best the given parameters or null if no such
	 *         organization was found
	 */
	public static Organisation findOrganisation(final String name, final String strasse,
		final String plz, final String ort, final CreateMode createMode){
		String[] hints = new String[HINTSIZE];
		hints[HINT_NAME] = name;
		hints[HINT_STREET] = strasse;
		hints[HINT_ZIP] = plz;
		hints[HINT_PLACE] = ort;
		Query<Organisation> qbe = new Query<Organisation>(Organisation.class);
		qbe.add("Name", "=", name);
		List<Organisation> found = qbe.execute();
		if (found.size() == 0) {
			if (createMode == CreateMode.CREATE) {
				Organisation org = new Organisation(name, "");
				addAddress(org, strasse, plz, ort);
				return org;
			} else if (createMode == CreateMode.ASK) {
				return (Organisation) KontaktSelektor.showInSync(Organisation.class,
					"Organisation nicht gefunden", name + ", " + strasse + ", " + plz + " " + ort,
					resolve1, hints);
			}
		}
		if (found.size() == 1) {
			return found.get(0);
		}
		// more than 1 hit
		if (createMode == CreateMode.ASK) {
			return (Organisation) KontaktSelektor.showInSync(Organisation.class,
				"Organisation nicht eindeutig", name + ", " + strasse + ", " + plz + " " + ort,
				resolve1, hints);
		} else {
			return (Organisation) matchAddress(found.toArray(new Kontakt[0]), strasse, plz, ort,
				null);
		}
	}
	
	/**
	 * find the Person matching the given parameters
	 * 
	 * @param name
	 * @param vorname
	 * @param gebdat
	 * @param gender
	 * @param strasse
	 * @param plz
	 * @param ort
	 * @param natel
	 * @param createIfNotExists
	 * @return the found person or null if no matching person wasd found
	 */
	public static Person findPerson(final String name, final String vorname, final String gebdat,
		final String gender, final String strasse, final String plz, final String ort,
		final String natel, final CreateMode createMode){
		String[] hints = new String[HINTSIZE];
		hints[HINT_NAME] = name;
		hints[HINT_FIRSTNAME] = vorname;
		hints[HINT_BIRTHDATE] = gebdat;
		hints[HINT_SEX] = gender;
		hints[HINT_STREET] = strasse;
		hints[HINT_ZIP] = plz;
		hints[HINT_PLACE] = ort;
		
		Query<Person> qbe = new Query<Person>(Person.class);
		String sex = "";
		String birthdate = "";
		
		if (!StringTool.isNothing(name)) {
			qbe.startGroup();
			qbe.add("Name", "LIKE", name + "%", true);
			String un = StringTool.unambiguify(name);
			if (!un.equalsIgnoreCase(name)) {
				qbe.or();
				qbe.add("Name", "LIKE", un + "%", true);
			}
			qbe.endGroup();
			qbe.and();
		}
		
		if (!StringTool.isNothing(vorname)) {
			qbe.startGroup();
			qbe.add("Vorname", "LIKE", vorname + "%", true);
			String un = StringTool.unambiguify(vorname);
			if (!un.equalsIgnoreCase(vorname)) {
				qbe.or();
				qbe.add("Vorname", "LIKE", un + "%", true);
			}
			qbe.endGroup();
			qbe.and();
		}
		if (!StringTool.isNothing(gebdat)) {
			TimeTool tt = new TimeTool();
			if (tt.set(gebdat)) {
				birthdate = tt.toString(TimeTool.DATE_GER);
				qbe.add("Geburtsdatum", "=", tt.toString(TimeTool.DATE_COMPACT));
			}
		}
		if (!StringTool.isNothing(gender)) {
			String gl = gender.toLowerCase();
			if (gl.startsWith("f") || gl.startsWith("w")) {
				sex = Person.FEMALE;
			} else if (gl.startsWith("m")) {
				sex = Person.MALE;
			} else {
				if (StringTool.isNothing(vorname)) {
					sex = "?";
				} else {
					sex = StringTool.isFemale(vorname) ? Person.FEMALE : Person.MALE;
				}
			}
			qbe.add("Geschlecht", "=", sex);
		}
		List<Person> found = qbe.execute();
		if (found.size() == 0) {
			if (createMode == CreateMode.CREATE) {
				Person ret = new Person(name, vorname, birthdate, sex);
				addAddress(ret, strasse, plz, ort);
				return ret;
			} else if (createMode == CreateMode.ASK) {
				return (Person) KontaktSelektor.showInSync(Person.class, "Person nicht gefunden",
					name + " " + vorname + (StringTool.isNothing(gebdat) ? "" : ", " + gebdat)
						+ ", " + strasse + ", " + plz + " " + ort, resolve1, hints);
			}
			return null;
		}
		if (found.size() == 1) {
			return found.get(0);
		}
		// more than 1 hit
		if (createMode == CreateMode.ASK) {
			return (Person) KontaktSelektor.showInSync(Person.class, "Person nicht eindeutig", name
				+ " " + vorname + (StringTool.isNothing(gebdat) ? "" : ", " + gebdat) + ", "
				+ strasse + ", " + plz + " " + ort, resolve1, hints);
		} else {
			return (Person) matchAddress(found.toArray(new Kontakt[0]), strasse, plz, ort, natel);
		}
	}
	
	/**
	 * Given an array of Kontakt, find the one that matches the given address best
	 * 
	 * @param kk
	 * @param strasse
	 * @param plz
	 * @param ort
	 * @param natel
	 * @return
	 */
	public static Kontakt matchAddress(final Kontakt[] kk, final String strasse, final String plz,
		final String ort, final String natel){
		
		int[] score = new int[kk.length];
		
		for (int i = 0; i < kk.length; i++) {
			
			// If we have the same mobile number, that's a strong hint
			if (!StringTool.isNothing(natel)) {
				if (normalizePhone(kk[i].get("NatelNr")).equals(normalizePhone(natel))) {
					score[i] += 5;
				}
			}
			
			// If we have the same street address, that's also a good hint
			if (!StringTool.isNothing(strasse)) {
				if (isSameStreet(kk[i].get("Strasse"), strasse)) {
					score[i] += 3;
				} else {
					score[i] -= 2;
				}
			}
			
			// If we have the same zip or the same olace, that's a quite weak hint.
			if (!StringTool.isNothing(plz)) {
				if (plz.equals(kk[i].get("Plz"))) {
					score[i] += 2;
				} else {
					score[i] -= 1;
				}
			}
			if (!StringTool.isNothing(ort)) {
				if (ort.equals(kk[i].get("Ort"))) {
					score[i] += 1;
				} else {
					score[i] -= 1;
				}
			}
			
		}
		Kontakt found = null;
		for (int i = 0; i < score.length; i++) {
			if (score[i] > 0) {
				if (found != null) {
					return null;
				}
				found = kk[i];
			}
		}
		if (found == null) { // nothing did match at all
			found = kk[0]; // we just take the first one
		}
		return found;
	}
	
	/**
	 * try to figure out which part of a string is the zip and which is the place
	 * 
	 * @param str
	 *            a string containing possibly zip and possibly place
	 * @return always a two element array, [0] is zip or "", [1] is place or ""
	 */
	public static String[] normalizeAddress(String str){
		String[] ret = str.split("\\s+", 2);
		if (ret.length < 2) {
			String[] rx = new String[2];
			rx[0] = "";
			rx[1] = ret[0];
			return rx;
		}
		return ret;
	}
	
	/**
	 * Remove all non-numbers out of phone strings
	 * 
	 * @param nr
	 * @return
	 */
	public static String normalizePhone(final String nr){
		return nr.replaceAll("[\\s-:\\.]", "");
	}
	
	/**
	 * Try to figure out if two street strings denote the same street address
	 * 
	 * @return true if the streets seem to be equal
	 */
	public static boolean isSameStreet(final String s1, final String s2){
		String[] ns1 = normalizeStrasse(s1);
		String[] ns2 = normalizeStrasse(s2);
		if (!(ns1[0].matches(ns2[0]))) {
			return false;
		}
		if (!(ns1[1].matches(ns2[1]))) {
			return false;
		}
		return true;
	}
	
	static String[] normalizeStrasse(final String strasse){
		String[] m1 = StringTool.normalizeCase(strasse).split("\\s");
		int m1l = m1.length;
		StringBuilder m2 = new StringBuilder();
		m2.append(m1[0]);
		String nr = "0";
		if (m1l > 1) {
			if (m1[m1l - 1].matches("[0-9]+[a-zA-Z]")) {
				nr = m1[m1l - 1];
				m1l -= 1;
			}
			if (m1l > 1) {
				for (int i = 1; i < m1l; i++) {
					m2.append(" ").append(m1[i]);
				}
			}
		}
		return new String[] {
			m2.toString(), nr
		};
		
	}
	
	public static void addAddress(final Kontakt k, String str, String plzort){
		String[] ort = plzort.split("[\\s+]");
		if (ort.length == 2) {
			addAddress(k, str, ort[0], ort[1]);
		} else if (ort.length > 2) {
			StringBuilder plz = new StringBuilder();
			for (int i = 1; i < ort.length; i++) {
				plz.append(ort[i]).append(" ");
			}
			addAddress(k, str, ort[0], plz.toString());
		} else {
			addAddress(k, str, ort[0], "");
		}
	}
	
	public static void addAddress(final Kontakt k, final String str, String plz, final String ort){
		Anschrift an = k.getAnschrift();
		if (!StringTool.isNothing(str)) {
			an.setStrasse(str);
		}
		if (!StringTool.isNothing(plz)) {
			if (plz.matches("[A-Z]{1,3}[\\s\\-]+[A-Za-z0-9]+")) {
				String[] plzx = plz.split("[\\s\\-]+", 1);
				if (plzx.length > 1) {
					plz = plzx[1];
					an.setLand(plzx[0]);
				}
			}
			an.setPlz(plz);
		}
		if (!StringTool.isNothing(ort)) {
			an.setOrt(ort);
		}
		k.setAnschrift(an);
		k.createStdAnschrift();
	}
	
	/**
	 * Decide whether a person is identical to given personal data. Normalize all names: Ulmlaute
	 * will be converted, accents will be eliminatet and double names will be reduced to their first
	 * part.
	 * 
	 * @return true if the given person seems to be the same than the given personalia
	 */
	public static boolean isSame(final Person a, final String nameB, final String firstnameB,
		final String gebDatB){
		try {
			String name1 = simpleName(StringTool.unambiguify(a.getName()));
			String name2 = simpleName(StringTool.unambiguify(nameB));
			if (name1.equals(name2)) {
				String vorname1 = simpleName(StringTool.unambiguify(a.getVorname()));
				String vorname2 = simpleName(StringTool.unambiguify(firstnameB));
				if (vorname1.equals(vorname2)) {
					String gd1 = a.getGeburtsdatum();
					if (StringTool.isNothing(gd1)) {
						return true;
					}
					if (StringTool.isNothing(gebDatB)) {
						return true;
					}
					String gd2 = new TimeTool(gebDatB).toString(TimeTool.DATE_GER);
					if (gd1.equals(gd2)) {
						return true;
					}
				}
			}
			
		} catch (Throwable t) {
			ExHandler.handle(t);
			
		}
		return false;
	}
	
	static String simpleName(final String name){
		String[] ret = name.split("\\s*[- ]\\s*");
		return ret[0];
	}
	
	final static String resolve1 =
		"Es kann nicht automatisch entschieden werden, ob dieser Kontakt in der\n"
			+ "Datenbank enthalten ist, bzw. welchem existierenden Kontakt dies entspricht.\n"
			+ "Bitte wählen Sie unten aus, welchem Kontakt dieser neue Eintrag entspricht,\n"
			+ "oder Klicken Sie 'Neu erstellen', um den Kontakt neu zu erstellen.\n"
			+ "'Cancel' bricht den Importvorgang ab.";
	
}
