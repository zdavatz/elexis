/*******************************************************************************
 * Copyright (c) 2005-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    D. Lutz	 - Import from different DBMS
 *    N. Giger   - direct import from MDB file using Gerrys AccessWrapper
 * 
 *******************************************************************************/

// 8.12.07 G.Weirich avoid duplicate imports
package ch.elexis.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Composite;

import com.healthmarketscience.jackcess.Database;

import ch.elexis.Hub;
import ch.elexis.arzttarife_schweiz.Messages;
import ch.elexis.importers.AccessWrapper;
import ch.elexis.preferences.PreferenceConstants;
import ch.elexis.util.ImporterPage;
import ch.elexis.util.SWTHelper;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.TimeSpan;
import ch.rgw.tools.TimeTool;
import ch.rgw.tools.JdbcLink.Stm;

/**
 * Import des Tarmed-Tarifsystems aus der Datenbank der ZMT. We use Gerry AccessWrapper to
 * 
 * * copy all tables from the MDB file into the actual DB. The tablenames are all prefixed with
 * TARMED_IMPORT_. Then we close the connection to the MDB file.
 * 
 * * now import everything using plain SQL-Statements.
 * 
 * * finally drop all intermediate tables again
 * 
 * (Download der Datenbank z.B.: <a
 * href="http://www.zmt.ch/de/tarmed/tarmed_tarifstruktur/tarmed_database.htm" >hier</a> oder <a
 * href= "http://www.tarmedsuisse.ch/site_tarmed/pages/edito/public/e_02_03.htm" >hier</a>.)
 * 
 * @author gerry
 * 
 */
public class TarmedImporter extends ImporterPage {
	
	AccessWrapper aw;
	JdbcLink pj;
	Stm source, dest;
	// Text tDb;
	private String lang;
	private Database mdbDB;
	private String mdbFilename;
	private Set<String> cachedDbTables = null;
	private static final String ImportPrefix = "TARMED_IMPORT_";
	private int count = 0; // Our counter for the progress monitor. Twice. Once for Access import,
// then real import
	
	public TarmedImporter(){}
	
	@Override
	public String getTitle(){
		return "TarMed code"; //$NON-NLS-1$
	}
	
	private IStatus openAccessDatabase(final IProgressMonitor monitor, String filename){
		mdbFilename = filename;
		File file = new File(mdbFilename);
		try {
			aw = new AccessWrapper(file);
			aw.setPrefixForImportedTableNames(ImportPrefix);
			mdbDB = Database.open(file, true, Database.DEFAULT_AUTO_SYNC);
			cachedDbTables = mdbDB.getTableNames();
		} catch (IOException e) {
			System.out.println("Failed to open access file " + file);
			e.printStackTrace();
			return Status.CANCEL_STATUS;
		}
		return Status.OK_STATUS;
	}
	
	/*
	 * Import all Access tables (using cache cachedDbTables)
	 */
	private IStatus importAllAccessTables(final IProgressMonitor monitor){
		String tablename = "";
		double weight = 0.1; // a work unit here is much less work than in the final import
		Iterator<String> iter;
		int totRows = 0;
		try {
			int nrTables = cachedDbTables.size();
			iter = cachedDbTables.iterator();
			iter = cachedDbTables.iterator();
			while (iter.hasNext()) {
				tablename = iter.next();
				totRows += mdbDB.getTable(tablename).getRowCount();
			}
			monitor.beginTask(Messages.TarmedImporter_importLstg,
				(int) (totRows*weight) + mdbDB.getTable("LEISTUNG").getRowCount()
					+ mdbDB.getTable("KAPITEL_TEXT").getRowCount());
			
			int j = 0;
			iter = cachedDbTables.iterator();
			while (iter.hasNext()) {
				j++;
				tablename = iter.next();
				String msg =
					String.format(Messages.TarmedImporter_convertTable, tablename, ImportPrefix
						+ tablename, j, nrTables, mdbDB.getTable(tablename).getRowCount(),
						mdbFilename);
				monitor.subTask(msg);
				try {
					int nrRows = aw.convertTable(tablename, pj);
					monitor.worked((int)(nrRows*weight));
				} catch (SQLException e) {
					System.out.println("Failed to import table " + tablename);//$NON-NLS-1$
					e.printStackTrace();
					return Status.CANCEL_STATUS;
				}
			}
			return Status.OK_STATUS;
		} catch (IOException e) {
			System.out.println("Failed to process access file " + mdbFilename);//$NON-NLS-1$
			e.printStackTrace();
			return Status.CANCEL_STATUS;
		}
	}
	
