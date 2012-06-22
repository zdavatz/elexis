/*******************************************************************************
 * Copyright (c) 2006-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Niklaus Giger - initial implementation
 *******************************************************************************/
package ch.ngiger.elexis.oddb_ch.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.oddb.ch.Import;

import au.com.bytecode.opencsv.CSVReader;
import ch.elexis.data.Artikel;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.elexis.util.ImporterPage;
import ch.elexis.util.SWTHelper;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.Money;
import ch.ngiger.elexis.oddb_ch.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OddbImporter extends ImporterPage {
	boolean bDelete = false;
	Button bClear;
	String mode;
	public static final Logger logger = LoggerFactory.getLogger(ImporterPage.class);
	
	public OddbImporter(){}
	
	@Override
	public String getTitle(){
		return OddbArtikel.ODDB_NAME; //$NON-NLS-1$
	}
	
	@Override
	public String getDescription(){
		return Messages.OddbImporter_PleaseSelectFile;
	}
	
	static final String EQUALS = "="; //$NON-NLS-1$
	static final String ODDB = "ODDB"; //$NON-NLS-1$
	static String mainTask = null;
	static int nrImportedArticles;
	
	@Override
	public IStatus doImport(final IProgressMonitor monitor) throws Exception{
		mode = Messages.OddbImporter_ModeUpdateAdd;
		IStatus status = Status.OK_STATUS;
		if (bDelete == true) {
			PersistentObject.getConnection().exec("DELETE FROM ARTIKEL WHERE TYP='ODDB'"); //$NON-NLS-1$
			mode = Messages.OddbImporter_ModeCreateNew;
		}
		mainTask = "ODDB-Import of " + results[0];
		monitor.beginTask(mainTask, 0);
		int cachetime = PersistentObject.getDefaultCacheLifetime();
		PersistentObject.setDefaultCacheLifetime(2);
		Import oddbImport = new Import();
		logger.info(String.format("Mode %1$s: Starting import of %2$s", mode, results[0]));
		File file = new File(results[0]);
		logger.info(String.format("Size is %d kB", file.length() / 1024));
		Date d1 = new Date(System.currentTimeMillis());
		nrImportedArticles = 0;
		if (file.getName().toLowerCase().endsWith("csv")) { //$NON-NLS-1$
			monitor
				.subTask(String
					.format(
						"Oddb - Read CSV %1$s (%2$d kBytes)", file.getCanonicalPath(), file.length() / 1024)); //$NON-NLS-1$
			status = importCSV(file, monitor);
		} else {
			monitor
				.subTask(String
					.format(
						"Oddb - Read YAML %1$s (%2$d kBytes)", file.getCanonicalPath(), file.length() / 1024)); //$NON-NLS-1$
			boolean res = oddbImport.importFile(file.getAbsolutePath());
			int nrArticles = oddbImport.articles.size();
			Query<Artikel> qbe = new Query<Artikel>(Artikel.class);
			if (res) {
				try {
					String msg = String.format("ODDB convert %1$d articles", nrArticles);//$NON-NLS-1$
					monitor.beginTask(mainTask, nrArticles);
					monitor.subTask(msg);
					logger.info(msg);
					for (nrImportedArticles = 0; nrImportedArticles < nrArticles; nrImportedArticles++) {
						Import.ElexisArtikel a = oddbImport.articles.get(nrImportedArticles);
						if (nrImportedArticles % 10 == 0)
							logger.info("j: " + nrImportedArticles + ":" + a.toString());
						OddbArtikel oddbA = null;
						qbe.clear();
						qbe.add("EAN", EQUALS, a.ean13);
						qbe.and();
						qbe.add("Typ", EQUALS, ODDB);
						List<Artikel> lArt = qbe.execute();
						if (lArt.size() == 1) {
							oddbA = (OddbArtikel) lArt.get(0);
						} else if (lArt.size() == 0) {
							oddbA = new OddbArtikel(a.name, a.ean13);
						} else {
							// TODO: handle duplicates
							logger.error(String.format("ODDB-Duplikat ?? %1$s", a.ean13));
						}
						if (a.EKPreis != null)
							oddbA.setEKPreis(a.EKPreis);
						if (a.VKPreis != null)
							oddbA.setVKPreis(a.VKPreis);
						if (a.atc_code != null)
							oddbA.setATC_code(a.atc_code);
						if (a.ean13 != null)
							oddbA.setEAN(a.ean13);
						if (a.verpackungsEinheit != null)
							oddbA.setExt(Artikel.VERPACKUNGSEINHEIT, a.verpackungsEinheit);
						if (a.abgabeEinheit != null)
							oddbA.setExt(Artikel.VERKAUFSEINHEIT, a.abgabeEinheit);
						monitor.worked(1);
						if (nrImportedArticles % 1000 == 500) { // Speicher freigeben
							PersistentObject.clearCache();
							System.gc();
							Thread.sleep(100);
						}
					}
					
				} catch (Exception ex) {
					logger.error("Error converting articles: " + ex.getMessage()
						+ ex.getStackTrace().toString());
					ExHandler.handle(ex);
				}
				PersistentObject.setDefaultCacheLifetime(cachetime);
				monitor.done();
			}
		}
		Date d2 = new Date(System.currentTimeMillis());
		long difference = d2.getTime() - d1.getTime();
		logger.info(String.format("Elapsed %1$d.%2$d seconds. Converted %3$d articles",
			difference / 1000, difference % 1000, nrImportedArticles));
		PersistentObject.setDefaultCacheLifetime(cachetime);
		monitor.done();
		return status;
	}
	
	@Override
	public void collect(){
		bDelete = bClear.getSelection();
	}
	
	@Override
	public Composite createPage(final Composite parent){
		Composite ret = new ImporterPage.FileBasedImporter(parent, this);
		ret.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		bClear = new Button(parent, SWT.CHECK | SWT.WRAP);
		bClear.setText(Messages.OddbImporter_ClearAllData);
		bClear.setSelection(true);
		bClear.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		return ret;
		
	}
	
	private IStatus importCSV(final File file, final IProgressMonitor monitor)
		throws FileNotFoundException, IOException{
// InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "UTF-8");
		InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "ISO-8859-15");
		CSVReader reader = new CSVReader(isr, ';');
		String[] line;
		String ATC_code = "";
		int nrArticles = (int) file.length() / 250; // avg length of an ODDB csv line
		String msg = String.format("ODDB read aboud %1$d articles", (nrArticles / 100) * 100);//$NON-NLS-1$
		monitor.beginTask(mainTask, nrArticles);
		monitor.subTask(msg);
		nrImportedArticles = 0;
		Query<Artikel> qbe = new Query<Artikel>(Artikel.class);
		while ((line = reader.readNext()) != null) {
			if (line[0].startsWith("rectype"))
				continue;
			if (line[0].startsWith("#MGrp")) {
				ATC_code = line[1];
				continue;
			}
			// CSV is like this:
			// 0: rectype;Reg.-Nr.;Packungsnummer;Swissmedic-Nr.;EAN-Code;BSV-Dossier;
			// 6: Pharmacode;Präparat;Galenische Form;Stärke;Packungsgrösse;
			// 11: Numerisch;EFP;PP;Zulassungsinh.;Kat.;SL;Aufnahme in SL;Limitation;
			// 19: Limitationspunkte;Limitationstext;LPPV;Reg.Dat.;Gültig bis;
			// 24: Deaktiviert;Exportprodukt;CAS Reg.Nr.;generic_type;Hat ein Generikum;SB;
			// 30: Ausser Handel (MedRef);Komplementärprodukt;Index Therapeuticus (BAG);
			// 33: Index Therapeuticus (Swissmedic);Betäubungsmittel;Impfstoff/Blutprodukt;
			// 36: renewal_flag_swissmedic
// #Medi;60116;005;60116005;7680601160056;;;Meropenem-TBS i.v. 1 g, Pulver zur Herstellung einer
// Injektions- oder Infusionslösung;Pulver zur Herstellung einer Injektions- oder Infusionslösung;1
// g;10 à 1 g;10;;;Target BioScience
// AG;A;Nein;;;;;Nein;13.04.2010;12.04.2015;17.12.2011;;"";;Nein;;Nein;;;08.01.25.;Nein;Nein;Nein
			nrImportedArticles++;
			String ean13 = line[4];
			String pharmacode = line[6];
			String artikelName = line[7]; // Präparat
			String galenischeForm = line[8];
			String verpackungsEinheit = line[10];
			Money ek_preis = new Money(0);
			Money vk_preis = new Money(0);
			try {
				ek_preis = new Money(line[12]);
			} catch (ParseException e2) {
				// Ignore errors
				logger.error(ean13 + ": Could not convert " + line[12]);
			}
			try {
				vk_preis = new Money(line[13]);
			} catch (ParseException e1) {
				// Ignore errors
				logger.error(ean13 + ": Could not convert " + line[13]);
				
			}
			OddbArtikel oddbA = null;
			List<Artikel> lArt = null;
			if (!bDelete) {
				qbe.clear();
				qbe.add("EAN", EQUALS, ean13);
				qbe.and();
				qbe.add("Typ", EQUALS, ODDB);
				lArt = qbe.execute();
			}
			if (lArt == null || lArt.size() == 0) {
				String sprechenderName =
					artikelName + " " + galenischeForm + " " + verpackungsEinheit;
				// Es gibt Fälle, wo schon der artikelName > 127 Zeichen ist. z.B
				// Meropenem-TBS i.v. 1 g, Pulver zur Herstellung einer Injektions- oder
// Infusionslösung;Pulver zur Herstellung einer Injektions- oder Infusionslösung
				if (sprechenderName.length() <= 127)
					oddbA = new OddbArtikel(sprechenderName, ean13);
				else
					oddbA = new OddbArtikel(sprechenderName.substring(0, 126), ean13);
			} else if (lArt.size() == 1) {
				oddbA = (OddbArtikel) lArt.get(0);
			} else {
				// TODO: handle duplicates
				logger.error(String.format("ODDB-Duplikat ?? %1$s", ean13));
			}
			
			oddbA.setEAN(ean13);
			oddbA.setPharmaCode(pharmacode);
			oddbA.setATC_code(ATC_code);
			oddbA.setEKPreis(ek_preis);
			oddbA.setVKPreis(vk_preis);
			// oddbA.setExt(Artikel.VERKAUFSEINHEIT, line[??]);
			oddbA.setExt(Artikel.VERPACKUNGSEINHEIT, verpackungsEinheit);
			monitor.worked(1);
			if (nrImportedArticles % 1000 == 500) { // Speicher type filter textfreigeben
				PersistentObject.clearCache();
				System.gc();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		logger.info(String.format("Imported %d articles", nrImportedArticles));
		monitor.done();
		return Status.OK_STATUS;
	}
	
}
