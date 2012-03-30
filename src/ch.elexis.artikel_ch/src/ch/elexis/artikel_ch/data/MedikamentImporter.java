/*******************************************************************************
 * Copyright (c) 2006-2011, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    G. Weirich 1/08 - major redesign to implement IGM updates etc.
 * 
 *  $Id$
 *******************************************************************************/

package ch.elexis.artikel_ch.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Composite;

import ch.elexis.data.Artikel;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.elexis.util.ImporterPage;
import ch.elexis.util.Log;
import ch.elexis.util.SWTHelper;
import ch.rgw.tools.ExHandler;

/**
 * Importing articles from an IGM-10 or IGM-11 file.
 * 
 * @author Gerry
 * 
 */
public class MedikamentImporter extends ImporterPage {
	static final String EQUALS = "="; //$NON-NLS-1$
	public static final String MWST_TYP = "MWSt-Typ"; //$NON-NLS-1$
	private static final String EAN = "EAN"; //$NON-NLS-1$
	private static final String HERSTELLER = "Hersteller"; //$NON-NLS-1$
	private static final String LAGERART = "Lagerart"; //$NON-NLS-1$
	public static final String KASSENTYP = "Kassentyp"; //$NON-NLS-1$
	private static final String VK_PREIS = "VK_Preis"; //$NON-NLS-1$
	
	// Button bClear;
	// boolean bDelete;
	public MedikamentImporter(){}
	
	final static String SUBID = "SubID"; //$NON-NLS-1$
	final static String NAME = "Name"; //$NON-NLS-1$
	final static String MEDIKAMENT = "Medikament"; //$NON-NLS-1$
	final static String MEDICAL = "Medical"; //$NON-NLS-1$
	final static String EK_PREIS = "EK_Preis"; //$NON-NLS-1$
	