	private IStatus deleteCachedAccessTables(final IProgressMonitor monitor){
		String tablename = "";
		Iterator<String> iter;
		iter = cachedDbTables.iterator();
		while (iter.hasNext()) {
			tablename = iter.next();
			pj = PersistentObject.getConnection();
			pj.exec("DROP TABLE IF EXISTS " + tablename);//$NON-NLS-1$
		}
		return Status.OK_STATUS;
	}
	
	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.elexis.util.ImporterPage#doImport(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IStatus doImport(final IProgressMonitor monitor) throws Exception{
		count = 0;
		if (openAccessDatabase(monitor, results[0]) != Status.OK_STATUS
			|| deleteCachedAccessTables(monitor) != Status.OK_STATUS
			|| importAllAccessTables(monitor) != Status.OK_STATUS) {
			mdbDB = null;
			cachedDbTables = null;
			return Status.CANCEL_STATUS;
		}
		
		pj = PersistentObject.getConnection();
		lang = JdbcLink.wrap(Hub.localCfg.get(PreferenceConstants.ABL_LANGUAGE, "d").toUpperCase()); //$NON-NLS-1$
		monitor.subTask(Messages.TarmedImporter_connecting);
		
		try {
			source = pj.getStatement();
			dest = pj.getStatement();
			monitor.subTask(Messages.TarmedImporter_deleteOldData);
			
			pj.exec("DELETE FROM TARMED"); //$NON-NLS-1$
			pj.exec("DELETE FROM TARMED_DEFINITIONEN"); //$NON-NLS-1$
			pj.exec("DELETE FROM TARMED_EXTENSION"); //$NON-NLS-1$
			monitor.subTask(Messages.TarmedImporter_definitions);
			importDefinition("ANAESTHESIE", "DIGNI_QUALI", "DIGNI_QUANTI", "LEISTUNG_BLOECKE", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				"LEISTUNG_GRUPPEN", "LEISTUNG_TYP", "PFLICHT", "REGEL_EL_ABR", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				"SEITE", "SEX", "SPARTE", "ZR_EINHEIT"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			monitor.worked(13);
			monitor.subTask(Messages.TarmedImporter_chapter);
			ResultSet res =
				source.query(String.format(
					"SELECT * FROM %sKAPITEL_TEXT WHERE SPRACHE=%s", ImportPrefix, lang)); //$NON-NLS-1$
			while (res != null && res.next()) {
				String code = res.getString("KNR"); //$NON-NLS-1$
				
				if (code.trim().equals("I")) { //$NON-NLS-1$
					continue;
				}
				TarmedLeistung tl = TarmedLeistung.load(code);
				String txt = convert(res, "BEZ_255"); //$NON-NLS-1$
				int subcap = code.lastIndexOf('.');
				String parent = "NIL"; //$NON-NLS-1$
				if (subcap != -1) {
					parent = code.substring(0, subcap);
				}
				if ((!tl.exists()) || (!parent.equals(tl.get("Parent")))) { //$NON-NLS-1$
					tl = new TarmedLeistung(code, parent, "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}
				tl.setText(txt);
				monitor.worked(1);
			}
			res.close();
			monitor.subTask(Messages.TarmedImporter_singleLst);
			res = source.query(String.format("SELECT * FROM %sLEISTUNG", ImportPrefix)); //$NON-NLS-1$
			PreparedStatement preps_extension =
				pj.prepareStatement("UPDATE TARMED_EXTENSION SET MED_INTERPRET=?,TECH_INTERPRET=? WHERE CODE=?"); //$NON-NLS-1$
			TimeTool ttToday = new TimeTool();
			while (res.next() == true) {
				String cc = res.getString("LNR"); //$NON-NLS-1$
				// System.out.println(cc);
				TarmedLeistung tl = TarmedLeistung.load(cc);
				if (tl.exists()) {
					tl.set("DigniQuanti", convert(res, "QT_DIGNITAET")); //$NON-NLS-1$ //$NON-NLS-2$
					tl.set("Sparte", convert(res, "Sparte")); //$NON-NLS-1$ //$NON-NLS-2$
				} else {
					tl = new TarmedLeistung(cc, res.getString("KNR"), //$NON-NLS-1$
						"0000", convert(res, "QT_DIGNITAET"), convert(res, "Sparte")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}
				TimeSpan tsValid =
					new TimeSpan(new TimeTool(res.getString("GUELTIG_VON")), new TimeTool(
						res.getString("GUELTIG_BIS")));
				// System.out.println(tsValid.dump());
				if (tsValid.contains(ttToday)) {
					tl.set(new String[] {
						"GueltigVon", "GueltigBis" //$NON-NLS-1$ //$NON-NLS-2$
					}, tsValid.from.toString(TimeTool.DATE_COMPACT),
						tsValid.until.toString(TimeTool.DATE_COMPACT)); //$NON-NLS-1$ //$NON-NLS-2$
					Stm sub = pj.getStatement();
					String dqua =
						sub.queryString(String
							.format(
								"SELECT QL_DIGNITAET FROM %sLEISTUNG_DIGNIQUALI WHERE LNR=%s", ImportPrefix, tl.getWrappedId())); //$NON-NLS-1$
					String kurz = ""; //$NON-NLS-1$
					ResultSet rsub =
						sub.query(String
							.format(
								"SELECT * FROM %sLEISTUNG_TEXT WHERE SPRACHE=%s AND LNR=%s", ImportPrefix, lang, tl.getWrappedId())); //$NON-NLS-1$
					if (rsub.next() == true) {
						kurz = convert(rsub, "BEZ_255"); //$NON-NLS-1$
						String med = convert(rsub, "MED_INTERPRET"); //$NON-NLS-1$
						String tech = convert(rsub, "TECH_INTERPRET"); //$NON-NLS-1$
						preps_extension.setString(1, med);
						preps_extension.setString(2, tech);
						preps_extension.setString(3, tl.getId());
						preps_extension.execute();
					}
					rsub.close();
					tl.set(new String[] {
						"DigniQuali", "Text"}, dqua, kurz); //$NON-NLS-1$ //$NON-NLS-2$
					Hashtable<String, String> ext = tl.loadExtension();
					put(ext, res, "LEISTUNG_TYP", "SEITE", "SEX", "ANAESTHESIE", "K_PFL", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
						"BEHANDLUNGSART", "TP_AL", "TP_ASSI", "TP_TL", "ANZ_ASSI", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
						"LSTGIMES_MIN", "VBNB_MIN", "BEFUND_MIN", "RAUM_MIN", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
						"WECHSEL_MIN", "F_AL", "F_TL"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					
					rsub =
						sub.query(String
							.format(
								"SELECT LNR_MASTER FROM %sLEISTUNG_HIERARCHIE WHERE LNR_SLAVE=%s", ImportPrefix, tl.getWrappedId())); //$NON-NLS-1$
					if (rsub.next()) {
						ext.put("Bezug", rsub.getString(1)); //$NON-NLS-1$
					}
					rsub.close();
					rsub =
						sub.query(String
							.format(
								"SELECT LNR_SLAVE,TYP FROM %sLEISTUNG_KOMBINATION WHERE LNR_MASTER=%s", ImportPrefix, tl.getWrappedId())); //$NON-NLS-1$
					String kombination_and = ""; //$NON-NLS-1$
					String kombination_or = ""; //$NON-NLS-1$
					while (rsub.next()) {
						String typ = rsub.getString(2);
						String slave = rsub.getString(1);
						if (typ != null) {
							if (typ.equals("and")) { //$NON-NLS-1$
								kombination_and += slave + ","; //$NON-NLS-1$
							} else if (typ.equals("or")) { //$NON-NLS-1$
								kombination_or += slave + ","; //$NON-NLS-1$
							}
						}
					}
					rsub.close();
					if (!kombination_and.equals("")) { //$NON-NLS-1$
						String k = kombination_and.replaceFirst(",$", ""); //$NON-NLS-1$ //$NON-NLS-2$
						ext.put("kombination_and", k); //$NON-NLS-1$
					}
					if (!kombination_or.equals("")) { //$NON-NLS-1$
						String k = kombination_or.replaceFirst(",$", ""); //$NON-NLS-1$ //$NON-NLS-2$
						ext.put("kombination_or", k); //$NON-NLS-1$
					}
					rsub =
						sub.query(String
							.format(
								"SELECT * FROM %sLEISTUNG_KUMULATION WHERE LNR_MASTER=%s", ImportPrefix, tl.getWrappedId())); //$NON-NLS-1$
					String exclusion = ""; //$NON-NLS-1$
					String inclusion = ""; //$NON-NLS-1$
					String exclusive = ""; //$NON-NLS-1$
					while (rsub.next()) {
						String typ = rsub.getString("typ"); //$NON-NLS-1$
						String slave = rsub.getString("LNR_SLAVE"); //$NON-NLS-1$
						if (typ != null) {
							if (typ.equals("E")) { //$NON-NLS-1$
								exclusion += slave + ","; //$NON-NLS-1$
							} else if (typ.equals("I")) { //$NON-NLS-1$
								inclusion += slave + ","; //$NON-NLS-1$
							} else if (typ.equals("X")) { //$NON-NLS-1$
								exclusive += slave + ","; //$NON-NLS-1$
							}
						}
					}
					rsub.close();
					if (!exclusion.equals("")) { //$NON-NLS-1$
						String k = exclusion.replaceFirst(",$", ""); //$NON-NLS-1$ //$NON-NLS-2$
						ext.put("exclusion", k); //$NON-NLS-1$
					}
					if (!inclusion.equals("")) { //$NON-NLS-1$
						String k = inclusion.replaceFirst(",$", ""); //$NON-NLS-1$ //$NON-NLS-2$
						ext.put("inclusion", k); //$NON-NLS-1$
					}
					if (!exclusive.equals("")) { //$NON-NLS-1$
						String k = exclusive.replaceFirst(",$", ""); //$NON-NLS-1$ //$NON-NLS-2$
						ext.put("exclusive", k); //$NON-NLS-1$
					}
					rsub =
						sub.query(String
							.format(
								"SELECT * FROM %sLEISTUNG_MENGEN_ZEIT WHERE LNR=%s", ImportPrefix, tl.getWrappedId())); //$NON-NLS-1$
					String limits = ""; //$NON-NLS-1$
					while (rsub.next()) {
						StringBuilder sb = new StringBuilder();
						sb.append(rsub.getString("Operator")).append(","); //$NON-NLS-1$ //$NON-NLS-2$
						sb.append(rsub.getString("Menge")).append(","); //$NON-NLS-1$ //$NON-NLS-2$
						sb.append(rsub.getString("ZR_ANZAHL")).append(","); //$NON-NLS-1$ //$NON-NLS-2$
						sb.append(rsub.getString("PRO_NACH")).append(","); //$NON-NLS-1$ //$NON-NLS-2$
						sb.append(rsub.getString("ZR_EINHEIT")).append("#"); //$NON-NLS-1$ //$NON-NLS-2$
						limits += sb.toString();
					}
					rsub.close();
					if (!limits.equals("")) { //$NON-NLS-1$
						ext.put("limits", limits); //$NON-NLS-1$
					}
					tl.flushExtension();
					pj.releaseStatement(sub);
					
				}
				monitor.worked(1);
				if (monitor.isCanceled()) {
					return Status.CANCEL_STATUS;
				}
			}
			res.close();
			monitor.done();
			return Status.OK_STATUS;
			
		} catch (Exception ex) {
			ex.printStackTrace();
			ExHandler.handle(ex);
		} finally {
			if (source != null) {
				pj.releaseStatement(source);
			}
			if (dest != null) {
				pj.releaseStatement(dest);
			}
			if (deleteCachedAccessTables(monitor) != Status.OK_STATUS) {
				mdbDB = null;
				return Status.CANCEL_STATUS;
			}
			mdbDB = null;
		}
		return Status.CANCEL_STATUS;
	}
	
	private void put(final Hashtable<String, String> h, final ResultSet r, final String... vv)
		throws Exception{
		for (String v : vv) {
			String val = r.getString(v);
			if (val != null) {
				h.put(v, val);
			}
		}
	}
	
	private void importDefinition(final String... strings) throws IOException, SQLException{
		
		Stm stm = pj.getStatement();
		PreparedStatement ps =
			pj.prepareStatement("INSERT INTO TARMED_DEFINITIONEN (Spalte,Kuerzel,Titel) VALUES (?,?,?)"); //$NON-NLS-1$
		try {
			for (String s : strings) {
				ResultSet res =
					stm.query(String.format(
						"SELECT * FROM %sCT_" + s + " WHERE SPRACHE=%s", ImportPrefix, lang)); //$NON-NLS-1$
				while (res.next()) {
					ps.setString(1, s);
					ps.setString(2, res.getString(1));
					ps.setString(3, res.getString(3));
					ps.execute();
				}
				res.close();
			}
		} catch (Exception ex) {
			ExHandler.handle(ex);
		} finally {
			pj.releaseStatement(stm);
		}
	}
	
	@Override
	public String getDescription(){
		return Messages.TarmedImporter_enterSource;
	}
	
	@Override
	public Composite createPage(final Composite parent){
		
		Composite ret = new ImporterPage.FileBasedImporter(parent, this);
		ret.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		return ret;
		
	}
	
	private String convert(ResultSet res, String field) throws Exception{
		Reader reader = res.getCharacterStream(field);
		if (reader == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(reader);
		int c;
		while ((c = br.read()) != -1) {
			sb.append((char) c);
		}
		return sb.toString();
	}
}
