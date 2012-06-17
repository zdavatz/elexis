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

// http://www.jdbc-tutorial.com/jdbc-select-data.htm has also a small menu

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipInputStream;
import java.util.Properties;
import ch.elexis.Hub;
import ch.elexis.data.Artikel;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.rgw.io.InMemorySettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Db_extinfo_updater {
	
	private static ch.rgw.tools.JdbcLink jdbc;
	private static Logger logger = LoggerFactory.getLogger(Db_extinfo_updater.class);
	private static final String FLD_EXTINFO = "ExtInfo";
	private static Connection conn = null;
	private static String currentTable = null;
	private static ArrayList<String> result;
	private static ArrayList<String> extFields = null;
	private static HashMap<String, Number> fieldsLonger150 = null;
	private static ArrayList<String> allTables;
	
	void showProgress(String msg){
		System.out.println(msg);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args){
		String driver = "com.mysql.jdbc.Driver";
		String dbConnectString = "jdbc:mysql://localhost:3306/elexis";
		String typ = "mySQl";
		String user = "elexis";
		String pw = "elexisTest";
		Method method1 = null;
		try {
			conn = getConnection(dbConnectString, user, pw);
			method1 = Db_extinfo_updater.class.getMethod("jdbcGetAllTableNames", null);
			measureProcTime(method1);
			for (int j = 0; j < allTables.size(); j++) {
				method1 = Db_extinfo_updater.class.getMethod("jdbcGetExtInfoForTable", null);
				// measureProcTime(method1);
				currentTable = allTables.get(j);
				method1 =
					Db_extinfo_updater.class.getMethod("jdbcCopyTableAndConvertExtInfo", null);
				measureProcTime(method1);
			}
			
			currentTable = "Artikel";
			method1 = Db_extinfo_updater.class.getMethod("jdbcCopyTableAndConvertExtInfo", null);
			measureProcTime(method1);
			
			method1 = Db_extinfo_updater.class.getMethod("getFieldsOfAllTables", null);
			measureProcTime(method1);
			logger.info(Db_extinfo_updater.class.toString() + " started");
			jdbc = persistenceInitDB(driver, dbConnectString, typ, user, pw);
			jdbc = PersistentObject.getConnection();
			method1 = Db_extinfo_updater.class.getMethod("getFieldsOfAllPersistenceTables", null);
			measureProcTime(method1);
			method1 = Db_extinfo_updater.class.getMethod("getInfoAboutPersistenceArtikel", null);
			measureProcTime(method1);
		} catch (NoSuchMethodException e) {
			System.out.println("NoSuchMethodException");
			System.exit(3);
			// TODO Auto-generated catch block
		}
	}
	
	public static void getFieldsOfAllPersistenceTables(){
		allTables = getAllTableNames();
		System.out.println(allTables.toString());
		String aTable = null;
		ArrayList<String> fields = null;
		
		for (int j = 0; j < allTables.size(); j++) {
			aTable = allTables.get(j);
// System.out.println(aTable);
			fields = getAllFields(aTable);
// System.out.println(fields.toString());
		}
		System.out.println(String.format(
			"getFieldsOfAllPersistenceTables: last table was\n%1$s with %2$s", aTable,
			fields.toString()));
	}
	
	public static void getFieldsOfAllTables(){
		jdbcGetAllTableNames();
		for (int j = 0; j < allTables.size(); j++) {
			currentTable = allTables.get(j);
			jdbcGetAllFields();
		}
		System.out.println(String.format("getFieldsOfAllTables: last table was\n%1$s with %2$s",
			currentTable, result.toString()));
	}
	
	public static Connection getConnection(String defConnect, String dbUser, String dbPassword){
		conn = null;
		Properties connectionProps = new Properties();
		connectionProps.put("user", dbUser);
		connectionProps.put("password", dbPassword);
		
		try {
			conn = DriverManager.getConnection(defConnect, connectionProps);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Could not open " + defConnect);
			System.exit(3);
		}
		System.out.println("Connected to database: " + defConnect);
		return conn;
	}
	
	public static void jdbcGetAllTableNames(){
		ArrayList<String> res = new ArrayList<String>();
		try {
			DatabaseMetaData dmd = conn.getMetaData();
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
		allTables = res;
		result = res;
	}
	
	public static ArrayList<String> jdbcGetAllFields(){
		ArrayList<String> fields = new ArrayList<String>();
		Statement stmt = null;
		String query = "Select * from " + currentTable;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			ResultSetMetaData md = rs.getMetaData();
			int col = md.getColumnCount();
			for (int i = 1; i <= col; i++) {
				fields.add(md.getColumnName(i));
			}
		} catch (SQLException e1) {
			logger.error("Fehler beim Abrufen der Felder der Tabelle");
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					logger.error("Fehler bei stmt.close");
				}
			}
		}
		result = fields;
		return fields;
	}
	
	private static <T> void measureProcTime(Method method1){
		Date d1 = new Date(System.currentTimeMillis());
		try {
			method1.invoke(null, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("\nError calling " + method1.toString());
			e.printStackTrace();
		}
		Date d2 = new Date(System.currentTimeMillis());
		long difference = d2.getTime() - d1.getTime();
		System.out.println(String.format("Elapsed %1$d.%2$d seconds while executing %3$s.",
			difference / 1000, difference % 1000, method1.getName()));
	}
	
	// From PersistenceOjbect
	@SuppressWarnings("unchecked")
	public static Hashtable<Object, Object> fold(final byte[] flat){
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(flat);
			ZipInputStream zis = new ZipInputStream(bais);
			zis.getNextEntry();
			ObjectInputStream ois = new ObjectInputStream(zis);
			Hashtable<Object, Object> res = (Hashtable<Object, Object>) ois.readObject();
			ois.close();
			bais.close();
			return res;
		} catch (Exception ex) {
// ExHandler.handle(ex);
			return null;
		}
	}
	
	// Adapted and modified from PersistenceOjbect
	@SuppressWarnings("rawtypes")
	private static Map mimicGetMap(ResultSet rs, final String field){
		byte[] blob;
		try {
			blob = rs.getBytes(field);
		} catch (SQLException e) {
			return new Hashtable();
		}
		if (blob == null) {
			return new Hashtable();
		}
		Hashtable<Object, Object> ret = fold(blob);
		if (ret == null) {
			return new Hashtable();
		}
		return ret;
	}
	
	public static void jdbcGetExtInfoForTable()
	
	{
		fieldsLonger150 = new HashMap<String, Number>();
		ArrayList<String> fields = jdbcGetAllFields();
		extFields = new ArrayList<String>();
		Statement stmt = null;
		ResultSet rs = null;
		int maxLength = 0;
		int j = 0;
		
		// Get all extinfo which are not null
		String query = "Select * from " + currentTable + " where extInfo is not null";
		System.out.println("Starting Query " + query);
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				j++;
				String extInfo = rs.getString("extinfo");
				Map<String, String> maps = mimicGetMap(rs, "extInfo");
				Iterator<?> x = maps.entrySet().iterator();
				while (x.hasNext()) {
					Object obj = x.next();
					Entry ht = (Entry) obj;
					String fieldName = ht.getKey().toString();
					String validFieldName = getValidFieldName(fieldName);
					int length = ht.getValue().toString().length();
					if (!extFields.contains(fieldName) && !fields.contains(fieldName))
						extFields.add(fieldName);
					// System.out.println(String.format("found <%1$s -> %2$s>", fieldName,
					// ht.getValue().toString()));
					if (maxLength < length)
						maxLength = length;
					if (length > 150) {
						if (fieldsLonger150.get(validFieldName) == null)
							fieldsLonger150.put(validFieldName, length);
						else if (fieldsLonger150.get(validFieldName).intValue() < length)
							fieldsLonger150.put(validFieldName, length);
					}
				}
			}
			stmt.close();
			
		} catch (SQLException e1) {
			logger.error("Fehler beim Abrufen extinfo der Tabelle " + currentTable);
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					logger.error("Fehler bei stmt.close");
				}
			}
		}
		System.out.println(String.format("Query %3$s returned %1$d rows\n%2$s longs %3$s", j,
			extFields.toString(), query, fieldsLonger150.toString()));
	}
	
	public static String getValidFieldName(String oldFieldName){
		String newFieldName = oldFieldName.replaceAll("[^a-zA-Z]", "_");
		ArrayList<String> reservedWords =
			new ArrayList<String>(Arrays.asList("FULLTEXT", "INT", "WORD"));
		if (reservedWords.contains(newFieldName.toUpperCase()))
			newFieldName = newFieldName + "_add";
		return newFieldName;
	}
	
	public static void jdbcConvertExtInfo(){
		String query = null;
		Statement stmt = null;
		ResultSet rs = null;
		int j = 0;
		String fieldName = "";
		String value = "";
		
		// - Extract all info from the extInfo
		// - Update the row with the new value and set extInfo to null
		
		query = "Select * from " + currentTable + " where extInfo is not null";
		System.out.println("Starting Query " + query);
		try {
			stmt =
				conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			conn.setAutoCommit(false);
			
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				j++;
				if (j % 8000 == 1) {
					System.out.print(String.format("\n%1$20s: %2$6d ", currentTable, j - 1));
					conn.commit();
				}
				if (j % 100 == 0)
					System.out.print(".");
				Map<String, String> maps = mimicGetMap(rs, "extInfo");
				Iterator<?> x = maps.entrySet().iterator();
				while (x.hasNext()) {
					Object obj = x.next();
					Entry<?, ?> ht = (Entry<?, ?>) obj;
					fieldName = getValidFieldName(ht.getKey().toString());
					value = ht.getValue().toString();
					rs.updateString(fieldName, value);
				}
				rs.updateString("extinfo", null);
				rs.updateRow();
			}
			stmt.close();
			
		} catch (SQLException e1) {
			System.out.println(value.length());
			System.out.println(value.length());
			logger.error("Fehler beim Updaten der extinfo der Tabelle " + currentTable + " "
				+ e1.getMessage());
			String msg =
				String.format("\n%1$s %2$d field %3$s value %4$s", currentTable, j, fieldName,
					value);
			System.out.println(msg);
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					logger.error("Fehler bei stmt.close" + currentTable + " " + e.getMessage());
				}
			}
		}
		System.out.println(String.format("\nTable %1$s updated %2$d rows", currentTable, j));
		
	}
	
	public static int getFieldLength(String fieldName){
		if (fieldsLonger150.get(fieldName) != null)
			// Add some margin
			return fieldsLonger150.get(fieldName).intValue() * 2;
		else
			return 255;
	}
	
	public static void jdbcCopyTableAndConvertExtInfo(){
		// Return we are running on a copy
		if (currentTable.toLowerCase().endsWith("_copy")) {
			System.out.println("\nSkipping as" + currentTable + " ends with _copy");
			return;
		}
		
		// Return if we don't have a field extinfo
		ArrayList<String> fields = jdbcGetAllFields();
		boolean found = false;
		int j = 0;
		for (j = 0; j < fields.size(); j++)
			if (fields.get(j).compareToIgnoreCase("extinfo") == 0) {
				found = true;
			}
		if (!found) {
			System.out.println("\nSkipping as no field extinfo in " + currentTable);
			return;
		}
		
		String query = null;
		extFields = null;
		Statement stmt = null;
		ArrayList<String> copyStmts = new ArrayList<String>();
		copyStmts.add(String.format("DROP TABLE IF EXISTS %1$s_COPY", currentTable));
		copyStmts.add(String.format("CREATE TABLE %1$s_COPY LIKE %1$s", currentTable));
		copyStmts.add(String.format("INSERT %1$s_COPY SELECT * FROM %1$s", currentTable));
		
		String tableCopy = String.format("%1$s_COPY", currentTable);
		try {
			for (int k = 0; k < copyStmts.size(); k++) {
				query = copyStmts.get(k);
				System.out.println(query);
				stmt = conn.createStatement();
				stmt.executeUpdate(query);
				stmt.close();
			}
		} catch (SQLException e1) {
			logger.error("Fehler beim Abrufen von " + query + " " + e1.getLocalizedMessage());
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					logger.error("Fehler bei stmt.close" + query);
				}
			}
		}
		
		currentTable = tableCopy;
		Method method = null;
		try {
			// Now we will get the info about alle extinfo fields
			method = Db_extinfo_updater.class.getMethod("jdbcGetExtInfoForTable", null);
			measureProcTime(method);
			// Now add all the extInfo fields as database fields
			// But watch out for invalid names!
			System.out.println(currentTable + " extFields to add are: " + extFields);
			for (j = 1; j < extFields.size(); j++) {
				extFields.set(j, getValidFieldName(extFields.get(j)));
			}
			
			System.out.println(currentTable + "extFields changed to: " + extFields);
			StringBuilder sb = null;
			String extFieldName;
			if (extFields.size() > 0) {
				extFieldName = extFields.get(0);
				sb =
					new StringBuilder(String.format("Alter table %1$s add %2$s VARCHAR(%3$d)",
						currentTable, extFieldName, getFieldLength(extFieldName)));
				for (j = 1; j < extFields.size(); j++) {
					extFieldName = extFields.get(j);
					sb.append(String.format(", add %1$s VARCHAR(%2$s)", extFieldName,
						getFieldLength(extFieldName)));
				}
				String addStatement = sb.toString();
				try {
					System.out.println(addStatement);
					Statement addStmt;
					addStmt = conn.createStatement();
					addStmt.executeUpdate(addStatement);
					addStmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					System.out.println("Error executing " + addStatement);
					System.out.println(e.getMessage());
				}
			}
			
			// Now we have all the info and will iterate over the copied table
			method = Db_extinfo_updater.class.getMethod("jdbcConvertExtInfo", null);
			measureProcTime(method);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			System.out.println("SecurityException executing " + method.getName());
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			System.out.println("NoSuchMethodException executing " + method.getName());
		}
		
		System.out.println(String.format("Query %3$s returned %1$d rows\n%2$s", j,
			extFields.toString(), query));
	}
	
	public static void getInfoAboutPersistenceArtikel()
	
	{
		Date d1 = new Date(System.currentTimeMillis());
		ArrayList<String> fields = getAllFields("Artikel");
		// Query<Artikel> qbe = new Query<Artikel>(Artikel.class,
// "select extinfo from artikel where extinfo is not null and ean like '7680556540057'");
		Query<Artikel> qbe = new Query<Artikel>(Artikel.class);
// qbe.clear();
// qbe.add("extInfo", "is", "not null");
// qbe.and();
// qbe.add("EAN", qbe.LIKE, "76805565%");
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
		extFields = new ArrayList<String>();
		int maxLength = 0;
		for (int j = 0; j < lArt.size(); j++) {
			Artikel art = lArt.get(j);
			Map<String, String> h = art.getMap(FLD_EXTINFO);
			if (j > 2000000) {
				if (h.containsKey("EAN")) {
					System.out.println(String.format(
						"%4$6d: Artikel mit EAN      %1$15s == %2$15s? %3$12s in extInfo",
						art.getEAN(), h.get("EAN"), art.get("Typ"), j));
					
				}
				if (h.containsKey("ATC_code")) {
					System.out.println(String.format(
						"%4$6d: Artikel mit ATC_code %1$15s == %2$15s? %3$12s in extInfo",
						art.getEAN(), h.get("ATC_code"), art.get("Typ"), j));
					
				}
			}
			Iterator<?> x = h.entrySet().iterator();
			while (x.hasNext()) {
				Object obj = x.next();
				Entry ht = (Entry) obj;
				String fieldName = ht.getKey().toString();
				if (!extFields.contains(fieldName) && !fields.contains(fieldName))
					extFields.add(fieldName);
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
	private static ch.rgw.tools.JdbcLink persistenceInitDB(String driver, String connection,
		String typ, String dbUser, String dbPassword){
		Hub.localCfg = new InMemorySettings();
		ch.rgw.tools.JdbcLink link = new ch.rgw.tools.JdbcLink(driver, connection, typ);
		link.connect(dbUser, dbPassword);
		PersistentObject.connect(link);
		return link;
	}
	
}