	@SuppressWarnings("unchecked")
	@Override
	public IStatus doImport(final IProgressMonitor monitor) throws Exception{
		File file = new File(results[0]);
		long l = file.length();
		InputStreamReader ir = new InputStreamReader(new FileInputStream(file), "iso-8859-1"); //$NON-NLS-1$
		BufferedReader br = new BufferedReader(ir);
		int cachetime = PersistentObject.getDefaultCacheLifetime();
		PersistentObject.setDefaultCacheLifetime(2);
		String in;
		String mode = Messages.MedikamentImporter_ModeOfImport;
		/*
		 * if(bDelete==true){ if(SWTHelper.askYesNo("Wirklich Daten löschen", "Achtung: Wenn die
		 * alten Daten gelöscht werden, kann es\nsein, dass bestehende Bezüge ungültig werden.")){
		 * PersistentObject.getConnection().exec("DELETE FROM ARTIKEL WHERE TYP='Medikament'");
		 * mode=" (Modus: Alles neu erstellen)"; } }
		 */
		monitor
			.beginTask(Messages.MedikamentImporter_MedikamentImportTitle + mode, (int) (l / 100));
		Query<Artikel> qbe = new Query<Artikel>(Artikel.class);
		int counter = 0;
		String titel, ek, vk, kasse, cmws;
		while ((in = br.readLine()) != null) {
			// Recordart (RECA) - 2stellig
			// 11: Stamm-Satz (mit Mehrwertsteuer-Code)
			String reca = new String(in.substring(0, 2));
			
			// Mutationscode (CMUT) - 1stellig
			// 1: Datensatz (Artikel) neu (im Handel)
			// 2: Datensatz (Artikel) Update
			// 3: Datensatz (Artikel) ausser Handel
			String cmut = new String(in.substring(2, 3));
			
			// Pharmacode (PHAR) - 7stellig
			String phar = new String(in.substring(3, 10)).trim();
			
			// String ckzl = new String(in.substring(7,8)); // Kassenpflicht
			String pk = "0"; //$NON-NLS-1$
			try {
				long pkl = Long.parseLong(phar); // führende Nullen entfernen
				pk = Long.toString(pkl);
			} catch (Exception ex) {
				ExHandler.handle(ex);
				log.log(Messages.MedikamentImporter_BadPharmaCode, Log.ERRORS);
			}
			
			// String id=qbe.findSingle(SUBID, EQUALS, pk);
			qbe.clear();
			qbe.add(SUBID, EQUALS, pk);
			qbe.or();
			qbe.add(SUBID, EQUALS, phar);
			List<Artikel> lArt = qbe.execute();
			if (lArt.size() > 1) {
				// Duplikate entfernen, genau einen gültigen und existierenden
				// Artikel behalten
				Iterator<Artikel> it = lArt.iterator();
				boolean hasValid = false;
				while (it.hasNext()) {
					Artikel ax = it.next();
					if (hasValid || (!ax.isValid())) {
						it.remove();
					} else {
						hasValid = true;
					}
				}
				
			}
			Artikel a = lArt.size() > 0 ? lArt.get(0) : null;
			if ((a == null) || (!a.exists())) {
				if (cmut.equals("3") || (!reca.equals("11"))) { // ausser handel //$NON-NLS-1$ //$NON-NLS-2$
					// oder kein
					// Stammsatz
					continue; // Dann Artikel nicht neu erstellen, falls er
					// nicht existiert
				}
			} else {
				if (cmut.equals("3")) { // Wenn er existiert, muss er gelöscht //$NON-NLS-1$
					// werden
					a.delete();
					continue;
				}
			}
			
			if (reca.equals("11")) {
				titel = new String(in.substring(10, 60)).trim(); // Text
				ek = new String(in.substring(60, 66)).trim(); // EK-Preis
				vk = new String(in.substring(66, 72)).trim(); // VK-Preis
				kasse = new String(in.substring(72, 73)); // Kassentyp
				String lager = new String(in.substring(73, 75)); // Lagerart
				String hix = new String(in.substring(75, 76)); // iks-listencode
				String ithe = new String(in.substring(76, 83)); // index
				// therapeuticus
				String ean = new String(in.substring(83, 96)); // EAN
				
				// Code Mehrwertsteuer (CMWS) - 1stellig
				// 1: voller MWSt-Satz (zur Zeit 6.5%)
				// 2: reduzierter MWSt-Satz (zur Zeit 2%)
				// 3: von der MWSt befreit
				cmws = new String(in.substring(96, 97)); // MWSt-Typ
				
				if (a == null) {
					if (cmws.equals("1")) { //$NON-NLS-1$
						a = new Artikel(titel, MEDICAL, pk);
						a.set(Artikel.FLD_KLASSE, Medical.class.getName());
					} else {
						a = new Artikel(titel, MEDIKAMENT, pk);
						a.set(Artikel.FLD_KLASSE, Medikament.class.getName());
					}
				}
				if (vk.matches("0+")) { //$NON-NLS-1$
					a.set(EK_PREIS, ek);
					a.set(EAN, ean);
				} else {
					String[] fields = {
						EK_PREIS, VK_PREIS, EAN
					};
					a.set(fields, ek, vk, ean);
				}
				
				Map ext = a.getMap(Artikel.FLD_EXTINFO);
				ext.put(Artikel.FLD_PHARMACODE, pk);
				ext.put(KASSENTYP, kasse);
				ext.put(LAGERART, lager);
				ext.put(HERSTELLER, hix);
				ext.put(EAN, ean);
				ext.put(MWST_TYP, cmws);
				
				a.setMap(Artikel.FLD_EXTINFO, ext);
			} else if (reca.equals("10")) { // Update-Satz //$NON-NLS-1$
				ek = new String(in.substring(10, 16));
				vk = new String(in.substring(16, 22));
				kasse = new String(in.substring(22, 23));
				cmws = new String(in.substring(23, 24));
				if (vk.matches("0+")) { //$NON-NLS-1$
					a.set(EK_PREIS, ek);
				} else {
					String[] fields = {
						EK_PREIS, VK_PREIS
					};
					a.set(fields, ek, vk);
				}
				
			} else {
				SWTHelper.showError(Messages.MedikamentImporter_BadFileFormat,
					Messages.MedikamentImporter_OnlyIGM10AndIGM11);
				return Status.CANCEL_STATUS;
			}
			
			monitor.worked(1);
			if (monitor.isCanceled()) {
				monitor.done();
				return Status.CANCEL_STATUS;
			}
			monitor.subTask(a.getLabel());
			a = null;
			in = null;
			if (counter++ > 1000) { // Speicher freigeben
				PersistentObject.clearCache();
				System.gc();
				Thread.sleep(100);
				counter = 0;
			}
			
		}
		monitor.done();
		PersistentObject.setDefaultCacheLifetime(cachetime);
		return Status.OK_STATUS;
	}
	
	@Override
	public String getTitle(){
		return Messages.MedikamentImporter_WindowTitleMedicaments;
	}
	
	@Override
	public String getDescription(){
		return Messages.MedikamentImporter_PleaseChoseFile;
	}
	
	@Override
	public Composite createPage(final Composite parent){
		Composite ret = new ImporterPage.FileBasedImporter(parent, this);
		ret.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		// bClear=new Button(parent,SWT.CHECK|SWT.WRAP);
		// bClear.setText("Alle Daten vorher löschen (VORSICHT! Bitte Anleitung
		// beachten)");
		// bClear.setLayoutData(SWTHelper.getFillGridData(1,true,1,false));
		return ret;
	}
	
	/*
	 * @Override public void collect() { bDelete=bClear.getSelection(); super.collect(); }
	 */
}
