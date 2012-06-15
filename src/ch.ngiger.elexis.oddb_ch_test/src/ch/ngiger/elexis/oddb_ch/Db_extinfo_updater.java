/*******************************************************************************
 * Copyright (c) 2012 Niklaus Giger <niklaus.giger@member.fsf.org>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Niklaus Giger <niklaus.giger@member.fsf.org> - initial API and implementation
 ******************************************************************************/
package ch.ngiger.elexis.oddb_ch;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ch.elexis.Hub;
import ch.elexis.data.Artikel;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.elexis.util.Log;
import ch.rgw.io.InMemorySettings;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.JdbcLink.Stm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Db_extinfo_updater {
	
	private static PersistentObject object = null;
	private static JdbcLink jdbc;
	private static Logger logger = LoggerFactory.getLogger(Db_extinfo_updater.class);
	private static final String FLD_EXTINFO = "ExtInfo";
	
	/**
	 * @param args
	 */
	public static void main(String[] args){
		String driver = "com.mysql.jdbc.Driver";
		String dbConnectString = "jdbc:mysql://localhost:3306/elexis";
		String typ = "mySQl";
		String user = "elexis";
		String pw = "elexisTest";
		logger.info(Db_extinfo_updater.class.toString() + " started");
		jdbc = initDB(driver, dbConnectString, typ, user, pw);
		jdbc = PersistentObject.getConnection();
		ArrayList<String> allTables = getAllTableNames();
		System.out.println(allTables.toString());
		
		for (int j = 0; j < 1 /* allTables.size() */; j++) {
			String aTable = allTables.get(j);
			System.out.println(aTable);
			ArrayList<String> fields = getAllFields(aTable);
			System.out.println(fields.toString());
		}
		ArrayList<String> fields = getAllFields("artikel");
		System.out.println(fields.toString());
		getInfoAboutArtikel();
	}
	
	private static void getInfoAboutArtikel()
	
	{
		Date d1 = new Date(System.currentTimeMillis());
		// Query<Artikel> qbe = new Query<Artikel>(Artikel.class,
// "select extinfo from artikel where extinfo is not null and ean like '7680556540057'");
		Query<Artikel> qbe = new Query<Artikel>(Artikel.class);
// qbe.clear();
// qbe.add("extInfo", "is", "not null");
// qbe.and();
//		qbe.add("EAN", qbe.LIKE, "76805565%");
		qbe.add("EAN", qbe.LIKE, "7680%");
		qbe.and();
		qbe.add("extInfo", qbe.NOT_EQUAL, null);
// qbe.add(feld, operator, wert)
		System.out.println("Starting Query " + qbe.toString());
		List<Artikel> lArt = qbe.execute();
		System.out.println("Query returned " + lArt.size());
		Date d2 = new Date(System.currentTimeMillis());
		long difference = d2.getTime() - d1.getTime();
		System.out.println(String.format("Elapsed %1$d.%2$d seconds. Got %3$d items",
			difference / 1000, difference % 1000, lArt.size()));
		ArrayList<String> extFields = new ArrayList<String>();
		int maxLength = 0;
		for (int j = 0; j < lArt.size(); j++) {
			Artikel art = lArt.get(j);
			Map<String, String> h = art.getMap(FLD_EXTINFO);
			if (h.containsKey("EAN")) {
				System.out.println(String.format(
					"%4$6d: Artikel mit EAN      %1$15s == %2$15s? %3$12s in extInfo", art.getEAN(),
					h.get("EAN"), art.get("Typ"), j));
				
			}
			if (h.containsKey("ATC_code")) {
				System.out.println(String.format(
					"%4$6d: Artikel mit ATC_code %1$15s == %2$15s? %3$12s in extInfo", art.getEAN(),
					h.get("ATC_code"), art.get("Typ"), j));
				
			}
			Iterator<?> x = h.entrySet().iterator();
			while (x.hasNext()) {
				Object obj = x.next();
				Entry ht = (Entry) obj;
				if (!extFields.contains(ht.getKey()))
					extFields.add(ht.getKey().toString());
				if (maxLength < ht.getValue().toString().length())
					maxLength = ht.getValue().toString().length();
			}
		}
		Date d3 = new Date(System.currentTimeMillis());
		long difference2 = d3.getTime() - d2.getTime();
		System.out.println(String.format("maxLenght was %2$d with %3$d Maps in extInfo are:\n%1$s",
			extFields.toString(), maxLength, extFields.size()));
		
		System.out.println(String.format("Elapsed %1$d.%2$d seconds. Got %3$d items",
			difference / 1000, difference % 1000, lArt.size()));
		System.out.println(String.format("Elapsed %1$d.%2$d seconds for handling %3$d extinfo",
			difference2 / 1000, difference2 % 1000, lArt.size()));
		
	}
	
	private static ArrayList<String> getAllTableNames(){
		ArrayList<String> res = new ArrayList<String>();
		try {
			DatabaseMetaData dmd = jdbc.getConnection().getMetaData();
			String[] onlyTables = {
				"TABLE"
			};
			ResultSet rs = dmd.getTables(null, null, "%", onlyTables);
			if (rs != null) {
				while (rs.next()) {
					// DatabaseMetaData#getTables() specifies TABLE_NAME is in
					// column 3
					res.add(rs.getString(3));
				}
			}
		} catch (SQLException je) {
			logger.error("Fehler beim Abrufen der Datenbank Tabellen Information.");
		}
		return res;
	}
	
	private static ArrayList<String> getAllFields(String tablename){
		ArrayList<String> fields = new ArrayList<String>();
		try {
			try {
				ResultSet res = jdbc.getStatement().query("Select * from " + tablename);
				ResultSetMetaData md = res.getMetaData();
				int col = md.getColumnCount();
				for (int i = 1; i <= col; i++) {
					fields.add(md.getColumnName(i));
				}
			} catch (SQLException s) {
				System.out.println("SQL statement is not executed!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return fields;
	}
	
	// create a JdbcLink with an initialized db for elexis
	private static JdbcLink initDB(String driver, String connection, String typ, String dbUser,
		String dbPassword){
		Hub.localCfg = new InMemorySettings();
		JdbcLink link = new JdbcLink(driver, connection, typ);
		link.connect(dbUser, dbPassword);
		PersistentObject.connect(link);
		return link;
	}
	
}
